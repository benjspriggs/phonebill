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
        var expectedBuffer = new StringBuffer();

        var shuffledCalls = new ArrayList<>(bill.getPhoneCalls());
        var shuffledBill = new PhoneBill(bill.getCustomer());

        for (var call : shuffledCalls) {
            shuffledBill.addPhoneCall(call);
        }

        bill = shuffledBill;

        // we need to add what we're expecting
        expectedBuffer.append(PrettyPrinter.formatCustomer(bill.getCustomer()));
        expectedBuffer.append(newline);

        List<AbstractPhoneCall> toSort = new ArrayList<>(bill.getPhoneCalls());

        toSort.sort(Comparator.comparing(AbstractPhoneCall::getStartTime));
        for (AbstractPhoneCall call : toSort) {
            expectedBuffer.append(PrettyPrinter.format(call));
            expectedBuffer.append(newline);
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
            var expectedString = PrettyPrinter.formatDuration(d);

            assertThat(PrettyPrinter.format(call), containsString(expectedString));
        }
    }
}