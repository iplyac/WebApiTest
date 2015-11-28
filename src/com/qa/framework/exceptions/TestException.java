package com.qa.framework.exceptions;

public class TestException extends RuntimeException {	

    private static final long serialVersionUID = -4185625971594809653L;

    public TestException() {
        super();
    }

    public TestException(String message) {
        super(message);
    }

    public TestException(Throwable t) {
        super(t);
    }

    public TestException(String message, Throwable t) {
        super(message, t);
    }

}
