package com.thoughtworks.wheels.beans;

public class CustomerName {
    private String first;
    private String last;
    private String nick;

    public CustomerName() {
    }

    public String getFirst() {
        return first;
    }

    public String getLast() {
        return last;
    }

    public String getNick() {
        return nick;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public void setLast(String last) {
        this.last = last;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }
}
