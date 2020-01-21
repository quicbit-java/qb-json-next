package com.quicbit.json.next;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.quicbit.json.next.TestKit.*;

public class ParserTest {
    @Test
    public void testRegex() {
        table(
            a( "src",                                    "lim", "exp" ),
            a( "\"x\", 4\n, null, 3.2e5 , true, false,", 0,     "!@0:A_BF" ),
            a( "\"x\", 4\n, null, 3.2e5 , true, false,", 1,     "!1@0:T:A_BF" ),
            a( "\"x\", 4\n, null, 3.2e5 , true, false,", 2,     "!2@0:T:A_BF" ),
            a( "\"x\", 4\n, null, 3.2e5 , true, false,", 3,     "s3@0,!@3:A_AV" ),
            a( "\"x\", 4\n, null, 3.2e5 , true, false,", 4,     "s3@0,!@4:A_BV" ),
            a( "\"x\", 4\n, null, 3.2e5 , true, false,", 5,     "s3@0,!@5:A_BV" ),
            a( "\"x\", 4\n, null, 3.2e5 , true, false,", 6,     "s3@0,!1@5:D:A_BV" ),
            a( "\"x\", 4\n, null, 3.2e5 , true, false,", 7,     "s3@0,d1@5,!@7:A_AV" ),
            a( "\"x\", 4\n, null, 3.2e5 , true, false,", 8,     "s3@0,d1@5,!@8:A_BV" ),
            a( "\"x\", 4\n, null, 3.2e5 , true, false,", 9,     "s3@0,d1@5,!@9:A_BV" ),
            a( "\"x\", 4\n, null, 3.2e5 , true, false,", 10,    "s3@0,d1@5,!1@9:T:A_BV" ),
            a( "\"x\", 4\n, null, 3.2e5 , true, false,", 15,    "s3@0,d1@5,n@9,!@15:A_BV" ),
            a( "\"x\", 4\n, null, 3.2e5 , true, false,", 20,    "s3@0,d1@5,n@9,!5@15:D:A_BV" ),
            a( "\"x\", 4\n, null, 3.2e5 , true, false,", 25,    "s3@0,d1@5,n@9,d5@15,!2@23:T:A_BV" ),
            a( "\"x\", 4\n, null, 3.2e5 , true, false,", 30,    "s3@0,d1@5,n@9,d5@15,t@23,!1@29:T:A_BV" ),
            a( "\"x\", 4\n, null, 3.2e5 , true, false,", 35,    "s3@0,d1@5,n@9,d5@15,t@23,f@29,!@35:A_BV" ),
            a( "\"x\", 4\n, null, 3.2e5 , true, false,!", 50,    "s3@0,d1@5,n@9,d5@15,t@23,f@29,!@35:B:A_BV" )
        ).test("regex(s)",
            (r) -> {
                Parser.Options opt = new Parser.Options();
                opt.ehandler = (e) -> {};
                Parser p = new Parser(r.str("src").getBytes(), 0, r.ival("lim"), opt);
                List<String> toks = new ArrayList<>();
                do {
                    p.next();
                    toks.add(p.ps.tokstr(p.ps.tok == 0));     // more detail for end token
                } while(p.ps.tok != 0);
                String lasttok = toks.get(toks.size() - 1);
                if (!p.ps.tokstr(true).equals(lasttok)) {
                    throw new RuntimeException("inconsistent last token: " + toks.get(toks.size()-1));
                }
                return String.join(",", toks);
            }
        );
    }
}
