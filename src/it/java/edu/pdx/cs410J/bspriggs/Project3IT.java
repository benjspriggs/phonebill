package edu.pdx.cs410J.bspriggs;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class Project3IT extends Project2IT {
    /**
     * Invokes the main method of {@link Project3} with the given arguments.
     */
    private MainMethodResult invokeMain(String... args) {
        return invokeMain(Project3.class, args);
    }

    private Date generateDateAfter(Date date) {
        return new Date(date.getTime() + r.nextLong());
    }

    private SimpleDateFormat timeFormat = new SimpleDateFormat("H:M a");
    private SimpleDateFormat timeOfDateFormat = new SimpleDateFormat("a");

    @Test
    public void testCommandlineAcceptsNewDateFormat() {
        var start = generateDate();
        var end = generateDateAfter(start);

        var result = invokeMain("customer", generatePhoneNumber(), generatePhoneNumber(),
                dateFormat.format(start), timeFormat.format(start), timeOfDateFormat.format(start),
                dateFormat.format(end), timeFormat.format(end), timeOfDateFormat.format(end));

        assertThat(result.getTextWrittenToStandardError(), result.getExitCode(), equalTo(0));
    }

    @Test
    public void testPrettyPrintsContentsOfFile() throws IOException {
        var printer = new PrettyPrinter();
        var bill = TextDumperTest.getPopulatedPhoneBill();

        var start = generateDate();
        var end = generateDateAfter(start);

        var result = invokeMain("customer", generatePhoneNumber(), generatePhoneNumber(),
                dateFormat.format(start), timeFormat.format(start), timeOfDateFormat.format(start),
                dateFormat.format(end), timeFormat.format(end), timeOfDateFormat.format(end));

        var expectedBuffer = new ByteArrayOutputStream();
        printer.dumpTo(bill, expectedBuffer);

        assertThat(result.getTextWrittenToStandardError(), result.getExitCode(), equalTo(0));
        assertThat(result.getTextWrittenToStandardOut(), is(not("")));
        assertThat(result.getTextWrittenToStandardOut(), containsString(new String(expectedBuffer.toByteArray())));
    }

    @Test
    public void testImproperArgumentsHaveErrorMessage() {
        var result = invokeMain("asf");

        assertThat(result.getTextWrittenToStandardError(), result.getExitCode(), equalTo(1));
        assertThat(result.getTextWrittenToStandardError(), containsString("usage"));
    }

    @Test
    public void testIncorrectDateTimeFormat() {
        var startDate = "1/11/11";
        var endDate = "1/11/12";
        var startTime = "11:11";
        var endTime = "12:11";

        var result = invokeMain("customer", generatePhoneNumber(), generatePhoneNumber(), startDate, startTime, "am", endDate, endTime, "pm");

        assertThat(result.getTextWrittenToStandardError(), result.getExitCode(), equalTo(1));
        assertThat(result.getTextWrittenToStandardError(), containsString("date format"));
    }

    @Test
    public void testInvalidStartAndEndTimes() {
        var start = generateDate();
        var end = generateDateAfter(start);

        var result = invokeMain("customer", generatePhoneNumber(), generatePhoneNumber(),
                dateFormat.format(start), timeFormat.format(start), timeOfDateFormat.format(start),
                dateFormat.format(end), timeFormat.format(end), timeOfDateFormat.format(end));

        assertThat(result.getTextWrittenToStandardError(), result.getExitCode(), equalTo(1));
        assertThat(result.getTextWrittenToStandardError(), containsString("after"));
    }
}