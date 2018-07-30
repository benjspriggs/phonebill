package edu.pdx.cs410J.bspriggs;

import edu.pdx.cs410J.AbstractPhoneBill;
import edu.pdx.cs410J.AbstractPhoneCall;
import edu.pdx.cs410J.ParserException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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

    public static String formatWordCount(int count )
    {
        return String.format( "Dictionary on server contains %d words", count );
    }

    public static String formatDictionaryEntry(String word, String definition )
    {
        return String.format("  %s : %s", word, definition);
    }

    public static String missingRequiredParameter( String parameterName )
    {
        return String.format("The required parameter \"%s\" is missing", parameterName);
    }

    public static Map.Entry<String, String> parseDictionaryEntry(String content) {
        Pattern pattern = Pattern.compile("\\s*(.*) : (.*)");
        Matcher matcher = pattern.matcher(content);

        if (!matcher.find()) {
            return null;
        }

        return new Map.Entry<>() {
            @Override
            public String getKey() {
                return matcher.group(1);
            }

            @Override
            public String getValue() {
                String value = matcher.group(2);
                if ("null".equals(value)) {
                    value = null;
                }
                return value;
            }

            @Override
            public String setValue(String value) {
                throw new UnsupportedOperationException("This method is not implemented yet");
            }
        };
    }

    public static void formatDictionaryEntries(PrintWriter pw, Map<String, String> dictionary) {
        pw.println(Messages.formatWordCount(dictionary.size()));

        for (Map.Entry<String, String> entry : dictionary.entrySet()) {
            pw.println(Messages.formatDictionaryEntry(entry.getKey(), entry.getValue()));
        }
    }

    public static Map<String, String> parseDictionary(String content) {
        Map<String, String> map = new HashMap<>();

        String[] lines = content.split("\n");
        for (int i = 1; i < lines.length; i++) {
            String line = lines[i];
            Map.Entry<String, String> entry = parseDictionaryEntry(line);
            map.put(entry.getKey(), entry.getValue());
        }

        return map;
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

    public static List<AbstractPhoneCall> parsePhoneCalls(String content) {
        return null;
    }

    public static String formatPhoneBill(AbstractPhoneBill bill) throws IOException {
        var out = new ByteArrayOutputStream();
        dumper.dumpTo(bill, out);
        return out.toString();
    }
}
