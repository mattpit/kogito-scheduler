package org.example.domain;

public class Strategy {
    private int speedWeight;
    private int smoothnessWeight;

    public Strategy() {}
    public Strategy(int speedWeight, int smoothnessWeight) {
        this.speedWeight = speedWeight; this.smoothnessWeight = smoothnessWeight;
    }

    public static Strategy fromDirective(String directive) {
        if (directive == null) directive = "SMOOTH";
        switch (directive.toUpperCase()) {
            case "FAST": return new Strategy(10, 3);
            case "SMOOTH": default: return new Strategy(3, 10);
        }
    }

    public int getSpeedWeight() { return speedWeight; }
    public int getSmoothnessWeight() { return smoothnessWeight; }
}
