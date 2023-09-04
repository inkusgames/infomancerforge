package com.inkus.infomancerforge.beans;

import java.io.Serializable;

public class FolderName implements Serializable{
	private static final long serialVersionUID = 1L;

	private String folderName;

	public FolderName() {
	}

	public String getFolderName() {
		return folderName;
	}

	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}
	
	
}