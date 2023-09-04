package com.inkus.infomancerforge.editor;

import com.inkus.infomancerforge.data.DataInstance;

public interface DataInstanceChangeListener {
	void dataInstanceObjectChanged(Object source,DataInstance dataInstance);
}
