package com.quicbit.json.next;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

//
// ParseState functions are for convenience and are NOT highly optimized like the Parser.  ParseState methods
// make working with raw buffers a bit simpler.  ParseState does not provide thorough checks on very
// large numbers etc.
//
public class ParseState {
    // values for ps.tok(en).  All but string and decimal are represented by the first ascii byte encountered
    static final int ARR = 91;        // [    array start
    static final int ARR_END = 93;    // ]    array end
    static final int DEC = 100;       // d    a decimal value starting with: -, 0, 1, ..., 9
    static final int FAL = 102;       // f    false
    static final int NUL = 110;       // n    null
    static final int STR = 115;       // s    a string value starting with "
    static final int TRU = 116;       // t    true
    static final int OBJ = 123;       // {    object start
    static final int OBJ_END = 125;   // }    object end

    byte[] src;
    byte[] next_src;
    List<Integer> stack = new ArrayList<>();
    int soff;
    int lim;
    int koff;
    int klim;
    int voff;
    int vlim;
    int tok;
    int pos = Parser.A_BF;
    int ecode;
    int vcount;
    int lineoff;
    int line = 1;

    public ParseState(byte[] src, int off, int lim) {
        this.src = src;
        koff = klim = voff = vlim = soff = off;
        this.lim = Math.min(lim, src.length);
    }

    //
    // ParseState is an optional object for holding parse state. Though only a simple plain
    // object is required, ParseState provides convenience for viewing keys and values.
    //
    public String key() {
        if (klim <= koff) {
            return null;
        }
        return new String(src, koff + 1, klim - koff - 2);
    }
    public String val() {
        if (vlim <= voff) {
            return null;
        }
        return new String (src, voff, vlim - voff);
    }
    public String sval() {
        if (vlim <= voff) {
            return null;
        }
        return new String (src, voff+1, vlim - voff - 2);
    }
    public int ival() {
        if (vlim <= voff) return 0;
        return Integer.parseInt(sval());
    }

    public boolean bval() {
        return tok == TRU;
    }

    public double dval() {
        if (vlim <= voff) return 0;
        return Double.parseDouble(sval());
    }

    static boolean aval(byte[] a, int aoff, int alim, byte[] b, int boff, int blim) {
        return arr_cmp(a, aoff, alim, b, boff, blim) == 0;
    }

    static int arr_cmp (byte[] a, int aoff, int alim, byte[] b, int boff, int blim) {
        var len_a = alim - aoff;
        var len_b = blim - boff;
        var lim = aoff + (len_a < len_b ? len_a : len_b);
        var adj = aoff - boff;
        while (aoff < lim) {
            if (a[aoff] != b[aoff - adj]) {
                return a[aoff] > b[aoff - adj] ? 1 : -1;
            }
            aoff++;
        }
        return len_a == len_b ? 0 : len_a > len_b ? 1 : -1;
    }

    public boolean key_equal (byte[] a, int off, int lim) {
        return this.key_cmp(a, off, lim) == 0;
    }
    public int key_cmp(byte[] a, int off, int lim) {
        return arr_cmp(this.src, this.koff + 1, this.klim - 1, a, off, lim);
    }
    public boolean val_equal (byte[] a, int off, int lim) {
        return this.val_cmp(a, off, lim) == 0;
    }
    public int val_cmp (byte[] a, int off, int lim) {
        return (this.tok == STR)
            ? arr_cmp(this.src, this.voff + 1, this.vlim - 1, a, off, lim)  // strip quotes
            : arr_cmp(this.src, this.voff, this.vlim, a, off, lim);
    }

    public String tokstr () {
        return tokstr(false);
    }

    public String tokstr (boolean detail) {
        String keystr = key();
        keystr = keystr == null ? "" : "k" + (klim - koff) + '@' + koff + ':';
        String vlen = (vlim == voff || (Parser.CMAP[tok] & Parser.NO_LEN_TOKENS) != 0) ? "" : (vlim - voff) + "";

        var tchar = tok == 0 ? "!" : Character.toString(tok);
        var ret = keystr + tchar + vlen + '@' + voff;
        if (ecode != 0) {
            ret += ':' + Character.toString(ecode);
        }
        if (detail) {
            ret += ':' + Parser.posname(pos);
            if (stack.size() > 0) {
                ret += ':' + stack.stream().map(Character::toString).collect(Collectors.joining(""));
            }
        }
        return ret;
    }

    public String toString () {
        JsonWriter jw = new JsonWriter();
        jw.obj();
        jw.key("tokstr").val(tokstr());
        if (klim > koff) {
            jw.key("key").val(key());
        }
        jw.key("val").val(val());
        jw.key("line").val(line);
        jw.key("col").val(this.soff + this.vlim - this.lineoff);
        jw.key("pos").val(pos);
        jw.objend();
        return jw.toString();
    }

}
