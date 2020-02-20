package com.quicbit.json;

public class TokenizerException extends RuntimeException {
    TokenizerState ps;
    TokenizerException(TokenizerState ps, String msg) {
        super(msg);
        this.ps = ps;
    }
}
