package edu.pdx.cs410J.bspriggs;

import edu.pdx.cs410J.AbstractPhoneBill;
import edu.pdx.cs410J.AbstractPhoneCall;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

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

    public void dumpTo(AbstractPhoneBill phoneBill, OutputStream outputStream) throws IOException {
        outputStream.write(phoneBill.getCustomer().getBytes());
        outputStream.write(System.getProperty("line.separator").getBytes());
        for (Object b : phoneBill.getPhoneCalls()) {
            outputStream.write(serialize((AbstractPhoneCall) b).getBytes());
            outputStream.write(System.getProperty("line.separator").getBytes());
        }
    }

    @Override
    public void dump(AbstractPhoneBill abstractPhoneBill) {
        var file = new File(this.fileName);

        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            dumpTo(abstractPhoneBill, fileOutputStream);
        } catch (IOException e) {
            throw new RuntimeException();
            // e.printStackTrace();
        }
    }
}
