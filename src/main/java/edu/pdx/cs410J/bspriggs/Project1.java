package edu.pdx.cs410J.bspriggs;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

/**
 * The main class for the CS410J Phone Bill Project
 */
public class Project1 {
    private static final List<Map.Entry<String, String>> ARGUMENTS = Arrays.asList(
            entry("customer", "Person whose phone bill we’re modeling"),
            entry("callerNumber", "Phone number of caller"),
            entry("calleeNumber", "Phone number of person who was called"),
            entry("startTime", "Date and time call began (24-hour time)"),
            entry("endTime", "Date and time call ended (24-hour time)")
    );

    private static final List<Map.Entry<String, String>> OPTIONS = Arrays.asList(
            entry("-print", "Prints a description of the new phone call"),
            entry("-README", "Prints a README for this project and exits")
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
        return "usage: java edu.pdx.cs410J.bspriggs.Project1 [options] <args>\n" +
                "args are (in this order):\n" +
                build(ARGUMENTS) +
                "options are (options may appear in any order):\n" +
                build(OPTIONS) +
                "Date and time should be in the format: mm/dd/yyyy hh:mm";
    }

    public static void main(String[] args) {
        int ptr = 0;
        boolean print = false;

        // parse options
        // (this is probably better done with the apache commons-cli)
        for (; args[ptr].equals("-print") || args[ptr].equals("-README"); ++ptr) {
            if (args[ptr].equals("-print"))
                print = true;
            else {
                System.out.println("Models a customer's phone bill at the command line.\n" +
                        usage());
                System.exit(0);
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
            PhoneCall call = new PhoneCall(args[ptr++], args[ptr++],
                    String.format("%s %s", args[ptr++], args[ptr++]),
                    String.format("%s %s", args[ptr++], args[ptr]));
            bill.addPhoneCall(call);

            if (print) {
                System.out.println(bill.toString());
                System.out.println(call.toString());
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

}