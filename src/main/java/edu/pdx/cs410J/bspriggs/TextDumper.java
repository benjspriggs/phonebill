package edu.pdx.cs410J.bspriggs;

import edu.pdx.cs410J.AbstractPhoneBill;
import edu.pdx.cs410J.AbstractPhoneCall;

import java.io.DataOutputStream;

public class TextDumper implements edu.pdx.cs410J.PhoneBillDumper {
    private final String fileName;
    /**
     * Delimiter used to separate fields in a {@link PhoneCall}. See {@link TextDumper::serizlize}.
     */
    private static String DELIMITER = "|";

    public TextDumper(String fileName) {
        this.fileName = fileName;
    }

    public TextDumper() {
        this.fileName = "";
    }

    /**
     * Serializes a single {@link PhoneCall}.
     *
     * @param call
     * @return A serialized {@link PhoneCall}
     */
    public static String serialize(AbstractPhoneCall call) {
        return String.join(TextDumper.DELIMITER, call.getCaller(), call.getCallee(), call.getStartTimeString(), call.getEndTimeString());
    }

    public void dumpTo(AbstractPhoneBill phoneBill, DataOutputStream dataOutputStream) {

    }

    @Override
    public void dump(AbstractPhoneBill abstractPhoneBill) {

    }
}
