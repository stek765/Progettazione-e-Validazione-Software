package it.univr.track.exceptions;

public class ProvisioningNotAllowedException extends RuntimeException {
    public ProvisioningNotAllowedException(String message) {
        super(message);
    }
}
