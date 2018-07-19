package edu.pdx.cs410J.bspriggs;

import edu.pdx.cs410J.AbstractPhoneCall;
import edu.pdx.cs410J.ParserException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

public class PhoneCall extends AbstractPhoneCall {
    private final String caller;
    private final String callee;
    private final Date startTime;
    private final Date endTime;
    public static final Pattern PHONE_NUMBER_PATTERN = Pattern.compile("\\d{3}-\\d{3}-\\d{4}");
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("m/d/y H:M a");

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

        try {
            this.startTime = DATE_FORMAT.parse(startDateAndTime);
            this.endTime = DATE_FORMAT.parse(endDateAndTime);
        } catch (final ParseException e) {
            throw new ParserException(e.toString());
        }

        if (startTime.after(endTime)) {
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
        return this.startTime;
    }

    @Override
    public String getStartTimeString() {
        return DATE_FORMAT.format(startTime);
    }

    @Override
    public Date getEndTime() {
        return this.endTime;
    }

    @Override
    public String getEndTimeString() {
        return DATE_FORMAT.format(endTime);
    }
}
