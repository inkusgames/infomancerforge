package com.cinch.adventurebuilderstoolkit.data;

import java.io.Serializable;

public interface DataInstance extends Serializable{
	
	public boolean hasChanges();
	public void touch();
	public void saved();
	public String getUuid();
}
