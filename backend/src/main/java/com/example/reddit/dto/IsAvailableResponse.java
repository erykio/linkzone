package com.example.reddit.dto;

public class IsAvailableResponse {
    boolean isAvailable;

    public IsAvailableResponse(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public boolean isAvailable() {
        return isAvailable;
    }
}