package edu.pdx.cs410J.bspriggs;

import edu.pdx.cs410J.AbstractPhoneBill;
import edu.pdx.cs410J.AbstractPhoneCall;
import edu.pdx.cs410J.ParserException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class for formatting messages on the server side.  This is mainly to enable
 * test methods that validate that the server returned expected strings.
 */
public class Messages
{
    public static final PrettyPrinter prettyPrinter = new PrettyPrinter();
    public static final TextDumper dumper = new TextDumper();
    public static final TextParser parser = new TextParser();

    public static String missingRequiredParameter( String parameterName )
    {
        return String.format("The required parameter \"%s\" is missing", parameterName);
    }

    public static String formatPhoneBillPretty(AbstractPhoneBill bill) throws IOException {
        var out = new ByteArrayOutputStream();
        prettyPrinter.dumpTo(bill, out);
        return out.toString();
    }

    public static String formatPhoneCalls(List<AbstractPhoneCall> callsInSearchRange) {
        return callsInSearchRange.stream()
                .map(AbstractPhoneCall::toString)
                .collect(Collectors.joining("\n"));
    }

    public static PhoneBill parsePhoneBill(String content) throws ParserException {
        return (PhoneBill) parser.parse(Arrays.asList(content.split(System.lineSeparator())));
    }

    public static String formatPhoneBill(AbstractPhoneBill bill) throws IOException {
        var out = new ByteArrayOutputStream();
        dumper.dumpTo(bill, out);
        return out.toString();
    }
}
