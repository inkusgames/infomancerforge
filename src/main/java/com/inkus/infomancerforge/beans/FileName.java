package com.inkus.infomancerforge.beans;

import java.io.Serializable;

public class FileName implements Serializable{
	private static final long serialVersionUID = 1L;

	private String fileName;

	public FileName() {
	}
	
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}