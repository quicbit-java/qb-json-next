package com.quicbit.json.next;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.quicbit.json.next.TestKit.*;

public class ParserTest {
    @Test
    public void testWithLimit() {
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
        ).test("with limit",
            (r) -> srctokens(r.str("src"), r.ival("lim"))
        );
    }

    @Test
    public void testVarious () {
        table(
            a("src", "exp"),
            a("", "!@0:A_BF"),
            a("1", "!1@0:D:A_BF"),
            a("1,2,3", "d1@0,d1@2,!1@4:D:A_BV"),
            a("[1, 2], 3", "[@0,d1@1,d1@4,]@5,!1@8:D:A_BV"),
            a("\"x\"", "s3@0,!@3:A_AV"),
            a("-3.05", "!5@0:D:A_BF"),
            a("\b  true", "t@3,!@7:A_AV"),
            a("  true", "t@2,!@6:A_AV"),
            a("false", "f@0,!@5:A_AV"),
            a(" false  ", "f@1,!@8:A_AV"),
            a(" false   ", "f@1,!@9:A_AV"),
            a("[1, 2, 3]", "[@0,d1@1,d1@4,d1@7,]@8,!@9:A_AV"),
            a("[3.05E-2]", "[@0,d7@1,]@8,!@9:A_AV"),
            a("[3.05E-2]", "[@0,d7@1,]@8,!@9:A_AV"),
            a("{\"a\":1}", "{@0,k3@1:d1@5,}@6,!@7:A_AV"),
            a("{\"a\":1,\"b\":{}}", "{@0,k3@1:d1@5,k3@7:{@11,}@12,}@13,!@14:A_AV"),
            a("{\"a\"  :1}", "{@0,k3@1:d1@7,}@8,!@9:A_AV"),
            a("{ \"a\" : 1 }", "{@0,k3@2:d1@8,}@10,!@11:A_AV"),
            a("\"\\\"\"", "s4@0,!@4:A_AV"),
            a("\"\\\\\"", "s4@0,!@4:A_AV"),
            a("\t\t\"x\\a\r\"  ", "s6@2,!@10:A_AV"),
            a("\"\\\"x\\\"a\r\\\"\"", "s11@0,!@11:A_AV"),
            a(" [0,1,2]", "[@1,d1@2,d1@4,d1@6,]@7,!@8:A_AV"),
            a("[\"a\", \"bb\"] ", "[@0,s3@1,s4@6,]@10,!@12:A_AV"),
            a("\"x\", 4\n, null, 3.2e5 , true, false", "s3@0,d1@5,n@9,d5@15,t@23,f@29,!@34:A_AV"),
            a("[\"a\",1.3,\n\t{ \"b\" : [\"v\", \"w\"]\n}\t\n ]", "[@0,s3@1,d3@5,{@11,k3@13:[@19,s3@20,s3@25,]@28,}@30,]@34,!@35:A_AV")
        ).test("various",
            (r) -> srctokens(r.str("src"))
        );
    };

    @Test
    public void testLineAndLineOff () {
        table(
            a( "src",                              "next_src",  "exp" ),
            a( "12,",                              "13",        a( 1, 6 ) ),
            a( "12,13",                            "",          a( 1, 6 ) ),
            a( "\n\n12,",                          "13",        a( 3, 6 ) ),
            a( "\n",                               "\n12,13",   a( 3, 6 ) ),
            a( "\n\r",                             "\n\r12,13", a( 3, 6 ) ),
            a( "\n\n12,13",                        "",          a( 3, 6 ) ),
            a( "\n\r\n\r12,13",                    "",          a( 3, 6 ) ),
            a( "\n\r\n",                           "\r12,13",   a( 3, 6 ) ),
            a( "\n12,\n13\n",                      "",          a( 4, 1 ) ),
            a( " \n\n12,13\n",                     "",          a( 4, 1 ) ),
            a( "12,\n13",                          "",          a( 2, 3 ) ),
            a( "\n12,",                            "13",        a( 2, 6 ) ),
            a( "\n12,13",                          "",          a( 2, 6 ) ),
            a( "\n\r\n\r",                         "12,13",     a( 3, 6 ) ),
            a( "\n\r\n\r12,",                      "13",        a( 3, 6 ) ),
            a( "{\"a\": 45, \"b\": true}",         "",          a( 1, 21 ) ),
            a( "\n{\"a\": 45, \"b\": true}",       "",          a( 2, 21 ) ),
            a( "{\"a\":\n 45, \"b\": true}",       "",          a( 2, 16 ) ),
            a( "{\"a\": 45, \"b\":\n true}",       "",          a( 2, 7 ) ),
            a( "\n{\"a\": 45, \"b\":\n true}",     "",          a( 3, 7 ) ),
            a( "\n\n{\"a\":\n 45, \"b\":\n true}", "",          a( 5, 7 ) )
        ).test("line and line off",
            (r) -> {
                byte[] src = r.str("src").getBytes();
                Parser p = new Parser(src);
                do { p.next(); } while (p.ps.tok != 0);
                p.ps.next_src = r.str("next_src").getBytes();
                do { p.next(); } while (p.ps.tok != 0);
                int[] ret = new int[2];

                ret[0] = p.ps.line;
                ret[1] = p.ps.soff + p.ps.vlim - p.ps.lineoff + 1;
                return ret;
            }
        );
    };

    @Test
    public void testObjectNoSpaces () {
        table(
            a( "src",                "exp" ),
            a( "",                   "!@0:A_BF" ),
            a( "{",                  "{@0,!@1:O_BF:{" ),
            a( "{\"",                "{@0,k1@1:!@2:T:O_BF:{" ),
            a( "{\"a",               "{@0,k2@1:!@3:T:O_BF:{" ),
            a( "{\"a\"",             "{@0,k3@1:!@4:K:O_AK:{" ),
            a( "{\"a\":",            "{@0,k3@1:!@5:K:O_BV:{" ),
            a( "{\"a\":7",           "{@0,k3@1:!1@5:D:O_BV:{" ),
            a( "{\"a\":71",          "{@0,k3@1:!2@5:D:O_BV:{" ),
            a( "{\"a\":71,",         "{@0,k3@1:d2@5,!@8:O_BK:{" ),
            a( "{\"a\":71,\"",       "{@0,k3@1:d2@5,k1@8:!@9:T:O_BK:{" ),
            a( "{\"a\":71,\"b",      "{@0,k3@1:d2@5,k2@8:!@10:T:O_BK:{" ),
            a( "{\"a\":71,\"b\"",    "{@0,k3@1:d2@5,k3@8:!@11:K:O_AK:{" ),
            a( "{\"a\":71,\"b\":",   "{@0,k3@1:d2@5,k3@8:!@12:K:O_BV:{" ),
            a( "{\"a\":71,\"b\":2",  "{@0,k3@1:d2@5,k3@8:!1@12:D:O_BV:{" ),
            a( "{\"a\":71,\"b\":2}", "{@0,k3@1:d2@5,k3@8:d1@12,}@13,!@14:A_AV" )
        ).test("object - no spaces",
            (r) -> srctokens(r.str("src"))
        );
    };

    @Test
    public void testArrayNoSpaces () {
        table(
            a( "src",          "exp" ),
            a( "",             "!@0:A_BF" ),
            a( "[",            "[@0,!@1:A_BF:[" ),
            a( "[8",           "[@0,!1@1:D:A_BF:[" ),
            a( "[83",          "[@0,!2@1:D:A_BF:[" ),
            a( "[83 ",         "[@0,d2@1,!@4:A_AV:[" ),
            a( "[83,",         "[@0,d2@1,!@4:A_BV:[" ),
            a( "[83,\"",       "[@0,d2@1,!1@4:T:A_BV:[" ),
            a( "[83,\"a",      "[@0,d2@1,!2@4:T:A_BV:[" ),
            a( "[83,\"a\"",    "[@0,d2@1,s3@4,!@7:A_AV:[" ),
            a( "[83,\"a\",",   "[@0,d2@1,s3@4,!@8:A_BV:[" ),
            a( "[83,\"a\",2",  "[@0,d2@1,s3@4,!1@8:D:A_BV:[" ),
            a( "[83,\"a\",2]", "[@0,d2@1,s3@4,d1@8,]@9,!@10:A_AV" )
        ).test("array - no spaces",
            (r) -> srctokens(r.str("src"))
        );
    };

    @Test
    public void testArrayWithSpaces () {
        table(
            a( "src",               "exp" ),
            a( "",                  "!@0:A_BF" ),
            a( "[",                 "[@0,!@1:A_BF:[" ),
            a( "[ ",                "[@0,!@2:A_BF:[" ),
            a( "[ 8",               "[@0,!1@2:D:A_BF:[" ),
            a( "[ 83",              "[@0,!2@2:D:A_BF:[" ),
            a( "[ 83,",             "[@0,d2@2,!@5:A_BV:[" ),
            a( "[ 83, ",            "[@0,d2@2,!@6:A_BV:[" ),
            a( "[ 83, \"",          "[@0,d2@2,!1@6:T:A_BV:[" ),
            a( "[ 83, \"a",         "[@0,d2@2,!2@6:T:A_BV:[" ),
            a( "[ 83, \"a\"",       "[@0,d2@2,s3@6,!@9:A_AV:[" ),
            a( "[ 83, \"a\" ",      "[@0,d2@2,s3@6,!@10:A_AV:[" ),
            a( "[ 83, \"a\" ,",     "[@0,d2@2,s3@6,!@11:A_BV:[" ),
            a( "[ 83, \"a\" , ",    "[@0,d2@2,s3@6,!@12:A_BV:[" ),
            a( "[ 83, \"a\" , 2",   "[@0,d2@2,s3@6,!1@12:D:A_BV:[" ),
            a( "[ 83, \"a\" , 2 ",  "[@0,d2@2,s3@6,d1@12,!@14:A_AV:[" ),
            a( "[ 83, \"a\" , 2 ]", "[@0,d2@2,s3@6,d1@12,]@14,!@15:A_AV" )
        ).test("array - with spaces",
            (r) -> srctokens(r.str("src"))
        );
    };

    @Test
    public void testObjectWithSpaces () {
        table(
            a( "src",                "exp" ),
            a( " ",                  "!@1:A_BF" ),
            a( " {",                 "{@1,!@2:O_BF:{" ),
            a( " { ",                "{@1,!@3:O_BF:{" ),
            a( " { \"",              "{@1,k1@3:!@4:T:O_BF:{" ),
            a( " { \"a",             "{@1,k2@3:!@5:T:O_BF:{" ),
            a( " { \"a\"",           "{@1,k3@3:!@6:K:O_AK:{" ),
            a( " { \"a\":",          "{@1,k3@3:!@7:K:O_BV:{" ),
            a( " { \"a\": ",         "{@1,k3@3:!@8:K:O_BV:{" ),
            a( " { \"a\": \"",       "{@1,k3@3:!1@8:T:O_BV:{" ),
            a( " { \"a\": \"x",      "{@1,k3@3:!2@8:T:O_BV:{" ),
            a( " { \"a\": \"x\"",    "{@1,k3@3:s3@8,!@11:O_AV:{" ),
            a( " { \"a\": \"x\" }",  "{@1,k3@3:s3@8,}@12,!@13:A_AV" ),
            a( " { \"a\" ",          "{@1,k3@3:!@7:K:O_AK:{" ),
            a( " { \"a\" :",         "{@1,k3@3:!@8:K:O_BV:{" ),
            a( " { \"a\" : ",        "{@1,k3@3:!@9:K:O_BV:{" ),
            a( " { \"a\" : \"",      "{@1,k3@3:!1@9:T:O_BV:{" ),
            a( " { \"a\" : \"x",     "{@1,k3@3:!2@9:T:O_BV:{" ),
            a( " { \"a\" : \"x\" ",  "{@1,k3@3:s3@9,!@13:O_AV:{" ),
            a( " { \"a\" : \"x\" }", "{@1,k3@3:s3@9,}@13,!@14:A_AV" )        ).test("object - with spaces",
            (r) -> srctokens(r.str("src"))
        );
    };

    @Test
    public void testIncrementalArray () {
        table(
            a( "src1",                 "src2",                 "exp" ),
            a( "",                     "1,[[[7,89.4],\"c\"]]", a( "!@0:A_BF", "d1@0,[@2,[@3,[@4,d1@5,d4@7,]@11,s3@13,]@16,]@17,!@18:A_AV" ) ),
            a( "1,",                   "[[[7,89.4],\"c\"]]",   a( "d1@0,!@2:A_BV", "[@0,[@1,[@2,d1@3,d4@5,]@9,s3@11,]@14,]@15,!@16:A_AV" ) ),
            a( "1,[",                  "[[7,89.4],\"c\"]]",    a( "d1@0,[@2,!@3:A_BF:[", "[@0,[@1,d1@2,d4@4,]@8,s3@10,]@13,]@14,!@15:A_AV" ) ),
            a( "1,[[",                 "[7,89.4],\"c\"]]",     a( "d1@0,[@2,[@3,!@4:A_BF:[[", "[@0,d1@1,d4@3,]@7,s3@9,]@12,]@13,!@14:A_AV" ) ),
            a( "1,[[[",                "7,89.4],\"c\"]]",      a( "d1@0,[@2,[@3,[@4,!@5:A_BF:[[[", "d1@0,d4@2,]@6,s3@8,]@11,]@12,!@13:A_AV" ) ),
            a( "1,[[[7,",              "89.4],\"c\"]]",        a( "d1@0,[@2,[@3,[@4,d1@5,!@7:A_BV:[[[", "d4@0,]@4,s3@6,]@9,]@10,!@11:A_AV" ) ),
            a( "1,[[[7,89.4]",         ",\"c\"]]",             a( "d1@0,[@2,[@3,[@4,d1@5,d4@7,]@11,!@12:A_AV:[[", "s3@1,]@4,]@5,!@6:A_AV" ) ),
            a( "1,[[[7,89.4],",        "\"c\"]]",              a( "d1@0,[@2,[@3,[@4,d1@5,d4@7,]@11,!@13:A_BV:[[", "s3@0,]@3,]@4,!@5:A_AV" ) ),
            a( "1,[[[7,89.4],\"c\"",   "]]",                   a( "d1@0,[@2,[@3,[@4,d1@5,d4@7,]@11,s3@13,!@16:A_AV:[[", "]@0,]@1,!@2:A_AV" ) ),
            a( "1,[[[7,89.4],\"c\"]",  "]",                    a( "d1@0,[@2,[@3,[@4,d1@5,d4@7,]@11,s3@13,]@16,!@17:A_AV:[", "]@0,!@1:A_AV" ) ),
            a( "1,[[[7,89.4],\"c\"]]", "",                     a( "d1@0,[@2,[@3,[@4,d1@5,d4@7,]@11,s3@13,]@16,]@17,!@18:A_AV", "!@18:A_AV" ) )
        ).test("incremental array",
            (r) -> parse_split(r.str("src1"), r.str("src2"))
        );
    };

    static String[] parse_split (String src1, String src2) {
        String[] ret = new String[2];
        var p = new Parser(src1.getBytes());
        ret[0] = srctokens(p);
        p.ps.next_src = src2.getBytes();
        ret[1] = srctokens(p);
        return ret;
    }

    static String srctokens (String src) {
        return srctokens(new Parser(src.getBytes(), 0, src.getBytes().length));
    }
    static String srctokens (String src, int lim) {
        return srctokens(new Parser(src.getBytes(), 0, lim));
    }
    static String srctokens (Parser p) {
        p.opt.ehandler = (e) -> {};
        List<String> toks = new ArrayList<>();
        do {
            p.next();
//            System.out.println(p.ps.toString());
            toks.add(p.ps.tokstr(p.ps.tok == 0));     // more detail for end token
        } while(p.ps.tok != 0);
        String lasttok = toks.get(toks.size() - 1);
        if (!p.ps.tokstr(true).equals(lasttok)) {
            throw new RuntimeException("inconsistent last token: " + toks.get(toks.size()-1));
        }
        return String.join(",", toks);
    }
}
