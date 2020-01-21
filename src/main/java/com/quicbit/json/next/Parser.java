// Software License Agreement (ISC License)
//
// Copyright (c) 2019, Matthew Voss
//
// Permission to use, copy, modify, and/or distribute this software for
// any purpose with or without fee is hereby granted, provided that the
// above copyright notice and this permission notice appear in all copies.
//
// THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
// WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
// MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
// ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
// WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
// ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
// OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.

package com.quicbit.json.next;

import java.util.List;

/**
 * This logic ported from the javascript version qb-json-next https://github.com/quicbit-js/qb-json-next/
 */
public class Parser {
    // values for ps.pos(ition).  LSB (0x7F) are reserved for token ascii value.
    static final int A_BF = 0x080;
    static final int A_BV = 0x100;   // in array, before value
    static final int A_AV = 0x180;   // in array, after value
    static final int O_BF = 0x200;   // in object, before first key
    static final int O_BK = 0x280;   // in object, before key
    static final int O_AK = 0x300;   // in object, after key
    static final int O_BV = 0x380;   // in object, before value
    static final int O_AV = 0x400;   // in object, after value

    static String posname (int pos) {
        switch (pos) {
            case A_BF: return "A_BF";
            case A_BV: return "A_BV";
            case A_AV: return "A_AV";
            case O_BF: return "O_BF";
            case O_BK: return "O_BK";
            case O_AK: return "O_AK";
            case O_BV: return "O_BV";
            case O_AV: return "O_AV";
            default: return "???";
        }
    }

    // ASCII flags
    static final int NON_TOKEN = 1;           // '\b\f\n\t\r ,:',
    static final int DELIM = 2;               // '\b\f\n\t\r ,:{}[]',
    static final int DECIMAL_END = 4;         // '0123456789',
    static final int DECIMAL_ASCII = 8;       // '-0123456789+.eE',
    static final int NO_LEN_TOKENS = 16;      // 'tfn[]{}()',

    //       0    1    2    3    4    5    6    7    8    9    A    B    C    D    E    F
    //    -----------------------------------------------------------------------------------
    // 0  |  NUL  SOH  STX  ETX  EOT  ENQ  ACK  BEL  BS   TAB  LF   VT   FF   CR   SO   SI  |  // 0
    // 1  |  DLE  DC1  DC2  DC3  DC4  NAK  SYN  ETB  CAN  EM   SUB  ESC  FS   GS   RS   US  |  // 1
    // 2  |  SPC  !    "    #    $    %    &    '    (    )    *    +    ,    -    .    /   |  // 2
    // 3  |  0    1    2    3    4    5    6    7    8    9    :    ;    <    =    >    ?   |  // 3
    // 4  |  @    A    B    C    D    E    F    G    H    I    J    K    L    M    N    O   |  // 4
    // 5  |  P    Q    R    S    T    U    V    W    X    Y    Z    [    \    ]    ^    _   |  // 5
    // 6  |  `    a    b    c    d    e    f    g    h    i    j    k    l    m    n    o   |  // 6
    // 7  |  p    q    r    s    t    u    v    w    x    y    z    {    |    }    ~        |  // 7
    //    -----------------------------------------------------------------------------------

