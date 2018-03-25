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
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.result.WordResult;
import edu.cmu.sphinx.util.TimeFrame;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A simple example that shows how to transcribe a continuous audio file that has multiple
 * utterances in it.
 */
public class PhoneToDecTest {

  public static void main(String[] args) throws Exception {

    final String TEST_FILE = args[0];

    System.out.println("Loading models...");

    Configuration configuration = new Configuration();

    // Load model from the jar
    configuration
        .setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");

    // You can also load model from folder
    // configuration.setAcousticModelPath("file:en-us");

    configuration
        .setDictionaryPath("resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict");
    Context context = new Context(configuration);
    context.setLocalProperty("decoder->searchManager", "allphoneSearchManager");
    Recognizer recognizer = context.getInstance(Recognizer.class);
    InputStream stream = new FileInputStream(new File(TEST_FILE));
    //stream.skip(44);

    // Simple recognition with generic model
    recognizer.allocate();
    context.setSpeechSource(stream, TimeFrame.INFINITE);
    Result result;

    // Writes to dectalk file
    String fileName = "out/DECFiles/" + new SimpleDateFormat("yyyyMMddHHmm").format(new Date())
        + "_out.txt";
    BufferedWriter DECtalkFile = new BufferedWriter(new FileWriter(fileName, true));
    DECtalkFile.write("[:phoneme on]\n");

    while ((result = recognizer.recognize()) != null) {
      DECtalkFile.write("[");

      SpeechResult speechResult = new SpeechResult(result);
      System.out.format("Hypothesis: %s\n", speechResult.getHypothesis());
      System.out.println("List of recognized words and their times:");

      PitchAnalysis pitchAnalysis = new PitchAnalysis(TEST_FILE);

      long lastEndTime = 0; // Time that the last word ended (for inserting pauses)

      for (WordResult r : speechResult.getWords()) {
        try {

          // Pronunciation

          String pronunciation = PhoneConversion.convertCMUPhone(r.getWord().toString());

          if (pronunciation.equals("")) {
            continue;
          }

          // Timing

          long timeLength = r.getTimeFrame().length();

          long thisStartTime = r.getTimeFrame().getStart();

          if (thisStartTime != lastEndTime) {
            DECtalkFile.write("_<" + (thisStartTime - lastEndTime) + ",1>");
          }

          lastEndTime = thisStartTime;

          // Pitch Analysis

          int toneNumber = pitchAnalysis.getDECtalkToneNumber(r.getTimeFrame());

          // Output

          String DECtalkCode = pronunciation + "<" + timeLength + "," + toneNumber + ">";
          DECtalkFile.write(DECtalkCode);
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