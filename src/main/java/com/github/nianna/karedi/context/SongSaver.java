package main.java.com.github.nianna.karedi.context;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import main.java.com.github.nianna.karedi.I18N;
import main.java.com.github.nianna.karedi.parser.Unparser;
import main.java.com.github.nianna.karedi.parser.element.VisitableSongElement;
import main.java.com.github.nianna.karedi.song.Song;
import main.java.com.github.nianna.karedi.song.SongTrack;
import main.java.com.github.nianna.karedi.song.tag.Tag;
import main.java.com.github.nianna.karedi.util.MultiplayerTags;

class SongSaver {
	private static final Logger LOGGER = Logger.getLogger(SongSaver.class.getName());

	private final TxtFileSaver txtFileSaver = new TxtFileSaver();
	private Unparser unparser;
	private SongDisassembler disassembler;

	SongSaver(Unparser unparser, SongDisassembler disassembler) {
		this.unparser = unparser;
		this.disassembler = disassembler;
	}

	boolean saveSongToFile(File file, Song song) {
		return save(file, song.getTags(), song.getTracks(), I18N.get("songsaver.save.success"),
				I18N.get("songsaver.save.fail"));
	}

	boolean exportToFile(File file, List<Tag> tags, List<SongTrack> tracks) {
		return save(file, tags, tracks, I18N.get("songsaver.export.success"),
				I18N.get("songsaver.export.fail"));
	}

	private boolean save(File file, List<Tag> tags, List<SongTrack> tracks, String successMsg,
			String errorMsg) {
		if (file != null) {
			try {
				txtFileSaver.save(file, unparse(disassemble(tags, tracks)));
				LOGGER.info(successMsg);
				return true;
			} catch (FileNotFoundException e) {
				LOGGER.severe(errorMsg);
			}
		}
		return false;
	}

	private List<VisitableSongElement> disassemble(List<Tag> tags, List<SongTrack> tracks) {
		tags = updateTrackNameTags(new ArrayList<>(tags), tracks);
		return disassembler.disassemble(tags, tracks);
	}

	private static List<Tag> updateTrackNameTags(List<Tag> tags, List<SongTrack> tracks) {
		tags.removeIf(MultiplayerTags::isANameTag);
		if (tracks.size() > 1) {
			for (int i = 0; i < tracks.size(); ++i) {
				MultiplayerTags.nameTagForTrack(i, tracks.get(i).getName()).ifPresent(tags::add);
			}
		}
		return tags;
	}

	private List<String> unparse(List<VisitableSongElement> elts) {
		return elts.stream().map(unparser::unparse).collect(Collectors.toList());
	}

}