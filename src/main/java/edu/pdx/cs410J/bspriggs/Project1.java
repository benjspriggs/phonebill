package edu.pdx.cs410J.bspriggs;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * The main class for the CS410J Phone Bill Project
 */
public class Project1 extends MainClassSkeleton<PhoneBill> {
    public static final List<Argument> ARGUMENTS = Arrays.asList(
            pop("customer", "Person whose phone bill we’re modeling"),
            pop("callerNumber", "Phone number of caller"),
            pop("calleeNumber", "Phone number of person who was called"),
            popN("startTime", "Date and time call began (24-hour time)", 3),
            popN("endTime", "Date and time call ended (24-hour time)", 3)
    );

    public static final List<Option> OPTIONS = Arrays.asList(
            popOpt("-print", "Prints a description of the new phone call")
    );

    @Override
    List<Argument> getArguments() {
        return ARGUMENTS;
    }

    @Override
    List<Option> getOptions() {
        return OPTIONS;
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
                String.join(" ", (List<String>) context.get("startTime")).toUpperCase(),
                String.join(" ", (List<String>) context.get("endTime")).toUpperCase());

        bill.addPhoneCall(call);

        var print = context.get("-print");

        if (print != null && (boolean) print) {
            System.out.println(bill.toString());
            System.out.println(call.toString());
        }

        return bill;
    }

    public static void main(String[] args) {
        wrapMain(new Project1(), args);
    }
}