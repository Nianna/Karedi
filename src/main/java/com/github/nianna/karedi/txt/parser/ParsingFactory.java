package com.github.nianna.karedi.txt.parser;

import com.github.nianna.karedi.txt.parser.elementparser.EndOfSongParser;
import com.github.nianna.karedi.txt.parser.elementparser.LineBreakParser;
import com.github.nianna.karedi.txt.parser.elementparser.NoteParser;
import com.github.nianna.karedi.txt.parser.elementparser.SongElementParser;
import com.github.nianna.karedi.txt.parser.elementparser.TagParser;
import com.github.nianna.karedi.txt.parser.elementparser.TrackParser;

public class ParsingFactory {

    private ParsingFactory() {
    }

    public static Parser createParser() {
        SongElementParser chain = new TagParser();
        chain.addNext(new NoteParser());
        chain.addNext(new LineBreakParser());
        chain.addNext(new TrackParser());
        chain.addNext(new EndOfSongParser());
        return chain;
    }

    public static Unparser createUnparser() {
        return new BaseUnparser();
    }

}
