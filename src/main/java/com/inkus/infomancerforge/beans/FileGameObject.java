package com.inkus.infomancerforge.beans;

import java.io.File;
import java.io.Serializable;

public interface FileGameObject extends Serializable{
	public String getUuid();
	public boolean hasChanges();
	public void touch();
	public void saved();
	public boolean renameFileResource(File tofile);
	public String getFileResourceName();
	public File getMyFile();
	public void setMyFile(File newFile);
}
