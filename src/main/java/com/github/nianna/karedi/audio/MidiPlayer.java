package main.java.com.github.nianna.karedi.audio;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;

import org.jfugue.midi.MidiParserListener;
import org.staccato.StaccatoParser;

import main.java.com.github.nianna.karedi.I18N;

/**
 * Generates requested tones. Uses default sequencer of the {@link MidiSystem}.
 */

public class MidiPlayer {
	private static final Logger LOGGER = Logger.getLogger(MidiPlayer.class.getName());

	public static final Integer MAX_NOTE = 127;
	public static final Integer MIN_NOTE = 0;

	private static Sequencer sequencer;
	private static SequenceGenerator sequenceGenerator = new SequenceGenerator();

	private MidiPlayer() {
	}

	public static void stop() {
		// Called only from outside, ignore - the generated sound is too short
		// to bother
	}

	public static void play(List<Integer> notes) {
		if (sequencer == null) {
			reset();
		}
		if (sequencer != null) {
			sequencer.stop();
			notes = notes.stream()
					.filter(MidiPlayer::isNoteValid)
					.collect(Collectors.toList());
			if (notes.size() > 0) {
				try {
					sequencer.setSequence(sequenceGenerator.getSequence(notes));
				} catch (InvalidMidiDataException e) {
					LOGGER.severe(I18N.get("player.midi.invalid_data"));
					e.printStackTrace();
				}
				sequencer.start();
			}
		}
	}

	public static boolean isNoteValid(Integer note) {
		return note >= MIN_NOTE && note <= MAX_NOTE;
	}

	public static void reset() {
		try {
			sequencer = MidiSystem.getSequencer();
			sequencer.open();
		} catch (MidiUnavailableException e) {
			LOGGER.severe(I18N.get("player.midi.unavailable"));
			e.printStackTrace();
		}
	}

	private static class SequenceGenerator {
		private static StaccatoParser staccatoParser = new StaccatoParser();
		private static MidiParserListener midiParserListener = new MidiParserListener();

		private SequenceGenerator() {
			staccatoParser.addParserListener(midiParserListener);
		}

		private static String generatePattern(List<Integer> notes) {
			return notes.stream().map(note -> note.toString() + "q")
					.collect(Collectors.joining("+"));
		}

		private Sequence getSequence(List<Integer> notes) {
			staccatoParser.parse(generatePattern(notes));
			return midiParserListener.getSequence();
		}
	}
}
