package com.andrewjoel.cms.exceptions;

public class CmsException extends RuntimeException {
    private final String message;

    public CmsException(final String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
