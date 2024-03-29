package edu.pdx.cs410J.bspriggs;

import edu.pdx.cs410J.ParserException;
import org.junit.Test;

import static edu.pdx.cs410J.bspriggs.TextDumperTest.generatePhoneCall;
import static junit.framework.TestCase.assertTrue;
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
    public void addPhoneCall() throws ParserException {
        var name = "name";
        PhoneBill b = new PhoneBill(name);

        b.addPhoneCall(generatePhoneCall());
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
    public void addedPhoneCallAppearsInPhoneBill() throws ParserException {
        var name = "name";
        PhoneBill b = new PhoneBill(name);
        var call = generatePhoneCall();

        b.addPhoneCall(call);

        var calls = b.getPhoneCalls();

        assertTrue(calls.contains(call));
    }
}