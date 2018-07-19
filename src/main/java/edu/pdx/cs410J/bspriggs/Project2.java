package edu.pdx.cs410J.bspriggs;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

public class Project2 extends Project1 {
    private static final List<Map.Entry<String, String>> OPTIONS = Arrays.asList(
            entry("-print", "Prints a description of the new phone call"),
            entry("-README", "Prints a README for this project and exits"),
            entry("-textFile file", "Where to read/write the phone bill")
    );

    protected static String usage() {
        return "usage: java edu.pdx.cs410J.bspriggs.Project2 [options] <args>\n" +
                "args are (in this order):\n" +
                build(ARGUMENTS) +
                "options are (options may appear in any order):\n" +
                build(OPTIONS) +
                "Date and time should be in the format: mm/dd/yyyy hh:mm";
    }

    protected static PhoneBill doMain(int ptr, boolean print, String filename, String[] args) {
        try {
            PhoneBill bill = doMain(ptr, print, args);

            if (bill == null) {
                throw new Exception("Failed to parse/ add phone call to bill");
            }

            // open the file
            if (filename != null && filename.length() > 0) {
                TextParser textParser = new TextParser(Paths.get(filename));
                var parsedBill = (PhoneBill) textParser.parse();

                if (parsedBill != null && !parsedBill.getCustomer().equals(bill.getCustomer())) {
                    throw new Exception(String.format(bill.getCustomer(), parsedBill.getCustomer()));
                }
            }

            // dump the file
            if (filename != null && filename.length() > 0) {
                Path path = Paths.get(filename);

                File file = path.toFile();

                if (file.exists()) {
                    if (!file.delete()) {
                        throw new IOException("Unable to remove existing file " + filename);
                    }
                }

                TextDumper textDumper = new TextDumper(path);
                textDumper.dump(bill);
            }

            return bill;
        } catch (Exception e) {
            System.err.println(e);
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

        // parse options
        // (this is probably better done with the apache commons-cli)
        for (; (ptr < args.length) && (args[ptr].equals("-print") || args[ptr].equals("-README") || args[ptr].equals("-textFile")); ++ptr) {
            switch (args[ptr]) {
                case "-print":
                    print = true;
                    break;
                case "-README":
                    System.out.println("Optionally reads a PhoneBill " +
                            "from a text file, " +
                            "creates a new PhoneCall as specified on the command line, " +
                            "adds the PhoneCall to the PhoneBill, " +
                            "and then optionally writes the PhoneBill back to the text file.");
                    System.out.println(usage());
                    System.exit(0);
                case "-textFile":
                    // advance to filename
                    ptr++;
                    // get filename
                    filename = args[ptr];
            }
        }

        doMain(ptr, print, filename, args);
        System.exit(0);
    }
}
