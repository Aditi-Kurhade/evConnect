package org.project.evconnectbackend.exception;

public class TransactionExistsException extends RuntimeException {
    private final Long bookingId;

    public TransactionExistsException(String message, Long bookingId) {
        super(message);
        this.bookingId = bookingId;
    }

    public Long getBookingId() {
        return bookingId;
    }
} 