    // Character map from ascii character to character type flags (above)
    // CMAP was lovingly crafted by util.js in the https://github.com/quicbit-js/qb-json-next/ package
    static final int[] CMAP = {
        //0     1     2     3     4     5     6     7     8     9     A     B     C     D     E     F
        0,    0,    0,    0,    0,    0,    0,    0,    0x03, 0x03, 0x03, 0,    0x03, 0x03, 0,    0,    // 0
        0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    // 1
        0x03, 0,    0,    0,    0,    0,    0,    0,    0x10, 0x10, 0,    0x08, 0x03, 0x08, 0x08, 0,    // 2
        0x0C, 0x0C, 0x0C, 0x0C, 0x0C, 0x0C, 0x0C, 0x0C, 0x0C, 0x0C, 0x03, 0,    0,    0,    0,    0,    // 3
        0,    0,    0,    0,    0,    0x08, 0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    // 4
        0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0x12, 0,    0x12, 0,    0,    // 5
        0,    0,    0,    0,    0,    0x08, 0x10, 0,    0,    0,    0,    0,    0,    0,    0x10, 0,    // 6
        0,    0,    0,    0,    0x10, 0,    0,    0,    0,    0,    0,    0x12, 0,    0x12, 0,    0,    // 7
        0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    // 8
        0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    // 9
        0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    // A
        0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    // B
        0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    // C
        0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    // D
        0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    // E
        0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,    // F
    };

    // for an unexpected or illegal value, or if src limit is reached before a value is complete, ps.tok will be zero
    // and ps.ecode will be one of the following:
    static final int BAD_VALUE = 66;
    static final int TRUNC_DEC = 68;
    static final int KEY_NO_VAL = 75;
    static final int TRUNCATED = 84;
    static final int UNEXPECTED = 85;

    // convert map of strings to array of arrays (of bytes)
    static final byte[] FALSE_BYTES = "alse".getBytes();
    static final byte[] TRUE_BYTES = "rue".getBytes();
    static final byte[] NULL_BYTES = "ull".getBytes();

    // return a state map from index = (position + ascii) to next valid position
    static int[] pos_map() {
        int[] pos_pairs = {
            // pos_pairs is generated by utils.js in https://github.com/quicbit-js/qb-json-next/
            219,128,221,384,228,384,230,384,238,384,243,384,244,384,251,512,
            347,128,356,384,358,384,366,384,371,384,372,384,379,512,428,256,
            477,384,627,768,637,384,755,768,826,896,987,128,996,1024,998,1024,
            1006,1024,1011,1024,1012,1024,1019,512,1068,640,1149,384,
        };
        int lim = 0x400 + 0xFF + 1;     // max pos + max ascii
        int[] ret = new int[lim];
        for (int i=0; i < lim; i++) {
            ret[i] = 0;
        }
        for (int i=0; i < pos_pairs.length; i+=2) {
            ret[pos_pairs[i]] = pos_pairs[i+1];
        }
        return ret;
    }

    static final int[] POS_MAP = pos_map();

    // skip as many bytes of src that match bsrc, up to lim.
    // return
    //     i    the new index after all bytes are matched (past matched bytes)
    //    -i    (negative) the index of the first unmatched byte (past matched bytes)
    static int skip_bytes (byte[] src, int off, int lim, byte[] bsrc) {
        var blen = bsrc.length;
        if (blen > lim - off) { blen = lim - off; }
        int i = 0;
        while (i < blen && bsrc[i] == src[i + off]) { i++; }       // reordered to (i < blen &&...)
        return i == bsrc.length ? i + off : -(i + off);
    }

    static int skip_str (byte[] src, int off, int lim) {
        int i = off;
        while (i < lim) {
            if (src[i] == 34) {
                if (src[i - 1] == 92) {
                    // count number of escapes going backwards (n = escape count +1)
                    int n = 2;
                    while (src[i - n] == 92 && i - n >= off) { n++; }          // \ BACKSLASH escape
                    if (n % 2 == 1) {
                        return i + 1;  // skip quote
                    }
                } else {
                    return i + 1;  // skip quote
                }
            }
            i++;
        }
        return -i;
    }

    static int skip_dec (byte[] src, int off, int lim) {
       while (off < lim && (CMAP[src[off]] & DECIMAL_ASCII) != 0) { off++; }
       return (off < lim && (CMAP[src[off]] & DELIM) != 0) ? off : -off;
    }


