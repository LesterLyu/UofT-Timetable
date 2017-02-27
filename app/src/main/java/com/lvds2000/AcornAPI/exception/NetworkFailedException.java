package com.lvds2000.AcornAPI.exception;

public class NetworkFailedException extends Exception {

	private static final long serialVersionUID = 1L;

	public NetworkFailedException(String msg){
		super(msg);
	}
	public NetworkFailedException(){
		super("Your device is not connected to Internet.");
	}
}
