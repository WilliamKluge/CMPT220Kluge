# autoDEC

autoDEC a program that takes a wav file and turns the audio into a script for the DECtalk speech 
synthesiser.

This code is located within another project to meet requirements, but is able to be opened as a 
standalone gradle project.

## Dependencies
* TarsosDSP - Used for detecting the pitch of phones (A jar file is bundled with this source, 
which is included with Gradle)
* Sphinx4 - Needed to detect phones from audio (included with Gradle)
* BasicPlayer v3.0 - Audio file playback
* WavFile - This source is bundled into the code of this program. Nothing needs to be put in gradle
or installed for this to work. I just wanted to note that this is not my code, shoutout to my boi 
A.Greensted for making my life a hell of a lot easier.

## How to use (TODO expand this section)
* For help figuring out DECtalk phones see 
https://msu.edu/course/asc/232/song_project/dectalk_pages/DECtalk_Phonemic%20Symbols_.pdf