package edu.pdx.cs410J.bspriggs;

import edu.pdx.cs410J.AbstractPhoneBill;
import edu.pdx.cs410J.AbstractPhoneCall;
import edu.pdx.cs410J.ParserException;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;

public class TextParser implements edu.pdx.cs410J.PhoneBillParser {
    private final Path path;

    public TextParser(Path path) {
        this.path = path;
    }

    public TextParser() {
        this.path = null;
    }

    /**
     * Parses an {@link AbstractPhoneBill} from a path (initialized in {@link TextParser#TextParser(Path)}.
     *
     * @return The parsed phone bill. May be null.
     * @throws ParserException Thrown if there's any issues parsing the file.
     */
    @Override
    public AbstractPhoneBill<AbstractPhoneCall> parse() throws ParserException {
        if (!path.toFile().exists()) {
            return null;
        }

        try {
            var lines = Files.readAllLines(this.path, Charset.defaultCharset());

            if (lines.size() == 0) {
                throw new ParserException("Malformed PhoneBill, missing customer name");
            }

            return parse(lines);
        } catch (Exception e) {
            throw new ParserException(e.toString());
        }
    }

    public AbstractPhoneBill parse(List<String> lines) throws ParserException {
        PhoneBill phoneBill;

        if (lines.size() == 0) {
            throw new ParserException("Malformed PhoneBill, missing customer name");
        }

        var customer = lines.remove(0);

        phoneBill = new PhoneBill(customer);

        for (String line : lines) {
            var fields = line.split(Pattern.quote(TextDumper.DELIMITER));

            if (fields.length != 4) {
                throw new ParserException(String.format("Line was incorrectly formatted: '%s'", line));
            }

            var call = new PhoneCall(fields[0], fields[1], fields[2], fields[3]);

            phoneBill.addPhoneCall(call);
        }

        return phoneBill;
    }
}
