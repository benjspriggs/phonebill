package edu.pdx.cs410J.bspriggs;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Unit tests for the {@link PhoneCall} class.
 */
public class PhoneCallTest {
  private final String validPhoneNumber = "503-333-3333";
  private final String validTime = "1/15/2031 2:33";

  private PhoneCall getPhoneCall() {
    return new PhoneCall(validPhoneNumber, validPhoneNumber, validTime, validTime);
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

  @Test(expected = Exception.class)
  public void passingInvalidPhoneNumberFails() {
    PhoneCall call = new PhoneCall("cat", "dog", validTime, validTime);
  }

  @Test(expected = Exception.class)
  public void passingInvalidDateFails() {
    PhoneCall call = new PhoneCall(validPhoneNumber, validPhoneNumber, "cat", "dog");
  }
}
