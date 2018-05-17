package edu.rosehulman.kozlowlw.exam1kozlowlw;

import java.util.Arrays;

/**
 * Created by boutell on 3/12/18.
 */

public class Triple {
    public int a;
    public int b;
    public int h;

    public Triple(int a, int b, int h) {
        int[] array = new int[] {a,b,h};
        Arrays.sort(array);
        this.a = array[0];
        this.b = array[1];
        this.h = array[2];
    }

    @Override
    public String toString() {
        return this.a + "," + this.b + "," + this.h;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Triple) {
            Triple other = (Triple)obj;
            return this.a == other.a && this.b == other.b && this.h == other.h;
        }
        return false;
    }

    public boolean onlyOneSideOffByOne(Triple other) {
        return Math.abs(this.a - other.a) + Math.abs(this.b - other.b) + Math.abs(this.h - other.h) == 1;
    }

    public boolean closeTo(Triple other) {
        return Math.abs(this.a - other.a) <= 1 && Math.abs(this.b - other.b) <= 1 && Math.abs(this.h - other.h) <= 1;
    }

}
