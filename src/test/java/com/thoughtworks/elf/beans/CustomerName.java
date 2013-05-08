package com.thoughtworks.elf.beans;

public class CustomerName {
    private String first;
    private String last;
    private String nick;
    private Integer status;
    private NameFormat nameFormat;

    public NameFormat getNameFormat() {
        return nameFormat;
    }

    public void setNameFormat(NameFormat nameFormat) {
        this.nameFormat = nameFormat;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

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
