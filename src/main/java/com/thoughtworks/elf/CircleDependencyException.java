package com.thoughtworks.elf;

public class CircleDependencyException extends RuntimeException {
    public CircleDependencyException(String s) {
        super(s);
    }
}
