package com.naderi.filehorse.exception;

public class StoreFileNotFoundException extends StoreException {

    public StoreFileNotFoundException(String message) {
        super(message);
    }

    public StoreFileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}