    // switch ps.src to ps.next_src if conditions are right (ps.src is null or is complete without errors)
    static boolean next_src (ParseState ps) {
       if (ps.ecode != 0 || (ps.src != null && ps.vlim < ps.lim)) {
          return false;
       }
       if (ps.next_src.length == 0) {
           ps.next_src = null;
           return false;
       }
       ps.soff += ps.src == null || ps.src.length == 0 ? 0 : ps.src.length;
       ps.src = ps.next_src;
       ps.next_src = null;
       ps.koff = ps.klim = ps.voff = ps.vlim = ps.tok = 0; // ps.ecode is zero
       ps.lim = ps.src.length;
       return true;
    }

    ParseState ps;
    Options opt;

    public Parser (byte[] src, int off, int lim) { this(src, off, lim, null); }
    public Parser (byte[] src, int off, int lim, Options opt) {
        this.ps = new ParseState(src, off, lim);
        this.opt = opt == null ? new Options() : opt;
    }

    public int next () {
        if (ps.ecode != 0) {                               // ecode is sticky (requires intentional fix)
            return ps.tok = 0;
        }
        ps.koff = ps.klim = ps.voff = ps.vlim;
        var pos1 = ps.pos;
        while (ps.vlim < ps.lim) {
            ps.voff = ps.vlim;
            ps.tok = ps.src[ps.vlim++];
            switch (ps.tok) {
                case 10:                                          // new-line
                    ps.lineoff = ps.soff + ps.vlim;
                    ps.line++;
                    continue;

                case 13:                                          // carriage return
                    ps.lineoff = ps.soff + ps.vlim;
                    continue;

                case 8: case 9: case 12: case 32:                 // other white-space
                    continue;

                case 44:                                          // ,    COMMA
                case 58:                                          // :    COLON
                    pos1 = POS_MAP[ps.pos | ps.tok];
                    if (pos1 == 0) { ps.voff = ps.vlim - 1; return handle_unexp(ps, opt); }
                    ps.pos = pos1;
                    continue;
                case 34:                                          // "    QUOTE
                    ps.tok = 115;                                  // s for string
                    ps.vlim = skip_str(ps.src, ps.vlim, ps.lim);
                    pos1 = POS_MAP[ps.pos | ps.tok];
                    if (pos1 == 0) return handle_unexp(ps, opt);
                    if (pos1 == O_AK) {
                        // key
                        ps.koff = ps.voff;
                        if (ps.vlim > 0) { ps.pos = pos1; ps.klim = ps.voff = ps.vlim; continue; } else { ps.klim = ps.voff = -ps.vlim; return handle_neg(ps, opt); }
                    } else {
                        // value
                        if (ps.vlim > 0) { ps.pos = pos1; ps.vcount++; return ps.tok; } else return handle_neg(ps, opt);
                    }
                case 102:                                         // f    false
                    ps.vlim = skip_bytes(ps.src, ps.vlim, ps.lim, FALSE_BYTES);
                    pos1 = POS_MAP[ps.pos | ps.tok];
                    if (pos1 == 0) return handle_unexp(ps, opt);
                    if (ps.vlim > 0) { ps.pos = pos1; ps.vcount++; return ps.tok; } else return handle_neg(ps, opt);
                case 110:                                         // n    null
                    ps.vlim = skip_bytes(ps.src, ps.vlim, ps.lim, NULL_BYTES);
                    pos1 = POS_MAP[ps.pos | ps.tok];
                    if (pos1 == 0) return handle_unexp(ps, opt);
                    if (ps.vlim > 0) { ps.pos = pos1; ps.vcount++; return ps.tok; } else return handle_neg(ps, opt);
                case 116:                                         // t    true
                    ps.vlim = skip_bytes(ps.src, ps.vlim, ps.lim, TRUE_BYTES);
                    pos1 = POS_MAP[ps.pos | ps.tok];
                    if (pos1 == 0) return handle_unexp(ps, opt);
                    if (ps.vlim > 0) { ps.pos = pos1; ps.vcount++; return ps.tok; } else return handle_neg(ps, opt);

                case 48:case 49:case 50:case 51:case 52:          // 0-4    digits
                case 53:case 54:case 55:case 56:case 57:          // 5-9    digits
                case 45:                                          // '-'    ('+' is not legal here)
                    ps.tok = 100;                                    // d for decimal
                    ps.vlim = skip_dec(ps.src, ps.vlim, ps.lim);
                    pos1 = POS_MAP[ps.pos | ps.tok];
                    if (pos1 == 0) return handle_unexp(ps, opt);
                    if (ps.vlim > 0) { ps.pos = pos1; ps.vcount++; return ps.tok; } else return handle_neg(ps, opt);

                case 91:                                          // [    ARRAY START
                case 123:                                         // {    OBJECT START
                    pos1 = POS_MAP[ps.pos | ps.tok];
                    if (pos1 == 0) return handle_unexp(ps, opt);
                    ps.pos = pos1;
                    ps.stack.add(ps.tok);
                    return ps.tok;

                case 93:                                          // ]    ARRAY END
                case 125:                                         // }    OBJECT END
                    if (POS_MAP[ps.pos | ps.tok] == 0) return handle_unexp(ps, opt);
                    pop(ps.stack);
                    ps.pos = last(ps.stack) == 123 ? O_AV : A_AV;
                    ps.vcount++; return ps.tok;

                default:
                    --ps.vlim;
                    ps.ecode = BAD_VALUE;
                    return end_src(ps, opt);
            }
        }

        // reached src limit without error or truncation
        if ((CMAP[ps.tok] & NON_TOKEN) != 0) {
            ps.voff = ps.vlim;
        }
        return end_src(ps, opt);
    }

