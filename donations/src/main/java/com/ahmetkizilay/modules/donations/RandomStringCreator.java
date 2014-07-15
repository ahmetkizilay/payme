package com.ahmetkizilay.modules.donations;

import java.util.Random;

/**
 * Code heavily based on the answer on StackOverflow
 * http://stackoverflow.com/a/18650153
 *
 * Created by ahmetkizilay on 14.07.2014.
 */
public class RandomStringCreator {
    private static final char[] symbols = new char[36];

    static {
        for (int idx = 0; idx < 10; ++idx)
            symbols[idx] = (char) ('0' + idx);
        for (int idx = 10; idx < 36; ++idx)
            symbols[idx] = (char) ('a' + idx - 10);
    }

    private final static Random random = new Random();

    public static String nextString(int length) {
        char[] buf = new char[length];
        for (int idx = 0; idx < buf.length; ++idx)
            buf[idx] = symbols[random.nextInt(symbols.length)];
        return new String(buf);
    }
}
