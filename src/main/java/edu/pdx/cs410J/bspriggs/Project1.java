package edu.pdx.cs410J.bspriggs;

import edu.pdx.cs410J.ParserException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

/**
 * The main class for the CS410J Phone Bill Project
 */
public class Project1 extends MainClassSkeleton<PhoneBill> {
    public static final List<Map.Entry<String, String>> ARGUMENTS = Arrays.asList(
            entry("customer", "Person whose phone bill we’re modeling"),
            entry("callerNumber", "Phone number of caller"),
            entry("calleeNumber", "Phone number of person who was called"),
            entry("startTime", "Date and time call began (24-hour time)"),
            entry("endTime", "Date and time call ended (24-hour time)")
    );

    public static final List<Map.Entry<String, String>> OPTIONS = Arrays.asList(
            entry("-print", "Prints a description of the new phone call"),
            entry("-README", "Prints a README for this project and exits")
    );

    protected static String build(List<Map.Entry<String, String>> f) {
        var b = new StringBuilder();

        for (Map.Entry<String, String> pair : f) {
            b.append(String.format("  %-10s\t%s", pair.getKey(), pair.getValue()));
            b.append(System.lineSeparator());
        }

        return b.toString();
    }

    protected static String usage() {
        return "usage: java edu.pdx.cs410J.bspriggs.Project1 [options] <args>\n" +
                "args are (in this order):\n" +
                build(ARGUMENTS) +
                "options are (options may appear in any order):\n" +
                build(OPTIONS) +
                "Date and time should be in the format: " + PhoneCall.DATE_FORMAT_STRING;
    }

    private static PhoneCall parsePhoneCallFromArguments(String[] args) throws ParserException {
        int ptr = 0;

        return new PhoneCall(args[ptr++], args[ptr++],
                String.format("%s %s %s", args[ptr++], args[ptr++], args[ptr++].toUpperCase()),
                String.format("%s %s %s", args[ptr++], args[ptr++], args[ptr].toUpperCase()));
    }

    private static String[] sliceArgumentsForPhoneCallParsing(String[] args, int ptr) {
        return Arrays.copyOfRange(args, ptr, args.length);
    }

    protected static PhoneCall getPhoneCall(String[] args, int ptr) throws ParserException {
        return parsePhoneCallFromArguments(sliceArgumentsForPhoneCallParsing(args, ptr));
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

    public static void validateEmptyArguments(String[] args) {
        if (args == null || args.length == 0) {
            System.err.println("Missing command line arguments");
            System.err.println(usage());
            System.exit(1);
        }
    }

    protected static PhoneBill doMain(int ptr, boolean print, String[] args) {
        try {
            PhoneBill bill = new PhoneBill(args[ptr++]);

            validateArguments(ARGUMENTS, args, ptr);

            PhoneCall call = getPhoneCall(args, ptr);

            bill.addPhoneCall(call);

            if (print) {
                System.out.println(bill.toString());
                System.out.println(call.toString());
            }

            return bill;
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.err.println(usage());
            System.exit(1);
        }

        return null;
    }

    @Override
    List<Argument> getArguments() {
        return Arrays.asList(
                pop("customer", "Person whose phone bill we’re modeling"),
                pop("callerNumber", "Phone number of caller"),
                pop("calleeNumber", "Phone number of person who was called"),
                popN("startTime", "Date and time call began (24-hour time)", 3),
                popN("endTime", "Date and time call ended (24-hour time)", 3)
        );
    }

    @Override
    List<Option> getOptions() {
        return Arrays.asList(
                popOpt("-print", "Prints a description of the new phone call")
        );
    }

    @Override
    String Readme() {
        return "More readme text";
    }

    @Override
    PhoneBill doWork(HashMap<String, Object> context) throws Exception {
        PhoneBill bill = new PhoneBill((String) context.get("customer"));

        PhoneCall call = new PhoneCall(
                (String) context.get("callerNumber"),
                (String) context.get("calleeNumber"),
                String.join(" ", (String) context.get("startTime")),
                String.join(" ", (String) context.get("endTime")));

        bill.addPhoneCall(call);

        if ((boolean) context.get("-print")) {
            System.out.println(bill.toString());
            System.out.println(call.toString());
        }

        return bill;
    }

    public static void main(String[] args) {
        wrapMain(new Project1(), args);

//        validateEmptyArguments(args);
//
//        int ptr = 0;
//        boolean print = false;
//
//        // parse options
//        // (this is probably better done with the apache commons-cli)
//        for (; (ptr < args.length) && (args[ptr].equals("-print") || args[ptr].equals("-README")); ++ptr) {
//            if (args[ptr].equals("-print"))
//                print = true;
//            else {
//                System.out.println("Models a customer's phone bill at the command line.\n" +
//                        usage());
//                System.exit(0);
//            }
//        }
//
//        doMain(ptr, print, args);
    }

}