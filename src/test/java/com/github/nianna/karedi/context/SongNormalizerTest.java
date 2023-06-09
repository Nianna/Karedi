package com.github.nianna.karedi.context;

import com.github.nianna.karedi.song.Song;
import com.github.nianna.karedi.song.tag.Tag;
import com.github.nianna.karedi.song.tag.TagKey;
import org.junit.Assert;
import org.junit.Test;

public class SongNormalizerTest {

    private final SongNormalizer songNormalizer = new SongNormalizer();

    @Test
    public void shouldSetDefaultBpmIfBpmTagIsMissing() {
        Song song = new Song();

        songNormalizer.normalize(song);

        Assert.assertEquals(240, song.getBpm(), 0.0);
    }

    @Test
    public void shouldSetDefaultGapIfGapTagIsMissing() {
        Song song = new Song();

        songNormalizer.normalize(song);

        Assert.assertEquals(0, song.getGap());
    }

    @Test
    public void shouldSetDefaultBpmIfBpmTagIsInvalid() {
        Song song = new Song();
        song.addTag(new Tag(TagKey.BPM, "invalid"));

        songNormalizer.normalize(song);

        Assert.assertEquals(240, song.getBpm(), 0.0);
    }

    @Test
    public void shouldSetDefaultGapIfGapTagIsInvalid() {
        Song song = new Song();
        song.addTag(new Tag(TagKey.GAP, "invalid"));

        songNormalizer.normalize(song);

        Assert.assertEquals(0, song.getGap());
    }

    @Test
    public void shouldAddFirstPlayerTrackIfThereAreNoTracks() {
        Song song = new Song();

        songNormalizer.normalize(song);

        Assert.assertEquals(song.getTrackCount(), 1);
        Assert.assertEquals(1, song.getTrack(0).getPlayer().intValue());
    }

}