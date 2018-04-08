package MIDIFileHandling;

import java.io.File;
import java.util.ArrayList;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;
import javax.sound.midi.spi.MidiFileReader;

/**
 * Copied from https://gist.github.com/sport4minus/2971754
 */
public class MidiLoader {

  /**
   * loads a midi file from disk and stores all notes in a nested Array List, using the Helper class
   * "Note" needs some cleanup though
   */
  public ArrayList<ArrayList<Note>> tracks;
  public Sequence mySeq;
  long maxTicks = 0;
  final boolean DO_PRINT = false;

  public MidiLoader(String fileName) {

    //Notes are stored in this nested array list to mirror the track-note structure (depending on type of midi file)
    tracks = new ArrayList<ArrayList<Note>>();
    Track[] trx;

    try {

      File myMidiFile = new File(fileName);
      mySeq = MidiSystem.getSequence(myMidiFile);
      trx = mySeq.getTracks();

      for (int i = 0; i < trx.length; i++) {
        ArrayList<Note> trackAsList = new ArrayList<Note>();
        tracks.add(trackAsList);
        Track t = trx[i];
        if (DO_PRINT) {
          System.out.println("track ");
          System.out.println("length:" + t.ticks());
          System.out.println("num events:" + t.size());
        }
        int counter = 0;
        //iterate over the vector, and remove each handled event.
        while (t.size() > 0 && counter < t.size()) {
          counter++;
          if (t.get(0).getMessage() instanceof ShortMessage) {

            ShortMessage s = (ShortMessage) (t.get(0).getMessage());
            //find note on events
            if (s.getCommand() == ShortMessage.NOTE_ON) {
              if (DO_PRINT) {
                System.out.println(
                    s.getCommand() + " " + s.getChannel() + " " + s.getData1() + " " + s
                        .getData2());
              }
              //store all the values temporarily in order to find the associated note off event
              long startTime = t.get(0).getTick();
              long endTime = 0;
              int ch = s.getChannel();
              int pitch = s.getData1();
              int vel = s.getData2();

              //if the first note has zero velocity (== noteOff), remove it
              if (vel == 0) {
                t.remove(t.get(0));
              } else {
                //start to look for the associated note off
                for (int j = 0; j < t.size(); j++) {
                  if (t.get(j).getMessage() instanceof ShortMessage) {
                    ShortMessage s2 = (ShortMessage) (t.get(j).getMessage());
                    //two types to send a note off... either as a clean command or as note on with 0 velocity
                    if ((s2.getCommand() == ShortMessage.NOTE_OFF)
                        || s2.getCommand() == ShortMessage.NOTE_ON) {
                      //compare to stored values, sending a note off with same channel and pitch means to stop the note
                      if (s2.getChannel() == ch && s2.getData1() == pitch && s2.getData2() == 0) {
                        //calculate note duration
                        endTime = t.get(j).getTick();
                        //extend maxticks, so we know when the last midi event happened (sometimes tracks are much longer than the last note
                        if (endTime > maxTicks) {
                          maxTicks = endTime;
                        }
                        //create a new "Note" instance, store it
                        Note n = new Note(startTime, endTime - startTime, ch, vel, pitch);
                        trackAsList.add(n);
                        t.remove(t.get(0));
                        break;
                      }
                    }
                  }
                }
              }
              //remove event when done
              t.remove(t.get(0));
            } else {
              //remove events which are shortmessages but not note on (e.g. control change)
              t.remove(t.get(0));
            }
          } else {
            //remove events which are not of type short message
            t.remove(t.get(0));
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public ArrayList<Note> trackAsArrayList(int i) {
    return tracks.get(i);
    // return null;
  }

  int numTracks() {
    return tracks.size();
  }
}

