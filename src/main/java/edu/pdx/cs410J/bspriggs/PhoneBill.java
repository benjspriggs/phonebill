package edu.pdx.cs410J.bspriggs;

import edu.pdx.cs410J.AbstractPhoneBill;
import edu.pdx.cs410J.AbstractPhoneCall;

import java.util.Collection;
import java.util.LinkedList;

public class PhoneBill extends AbstractPhoneBill {
    private final String customer;
    private LinkedList<AbstractPhoneCall> calls;

    PhoneBill(String customerName) {
        this.customer = customerName;
        this.calls = new LinkedList<>();
    }

    @Override
    public String getCustomer() {
        return this.customer;
    }

    @Override
    public void addPhoneCall(AbstractPhoneCall abstractPhoneCall) {
        this.calls.add(abstractPhoneCall);
    }

    @Override
    public Collection getPhoneCalls() {
        return this.calls;
    }
}
