package edu.pdx.cs410J.bspriggs;

import edu.pdx.cs410J.ParserException;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

public class Project3 extends Project2 {
    private static final List<Map.Entry<String, String>> OPTIONS = Arrays.asList(
            entry("-print", "Prints a description of the new phone call"),
            entry("-README", "Prints a README for this project and exits"),
            entry("-textFile file", "Where to read/write the phone bill"),
            entry("-pretty file", "Pretty print the phone bill to a text file or standard out (file -).")
    );

    public static PhoneCall parsePhoneCallFromArguments(String[] args) throws ParserException {
        int ptr = 0;

        return new PhoneCall(args[ptr++], args[ptr++],
                String.format("%s %s %s", args[ptr++], args[ptr++], args[ptr++]),
                String.format("%s %s %s", args[ptr++], args[ptr++], args[ptr]));
    }

    protected static String usage() {
        return "usage: java edu.pdx.cs410J.bspriggs.Project3 [options] <args>\n" +
                "args are (in this order):\n" +
                build(ARGUMENTS) +
                "options are (options may appear in any order):\n" +
                build(OPTIONS) +
                "Date and time should be in the format: mm/dd/yyyy hh:mm";
    }

    public static PhoneBill doMain(int ptr, boolean print, String filename, String prettyFilename, String[] args) {
        try {
            PhoneBill bill = doMain(ptr, print, filename, args);

            if (bill == null) {
                throw new Exception("Unable to parse phone bill/ customer");
            }

            if (prettyFilename != null) {
                PrettyPrinter printer;

                if (prettyFilename.compareTo("-") == 0) {
                    printer = new PrettyPrinter(System.out);
                } else {
                    printer = new PrettyPrinter(Paths.get(prettyFilename));
                }

                printer.dump(bill);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.err.println(usage());
            System.exit(1);
        }

        return null;
    }

    public static void main(String[] args) {
        validateEmptyArguments(args);

        int ptr = 0;
        boolean print = false;
        String filename = null;
        String prettyFilename = null;

        // parse options
        // (this is probably better done with the apache commons-cli)
        for (; args[ptr].equals("-print") || args[ptr].equals("-README") || args[ptr].equals("-textFile") || args[ptr].equals("-pretty"); ++ptr) {
            if (args[ptr].equals("-print"))
                print = true;
            else if (args[ptr].equals("-README")) {
                System.out.println("Optionally reads a PhoneBill " +
                        "from a text file, " +
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
            } else if (args[ptr].equals("-pretty")) {
                ptr++;
                prettyFilename = args[ptr];
            }
        }

        doMain(ptr, print, filename, prettyFilename, args);
        System.exit(0);
    }
}
