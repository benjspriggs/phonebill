package edu.pdx.cs410J.bspriggs;

import edu.pdx.cs410J.AbstractPhoneCall;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

public class PhoneCall extends AbstractPhoneCall {
  private final String caller;
  private final String callee;
  private final String startTime;
  private final String endTime;
    private static final Pattern phoneNumberPattern = Pattern.compile("\\d\\d\\d-\\d\\d\\d-\\d\\d\\d\\d");
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("mm/dd/yyyy hh:mm");

    PhoneCall(String caller, String callee, String s, String s1) throws Exception {
        this.caller = validatePhoneNumber(caller);
        this.callee = validatePhoneNumber(callee);
        this.startTime = validateDate(s);
        this.endTime = validateDate(s1);
    }

    private String validatePhoneNumber(String in) throws ParseException {
        var matcher = phoneNumberPattern.matcher(in);

        if (!matcher.matches()) {
            throw new ParseException(in, 0);
        }

        return in;
    }

    private String validateDate(String in) throws ParseException {
        if (dateFormat.parse(in) == null) {
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
  public String getStartTimeString() {
    return this.startTime;
  }

  @Override
  public String getEndTimeString() {
    return this.endTime;
  }
}
