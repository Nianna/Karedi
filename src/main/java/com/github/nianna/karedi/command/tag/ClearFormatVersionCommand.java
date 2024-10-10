package com.github.nianna.karedi.command.tag;

import com.github.nianna.karedi.I18N;
import com.github.nianna.karedi.command.CommandComposite;
import com.github.nianna.karedi.song.Song;
import com.github.nianna.karedi.song.tag.TagKey;

public class ClearFormatVersionCommand extends CommandComposite {

    private final Song song;

    public ClearFormatVersionCommand(Song song) {
        super(I18N.get("command.clear_format_version"));
        this.song = song;
    }

    @Override
    protected void buildSubCommands() {
        song.formatSpecificationVersion()
                .ifPresent(ignored ->
                        addSubCommand(new DeleteTagCommand(song, song.getTag(TagKey.VERSION).orElseThrow()))
                );
    }

}
