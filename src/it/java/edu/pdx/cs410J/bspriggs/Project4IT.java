package edu.pdx.cs410J.bspriggs;

import edu.pdx.cs410J.InvokeMainTestCase;
import edu.pdx.cs410J.ParserException;
import net.bytebuddy.utility.RandomString;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Stream;

import static edu.pdx.cs410J.bspriggs.TextDumperTest.*;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

/**
 * Tests the {@link Project4} class by invoking its main method with various arguments
 */
public class Project4IT extends InvokeMainTestCase {
    private static final String HOSTNAME = "localhost";
    private static final String PORT = System.getProperty("http.port", "8080");

    private MainMethodResult invokeMain(String... args) {
        var connectionArgs = asList("-host", HOSTNAME, "-port", PORT);
        var newArgs = Stream.concat(connectionArgs.stream(), asList(args).stream());
        return invokeMain(Project4.class, newArgs.toArray(String[]::new));
    }

    private MainMethodResult invokeMainWithDate(String startDate, String endDate, String... args) {
        var dateArgs = Stream.concat(asList(startDate.split(" ")).stream(), asList(endDate.split(" ")).stream());
        var newArgs = Stream.concat(asList(args).stream(), dateArgs);
        return invokeMain(newArgs.toArray(String[]::new));
    }

    @Test
    public void testNoCommandLineArguments() {
        MainMethodResult result = invokeMain();
        assertThatResultCodeIs(result, 1);
        assertThat(result.getTextWrittenToStandardError(), containsString(Project4.MISSING_ARGS));
    }

    private void assertThatResultCodeIs(MainMethodResult result, int i) {
        assertThat(result.getTextWrittenToStandardError(), result.getExitCode(), equalTo(i));
    }

    @Test
    public void testREADME() {
        MainMethodResult result = invokeMain("-README");
        assertThatResultCodeIs(result, 0);
        assertThat(result.getTextWrittenToStandardError(), equalTo(""));
    }

    @Test
    public void testCreateNewPhoneBillWorks() throws ParserException, IOException {
        var customer = RandomString.make();
        var bill = new PhoneBill(customer);
        var phoneCall = generatePhoneCall();
        var prettyPrinter = new PrettyPrinter();
        var out = new ByteArrayOutputStream();

        prettyPrinter.dumpTo(bill, out);

        bill.addPhoneCall(phoneCall);
        MainMethodResult result = invokeMainWithDate(phoneCall.getStartTimeString(), phoneCall.getEndTimeString(),
                bill.getCustomer(), phoneCall.getCaller(), phoneCall.getCallee());

        assertThatResultCodeIs(result, 0);
        assertThat(result.getTextWrittenToStandardError(), equalTo(""));
        assertThat(result.getTextWrittenToStandardOut(), containsString(out.toString()));
    }

    @Test
    public void testAddCallToExistingPhoneBillWorks() throws ParserException, IOException {
        var customer = RandomString.make();
        var bill = new PhoneBill(customer);
        var phoneCall = generatePhoneCall();
        var prettyPrinter = new PrettyPrinter();
        var out = new ByteArrayOutputStream();

        prettyPrinter.dumpTo(bill, out);

        bill.addPhoneCall(phoneCall);
        // add the first call
        MainMethodResult result = invokeMainWithDate(phoneCall.getStartTimeString(), phoneCall.getEndTimeString(),
                bill.getCustomer(), phoneCall.getCaller(), phoneCall.getCallee());

        assertThatResultCodeIs(result, 0);
        assertThat(result.getTextWrittenToStandardError(), equalTo(""));
        assertThat(result.getTextWrittenToStandardOut(), containsString(out.toString()));

        phoneCall = generatePhoneCall();
        bill.addPhoneCall(phoneCall);
        out = new ByteArrayOutputStream();

        // and the second call
        result = invokeMainWithDate(phoneCall.getStartTimeString(), phoneCall.getEndTimeString(),
                bill.getCustomer(), phoneCall.getCaller(), phoneCall.getCallee());

        prettyPrinter.dumpTo(bill, out);
        assertThatResultCodeIs(result, 0);
        assertThat(result.getTextWrittenToStandardOut(), containsString(out.toString()));
    }

    @Test
    public void testSearchNonexistentPhoneBillWorks() {
        var start = generateDate();
        var startTime = PhoneCall.formatDate(generateDate());
        var endTime = PhoneCall.formatDate(generateDateAfter(start));
        MainMethodResult result = invokeMainWithDate(startTime, endTime,
                "-search", RandomString.make(), startTime, endTime);

        assertThatResultCodeIs(result, 1);
        assertThat(result.getTextWrittenToStandardError(), is(not("")));
    }

    @Test
    public void testSearchExistingPhoneBillWorks() throws ParserException, IOException {
        var customer = RandomString.make();
        var bill = new PhoneBill(customer);
        var phoneCall = generatePhoneCall();
        var prettyPrinter = new PrettyPrinter();
        var out = new ByteArrayOutputStream();

        prettyPrinter.dumpTo(bill, out);

        bill.addPhoneCall(phoneCall);
        MainMethodResult result = invokeMainWithDate(phoneCall.getStartTimeString(), phoneCall.getEndTimeString(),
                bill.getCustomer(), phoneCall.getCaller(), phoneCall.getCallee());

        assertThatResultCodeIs(result, 0);
        assertThat(result.getTextWrittenToStandardError(), equalTo(""));
        assertThat(result.getTextWrittenToStandardOut(), containsString(out.toString()));

        result = invokeMainWithDate(phoneCall.getStartTimeString(), phoneCall.getEndTimeString(),
                "-search", bill.getCustomer());

        assertThatResultCodeIs(result, 0);
        assertThat(result.getTextWrittenToStandardOut(),
                containsString(Messages.formatPhoneCalls(new ArrayList<>(Collections.singleton(phoneCall)))));
    }
}