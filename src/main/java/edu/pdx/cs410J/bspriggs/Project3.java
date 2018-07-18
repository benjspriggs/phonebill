package edu.pdx.cs410J.bspriggs;

import java.text.ParseException;

public class Project3 extends Project2 {
    public static PhoneCall parsePhoneCallFromArguments(String[] args) throws ParseException {
        int ptr = 0;

        PhoneCall call = new PhoneCall(args[ptr++], args[ptr++],
                String.format("%s %s", args[ptr++], args[ptr++]),
                String.format("%s %s", args[ptr++], args[ptr]));

        return call;
    }
    public static void main(String[] args) {
        System.exit(1);
    }
}
