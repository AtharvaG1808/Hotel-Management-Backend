package cg.dfs.hotel.exception;

import java.time.Instant;
import java.util.Map;

public class ApiError {
    private Instant timestamp;
    private int status;
    private String error;   // Reason phrase
    private String code;    // App-specific error code (e.g., TRAVEL_PACKAGE_NOT_FOUND)
    private String message; // Human-readable message
    private String path;    // Request path
    private Map<String, Object> details; // Optional field & other details

    public ApiError() {}

    public ApiError(Instant timestamp, int status, String error, String code, String message, String path, Map<String, Object> details) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.code = code;
        this.message = message;
        this.path = path;
        this.details = details;
    }

    public static ApiError of(Instant timestamp, int status, String error, String code, String message, String path) {
        return new ApiError(timestamp, status, error, code, message, path, null);
    }

    // Getters and setters
    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    public Map<String, Object> getDetails() { return details; }
    public void setDetails(Map<String, Object> details) { this.details = details; }
}
