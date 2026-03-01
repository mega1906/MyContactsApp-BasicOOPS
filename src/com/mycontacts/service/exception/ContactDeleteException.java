package com.mycontacts.service.exception;

// Exception used for delete flow failures.
public class ContactDeleteException extends RuntimeException {
    public ContactDeleteException(String message) {
        super(message);
    }
}
