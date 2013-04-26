package com.thoughtworks.wheels;

/**
 * User: fff
 * Date: 4/26/13
 * Time: 12:45 PM
 */
public class CircleDependencyException extends RuntimeException {
    public CircleDependencyException(String s) {
        super(s);
    }
}
