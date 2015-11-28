package com.qa.framework.exceptions;

public class StepException extends Exception {	

    private static final long serialVersionUID = -4185625941594809653L;

    public StepException() {
        super();
    }

    public StepException(String message) {
        super(message);
    }

    public StepException(Throwable t) {
        super(t);
    }

    public StepException(String message, Throwable t) {
        super(message, t);
    }

}
