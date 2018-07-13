package edu.pdx.cs410J.bspriggs;

import edu.pdx.cs410J.AbstractPhoneCall;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.nullValue;

public class PhoneBillTest {
    /**
     * PhoneBills have a customer name.
     */
    @Test
    public void getCustomer() {
        var name = "name";
        PhoneBill b = new PhoneBill(name);

        assertThat(b.getCustomer(), not(nullValue()));
        assertThat(b.getCustomer(), containsString(name));
    }

    /**
     * Adding a call to the phone bill works.
     */
    @Test
    public void addPhoneCall() {
        var name = "name";
        PhoneBill b = new PhoneBill(name);

        b.addPhoneCall(getPhoneCall());
    }

    private AbstractPhoneCall getPhoneCall() {
        try {
            return new PhoneCall("555-555-5555", "555-555-5555", "1/1/1 0:00", "1/1/1 0:00");
        } catch (Exception e) {
            fail(e.getMessage());
            return null;
        }
    }

    /**
     * Getting phone calls for an empty Phone Bill works.
     */
    @Test
    public void getPhoneCalls() {
        var name = "name";
        PhoneBill b = new PhoneBill(name);

        var calls = b.getPhoneCalls();

        assertTrue(calls.isEmpty());
    }

    /**
     * Adding a call to the PhoneBill works, and that call appears in the list.
     */
    @Test
    public void addedPhoneCallAppearsInPhoneBill() {
        var name = "name";
        PhoneBill b = new PhoneBill(name);
        var call = getPhoneCall();

        b.addPhoneCall(call);

        var calls = b.getPhoneCalls();

        assertTrue(calls.contains(call));
    }
}