package com.github.ClassFormatFile;

public class TestClass {
    private int m;

    public int inc() {
        return m + 1;
    }

    public static void main(String[] args) {
        float a = 0.9f;
        System.out.println(Float.floatToIntBits(a));
        System.out.println(Float.floatToRawIntBits(a));
        System.out.println(a);
    }
}
