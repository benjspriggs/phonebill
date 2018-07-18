package edu.pdx.cs410J.bspriggs;

import edu.pdx.cs410J.AbstractPhoneCall;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

public class PhoneCall extends AbstractPhoneCall {
    private final String caller;
    private final String callee;
    private final Date startTime;
    private final Date endTime;
    private static final Pattern phoneNumberPattern = Pattern.compile("\\d{3}-\\d{3}-\\d{4}");
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("mm/dd/yyyy hh:mm");

    PhoneCall(String caller, String callee, String startDateAndTime, String endDateAndTime) throws ParseException {
        this.caller = validatePhoneNumber(caller);
        this.callee = validatePhoneNumber(callee);
        this.startTime = dateFormat.parse(startDateAndTime);
        this.endTime = dateFormat.parse(endDateAndTime);
    }

    private String validatePhoneNumber(String in) throws ParseException {
        var matcher = phoneNumberPattern.matcher(in);

        if (!matcher.matches()) {
            throw new ParseException(in, 0);
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
        return dateFormat.format(startTime);
    }

    @Override
    public Date getEndTime() {
        return this.endTime;
    }

    @Override
    public String getEndTimeString() {
        return dateFormat.format(endTime);
    }
}
