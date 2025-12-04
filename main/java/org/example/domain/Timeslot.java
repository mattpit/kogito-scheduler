package org.example.domain;

import java.util.Objects;

public class Timeslot {
    private String start;
    private String end;
    public Timeslot() {}
    public Timeslot(String start, String end) { this.start = start; this.end = end; }

    public String getStart() { return start; }
    public String getEnd() { return end; }
    @Override public String toString() { return start + "-" + end; }
    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Timeslot)) return false;
        Timeslot t = (Timeslot) o;
        return Objects.equals(start, t.start) && Objects.equals(end, t.end);
    }
    @Override public int hashCode() { return Objects.hash(start, end); }
}
