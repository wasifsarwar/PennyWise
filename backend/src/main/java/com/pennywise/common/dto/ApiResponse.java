package com.pennywise.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;

/**
 * Standard API response wrapper for consistent JSON structure.
 * 
 * <p>All API endpoints should return this wrapper for consistency.</p>
 * 
 * @param <T> The type of data in the response
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private Instant timestamp;

    public ApiResponse() {
        this.timestamp = Instant.now();
    }

    public ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.timestamp = Instant.now();
    }

    /**
     * Create a successful response with data.
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, null, data);
    }

    /**
     * Create a successful response with message and data.
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    /**
     * Create a successful response with just a message.
     */
    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(true, message, null);
    }

    /**
     * Create an error response with a message.
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null);
    }

    /**
     * Create an error response with message and additional data.
     */
    public static <T> ApiResponse<T> error(String message, T data) {
        return new ApiResponse<>(false, message, data);
    }

    // Getters and setters

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
