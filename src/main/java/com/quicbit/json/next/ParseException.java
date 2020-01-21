package com.quicbit.json.next;

public class ParseException extends RuntimeException {
    ParseState ps;
    ParseException (ParseState ps, String msg) {
        super(msg);
        this.ps = ps;
    }
}
