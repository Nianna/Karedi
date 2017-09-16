# Karedi

Karedi is a new, improved graphical editor designed for creating songs for Ultrastar - one of the most popular karaoke games for PC.
Its main objective is to simplify the process of creation for both beginners and advanced users while assuring high-quality of the final txt files.

The project is still under development, every bug report will be greatly appreciated.
##
![karedi](https://user-images.githubusercontent.com/31940002/30511294-ff9004bc-9ad5-11e7-8b59-f5c45d9d87d0.png)

## Main features
Karedi's main features include:
  * two display modes:
     * line by line - visible area's bounds are adjusted automatically
     * continuous - they are set manually by you
  * multiple playback modes
  * support for songs with multiple tracks
  * intuitive new song wizard
  * tag features (e.g. rescaling to new BPM, setting medley from selection) & validation
  * copy & paste (in 2 modes: traditional and merge)
  * automatic search for problems (errors & warnings)
  * solver that offers both safe and potentially risky (useful if safe fix is not available) solutions to almost all the problems
  * support for multiple mp3 files with the possibility of adjusting the volume for each one of them
  * auxiliary note method support:
      * notes are sorted according to their startbeats
      * shortcuts for playing fragments before (alt + UP) or after (alt + DOWN) the selected note
  * extra modules:
      * Tap Notes - add notes by pressing a key while listening to playback
      * Write Tones - set tones of selected notes by pressing an appropriate key, e.g. "E".
      
Please refer to the [wiki](https://github.com/Nianna/Karedi/wiki) to learn more.

## Requirements

Karedi requires <b>Java 8</b>, please update it to the newest version to ensure that everything runs smoothly.

<b>Operating system:</b>
<br>
  Karedi is primarily targeted for <b>Windows</b> users and compatibility issues with other systems have low priority for now.
If you want to run it on Linux, please make sure to use Java distributed by Oracle, not the default OpenJDK distribution. Otherwise you will not be able to play audio at all <i>(the cause of the bug is known and maybe some workaround will be added later)</i>.

## Downloads

The program is currently released as an executable jar file, you can found the newest version in the [Releases](https://github.com/Nianna/Karedi/releases/latest) section.
<br><br>
<b>Quick download: [Karedi-1.0.jar](https://github.com/Nianna/Karedi/releases/download/v1.0.0/Karedi-1.0.0.jar)</a></b>

## Libraries

Karedi uses the following libraries:
* [JavaFX](http://www.oracle.com/technetwork/java/javase/overview/javafx-overview-2158620.html) (included in the standard JDK & JRE) with [ControlsFX](http://fxexperience.com/controlsfx/) & [RichTextFx](https://github.com/TomasMikula/RichTextFX) for GUI,
* [mp3agic](https://github.com/mpatric/mp3agic) for reading mp3 info,
* [jLayer](http://www.javazoom.net/javalayer/javalayer.html) for playing mp3 files,
* [jFugue](http://www.jfugue.org) to simplify the process of creating MIDI sequences.

## License

The project is licensed under [GNU General Public License](https://www.gnu.org/licenses/gpl-3.0.en.html).
