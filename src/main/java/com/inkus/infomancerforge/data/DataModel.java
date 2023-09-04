package com.inkus.infomancerforge.data;

import java.util.List;

import com.inkus.infomancerforge.beans.gobs.GOB;
import com.inkus.infomancerforge.beans.gobs.GOBInstance;
import com.inkus.infomancerforge.beans.gobs.GOBPropertyDefinition;

import ca.odell.glazedlists.EventList;

public interface DataModel {
	public EventList<GOBInstance> selectAll(GOB gob,List<GOBPropertyDefinition> fullProperties);
	public List<GOBInstance> selectUnused(GOB gob,List<GOB> thisAndParentGobs,List<GOBPropertyDefinition> fullProperties,List<GOB> gobsThatContainIt,List<List<GOBPropertyDefinition>> fullPropertiesPerGob);
	
	public void updateStructure(GOB gob,List<GOBPropertyDefinition> fullProperties);
	public void update(GOB gob,List<GOBPropertyDefinition> fullProperties,GOBInstance gobInstance);
	public void delete(GOB gob,String uuid);
	
	public void save();

}
