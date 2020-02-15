package com.quicbit.json.next;

import java.nio.file.Files;
import java.nio.file.Path;

public class PerfTest {

    static long parse (byte[] buf) {
        long t0 = System.currentTimeMillis();
        Parser p = Parser.parser(buf);
        while(p.next() != 0) {};
        return System.currentTimeMillis() - t0;
    }

    public static void main (String[] args) throws Exception {
        String fname = "/Users/dad/dev/json-samples/cache_150mb.json";
        byte[] buf = Files.readAllBytes(Path.of(fname));

        System.out.println("read " + fname);

        long total_ms = 0;
        int iter = 5;
        for (var i=0; i<iter; i++) {
            long ms = parse(buf);
            System.out.println("parsed " + (buf.length/(1024*1024)) + " MB in " + (ms/1000.0) + " seconds");
            total_ms += ms;
        }
        System.out.println((iter * buf.length / ((total_ms/1000.0) * 1024 * 1024)) + " MB/second");
     }
}

/*
Test results on 2014 Macbook Pro, 2020-02-15

read /Users/dad/dev/json-samples/cache_150mb.json
parsed 144 MB in 0.427 seconds
parsed 144 MB in 0.504 seconds
parsed 144 MB in 0.46 seconds
parsed 144 MB in 0.461 seconds
parsed 144 MB in 0.461 seconds
312.0050191157729 MB/second

 */
