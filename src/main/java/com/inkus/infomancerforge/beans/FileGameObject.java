package com.inkus.infomancerforge.beans;

import java.io.Serializable;

public interface FileGameObject extends Serializable{
	public String getUuid();
	public boolean hasChanges();
	public void touch();
	public void saved();
}
