/*
    * obj for allele vector
    * basically stores the 10 values defiend in taxonomy.java
*/
package com.system.models;

import java.util.Objects;

public final class MediaVector {

    public final float d1, d2, d3, d4, d5, d6, d7, d8, d9, d10;

    public MediaVector(float d1, float d2, float d3, float d4, float d5,
                       float d6, float d7, float d8, float d9, float d10) {
        this.d1 = d1; this.d2 = d2; this.d3 = d3; this.d4 = d4; this.d5 = d5;
        this.d6 = d6; this.d7 = d7; this.d8 = d8; this.d9 = d9; this.d10 = d10;
    }

    public float[] toArray() {
        return new float[]{d1, d2, d3, d4, d5, d6, d7, d8, d9, d10};
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MediaVector)) return false;
        MediaVector v = (MediaVector) o;
        return Float.compare(d1, v.d1) == 0 && Float.compare(d2, v.d2) == 0
            && Float.compare(d3, v.d3) == 0 && Float.compare(d4, v.d4) == 0
            && Float.compare(d5, v.d5) == 0 && Float.compare(d6, v.d6) == 0
            && Float.compare(d7, v.d7) == 0 && Float.compare(d8, v.d8) == 0
            && Float.compare(d9, v.d9) == 0 && Float.compare(d10, v.d10) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(d1, d2, d3, d4, d5, d6, d7, d8, d9, d10);
    }

    @Override
    public String toString() {
        return String.format("MediaVector{d1=%.2f, d2=%.2f, d3=%.2f, d4=%.2f, d5=%.2f, "
            + "d6=%.2f, d7=%.2f, d8=%.2f, d9=%.2f, d10=%.2f}",
            d1, d2, d3, d4, d5, d6, d7, d8, d9, d10);
    }
}
