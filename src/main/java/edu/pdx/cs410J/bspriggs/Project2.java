package edu.pdx.cs410J.bspriggs;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

public class Project2 {
    private static final List<Map.Entry<String, String>> ARGUMENTS = Arrays.asList(
            entry("customer", "Person whose phone bill we’re modeling"),
            entry("callerNumber", "Phone number of caller"),
            entry("calleeNumber", "Phone number of person who was called"),
            entry("startTime", "Date and time call began (24-hour time)"),
            entry("endTime", "Date and time call ended (24-hour time)")
    );

    private static final List<Map.Entry<String, String>> OPTIONS = Arrays.asList(
            entry("-print", "Prints a description of the new phone call"),
            entry("-README", "Prints a README for this project and exits"),
            entry("-textFile file", "Where to read/write the phone bill")
    );

    private static String build(List<Map.Entry<String, String>> f) {
        var b = new StringBuilder();

        for (Map.Entry<String, String> pair : f) {
            b.append(String.format("  %-10s\t%s", pair.getKey(), pair.getValue()));
            b.append(System.lineSeparator());
        }

        return b.toString();
    }

    private static String usage() {
        return "usage: java edu.pdx.cs410J.bspriggs.Project2 [options] <args>\n" +
                "args are (in this order):\n" +
                build(ARGUMENTS) +
                "options are (options may appear in any order):\n" +
                build(OPTIONS) +
                "Date and time should be in the format: mm/dd/yyyy hh:mm";
    }

    public static void main(String[] args) {
        if (args == null || args.length == 0) {
            System.err.println("Missing command line arguments");
            System.err.println(usage());
            System.exit(1);
        }

        int ptr = 0;
        boolean print = false;
        String filename = "";

        // parse options
        // (this is probably better done with the apache commons-cli)
        for (; args[ptr].equals("-print") || args[ptr].equals("-README"); ++ptr) {
            if (args[ptr].equals("-print"))
                print = true;
            else if (args[ptr].equals("-README")) {
                System.out.println("Optionally reads a PhoneBill " +
                        "from the contents of a text file, " +
                        "creates a new PhoneCall as specified on the command line, " +
                        "adds the PhoneCall to the PhoneBill, " +
                        "and then optionally writes the PhoneBill back to the text file.");
                System.out.println(usage());
                System.exit(0);
            } else if (args[ptr].equals("-textFile")) {
                // advance to filename
                ptr++;
                // get filename
                filename = args[ptr];
                // advance past filename
                ptr++;
            }
        }

        // parse args
        if (args.length - ptr != ARGUMENTS.size() + 2) {
            if (args.length - ptr < ARGUMENTS.size() + 2)
                System.err.println("Missing command line arguments");
            else
                System.err.println("Extra command line arguments");

            for (String arg : args) {
                System.out.println(arg);
            }

            System.err.println(usage());
            System.exit(1);
        }

        try {
            PhoneBill bill = new PhoneBill(args[ptr++]);

            // open the file
            if (filename.length() > 0) {
                TextParser textParser = new TextParser(Paths.get(filename));
                bill = (PhoneBill) textParser.parse();
            }

            PhoneCall call = Project1.parsePhoneCallFromArguments(Arrays.copyOfRange(args, ptr, args.length));

            bill.addPhoneCall(call);

            // print the stuff
            if (print) {
                System.out.println(bill.toString());
                System.out.println(call.toString());
            }

            // dump the file
            if (filename.length() > 0) {
                TextDumper textDumper = new TextDumper(Paths.get(filename));
                textDumper.dump(bill);
            }

            System.exit(0);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.err.println(usage());
            System.exit(1);
        }
    }
}
