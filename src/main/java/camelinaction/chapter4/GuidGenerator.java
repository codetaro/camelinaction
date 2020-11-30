package camelinaction.chapter4;

import java.util.Random;

public class GuidGenerator {

    public static int generate() {
        Random ran = new Random();
        return ran.nextInt(10000000);
    }
}
