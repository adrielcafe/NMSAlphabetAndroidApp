package cafe.adriel.nmsalphabet.util;

import java.io.Serializable;

public class Triple<T, U, V> implements Serializable {
    T a;
    U b;
    V c;

    public Triple(T a, U b, V c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public T getA() {
        return a;
    }

    public U getB() {
        return b;
    }

    public V getC() {
        return c;
    }
}