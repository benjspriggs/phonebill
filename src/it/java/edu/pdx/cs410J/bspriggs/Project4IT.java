package edu.pdx.cs410J.bspriggs;

import edu.pdx.cs410J.InvokeMainTestCase;
import edu.pdx.cs410J.ParserException;
import net.bytebuddy.utility.RandomString;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import static edu.pdx.cs410J.bspriggs.TextDumperTest.*;
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

    @Test
    public void testNoCommandLineArguments() {
        MainMethodResult result = invokeMain( Project4.class );
        assertThat(result.getExitCode(), equalTo(1));
        assertThat(result.getTextWrittenToStandardError(), containsString(Project4.MISSING_ARGS));
    }

    @Test
    public void testREADME() {
        MainMethodResult result = invokeMain(Project4.class, "-README");
        assertThat(result.getExitCode(), equalTo(0));
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
        MainMethodResult result = invokeMain(Project4.class,
                bill.getCustomer(), phoneCall.getCaller(), phoneCall.getCallee(),
                phoneCall.getStartTimeString(), phoneCall.getEndTimeString());

        assertThat(result.getExitCode(), equalTo(0));
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
        MainMethodResult result = invokeMain(Project4.class,
                bill.getCustomer(), phoneCall.getCaller(), phoneCall.getCallee(),
                phoneCall.getStartTimeString(), phoneCall.getEndTimeString());

        assertThat(result.getExitCode(), equalTo(0));
        assertThat(result.getTextWrittenToStandardError(), equalTo(""));
        assertThat(result.getTextWrittenToStandardOut(), containsString(out.toString()));

        phoneCall = generatePhoneCall();
        bill.addPhoneCall(phoneCall);
        out = new ByteArrayOutputStream();

        // and the second call
        result = invokeMain(Project4.class,
                bill.getCustomer(), phoneCall.getCaller(), phoneCall.getCallee(),
                phoneCall.getStartTimeString(), phoneCall.getEndTimeString());

        prettyPrinter.dumpTo(bill, out);
        assertThat(result.getExitCode(), equalTo(0));
        assertThat(result.getTextWrittenToStandardOut(), containsString(out.toString()));
    }

    @Test
    public void testSearchNonexistentPhoneBillWorks() {
        var start = generateDate();
        var startTime = PhoneCall.formatDate(generateDate());
        var endTime = PhoneCall.formatDate(generateDateAfter(start));
        MainMethodResult result = invokeMain(Project4.class,
                RandomString.make(), startTime, endTime);

        assertThat(result.getExitCode(), equalTo(0));
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
        MainMethodResult result = invokeMain(Project4.class,
                bill.getCustomer(), phoneCall.getCaller(), phoneCall.getCallee(),
                phoneCall.getStartTimeString(), phoneCall.getEndTimeString());

        assertThat(result.getExitCode(), equalTo(0));
        assertThat(result.getTextWrittenToStandardError(), equalTo(""));
        assertThat(result.getTextWrittenToStandardOut(), containsString(out.toString()));

        result = invokeMain(Project4.class,
                bill.getCustomer(), phoneCall.getStartTimeString(), phoneCall.getEndTimeString());

        assertThat(result.getExitCode(), equalTo(0));
        assertThat(result.getTextWrittenToStandardOut(),
                containsString(Messages.formatPhoneCalls(new ArrayList<>(Collections.singleton(phoneCall)))));
    }

    @Test
    public void test2EmptyServer() {
        MainMethodResult result = invokeMain( Project4.class, HOSTNAME, PORT );
        assertThat(result.getTextWrittenToStandardError(), result.getExitCode(), equalTo(0));
        String out = result.getTextWrittenToStandardOut();
        assertThat(out, out, containsString(Messages.formatWordCount(0)));
    }

    @Test
    public void test3NoDefinitions() {
        String word = "WORD";
        MainMethodResult result = invokeMain( Project4.class, HOSTNAME, PORT, word );
        assertThat(result.getTextWrittenToStandardError(), result.getExitCode(), equalTo(0));
        String out = result.getTextWrittenToStandardOut();
        assertThat(out, out, containsString(Messages.formatDictionaryEntry(word, null)));
    }

    @Test
    public void test4AddDefinition() {
        String word = "WORD";
        String definition = "DEFINITION";

        MainMethodResult result = invokeMain( Project4.class, HOSTNAME, PORT, word, definition );
        assertThat(result.getTextWrittenToStandardError(), result.getExitCode(), equalTo(0));
        String out = result.getTextWrittenToStandardOut();
        assertThat(out, out, containsString(Messages.definedWordAs(word, definition)));

        result = invokeMain( Project4.class, HOSTNAME, PORT, word );
        out = result.getTextWrittenToStandardOut();
        assertThat(out, out, containsString(Messages.formatDictionaryEntry(word, definition)));

        result = invokeMain( Project4.class, HOSTNAME, PORT );
        out = result.getTextWrittenToStandardOut();
        assertThat(out, out, containsString(Messages.formatDictionaryEntry(word, definition)));
    }
}