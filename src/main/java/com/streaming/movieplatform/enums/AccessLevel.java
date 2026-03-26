package com.streaming.movieplatform.enums;

public enum AccessLevel {
    FREE,
    STANDARD,
    PREMIUM;

    public boolean allows(AccessLevel movieLevel) {
        if (this == PREMIUM) {
            return true;
        }
        if (this == STANDARD) {
            return movieLevel == FREE || movieLevel == STANDARD;
        }
        return movieLevel == FREE;
    }
}
