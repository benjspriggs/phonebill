package edu.pdx.cs410J.bspriggs;

import edu.pdx.cs410J.AbstractPhoneBill;

import java.io.DataOutputStream;

public class TextDumper implements edu.pdx.cs410J.PhoneBillDumper {
    private final String fileName;

    public TextDumper(String fileName) {
        this.fileName = fileName;
    }

    public TextDumper() {
        this.fileName = "";
    }

    public void dumpTo(AbstractPhoneBill phoneBill, DataOutputStream dataOutputStream) {

    }

    @Override
    public void dump(AbstractPhoneBill abstractPhoneBill) {

    }
}
