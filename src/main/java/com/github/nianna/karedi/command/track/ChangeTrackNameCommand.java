package com.github.nianna.karedi.command.track;

import com.github.nianna.karedi.I18N;
import com.github.nianna.karedi.command.CommandComposite;
import com.github.nianna.karedi.command.tag.ChangeTagValueCommand;
import com.github.nianna.karedi.command.tag.DeleteTagCommand;
import com.github.nianna.karedi.song.SongTrack;
import com.github.nianna.karedi.song.tag.MultiplayerTags;
import com.github.nianna.karedi.song.tag.Tag;

import java.util.List;

public class ChangeTrackNameCommand extends CommandComposite {

    private final SongTrack track;

    private final String newValue;

    public ChangeTrackNameCommand(SongTrack track, String newValue) {
        super(I18N.get("command.change_track_name"));
        this.track = track;
        this.newValue = newValue;
    }

    @Override
    protected void buildSubCommands() {
        int trackIndex = track.getSong().indexOf(track);
        // Implementation relies on the automatic update of track names on tag change done by Song class
        List<Tag> trackMultiplayerTags = getMultiplayerTagsForTrack(trackIndex);
        if (nameIsBlankOrDefault(trackIndex)) {
            removeMultiplayerTags(trackMultiplayerTags);
        } else {
            if (trackMultiplayerTags.isEmpty()) {
                addDefaultMultiplayerTagForTrack(trackIndex);
            } else {
                updateMultiplayerTagsValue(trackMultiplayerTags);
            }
        }
    }

    private void removeMultiplayerTags(List<Tag> multiplayerTags) {
        multiplayerTags
                .stream()
                .map(tag -> new DeleteTagCommand(track.getSong(), tag))
                .forEach(this::addSubCommand);
    }

    private void addDefaultMultiplayerTagForTrack(int trackIndex) {
        addSubCommand(
                new ChangeTagValueCommand(
                        track.getSong(),
                        MultiplayerTags.getTagKeyForTrackNumber(trackIndex),
                        newValue
                )
        );
    }

    private void updateMultiplayerTagsValue(List<Tag> trackMultiplayerTags) {
        trackMultiplayerTags
                .stream()
                .map(tag -> new ChangeTagValueCommand(track.getSong(), tag.getKey(), newValue))
                .forEach(this::addSubCommand);
    }

    private List<Tag> getMultiplayerTagsForTrack(int trackIndex) {
        return track.getSong().getTags().stream()
                .filter(MultiplayerTags::isANameTag)
                .filter(tag -> isForThisTrack(tag, trackIndex))
                .toList();
    }

    private static boolean isForThisTrack(Tag tag, Integer trackIndex) {
        return MultiplayerTags.getTrackNumber(tag)
                .filter(trackIndex::equals)
                .isPresent();
    }

    private boolean nameIsBlankOrDefault(Integer trackIndex) {
        return newValue == null || newValue.isBlank() || SongTrack.getDefaultTrackName(trackIndex + 1).equals(newValue);
    }

}
