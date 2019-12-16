package net.endimyon.util;

public class Tools {
    public static int castToBinary(int i, int j) {
        int result = 1;
        if (j < 0) {
            throw new IllegalArgumentException("Index " + j + " must be positive!");
        }
        if (j == 0){
            return 0;
        }
        for (int _i = 0; _i < j; _i++) {
            result *= i;
        }
        return result / 2;
    }

    public static int castToDenary(int i) {
        int bit = 1;
        int pool = 0;
        for (int index = 1; index < 32; index++) {
            int p = i & bit;
            p = (p == 0) ? 0 : 1;
            pool += p * index;
            bit *= 2;
        }
        return pool;
    }

    public static String timeToString(int time) {
        String t = "";
        if (time == 0) {
            t += "00";
        } else if (time > 0 && time < 10) {
            t = t + "0" + time;
        } else {
            t += time;
        }
        return t;
    }

    public static String toElementString(int e) {
        int bit = 1;
        StringBuilder s = new StringBuilder();
        for (int index = 1; index < 10; index++) {
            if ((e & bit) == 0) {
                s.append(" |");
            } else {
                s.append(index).append("|");
            }
            bit *= 2;
        }
        return s.toString();
    }

    public static boolean check2PowerN(int a) {
        boolean verified;
        int pw = 1;
        int PWCount = 0;
        for(int c2p = 1; c2p <= 10; c2p ++) {
            if (a == pw) {
                PWCount++;
            }
            pw = 2 * pw;
        }
        verified = (PWCount == 1);
        return verified;
    }
}
