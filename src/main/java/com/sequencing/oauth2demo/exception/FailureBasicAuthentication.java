package com.sequencing.oauth2demo.exception;

public class FailureBasicAuthentication extends Exception {

	private static final long serialVersionUID = 4607326018356301058L;

	public FailureBasicAuthentication() {
	}

	public FailureBasicAuthentication(String message) {
		super(message);
	}

	public FailureBasicAuthentication(Throwable cause) {
		super(cause);
	}

	public FailureBasicAuthentication(String message, Throwable cause) {
		super(message, cause);
	}

	public FailureBasicAuthentication(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
