package fr.pascalmahe.ex;

public class MalformedLineException extends Exception {

	private static final long serialVersionUID = -9084732581567304384L;

	public MalformedLineException(String message){
		super(message);
	}

	public MalformedLineException(String message, Throwable t) {
		super(message, t);
	}
}
