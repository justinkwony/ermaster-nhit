package org.insightech.er.common.exception;

public class InputException extends Exception {

	private static final long serialVersionUID = -6325812774566059357L;

	private String[] args;

	public InputException() {
	}

	public InputException(String message) {
		super(message);
	}

	public InputException(Throwable exception) {
		super(exception);
	}

	public InputException(String message, String[] args) {
		super(message);

		this.args = args;
	}

	public String[] getArgs() {
		return this.args;
	}

}
