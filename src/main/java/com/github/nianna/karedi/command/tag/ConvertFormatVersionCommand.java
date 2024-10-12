package com.github.nianna.karedi.command.tag;

import com.github.nianna.karedi.I18N;
import com.github.nianna.karedi.command.CommandComposite;
import com.github.nianna.karedi.song.Song;
import com.github.nianna.karedi.song.tag.FormatSpecification;
import com.github.nianna.karedi.song.tag.Tag;
import com.github.nianna.karedi.song.tag.TagKey;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConvertFormatVersionCommand extends CommandComposite {

    private final Song song;
    private final FormatSpecification targetFormat;
    private final List<Tag> tagsToBeRemoved;
    private final Map<TagKey, String> tagsToBeAdded;
    private final Map<Tag, String> tagsToBeUpdated;

    public ConvertFormatVersionCommand(Song song, FormatSpecification targetFormat) {
        super(I18N.get("command.convert_format_version", targetFormat));
        this.song = song;
        this.targetFormat = targetFormat;
        this.tagsToBeRemoved = tagsToBeRemoved();
        this.tagsToBeAdded = tagsToBeAdded();
        this.tagsToBeUpdated = tagsToBeUpdated();
    }

    @Override
    protected void buildSubCommands() {
        FormatSpecification currentFormat = song.getFormatSpecificationVersion();
        if (currentFormat == targetFormat) {
            return;
        }

        tagsToBeRemoved.stream()
                .map(tag -> new DeleteTagCommand(song, tag))
                .forEach(this::addSubCommand);
        tagsToBeAdded.entrySet().stream()
                .map(entry -> new ChangeTagValueCommand(song, entry.getKey(), entry.getValue()))
                .forEach(this::addSubCommand);
        tagsToBeUpdated.entrySet().stream()
                .map(entry -> new ChangeTagValueCommand(song, entry.getKey().getKey(), entry.getValue()))
                .forEach(this::addSubCommand);
    }

	public List<Tag> getTagsToBeRemoved() {
		return tagsToBeRemoved;
	}

	public Map<TagKey, String> getTagsToBeAdded() {
		return tagsToBeAdded;
	}

    public Map<Tag, String> getTagsToBeUpdated() {
        return tagsToBeUpdated;
    }

	private List<Tag> tagsToBeRemoved() {
        return song.getTags().stream()
                .filter(tag -> !FormatSpecification.supports(targetFormat, tag.getKey()))
                .toList();
    }

    private Map<TagKey, String> tagsToBeAdded() {
        Map<TagKey, String> result = new HashMap<>();
        copyTagValue(TagKey.MP3, TagKey.AUDIO, result);
        copyTagValue(TagKey.AUDIO, TagKey.MP3, result);
		copyTagValue(TagKey.DUETSINGERP1, TagKey.P1, result);
		copyTagValue(TagKey.DUETSINGERP2, TagKey.P2, result);
        if (!song.hasTag(TagKey.VERSION)) {
            result.put(TagKey.VERSION, targetFormat.toString());
        }
		return result;
    }

	private void copyTagValue(TagKey sourceKey, TagKey targetKey, Map<TagKey, String> result) {
		if (!song.hasTag(targetKey) && FormatSpecification.supports(targetFormat, targetKey)) {
			song.getTagValue(sourceKey).ifPresent(value -> result.put(targetKey, value));
		}
	}

    private Map<Tag, String> tagsToBeUpdated() {
        Map<Tag, String> result = new HashMap<>();
        song.getTag(TagKey.VERSION).ifPresent(tag -> result.put(tag, targetFormat.toString()));
        return result;
    }

}
