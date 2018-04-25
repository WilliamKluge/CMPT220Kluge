# autoDEC

autoDEC is a program that takes a MIDI file as input and outputs a series of WAVE files via
DECtalk's say.exe program that recreate the input song when played simultaneously.

A test version of autoDEC exists in this code that allows a WAVE file to be used as input and will
output the DECtalk commands to create that file's speech audio, but this does not work as well as
one would hope.

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
* MIDI-Tempo-Converter - https://github.com/danph/Midi-Tempo-Converter The jar needs to be in the
libs/ folder relative to the running directory of the program. This is used to convert all MIDI 
files to be the same tempo so that note duration calculations are easier.

## How to use (TODO expand this section)
* Run autoDEC with the path to a MIDI file as the parameter