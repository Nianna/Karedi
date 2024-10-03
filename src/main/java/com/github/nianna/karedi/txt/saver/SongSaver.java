package com.github.nianna.karedi.txt.saver;

import com.github.nianna.karedi.I18N;
import com.github.nianna.karedi.song.Song;
import com.github.nianna.karedi.song.SongTrack;
import com.github.nianna.karedi.song.tag.Tag;
import com.github.nianna.karedi.txt.parser.Unparser;
import com.github.nianna.karedi.song.tag.MultiplayerTags;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Stream;

class SongSaver {

	private static final Logger LOGGER = Logger.getLogger(SongSaver.class.getName());

	private final TxtFileSaver txtFileSaver = new TxtFileSaver();

	private final Unparser unparser;

	private final SongDisassembler disassembler;

	SongSaver(Unparser unparser, SongDisassembler disassembler) {
		this.unparser = unparser;
		this.disassembler = disassembler;
	}

	boolean saveSongToFile(File file, Song song) {
		return save(
				file,
				song.getTags(),
				song.getTracks(),
				I18N.get("songsaver.save.success"),
				I18N.get("songsaver.save.fail")
		);
	}

	boolean exportToFile(File file, List<Tag> tags, List<SongTrack> tracks) {
		return save(
				file,
				tags,
				tracks,
				I18N.get("songsaver.export.success"),
				I18N.get("songsaver.export.fail")
		);
	}

	private boolean save(File file, List<Tag> tags, List<SongTrack> tracks, String successMsg, String errorMsg) {
		if (file != null) {
			try {
				txtFileSaver.save(file, prepareTxtFileContent(tags, tracks));
				LOGGER.info(successMsg);
				return true;
			} catch (FileNotFoundException e) {
				LOGGER.severe(errorMsg);
			}
		}
		return false;
	}

	private Stream<String> prepareTxtFileContent(List<Tag> tags, List<SongTrack> tracks) {
		tags = refreshMultiplayerNameTags(new ArrayList<>(tags), tracks);
		return disassembler.disassemble(tags, tracks)
				.stream()
				.map(unparser::unparse);
	}

	private static List<Tag> refreshMultiplayerNameTags(List<Tag> tags, List<SongTrack> tracks) {
		tags.removeIf(MultiplayerTags::isANameTag);
		if (tracks.size() > 1) {
			for (int i = 0; i < tracks.size(); ++i) {
				MultiplayerTags.nameTagForTrack(i, tracks.get(i).getName())
						.ifPresent(tags::add);
			}
		}
		return tags;
	}

}
