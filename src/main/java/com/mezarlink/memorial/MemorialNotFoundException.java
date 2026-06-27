package com.mezarlink.memorial;

public class MemorialNotFoundException extends RuntimeException {

    public MemorialNotFoundException(String identifier) {
        super("Ani sayfasi bulunamadi: " + identifier);
    }
}
