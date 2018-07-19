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
     * Delimiter used to separate fields in a {@link PhoneCall}. See {@link TextDumper#serialize(AbstractPhoneCall)}.
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
     * @param call The call to serialize.
     * @return A serialized {@link PhoneCall}
     */
    public static String serialize(AbstractPhoneCall call) {
        return String.join(TextDumper.DELIMITER, call.getCaller(), call.getCallee(), call.getStartTimeString(), call.getEndTimeString());
    }

    /**
     * Dumps a phone bill to an {@link OutputStream}. Does not close the stream after it's done.
     *
     * @param phoneBill    The phone blll to dump.
     * @param outputStream The stream to dump to.
     * @throws IOException Thrown if there's any issue dumping to the stream.
     */
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
    }

    /**
     * Dumps a {@link AbstractPhoneBill} to the given path (initialized in {@link TextDumper#TextDumper(Path)}.
     * Overwrites any existing content in the file, if there's any.
     * @param abstractPhoneBill The phone bill to dump.
     * @throws IOException Thrown if there's issues opening or updating the file.
     */
    @Override
    public void dump(AbstractPhoneBill abstractPhoneBill) throws IOException {
        var file = this.path.toFile();

        if (file.length() != 0) {
            if (!(file.delete() && file.createNewFile())) {
                throw new IOException("Unable to create new file: " + file.getAbsolutePath());
            }
        }

        FileOutputStream fileOutputStream = new FileOutputStream(file);

        dumpTo(abstractPhoneBill, fileOutputStream);
    }
}
