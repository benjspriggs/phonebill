package edu.pdx.cs410J.bspriggs;

import edu.pdx.cs410J.AbstractPhoneBill;
import edu.pdx.cs410J.AbstractPhoneCall;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;

public class TextDumper implements edu.pdx.cs410J.PhoneBillDumper {
    private final Path path;
    /**
     * Delimiter used to separate fields in a {@link PhoneCall}. See {@link TextDumper::serizlize}.
     */
    public static final String DELIMITER = "|";
    public static final String NEWLINE = System.getProperty("line.separator");

    public TextDumper(Path path) {
        this.path = path;
    }

    public TextDumper() {
        this.path = null;
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
        if (phoneBill == null) {
            return;
        }

        outputStream.write(phoneBill.getCustomer().getBytes());
        outputStream.write(NEWLINE.getBytes());
        for (Object b : phoneBill.getPhoneCalls()) {
            outputStream.write(serialize((AbstractPhoneCall) b).getBytes());
            outputStream.write(NEWLINE.getBytes());
        }

        outputStream.close();
    }

    @Override
    public void dump(AbstractPhoneBill abstractPhoneBill) throws IOException {
        var file = this.path.toFile();

        if (file.length() != 0) {
            throw new IOException("File not empty: " + file.getAbsolutePath());
        }

        FileOutputStream fileOutputStream = new FileOutputStream(file);

        dumpTo(abstractPhoneBill, fileOutputStream);
    }
}
