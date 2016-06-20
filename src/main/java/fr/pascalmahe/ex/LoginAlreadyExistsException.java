package fr.pascalmahe.ex;

public class LoginAlreadyExistsException extends Exception {

	private static final long serialVersionUID = -1591988498847467742L;

	public LoginAlreadyExistsException(String message){
		super(message);
	}
}
