package ru.hm4;

public enum Letters {
    A("A"),
    B("B"),
    C("C");

    private final String letters;

    Letters(String letters) {
        this.letters = letters;
    }

    public String getLetter() {
        return letters;
    }
}
