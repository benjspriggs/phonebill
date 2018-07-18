package edu.pdx.cs410J.bspriggs;

import edu.pdx.cs410J.AbstractPhoneBill;
import edu.pdx.cs410J.AbstractPhoneCall;
import edu.pdx.cs410J.PhoneBillDumper;

import java.io.OutputStream;

public class PrettyPrinter implements PhoneBillDumper {
    public static String format(AbstractPhoneCall call) {
        return "";
    }

    public static String formatCustomer(String customer) {
        return "";
    }

    @Override
    public void dump(AbstractPhoneBill bill) {

    }

    public void dumpTo(AbstractPhoneBill<AbstractPhoneCall> bill, OutputStream expected) {

    }
}
