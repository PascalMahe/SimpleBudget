package fr.pascalmahe.ex;

public class MalformedTextException extends Exception {

	private static final long serialVersionUID = -9084732581567304384L;

	public MalformedTextException(String message){
		super(message);
	}

	public MalformedTextException(String message, Throwable t) {
		super(message, t);
	}
}
