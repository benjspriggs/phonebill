package edu.pdx.cs410J.bspriggs;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Unit tests for the {@link PhoneCall} class.
 */
public class PhoneCallTest {
  private PhoneCall getPhoneCall() {
    return new PhoneCall("caller", "callee", "start-time", "end-time");
  }

  @Test
  public void getStartTimeStringNeedsToBeImplemented() {
    PhoneCall call = getPhoneCall();
    assertThat(call.getStartTimeString(), not(nullValue()));
  }

  @Test
  public void getCallerNeedsToBeImplemented() {
    PhoneCall call = getPhoneCall();
    assertThat(call.getCaller(), not(nullValue()));
  }

  @Test
  public void getCalleeNeedsToBeImplemented() {
    PhoneCall call = getPhoneCall();
    assertThat(call.getCallee(), not(nullValue()));
  }

  @Test
  public void forProject1ItIsOkayIfGetStartTimeReturnsNull() {
    PhoneCall call = getPhoneCall();
    assertThat(call.getStartTime(), is(nullValue()));
  }
}
