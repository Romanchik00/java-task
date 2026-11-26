public record TestResult(String name, Status status, long durationMs) {
    public String toString(){ return name + "\t\t|  " + status + "  |  " + durationMs; }
}
