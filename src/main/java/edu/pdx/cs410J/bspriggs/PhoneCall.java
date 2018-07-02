package edu.pdx.cs410J.bspriggs;

import edu.pdx.cs410J.AbstractPhoneCall;

public class PhoneCall extends AbstractPhoneCall {
  private final String caller;
  private final String callee;
  private final String startTime;
  private final String endTime;

  PhoneCall(String caller, String callee, String s, String s1) {
    this.caller = caller;
    this.callee = callee;
    this.startTime = s;
    this.endTime = s1;
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
