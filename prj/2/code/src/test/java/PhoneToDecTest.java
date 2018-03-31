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

  private static void playClipSection(Clip clip, TimeFrame timeFrame) {

    double framesPerMillisecond = clip.getFrameLength() / (clip.getMicrosecondLength() * 1000.0);
    int phoneStartFrame = (int) Math.floor(timeFrame.getStart() * framesPerMillisecond);
    int phoneEndFrame = (int) Math.ceil(timeFrame.getEnd() * framesPerMillisecond);

    clip.setLoopPoints(phoneStartFrame, phoneEndFrame);

    clip.loop(0);

  }

  public static void main(String[] args) throws Exception {

    final String TEST_FILE = args[0];

    if (TEST_FILE == null) {
      System.out.println("No file argument given, please specify a path to the target file as the"
          + "only argument.");
      return;
    }

    Scanner input = new Scanner(System.in);

    ///// Open audio input stream /////
    AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(TEST_FILE));
    // load the sound into memory (a Clip)
    DataLine.Info info = new DataLine.Info(Clip.class, audioInputStream.getFormat());
    Clip clip;
    clip = (Clip) AudioSystem.getLine(info);
    clip.open(audioInputStream);

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
    File sourceFile = new File(TEST_FILE);
    InputStream stream = new FileInputStream(sourceFile);
    SourceAudioPlayer sourceAudioPlayer = new SourceAudioPlayer(sourceFile);

    // Simple recognition with generic model
    recognizer.allocate();
    context.setSpeechSource(stream, TimeFrame.INFINITE);
    Result result;

    { // Make the directory for output if it doesn't exist
      File directory = new File("out/DECFiles/");
      if (! directory.exists()){
        directory.mkdirs();
        // If you require it to make the entire directory path including parents,
        // use directory.mkdirs(); here instead.
      }
    }

    // Writes to dectalk file
    String fileName = "out/DECFiles/" + new SimpleDateFormat("yyyyMMddHHmm").format(new Date())
        + "_out.txt";
    BufferedWriter DECtalkFile = new BufferedWriter(new FileWriter(fileName, true));
    DECtalkFile.write("[:phoneme on]\n");
    ArrayList<DECtalkPhone> dectalkPhones = new ArrayList<>();

    while ((result = recognizer.recognize()) != null) {
      DECtalkFile.write("[");

      SpeechResult speechResult = new SpeechResult(result);
      System.out.format("Hypothesis: %s\n", speechResult.getHypothesis());
      System.out.println("List of recognized words and their times:");

      PitchAnalysis pitchAnalysis = new PitchAnalysis(TEST_FILE);

      long lastEndTime = 0; // Time that the last word ended (for inserting pauses)

      for (WordResult r : speechResult.getWords()) {
        try {

          DECtalkPhone phone = new DECtalkPhone(r.getWord().toString(), r.getTimeFrame());

          long thisStartTime = r.getTimeFrame().getStart();

          if (thisStartTime > lastEndTime + PHONE_GAP_PAUSE && !DELETE_LONG_PAUSES) {
            dectalkPhones.add(new DECtalkPhone(new TimeFrame(lastEndTime + 1, thisStartTime - 1)));
          }

          lastEndTime = r.getTimeFrame().getEnd();

          // Pitch Analysis

          phone.setToneNumber(pitchAnalysis.getDECtalkToneNumber(r.getTimeFrame()));

          // Ask user about clip

          if (INTERACTIVE_MODE) {

            sourceAudioPlayer.playPhoneSection(phone);

            System.out.println("Did this sound like the phone " + phone.getPhone() + " (y/n)? ");

            if (input.nextLine().equals("n")) {
              System.out.print("Enter the correct phone: ");
              phone.setPhone(input.nextLine());
            }

          }

          // Output

          if (BEAT_ONLY) {
            phone.setPhone("uw");
          }

          if (phone.getPhone().equals("")) {
            continue;
          }

          DECtalkFile.write(phone.toString());
          //System.out.println(DECtalkCode);
        } catch (IndexOutOfBoundsException e) {
          System.out.println(e.getMessage());
        }
        //System.out.println(r);
      }
      DECtalkFile.append("]\n"); // Closes the [ from the first print statement
    }
    DECtalkFile.close();
    recognizer.deallocate();

  }

}