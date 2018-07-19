package edu.pdx.cs410J.bspriggs;

import edu.pdx.cs410J.AbstractPhoneBill;
import edu.pdx.cs410J.AbstractPhoneCall;
import edu.pdx.cs410J.PhoneBillDumper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PrettyPrinter implements PhoneBillDumper {
    private final Path filePath;
    private final boolean toStdOut;

    public PrettyPrinter(Path path) {
        this.filePath = path;
        this.toStdOut = false;
    }

    public PrettyPrinter() {
        this.filePath = null;
        this.toStdOut = false;
    }

    public PrettyPrinter(PrintStream out) {
        this.filePath = null;
        this.toStdOut = true;
    }

    public static String format(AbstractPhoneCall call) {
        return String.format("%s - %s",
                call.toString(),
                formatDuration(Duration.between(call.getStartTime().toInstant(), call.getEndTime().toInstant()))
        );
    }

    public static String formatCustomer(String customer) {
        return "Customer " + customer;
    }

    public static String formatDuration(Duration duration) {
        return duration.toString();
    }

    @Override
    public void dump(AbstractPhoneBill bill) {
        try {
            OutputStream file;
            if (toStdOut)
                file = System.out;
            else
                file = new FileOutputStream(this.filePath.toFile());

            dumpTo(bill, file);
        } catch (Exception e) {
            // e.printStackTrace();
        }
    }

    public void dumpTo(AbstractPhoneBill<AbstractPhoneCall> bill, OutputStream expected) throws IOException {
        if (bill == null || expected == null) {
            return;
        }

        expected.write(formatCustomer(bill.getCustomer()).getBytes());
        expected.write(TextDumper.NEWLINE.getBytes());

        List<AbstractPhoneCall> toSort = new ArrayList<>(bill.getPhoneCalls());

        toSort.sort(Comparator.comparing(AbstractPhoneCall::getStartTime));
        for (AbstractPhoneCall call : toSort) {
            expected.write(format(call).getBytes());
            expected.write((TextDumper.NEWLINE.getBytes()));
        }
    }
}