    static <T> T pop (List<T> list) { int len = list.size(); return len == 0 ? null : list.remove(len - 1); }
    static <T> T last (List<T> list) { int len = list.size(); return len == 0 ? null : list.get(len - 1); }

    int handle_unexp (ParseState ps, Options opt) {
        if (ps.vlim < 0) { ps.vlim = -ps.vlim; }
        ps.ecode = UNEXPECTED;
        return end_src(ps, opt);
    }

    int handle_neg (ParseState ps, Options opt) {
        ps.vlim = -ps.vlim;
        if (ps.vlim >= ps.lim) {
            ps.ecode =
                ps.tok == ParseState.DEC && (CMAP[ps.src[ps.vlim - 1]] & DECIMAL_END) != 0
                    ? TRUNC_DEC
                    : TRUNCATED;
        } else {
            ps.ecode = BAD_VALUE;
            ps.vlim++;
        }
        return end_src(ps, opt);
    }

    int end_src (ParseState ps, Options opt) {
        switch (ps.ecode) {
            case 0:
                if (ps.pos == O_AK || ps.pos == O_BV) {
                    ps.ecode = KEY_NO_VAL;
                } else {
                    if (ps.next_src != null && next_src(ps)) { return next(); }
                }
                break;
            case BAD_VALUE: case UNEXPECTED:
                ps.tok = 0;
                if (opt != null && opt.ehandler != null) {
                opt.ehandler.err(ps);
                return ps.tok;
            } else {
                checke();  // throws error
            }
            // any other ecode is just sticky (prevents progress)
        }
        return ps.tok = 0;
    }

    void err (String msg) {
        String ctx = "(line " + (ps.line + 1) + ", col " + (ps.soff + ps.voff - ps.lineoff) + ", tokstr " + ps.tokstr(true) + ")";
        throw new ParseException(ps, msg + ": " + ctx);
    }

    void checke () {
        if (ps.ecode == UNEXPECTED) err("unexpected token at " + ps.voff + ".." + ps.vlim);
        if (ps.ecode == BAD_VALUE) err("bad value at " + ps.voff + ".." + ps.vlim);
    }

    interface ErrHandler {
        void err(ParseState ps);
    }
    static class Options {
        ErrHandler ehandler;
    }
}
