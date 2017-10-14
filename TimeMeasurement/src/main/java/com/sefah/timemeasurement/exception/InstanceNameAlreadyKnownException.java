package com.sefah.timemeasurement.exception;

public class InstanceNameAlreadyKnownException extends Exception {

	private static final long serialVersionUID = 4149271725209981386L;
	
	public InstanceNameAlreadyKnownException(String exceptionMessage) {
		super(exceptionMessage);
	}
}
