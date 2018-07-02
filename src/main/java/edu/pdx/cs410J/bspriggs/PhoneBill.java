package edu.pdx.cs410J.bspriggs;

import edu.pdx.cs410J.AbstractPhoneBill;
import edu.pdx.cs410J.AbstractPhoneCall;

import java.util.Collection;

public class PhoneBill extends AbstractPhoneBill {
    private final String customer;

    public PhoneBill(String customerName) {
        this.customer = customerName;
    }

    @Override
    public String getCustomer() {
        return this.customer;
    }

    @Override
    public void addPhoneCall(AbstractPhoneCall abstractPhoneCall) {

    }

    @Override
    public Collection getPhoneCalls() {
        return null;
    }
}
