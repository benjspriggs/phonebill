package edu.pdx.cs410J.bspriggs;

import edu.pdx.cs410J.AbstractPhoneBill;

import java.nio.file.Path;

public class TextParser implements edu.pdx.cs410J.PhoneBillParser {
    private final Path path;

    public TextParser(Path path) {
        this.path = path;
    }

    @Override
    public AbstractPhoneBill parse() {
        return null;
    }
}
