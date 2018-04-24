package soundhandling;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.io.jvm.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;
import be.tarsos.dsp.pitch.PitchProcessor.PitchEstimationAlgorithm;
import edu.cmu.sphinx.util.TimeFrame;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import javafx.util.Pair;
import javax.sound.sampled.UnsupportedAudioFileException;

public class PitchAnalysis {

  private ArrayList<Pair<Double, Float>> pitches;

  private static final int BUFFER_SIZE = 2048;
  private static final int SAMPLE_RATE = 16000;
  private static final boolean ROUND_UNDEFINED_TONES = true;

  private static int[][] toneNumbersAndHz = new int[][]{
      {1, 65}, {2, 69}, {3, 73}, {4, 77}, {5, 82}, {6, 87}, {7, 92}, {8, 98}, {9, 103},
      {10, 110}, {11, 116}, {12, 123}, {13, 130}, {14, 138}, {15, 146}, {16, 155}, {17, 164},
      {18, 174}, {19, 185}, {20, 196}, {21, 208}, {22, 220}, {23, 233}, {24, 247}, {25, 261},
      {26, 277}, {27, 293}, {28, 311}, {29, 329}, {30, 348}, {31, 370}, {32, 392}, {33, 415},
      {34, 440}, {35, 466}, {36, 494}, {37, 523}}; // TODO make into map

  private static Map<Integer, Integer> pianoKeysAndToneNumber;
  static {
    pianoKeysAndToneNumber = new HashMap<>();
    if (ROUND_UNDEFINED_TONES) {
      for (int i = 22; i <= 35; ++i) { // 22 to 35 < 65 Hz
        pianoKeysAndToneNumber.put(i, 1);
      }
    }
    pianoKeysAndToneNumber.put(36, 2); // 36 = 65.406 Hz
    pianoKeysAndToneNumber.put(37, 2); // 37 = 69.296 Hz
    // Everything in this interval actually increments by one
    for (int key = 38, tone = 3; key < 73; ++key, ++tone) {
      pianoKeysAndToneNumber.put(key, tone);
    }
    if (ROUND_UNDEFINED_TONES) {
      for (int i = 73; i <= 108; ++i) {
        pianoKeysAndToneNumber.put(i, 37);
      }
    }
  }

  /**
   * Constructs the soundhandling.PitchAnalysis class.
   *
   * This constructs the class and analysis the pitches of the file. After this the class is ready
   * to produce a pitch for any specified time in the file.
   *
   * @param fileName Name of the file to work with
   * @throws IOException ...
   * @throws UnsupportedAudioFileException ...
   */
  public PitchAnalysis(String fileName) throws IOException, UnsupportedAudioFileException {

    pitches = new ArrayList<>();

    PitchDetectionHandler handler = new PitchDetectionHandler() {
      @Override
      public void handlePitch(PitchDetectionResult pitchDetectionResult,
          AudioEvent audioEvent) {
        pitches.add(new Pair<>(audioEvent.getTimeStamp(), pitchDetectionResult.getPitch()));
        //System.out.println(audioEvent.getTimeStamp() + " " + pitchDetectionResult.getPitch());
      }
    };

    AudioDispatcher adp = AudioDispatcherFactory.fromFile(new File(fileName), BUFFER_SIZE, 0);
    adp.addAudioProcessor(
        new PitchProcessor(PitchEstimationAlgorithm.YIN, SAMPLE_RATE, BUFFER_SIZE, handler));
    adp.run();

    // Remove pitches equal to -1, cause they lying TODO update other methods now that this happens
    Predicate<Pair<Double, Float>> pitchPredicate = p-> p.getValue() == -1;
    pitches.removeIf(pitchPredicate);

  }

  /**
   * Gets the DECtalk tone number associated with the pitch at the specified timestamp
   *
   * @param timeFrame Time frame the phone is occurring in
   * @return Key code corresponding to the pitch
   *
   * TODO get this to use a time frame
   */
  public int getDECtalkToneNumber(TimeFrame timeFrame) {
    // Associates a DECtalk tone key with a pitch
    float pitch = getPitchAtTime(timeFrame);
    return pitchToToneNumber(pitch);
  }

  public static int pitchToToneNumber(float pitch) {
    int closestToneNumberIndex = 0;
    float bestToneDifference = Math.abs(toneNumbersAndHz[0][1] - pitch);

    for (int i = 0; i < toneNumbersAndHz.length; ++i) {
      float hzDifference = Math.abs(toneNumbersAndHz[i][1] - pitch);

      if (hzDifference < bestToneDifference) {
        bestToneDifference = hzDifference;
        closestToneNumberIndex = i;
      }

    }

    return toneNumbersAndHz[closestToneNumberIndex][0];
  }

  public static int pianoKeyToToneNumber(int key) {
    if (pianoKeysAndToneNumber.containsKey(key)) {
      return pianoKeysAndToneNumber.get(key);
    } else {
      return (int) keyToHz(key);
    }
  }

  public static float keyToHz(int key) {
    float hz = (float) (2 * ((key - 49.0) / 12.0) * 440.0);
    return hz;
  }

  /**
   * Gets the pitch (in Hz) at a specified time.
   *
   * @param timeFrame Time frame to analyze the pitch at
   * @return The pitch at this time (Hz)
   */
  private float getPitchAtTime(TimeFrame timeFrame) {

    float pitchSum = 0;
    int addedNumbers = 0;

    for (int i = 0; i < pitches.size(); i++) {
      Pair<Double, Float> pitch = pitches.get(i);

      if (pitch.getKey() >= timeFrame.getStart() / 1000.0
          && pitch.getKey() <= timeFrame.getEnd() / 1000.0) {
        if (pitch.getValue() == -1) {
          // Do not allow the pitch to be -1 (no sound, if we have a phone there is sound)
          // TODO make sure this isn't as dumb as it looks (it probs is, whatevs)
          int oldAddedNumbers = addedNumbers;
          for (int j = i, k = i; j > 0 || k < pitches.size(); --j, ++k) {

            if (j > 0 && pitches.get(j).getValue() != -1) {
              pitchSum += pitches.get(j).getValue();
              ++addedNumbers;
            }

            if (k < pitches.size() && pitches.get(k).getValue() != -1) {
              pitchSum += pitches.get(k).getValue();
              ++addedNumbers;
            }

            if (oldAddedNumbers != addedNumbers) {
              break; // Once we find one number we're done
            }

          }
        } else {
          pitchSum += pitch.getValue();
        }
        ++addedNumbers;
      }

    }

    return pitchSum / addedNumbers;

  }

}
