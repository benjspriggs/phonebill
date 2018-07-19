package edu.pdx.cs410J.bspriggs;

import edu.pdx.cs410J.AbstractPhoneCall;
import edu.pdx.cs410J.ParserException;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.regex.Pattern;

import static java.time.LocalDateTime.ofInstant;
import static java.time.format.DateTimeFormatter.ofPattern;

public class PhoneCall extends AbstractPhoneCall {
    private final String caller;
    private final String callee;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    public static final Pattern PHONE_NUMBER_PATTERN = Pattern.compile("\\d{3}-\\d{3}-\\d{4}");
    public static DateTimeFormatter DATE_FORMAT = ofPattern("MM/DD/yyyy hh:mm a");

    public static String formatDate(Date d) {
        return DATE_FORMAT.format(ofInstant(d.toInstant(), ZoneOffset.UTC));
    }

    /**
     * Initializes a phone call between two numbers.
     *
     * @param caller           Caller's phone number. Must match {@link PhoneCall#PHONE_NUMBER_PATTERN}.
     * @param callee           Callee's phone number. Must match {@link PhoneCall#PHONE_NUMBER_PATTERN}.
     * @param startDateAndTime Start date and time for the call. Must match {@link PhoneCall#DATE_FORMAT}.
     * @param endDateAndTime   Start date and time for the call. Must match {@link PhoneCall#DATE_FORMAT}.
     * @throws ParserException Thrown if any of the fields do not match the provided patterns.
     */
    PhoneCall(String caller, String callee, String startDateAndTime, String endDateAndTime) throws ParserException {
        this.caller = validatePhoneNumber(caller);
        this.callee = validatePhoneNumber(callee);

        this.startTime = LocalDateTime.parse(startDateAndTime, DATE_FORMAT);
        this.endTime = LocalDateTime.parse(endDateAndTime, DATE_FORMAT);

        if (startTime.toInstant(ZoneOffset.UTC).isAfter(endTime.toInstant(ZoneOffset.UTC))) {
            throw new ParserException("End time must be after start time");
        }
    }

    private String validatePhoneNumber(String in) throws ParserException {
        var matcher = PHONE_NUMBER_PATTERN.matcher(in);

        if (!matcher.matches()) {
            throw new ParserException("Invalid phone number: " + in);
        }

        return in;
    }

    @Override
    public String getCaller() {
        return this.caller;
    }

    @Override
    public String getCallee() {
        return this.callee;
    }

    @Override
    public Date getStartTime() {
        return new Date(this.startTime.toInstant(ZoneOffset.UTC).toEpochMilli());
    }

    @Override
    public String getStartTimeString() {
        return DATE_FORMAT.format(startTime);
    }

    @Override
    public Date getEndTime() {
        return new Date(this.endTime.toInstant(ZoneOffset.UTC).toEpochMilli());
    }

    @Override
    public String getEndTimeString() {
        return DATE_FORMAT.format(endTime);
    }
}
