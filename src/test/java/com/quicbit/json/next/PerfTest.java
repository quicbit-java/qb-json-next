package com.quicbit.json.next;

public class PerfTest {

    static void parse (byte[] buf) {
       long t0 = System.currentTimeMillis();
    }

    /*
 var ps = {src: buf}
 while (next(ps)) {}
 return new Date() - t0;
 }

 var fname = '/Users/dad/dev/json-samples/cache_150mb.json'
 var buf = fs.readFileSync(fname)
 console.log('read', fname)

 var total_ms = 0
 var iter = 5
 for (var i=0; i<iter; i++) {
 var ms = parse(buf)
 console.log('parsed ' + (buf.length/(1024*1024)) + ' MB in', ms/1000, 'seconds')
 total_ms += ms
 }

 console.log(iter * buf.length / ((total_ms/1000) * 1024 * 1024) + ' MB/second')


 */
}
