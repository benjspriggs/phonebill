package edu.pdx.cs410J.bspriggs;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Project3 extends Project2 {
    public static void main(String[] args) {
        wrapMain(new Project3(), args);
    }

    @Override
    List<Option> getOptions() {
        var all = new ArrayList<>(super.getOptions());
        all.add(
                popOpt("-pretty file", "Pretty print the phone bill to a text file or standard out (file -).")
        );
        return Collections.unmodifiableList(all);
    }

    @Override
    String Readme() {
        return "Pretty prints a phone bill" +
                "from a text file, " +
                "creates a new PhoneCall as specified on the command line, " +
                "adds the PhoneCall to the PhoneBill, " +
                "and then optionally writes the PhoneBill back to the text file.";
    }

    @Override
    PhoneBill doWork(HashMap<String, Object> context) throws Exception {
        PhoneBill bill = super.doWork(context);

        if (bill == null) {
            throw new Exception("Unable to parse phone bill/ customer");
        }

        var pretty = context.get("-pretty");

        if (pretty != null) {
            String prettyFilename = (String) pretty;
            PrettyPrinter printer;

            if (prettyFilename.compareTo("-") == 0) {
                printer = new PrettyPrinter(System.out);
            } else {
                printer = new PrettyPrinter(Paths.get(prettyFilename));
            }

            printer.dump(bill);
        }

        return bill;
    }
}
