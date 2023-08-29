package com.cinch.adventurebuilderstoolkit.exceptions;

import com.cinch.adventurebuilderstoolkit.beans.NamedResource;

public class NamedResourceException extends ResourceException {
	private static final long serialVersionUID = 1L;

	private NamedResource resource;
	
	public NamedResourceException(NamedResource resource) {
		this.resource=resource;
	}

	public NamedResourceException(NamedResource resource,String message) {
		super(message);
		this.resource=resource;
	}

	public NamedResourceException(NamedResource resource,Throwable cause) {
		super(cause);
		this.resource=resource;
	}

	public NamedResourceException(NamedResource resource,String message, Throwable cause) {
		super(message, cause);
		this.resource=resource;
	}

	public NamedResourceException(NamedResource resource,String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		this.resource=resource;
	}

	public NamedResource getResource() {
		return resource;
	}

}
