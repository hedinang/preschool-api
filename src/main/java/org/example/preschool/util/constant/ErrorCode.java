package org.example.preschool.util.constant;

public enum ErrorCode {
    E400("Invalid request"),
    E401("Authentication failed"),
    E403("Insufficient permissions"),
    E404("Resource not found"),
    E409("Resource already exists");

    private final String describe;

    ErrorCode(String des) {
        this.describe = des;
    }

    /**
     * Get exception code
     *
     * @return
     */
    public int code() {
        return Integer.parseInt(this.name().replaceAll("E", ""));
    }

    public String describe() {
        return describe;
    }
}
