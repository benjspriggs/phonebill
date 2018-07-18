package edu.pdx.cs410J.bspriggs;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

public class Project3 extends Project2 {
    private static final List<Map.Entry<String, String>> OPTIONS = Arrays.asList(
            entry("-print", "Prints a description of the new phone call"),
            entry("-README", "Prints a README for this project and exits"),
            entry("-textFile file", "Where to read/write the phone bill")
    );

    public static PhoneCall parsePhoneCallFromArguments(String[] args) throws ParseException {
        int ptr = 0;

        PhoneCall call = new PhoneCall(args[ptr++], args[ptr++],
                String.format("%s %s %s", args[ptr++], args[ptr++], args[ptr++]),
                String.format("%s %s %s", args[ptr++], args[ptr++], args[ptr]));

        return call;
    }

    public static void validateArguments(List<Map.Entry<String, String>> arguments, String[] args, int ptr) {
        if (args.length - ptr != arguments.size() + 3) {
            if (args.length - ptr < arguments.size() + 3)
                System.err.println("Missing command line arguments");
            else
                System.err.println("Extra command line arguments");

            for (String arg : args) {
                System.out.println(arg);
            }

            System.err.println(usage());
            System.exit(1);
        }
    }

    private static String usage() {
        return "usage: java edu.pdx.cs410J.bspriggs.Project3 [options] <args>\n" +
                "args are (in this order):\n" +
                build(Project1.ARGUMENTS) +
                "options are (options may appear in any order):\n" +
                build(OPTIONS) +
                "Date and time should be in the format: mm/dd/yyyy hh:mm";
    }

    public static void main(String[] args) {
        validateEmptyArguments(args);

        int ptr = 0;
        boolean print = false;
        boolean pretty = false;
        String filename = null;

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
                pretty = true;
            }
        }

        try {
            PhoneBill bill = new PhoneBill(args[ptr++]);

            validateArguments(Project1.ARGUMENTS, args, ptr);

            // open the file
            if (filename != null && filename.length() > 0) {
                TextParser textParser = new TextParser(Paths.get(filename));
                var parsedBill = (PhoneBill) textParser.parse();
                if (!parsedBill.getCustomer().equals(bill.getCustomer()))
                    throw new Exception(String.format(bill.getCustomer(), parsedBill.getCustomer()));
            }

            var parseableArgs = Project1.sliceArgumentsForPhoneCallParsing(args, ptr);
            PhoneCall call = parsePhoneCallFromArguments(parseableArgs);

            bill.addPhoneCall(call);

            // print the stuff
            if (print) {
                System.out.println(bill.toString());
                System.out.println(call.toString());
            }

            if (pretty) {
                var printer = new PrettyPrinter();
                printer.dumpTo(bill, System.out);
            }

            // dump the file
            if (filename != null && filename.length() > 0) {
                Path path = Paths.get(filename);

                File file = path.toFile();

                if (file.exists()) {
                    file.delete();
                }

                TextDumper textDumper = new TextDumper(path);
                textDumper.dump(bill);
            }

        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.err.println(usage());
            System.exit(1);
        }

        System.exit(0);
    }
}
