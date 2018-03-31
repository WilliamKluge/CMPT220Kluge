/*
 * Copyright 2014 Carnegie Mellon University.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 */

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.Context;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.linguist.dictionary.Pronunciation;
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.result.WordResult;
import edu.cmu.sphinx.util.TimeFrame;
import java.applet.AudioClip;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import sun.audio.AudioData;
import sun.audio.AudioDataStream;
import sun.audio.AudioPlayer;

/**
 * A simple example that shows how to transcribe a continuous audio file that has multiple
 * utterances in it.
 */
public class PhoneToDecTest {

  /* Number of milliseconds that are allowed between phones before a pause is added */
  private static final int PHONE_GAP_PAUSE = 10;
  /* If all phones should be replaced with "uw"(oo)s */
  private static final boolean BEAT_ONLY = false;
  /* If the program should not wait if the pause is more than 1 second */
  private static final boolean DELETE_LONG_PAUSES = true;
  /* Run with user interaction */
  private static final boolean INTERACTIVE_MODE = true;

  /**
   * Tests phone recognition, pitch detection, and DECtalk conversion
   */
  public static void main(String[] args) throws Exception {

    final String SOURCE_FILE = args[0];

    if (SOURCE_FILE == null) {
      System.out.println("No file argument given, please specify a path to the target file as the"
          + "only argument.");
      return;
    }

    Scanner input = new Scanner(System.in);

    ///// Open audio input stream /////
    AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(SOURCE_FILE));
    // Load the entire source audio into a clip
    DataLine.Info info = new DataLine.Info(Clip.class, audioInputStream.getFormat());
    Clip sourceClip;
    sourceClip = (Clip) AudioSystem.getLine(info);
    sourceClip.open(audioInputStream);

    ///// Setup audio playback and file handling /////
    File sourceFile = new File(SOURCE_FILE);
    InputStream stream = new FileInputStream(sourceFile);

    ///// Setup Sphinx /////
    System.out.println("Loading models...");
    Configuration configuration = new Configuration();
    // Load model from the jar
    configuration.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
    // You can also load model from folder
    // configuration.setAcousticModelPath("file:en-us");
    configuration.setDictionaryPath("resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict");
    Context context = new Context(configuration);
    context.setLocalProperty("decoder->searchManager", "allphoneSearchManager");
    Recognizer recognizer = context.getInstance(Recognizer.class);
    // Simple recognition with generic model
    recognizer.allocate();
    context.setSpeechSource(stream, TimeFrame.INFINITE);
    Result result;

    ///// Setup phone collection and file output /////
    String outFileName = "out/DECFiles/" + new SimpleDateFormat("yyyyMMddHHmm").format(new Date())
        + "_out.txt";
    DECPhoneCollection dectalkPhones = new DECPhoneCollection(sourceFile, outFileName);
    PitchAnalysis pitchAnalysis = new PitchAnalysis(SOURCE_FILE);

    ///// Process phone recognition results /////
    while ((result = recognizer.recognize()) != null) {
      SpeechResult speechResult = new SpeechResult(result);
      System.out.format("Hypothesis: %s\n", speechResult.getHypothesis());
      System.out.println("List of recognized words and their times:");

      long lastEndTime = 0; // Time that the last word ended (for inserting pauses)

      // If phrases are going to be supported, add them here
      for (WordResult r : speechResult.getWords()) {
        try {

          DECtalkPhone phone = new DECtalkPhone(r.getWord().toString(), r.getTimeFrame());

          ///// Handle pauses between phones /////
          long thisStartTime = r.getTimeFrame().getStart();

          if (thisStartTime > lastEndTime + PHONE_GAP_PAUSE && !DELETE_LONG_PAUSES) {
            dectalkPhones.addPause(new TimeFrame(lastEndTime + 1, thisStartTime - 1));
          }

          lastEndTime = r.getTimeFrame().getEnd();

          ///// Pitch Analysis ///
          phone.setToneNumber(pitchAnalysis.getDECtalkToneNumber(r.getTimeFrame()));

          ///// Output /////
          if (BEAT_ONLY) {
            // If the phone is not important, replace it with an "uw" (as in boom) sound
            phone.setPhone("uw");
          }

          if (phone.getPhone().equals("")) {
            // If the phone is blank (something that CMU processes but DECtalk does not), skip it
            continue;
          }

          dectalkPhones.addPhone(phone);

        } catch (IndexOutOfBoundsException e) {
          System.out.println(e.getMessage());
        }
      }
    }
    recognizer.deallocate(); // We're done with recognition, get rid of the recognizer

    if (!INTERACTIVE_MODE) {
      // If there should not be user interation, write the output file and end the program
      dectalkPhones.writeDECtalkFile();
      return;
    }

    ///// Get user input about generated phones /////
    while (dectalkPhones.currentPhoneIndex < dectalkPhones.getPhoneCount()) {
      // While the current phone's index is less than the total number of phones
      dectalkPhones.playCurrentPhone();

      System.out.println(
          "Did this sound like the phone " + dectalkPhones.getCurrentPhonePronunciation()
              + " (y/n)? ");

      if (input.nextLine().equals("n")) {
        System.out.print("Enter the correct phone: ");
        dectalkPhones.setCurrentPhonePronunciation(input.nextLine());
      }

    } // End user interaction while loop

    dectalkPhones.writeDECtalkFile();

  } // End method main

} // End class PhoneToDecTest