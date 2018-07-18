package edu.pdx.cs410J.bspriggs;

import edu.pdx.cs410J.AbstractPhoneCall;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;

public class PrettyPrinterTest extends TextDumperTest {
    @Test
    public void testCallsAreSortedChronologicallyByStartTime() throws IOException {
        var bill = TextDumperTest.getPopulatedPhoneBill();
        var printer = new PrettyPrinter();
        var expectedBuffer = new ByteArrayOutputStream();

        // we need to add what we're expecting
        expectedBuffer.write(PrettyPrinter.formatCustomer(bill.getCustomer()).getBytes());
        expectedBuffer.write(newline.getBytes());

        List<AbstractPhoneCall> toSort = new ArrayList<>(bill.getPhoneCalls());

        toSort.sort(Comparator.comparing(AbstractPhoneCall::getStartTime));
        for (AbstractPhoneCall call : toSort) {
            expectedBuffer.write(PrettyPrinter.format(call).getBytes());
            expectedBuffer.write(newline.getBytes());
        }

        var actualBuffer = new ByteArrayOutputStream();
        printer.dumpTo(bill, actualBuffer);

        assertThat(actualBuffer.toString(), containsString(expectedBuffer.toString()));
    }

    @Test
    public void testPrintedCallsIncludeDuration() {
        var bill = TextDumperTest.getPopulatedPhoneBill();

        for (AbstractPhoneCall call : bill.getPhoneCalls()) {
            Duration d = Duration.between(call.getStartTime().toInstant(), call.getEndTime().toInstant());

            assertThat(PrettyPrinter.format(call), containsString(d.toString()));
        }
    }
}