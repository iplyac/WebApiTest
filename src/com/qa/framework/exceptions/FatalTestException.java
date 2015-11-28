package com.qa.framework.exceptions;

import com.reltio.qa.exceptions.ApplicationGlobalException;

public class FatalTestException extends ApplicationGlobalException {

    private static final long serialVersionUID = -5013409017866924595L;

    public FatalTestException() {
        super();
    }

    public FatalTestException(String message) {
        super(message);
    }

    public FatalTestException(Throwable t) {
        super(t);
    }

    public FatalTestException(String message, Throwable t) {
        super(message, t);
    }

}
