package SoundHandling;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
//import org.apache.commons.io.FileUtils;

/**
 * @author Vincent Caputa
 * <p>This is just modified code from this (https://stackoverflow.com/questions/32856836/java-mixing-two-wav-files-without-introducing-noise?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa)
 * SO post he made, thanks bro!</p>
 */
public class WAVEMixer {

  /**
   *
   * @param fileOne
   * @param fileTwo
   * @return The file created
   * @throws IOException
   * @throws UnsupportedAudioFileException
   *
   */
  public static File mixWAVEFiles(File fileOne, File fileTwo)
      throws IOException, UnsupportedAudioFileException {

    byte[] byteBufferC = mixBuffers(getFileAsByteArray(fileOne), getFileAsByteArray(fileTwo));

    String filePath = "dectalk\\generated\\" + fileOne.getName().substring(0, 11) + "^"
        + fileTwo.getName().substring(0, 11) + " mix.wav";

    File resultFile = new File(filePath);
//    FileUtils.writeByteArrayToFile(resultFile, byteBufferC);

    return resultFile;
  }

  private static byte[] getFileAsByteArray(File audioFile)
      throws IOException, UnsupportedAudioFileException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    AudioInputStream ais;
    ais = AudioSystem.getAudioInputStream(audioFile);
    int read;
    byte[] buffer = new byte[1024];
    while ((read = ais.read(buffer)) != -1) {
      baos.write(buffer, 0, read);
    }
    baos.flush();
    byte[] r = baos.toByteArray();
    baos.close();
    return r;
  }

  private static byte[] mixBuffers(byte[] bufferA, byte[] bufferB) {
    byte[] largerArray = bufferA;
    byte[] smallerArray = bufferB;

    // If the bufferA is actually smaller than bufferB, then adjust sizes accordingly
    if (bufferA.length < bufferB.length) {
      smallerArray = bufferA;
      largerArray = bufferB;
    }

    byte[] array = new byte[largerArray.length];

    for (int i = 0; i < smallerArray.length; i += 2) {
      short buf1A = bufferA[i + 1];
      short buf2A = bufferA[i];
      buf1A = (short) ((buf1A & 0xff) << 8);
      buf2A = (short) (buf2A & 0xff);

      short buf1B = bufferB[i + 1];
      short buf2B = bufferB[i];
      buf1B = (short) ((buf1B & 0xff) << 8);
      buf2B = (short) (buf2B & 0xff);

      short buf1C = (short) (buf1A + buf1B);
      short buf2C = (short) (buf2A + buf2B);

      short res = (short) (buf1C + buf2C);

      array[i] = (byte) res;
      array[i + 1] = (byte) (res >> 8);
    }

    // Copy the rest of the data from the larger array
    System.arraycopy(largerArray, smallerArray.length, array, smallerArray.length,
        largerArray.length - smallerArray.length);

    return array;
  }

}
