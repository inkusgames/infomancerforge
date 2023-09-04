package com.inkus.infomancerforge.data;

import java.io.Serializable;

public interface DataInstance extends Serializable{
	
	public boolean hasChanges();
	public void touch();
	public void saved();
	public String getUuid();
}
