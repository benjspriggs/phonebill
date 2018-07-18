package edu.pdx.cs410J.bspriggs;

import org.junit.Test;

import java.io.ByteArrayOutputStream;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class Project3IT extends Project2IT {
    /**
     * Invokes the main method of {@link Project3} with the given arguments.
     */
    private MainMethodResult invokeMain(String... args) {
        return invokeMain(Project3.class, args);
    }

    private String generateDateAfter(String date) {
        return null;
    }

    private String generateTimeAfter(String startTime) {
        return null;
    }

    @Test
    public void testCommandlineAcceptsNewDateFormat() {
        var startDate = generateDate();
        var endDate = generateDateAfter(startDate);
        var startTime = generateTime();
        var endTime = generateTimeAfter(startTime);
        var result = invokeMain("customer", generatePhoneNumber(), generatePhoneNumber(), startDate, startTime, endDate, endTime);

        assertThat(result.getTextWrittenToStandardError(), result.getExitCode(), equalTo(0));
    }

    @Test
    public void testPrettyPrintsContentsOfFile() {
        var printer = new PrettyPrinter();
        var bill = TextDumperTest.getPopulatedPhoneBill();

        var startDate = generateDate();
        var endDate = generateDateAfter(startDate);
        var startTime = generateTime();
        var endTime = generateTimeAfter(startTime);

        var result = invokeMain("customer", generatePhoneNumber(), generatePhoneNumber(), startDate, startTime, endDate, endTime);
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
    }

    @Test
    public void testIncorrectDateTimeFormat() {
        var startDate = "1/11/11";
        var endDate = "1/11/12";
        var startTime = "11:11";
        var endTime = "!2:11";

        var result = invokeMain("customer", generatePhoneNumber(), generatePhoneNumber(), startDate, startTime, endDate, endTime);

        assertThat(result.getTextWrittenToStandardError(), result.getExitCode(), equalTo(1));
    }

    @Test
    public void testInvalidStartAndEndTimes() {
        var endDate = generateDate();
        var startDate = generateDateAfter(endDate);
        var endTime = generateTime();
        var startTime = generateTimeAfter(endTime);
        var result = invokeMain("customer", generatePhoneNumber(), generatePhoneNumber(), startDate, startTime, endDate, endTime);

        assertThat(result.getTextWrittenToStandardError(), result.getExitCode(), equalTo(1));
    }
}