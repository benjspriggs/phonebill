package edu.pdx.cs410J.bspriggs;

import edu.pdx.cs410J.InvokeMainTestCase;
import edu.pdx.cs410J.ParserException;
import org.junit.Test;

import static edu.pdx.cs410J.bspriggs.TextDumperTest.*;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Tests the functionality in the {@link Project1} main class.
 */
public class Project1IT extends InvokeMainTestCase {

    /**
     * Invokes the main method of {@link Project1} with the given arguments.
     */
    protected MainMethodResult invokeMain(String... args) {
        return invokeMain( Project1.class, args );
    }

    /**
     * Tests that invoking the main method with no arguments issues an error
     */
    @Test
    public void testNoCommandLineArguments() {
        MainMethodResult result = invokeMain();
        assertThat(result.getExitCode(), equalTo(1));
        assertThat(result.getTextWrittenToStandardError(), containsString("Missing command line arguments"));
    }

    @Test
    public void testMissingArgs() {
        MainMethodResult result = invokeMain("-print");
        assertThat(result.getExitCode(), equalTo(1));
        assertThat(result.getTextWrittenToStandardError(), containsString("Missing command line arguments"));
    }

    @Test
    public void testPrintWorks() throws ParserException {
        var start = generateDate();
        var end = generateDateAfter(start);
        var startFormatted = PhoneCall.formatDate(start).split(" ");
        var endFormatted = PhoneCall.formatDate(end).split(" ");

        var bill = new PhoneBill("customer");
        var call = new PhoneCall(generatePhoneNumber(), generatePhoneNumber(), PhoneCall.formatDate(start), PhoneCall.formatDate(end));

        bill.addPhoneCall(call);

        MainMethodResult result = invokeMain("-print",
                bill.getCustomer(),
                call.getCaller(), call.getCallee(),
                startFormatted[0], startFormatted[1], startFormatted[2],
                endFormatted[0], endFormatted[1], endFormatted[2]);

        assertThat(result.getTextWrittenToStandardError(), result.getExitCode(), equalTo(0));
        assertThat(result.getTextWrittenToStandardOut(), containsString(bill.toString()));
        assertThat(result.getTextWrittenToStandardOut(), containsString(call.toString()));
    }

    @Test
    public void testREADME() {
        MainMethodResult result = invokeMain("-README");
        assertThat(result.getTextWrittenToStandardError(), result.getExitCode(), equalTo(0));
        assertThat(result.getTextWrittenToStandardOut(), containsString("usage"));
    }
}