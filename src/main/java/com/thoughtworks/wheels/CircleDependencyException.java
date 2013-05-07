package com.thoughtworks.wheels;

public class CircleDependencyException extends RuntimeException {
    public CircleDependencyException(String s) {
        super(s);
    }
}
