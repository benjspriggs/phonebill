package edu.pdx.cs410J.bspriggs;

import edu.pdx.cs410J.AbstractPhoneCall;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Project2 extends Project1 {
    public static void main(String[] args) {
        wrapMain(new Project2(), args);
    }

    @Override
    List<Option> getOptions() {
        var all = new ArrayList<>(super.getOptions());
        all.add(
                popOpt("-textFile file", "Where to read/write the phone bill")
        );
        return Collections.unmodifiableList(all);
    }

    @Override
    String Readme() {
        return "Optionally reads a PhoneBill " +
                "from a text file, " +
                "creates a new PhoneCall as specified on the command line, " +
                "adds the PhoneCall to the PhoneBill, " +
                "and then optionally writes the PhoneBill back to the text file.";
    }

    @Override
    PhoneBill doWork(HashMap<String, Object> context) throws Exception {
        PhoneBill bill = super.doWork(context);
        String filename = (String) context.get("-textFile");

        // open the file
        if (filename != null && filename.length() > 0) {
            TextParser textParser = new TextParser(Paths.get(filename));
            var parsedBill = (PhoneBill) textParser.parse();

            if (parsedBill != null) {
                if (!parsedBill.getCustomer().equals(bill.getCustomer())) {
                    throw new Exception(String.format(bill.getCustomer(), parsedBill.getCustomer()));
                }

                for (var call : parsedBill.getPhoneCalls()) {
                    bill.addPhoneCall((AbstractPhoneCall) call);
                }
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
    }
}
