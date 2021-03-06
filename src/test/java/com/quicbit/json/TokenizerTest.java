package com.quicbit.json;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.quicbit.testkit.TestKit.*;

public class TokenizerTest {
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
            (r) -> srctokens(parser(r.str("src"), 0, r.ival("lim")))
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
            (r) -> srctokens(parser(r.str("src")))
        );
    }

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
                Tokenizer p = parser(r.str("src"));
                do { p.next(); } while (p.ps.tok != 0);
                p.ps.next_src = r.str("next_src").getBytes();
                do { p.next(); } while (p.ps.tok != 0);
                int[] ret = new int[2];

                ret[0] = p.ps.line;
                ret[1] = p.ps.soff + p.ps.vlim - p.ps.lineoff + 1;
                return ret;
            }
        );
    }

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
            (r) -> srctokens(parser(r.str("src")))
        );
    }

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
            (r) -> srctokens(parser(r.str("src")))
        );
    }

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
            (r) -> srctokens(parser(r.str("src")))
        );
    }

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
            (r) -> srctokens(parser(r.str("src")))
        );
    }

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
    }

    @Test
    public void testIncrementalArraySpaces () {
        table(
            a( "src1",                          "src2",                          "exp" ),
            a( "",                              " 1 , [ [ [7,89.4], \"c\" ] ] ", a( "!@0:A_BF", "d1@1,[@5,[@7,[@9,d1@10,d4@12,]@16,s3@19,]@23,]@25,!@27:A_AV" ) ),
            a( " ",                             "1 , [ [ [7,89.4], \"c\" ] ] ",  a( "!@1:A_BF", "d1@0,[@4,[@6,[@8,d1@9,d4@11,]@15,s3@18,]@22,]@24,!@26:A_AV" ) ),
            a( " 1 ",                           ", [ [ [7,89.4], \"c\" ] ] ",    a( "d1@1,!@3:A_AV", "[@2,[@4,[@6,d1@7,d4@9,]@13,s3@16,]@20,]@22,!@24:A_AV" ) ),
            a( " 1 ,",                          " [ [ [7,89.4], \"c\" ] ] ",     a( "d1@1,!@4:A_BV", "[@1,[@3,[@5,d1@6,d4@8,]@12,s3@15,]@19,]@21,!@23:A_AV" ) ),
            a( " 1 , ",                         "[ [ [7,89.4], \"c\" ] ] ",      a( "d1@1,!@5:A_BV", "[@0,[@2,[@4,d1@5,d4@7,]@11,s3@14,]@18,]@20,!@22:A_AV" ) ),
            a( " 1 , [",                        " [ [7,89.4], \"c\" ] ] ",       a( "d1@1,[@5,!@6:A_BF:[", "[@1,[@3,d1@4,d4@6,]@10,s3@13,]@17,]@19,!@21:A_AV" ) ),
            a( " 1 , [ ",                       "[ [7,89.4], \"c\" ] ] ",        a( "d1@1,[@5,!@7:A_BF:[", "[@0,[@2,d1@3,d4@5,]@9,s3@12,]@16,]@18,!@20:A_AV" ) ),
            a( " 1 , [ [",                      " [7,89.4], \"c\" ] ] ",         a( "d1@1,[@5,[@7,!@8:A_BF:[[", "[@1,d1@2,d4@4,]@8,s3@11,]@15,]@17,!@19:A_AV" ) ),
            a( " 1 , [ [ ",                     "[7,89.4], \"c\" ] ] ",          a( "d1@1,[@5,[@7,!@9:A_BF:[[", "[@0,d1@1,d4@3,]@7,s3@10,]@14,]@16,!@18:A_AV" ) ),
            a( " 1 , [ [ [",                    "7,89.4], \"c\" ] ] ",           a( "d1@1,[@5,[@7,[@9,!@10:A_BF:[[[", "d1@0,d4@2,]@6,s3@9,]@13,]@15,!@17:A_AV" ) ),
            a( " 1 , [ [ [7,",                  "89.4], \"c\" ] ] ",             a( "d1@1,[@5,[@7,[@9,d1@10,!@12:A_BV:[[[", "d4@0,]@4,s3@7,]@11,]@13,!@15:A_AV" ) ),
            a( " 1 , [ [ [7,89.4]",             ", \"c\" ] ] ",                  a( "d1@1,[@5,[@7,[@9,d1@10,d4@12,]@16,!@17:A_AV:[[", "s3@2,]@6,]@8,!@10:A_AV" ) ),
            a( " 1 , [ [ [7,89.4],",            " \"c\" ] ] ",                   a( "d1@1,[@5,[@7,[@9,d1@10,d4@12,]@16,!@18:A_BV:[[", "s3@1,]@5,]@7,!@9:A_AV" ) ),
            a( " 1 , [ [ [7,89.4], ",           "\"c\" ] ] ",                    a( "d1@1,[@5,[@7,[@9,d1@10,d4@12,]@16,!@19:A_BV:[[", "s3@0,]@4,]@6,!@8:A_AV" ) ),
            a( " 1 , [ [ [7,89.4], \"c\"",      " ] ] ",                         a( "d1@1,[@5,[@7,[@9,d1@10,d4@12,]@16,s3@19,!@22:A_AV:[[", "]@1,]@3,!@5:A_AV" ) ),
            a( " 1 , [ [ [7,89.4], \"c\" ",     "] ] ",                          a( "d1@1,[@5,[@7,[@9,d1@10,d4@12,]@16,s3@19,!@23:A_AV:[[", "]@0,]@2,!@4:A_AV" ) ),
            a( " 1 , [ [ [7,89.4], \"c\" ]",    " ] ",                           a( "d1@1,[@5,[@7,[@9,d1@10,d4@12,]@16,s3@19,]@23,!@24:A_AV:[", "]@1,!@3:A_AV" ) ),
            a( " 1 , [ [ [7,89.4], \"c\" ] ",   "] ",                            a( "d1@1,[@5,[@7,[@9,d1@10,d4@12,]@16,s3@19,]@23,!@25:A_AV:[", "]@0,!@2:A_AV" ) ),
            a( " 1 , [ [ [7,89.4], \"c\" ] ]",  " ",                             a( "d1@1,[@5,[@7,[@9,d1@10,d4@12,]@16,s3@19,]@23,]@25,!@26:A_AV", "!@1:A_AV" ) ),
            a( " 1 , [ [ [7,89.4], \"c\" ] ] ", "",                              a( "d1@1,[@5,[@7,[@9,d1@10,d4@12,]@16,s3@19,]@23,]@25,!@27:A_AV", "!@27:A_AV" ) )
        ).test("incremental array - spaces",
            (r) -> parse_split(r.str("src1"), r.str("src2"))
        );
    }

    @Test
    public void testIncrementalObject () {
        table(
            a( "src1",                        "src2",                        "exp" ),
            a( "",                            "1,{\"a\":\"one\",\"b\":[2]}", a( "!@0:A_BF", "d1@0,{@2,k3@3:s5@7,k3@13:[@17,d1@18,]@19,}@20,!@21:A_AV" ) ),
            a( "1,",                          "{\"a\":\"one\",\"b\":[2]}",   a( "d1@0,!@2:A_BV", "{@0,k3@1:s5@5,k3@11:[@15,d1@16,]@17,}@18,!@19:A_AV" ) ),
            a( "1,{",                         "\"a\":\"one\",\"b\":[2]}",    a( "d1@0,{@2,!@3:O_BF:{", "k3@0:s5@4,k3@10:[@14,d1@15,]@16,}@17,!@18:A_AV" ) ),
            a( "1,{\"a\":\"one\"",            ",\"b\":[2]}",                 a( "d1@0,{@2,k3@3:s5@7,!@12:O_AV:{", "k3@1:[@5,d1@6,]@7,}@8,!@9:A_AV" ) ),
            a( "1,{\"a\":\"one\",",           "\"b\":[2]}",                  a( "d1@0,{@2,k3@3:s5@7,!@13:O_BK:{", "k3@0:[@4,d1@5,]@6,}@7,!@8:A_AV" ) ),
            a( "1,{\"a\":\"one\",\"b\":[2]",  "}",                           a( "d1@0,{@2,k3@3:s5@7,k3@13:[@17,d1@18,]@19,!@20:O_AV:{", "}@0,!@1:A_AV" ) ),
            a( "1,{\"a\":\"one\",\"b\":[2]}", "",                            a( "d1@0,{@2,k3@3:s5@7,k3@13:[@17,d1@18,]@19,}@20,!@21:A_AV", "!@21:A_AV" ) )
        ).test("incremental object",
            (r) -> parse_split(r.str("src1"), r.str("src2"))
        );
    }

    @Test
    public void testIncomplete () {
        table(
            a( "src",         "exp" ),
            a( "1, 2,",       "d1@0,d1@3,!@5:A_BV" ),
            a( "[1, 2, ",     "[@0,d1@1,d1@4,!@7:A_BV:[" ),
            a( "fal",         "!3@0:T:A_BF" ),
            a( "\"ab",        "!3@0:T:A_BF" ),
            a( "{\"ab\":",    "{@0,k4@1:!@6:K:O_BV:{" ),
            a( "\"\\\\\\\"",  "!5@0:T:A_BF" ),
            a( "[3.05E-2",    "[@0,!7@1:D:A_BF:[" ),
            a( "[3.05E-2,4.", "[@0,d7@1,!2@9:T:A_BV:[" ),
            a( "{\"a",        "{@0,k2@1:!@3:T:O_BF:{" ),
            a( "{\"a\": ",    "{@0,k3@1:!@6:K:O_BV:{" )
        ).test("incremental object - spaces",
            (r) -> srctokens(parser(r.str("src")))
        );
    }

    @Test
    public void testBadValue () {
        table(
            a( "src",          "exp" ),
            a( "{\"a\"q",      "{@0,k3@1:!@4:B:O_AK:{" ),
            a( "{\"a\":q",     "{@0,k3@1:!@5:B:O_BV:{" ),
            a( "{\"a\": q",    "{@0,k3@1:!@6:B:O_BV:{" ),
            a( "{\"a\" :  q",  "{@0,k3@1:!@8:B:O_BV:{" ),
            a( "0*",           "!2@0:B:A_BF" ),
            a( "1, 2.4n",      "d1@0,!4@3:B:A_BV" ),
            a( "{\"a\": 3^6}", "{@0,k3@1:!2@6:B:O_BV:{" ),
            a( " 1f",          "!2@1:B:A_BF" ),
            a( "{\"a\": t,",   "{@0,k3@1:!2@6:B:O_BV:{" )
        ).test("bad value",
            (r) -> srctokens(parser(r.str("src")))
        );
    }

    @Test
    public void testUnexpectedValue () {
        table(
            a( "src",                  "exp" ),
            a( "\"a\"\"b\"",           "s3@0,!3@3:U:A_AV" ),
            a( "{\"a\"]",              "{@0,k3@1:!1@4:U:O_AK:{" ),
            a( "{\"a\"\"b\"}",         "{@0,k3@1:!3@4:U:O_AK:{" ),
            a( "{\"a\": \"b\"]",       "{@0,k3@1:s3@6,!1@9:U:O_AV:{" ),
            a( "[\"a\", \"b\"}",       "[@0,s3@1,s3@6,!1@9:U:A_AV:[" ),
            a( "0{",                   "d1@0,!1@1:U:A_AV" ),
            a( "{\"a\"::",             "{@0,k3@1:!1@5:U:O_BV:{" ),
            a( "{ false:",             "{@0,!5@2:U:O_BF:{" ),
            a( "{ fal",                "{@0,!3@2:U:O_BF:{" ),
            a( "{ fal:",               "{@0,!3@2:U:O_BF:{" ),
            a( "{\"a\": \"b\", 3: 4}", "{@0,k3@1:s3@6,!1@11:U:O_BK:{" ),
            a( "{ \"a\"]",             "{@0,k3@2:!1@5:U:O_AK:{" ),
            a( "{ \"a\" ]",            "{@0,k3@2:!1@6:U:O_AK:{" ),
            a( "{ \"a\":]",            "{@0,k3@2:!1@6:U:O_BV:{" ),
            a( "{ \"a\": ]",           "{@0,k3@2:!1@7:U:O_BV:{" ),
            a( "{ 2.4",                "{@0,!3@2:U:O_BF:{" ),
            a( "[ 1, 2 ] \"c",         "[@0,d1@2,d1@5,]@7,!2@9:U:A_AV" ),
            a( "[ 1, 2 ] \"c\"",       "[@0,d1@2,d1@5,]@7,!3@9:U:A_AV" )
        ).test("unexpected value",
            (r) -> srctokens(parser(r.str("src")))
        );
    }

    @Test
    public void testNextErrors () {
        table(
            a( "s1", "s2",        "s3", "exp" ),
            a( "[{", " true}",    "",   "unexpected token at 1..5" ),
            a( "[",  "true, fax", "",   "bad value at 6..9" )
        ).teste("next() errors",
            (r) -> {
                Object[] sources = a(r.str("s1"), r.str("s2"), r.str("s3"));
                Tokenizer p = Tokenizer.parser();
                for (Object src : sources) {
                    p.ps.next_src = ((String) src).getBytes();
                    while (p.next() != 0) {};
                }
                return null;
            }
        );
    }

    @Test
    public void testSrcNotFinished () {
        String s1 = "[1,2,3,4,";
        String s2 = "5]";
        Tokenizer p = parser(s1);
        p.ps.next_src = s2.getBytes();
        String exp = "[@0,d1@1,d1@3,d1@5,d1@7,d1@0,]@1,!@2:A_AV";
        Assert.assertEquals(desc("src not finished", a(s1, s2), exp), srctokens(p), exp);
    }

    @Test
    public void testSoffAndVcount () {
        table(
            a( "s1",            "s2",                "s3",      "exp" ),
            a( "[1, ",          "2,3,",              "4]",      a(a(0, 4, 8), a(1, 3, 5) )),
            a( "[ {\"a\": 7, ", "\"b\": [1,2,3] },", " true ]", a(a(0, 11, 26), a(1, 6, 8)))
        ).test("soff and vcount",
            (r) -> {
                Object[] sources = a(r.str("s1"), r.str("s2"), r.str("s3"));
                int[] soffs = new int[3];
                int[] voffs = new int[3];
                Tokenizer p = Tokenizer.parser();
                for (int i=0; i<sources.length; i++) {
                    Object src = sources[i];
                    p.ps.next_src = ((String) src).getBytes();
                    while (p.next() != 0) {};
                    p.checke();
                    soffs[i] = p.ps.soff;
                    voffs[i] = p.ps.vcount;
                }
                return a(soffs, voffs);
            }
        );
    }

    @Test
    public void testStickyEcode () {
        table(
            a( "src",        "exp" ),
            a( "",           ", !@0:A_BF, !@0:A_BF" ),
            a( "1",          ", !1@0:D:A_BF, !1@0:D:A_BF" ),
            a( "1,",         "d1@0:A_AV, !@2:A_BV, !@2:A_BV" ),
            a( "1,2",        "d1@0:A_AV, !1@2:D:A_BV, !1@2:D:A_BV" ),
            a( "[\"",        "[@0:A_BF:[, !1@1:T:A_BF:[, !1@1:T:A_BF:[" ),
            a( "{\"",        "{@0:O_BF:{, k1@1:!@2:T:O_BF:{, k1@1:!@2:T:O_BF:{" ),
            a( "[\"a",       "[@0:A_BF:[, !2@1:T:A_BF:[, !2@1:T:A_BF:[" ),
            a( "{\"a",       "{@0:O_BF:{, k2@1:!@3:T:O_BF:{, k2@1:!@3:T:O_BF:{" ),
            a( "{\"a\"",     "{@0:O_BF:{, k3@1:!@4:K:O_AK:{, k3@1:!@4:K:O_AK:{" ),
            a( "{\"a\":",    "{@0:O_BF:{, k3@1:!@5:K:O_BV:{, k3@1:!@5:K:O_BV:{" ),
            a( "{\"a\":\"",  "{@0:O_BF:{, k3@1:!1@5:T:O_BV:{, k3@1:!1@5:T:O_BV:{" ),
            a( "{\"a\":\"b", "{@0:O_BF:{, k3@1:!2@5:T:O_BV:{, k3@1:!2@5:T:O_BV:{" ),
            a( "{\"a\":n",   "{@0:O_BF:{, k3@1:!1@5:T:O_BV:{, k3@1:!1@5:T:O_BV:{" ),
            a( "{\"a\":no",  "{@0:O_BF:{, k3@1:!2@5:B:O_BV:{, k3@1:!2@5:B:O_BV:{" ),
            a( "{t",         "{@0:O_BF:{, !1@1:U:O_BF:{, !1@1:U:O_BF:{" ),
            a( "{7",         "{@0:O_BF:{, !1@1:U:O_BF:{, !1@1:U:O_BF:{" ),
            a( "[tx",        "[@0:A_BF:[, !2@1:B:A_BF:[, !2@1:B:A_BF:[" ),
            a( "{tx",        "{@0:O_BF:{, !1@1:U:O_BF:{, !1@1:U:O_BF:{" )
        ).test("sticky ecode",
            (r) -> {

                Tokenizer p = parser(r.str("src"));
                p.opt.ehandler = (e) -> {};
                String last = "";
                List<String> toks = new ArrayList<>();
                while (p.next() != 0) {
                    last = p.ps.tokstr(true);
                }
                toks.add(last);
                toks.add(p.ps.tokstr(true));
                p.next();
                toks.add(p.ps.tokstr(true));
                Assert.assertEquals("not sticky", toks.get(toks.size()-1), toks.get(toks.size()-2));

                return join(toks.toArray(), ", ");
            }
        );
    }

    @Test
    public void testParseStateObject() {
        table(
            a( "src",            "opt", "prop_or_fn", "args",                      "exp" ),
            a( "{\"num\":7 ",    null,  "toString",     a(),                       "{\"tokstr\":k5@1:d1@7,\"key\":\"num\",\"val\":7,\"line\":1,\"col\":8,\"pos\":O_AV}" ),
            a( "{\"num\":7 ",    null,  "key",        a(true),                        "num" ),
            a( "{\"num\":7 ",    null,  "key",        a(false),                        "\"num\"" ),
            a( "{\"num\":7 ",    null,  "key_cmp",    a( "num".getBytes(), 0, 1 ), 1 ),
            a( "{\"num\":7 ",    null,  "key_cmp",    a( "num".getBytes(), 0, 2 ), 1 ),
            a( "{\"num\":7 ",    null,  "key_cmp",    a( "num".getBytes(), 0, 3 ), 0 ),
            a( "{\"num\":7 ",    null,  "key_equal",  a( "num".getBytes(), 0, 3),       true ),
            a( "{\"num\":7 ",    null,  "key_equal",  a( "numm".getBytes(), 0, 4),  false ),
            a( "{\"num\":7 ",    null,  "val",        a(),                     7 ),
            a( "{\"num\":7 ",    null,  "val_cmp",    a( "7".getBytes(), 0, 1 ),            0 ),
            a( "{\"num\":7 ",    null,  "val_cmp",    a( "7".getBytes(), 1, 1 ),            1 ),
            a( "{\"a\":[ ",      null,  "toString",     a(),                       "{\"tokstr\":k3@1:[@5,\"key\":\"a\",\"val\":[,\"line\":1,\"col\":6,\"pos\":A_BF}" ),
            a( "{\"a\":[ ",      null,  "key",        a(true),                        "a" ),
            a( "{\"a\":[ ",      null,  "val",        a(),                        "[" ),
            a( "{\"a\":[3 ",     null,  "key",        a(true),                        null ),
            a( "{\"a\":[3 ",     null,  "key",        a(false),                        null ),
            a( "{\"a\":[3 ",     null,  "val",        a(),                        3 ),
            a( "{\"a\":[3] ",    null,  "key",        a(true),                        null ),
            a( "{\"a\":[3] ",    null,  "val",        a(),                        "]" ),
            a( "{\"a\":[\"x\" ", null,  "val",        a(),                        "x" ),
            a( "{\"a\":[\"x\" ", null,  "val_cmp",    a( "w".getBytes(), 0, 1 ),                 1 ),
            a( "{\"a\":[\"x\" ", null,  "val_cmp",    a( "x".getBytes(), 0, 1 ),                 0 ),
            a( "{\"a\":[\"x\" ", null,  "val_equal",  a( "x".getBytes(), 0, 1 ),                 true ),
            a( "{\"a\":[\"x\" ", null,  "val_cmp",    a( "y".getBytes(), 0, 1 ),                 -1 ),
            a( "{\"a\":[\"x\" ", null,  "val_equal",  a( "y".getBytes(), 0, 1 ),                 false ),
            a( "{\"a\":4} ",     null,  "toString",     a(),                      "{\"tokstr\":}@6,\"val\":},\"line\":1,\"col\":7,\"pos\":A_AV}"),
            a( "{\"a\":4} ",     null,  "toString",   a(),                         "{\"tokstr\":}@6,\"val\":},\"line\":1,\"col\":7,\"pos\":A_AV}" ),
            a( "{\"a\":4.1 ",    null,  "val",        a(),                        4.1 ),
            a( "{\"a\":4} ",     null,  "val",        a(),                        "}" ),
            a( "{\"a\": true ",  null,  "val",        a(),                        true ),
            a( "{\"a\": false ", null,  "val",        a(),                        false ),
            a( "{\"a\": null ",  null,  "val",        a(),                        null ),
//            "# pending/incomplete decimal",
            a( "2",              null,  "val",        a(),                        null ),
            a( "{\"a\":4}  ",    null,  "val",        a(),                        null )
        ).test("",
            (r) -> {
                String src = r.str("src");
                String prop_or_fn = r.str("prop_or_fn");
                Object[] args = r.arr("args");
                Tokenizer p = parser(r.str("src"));
                while (p.next() != 0 && p.ps.vlim < src.length() - 1) {}
                Object ret;
                if (args != null) {
                    // function call
                    ret = call(p.ps, prop_or_fn, args);
                } else {
                    // property
                    ret = field(p.ps, prop_or_fn);
                }
                return ret;
            }
        );
    }

    // return a new parser with that silences errors (to allow token assertion on error state, etc)
    static Tokenizer parser (String src) { return parser(src, 0, src.getBytes().length); }
    static Tokenizer parser (String src, int off, int lim) {
        Tokenizer p = Tokenizer.parser(src.getBytes(), off, lim);
        p.opt.ehandler = (e) -> {};
        return p;
    }

    static String[] parse_split (String src1, String src2) {
        String[] ret = new String[2];
        Tokenizer p = parser(src1);
        ret[0] = srctokens(p);
        p.ps.next_src = src2.getBytes();
        ret[1] = srctokens(p);
        return ret;
    }

    static String srctokens (Tokenizer p) {
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
