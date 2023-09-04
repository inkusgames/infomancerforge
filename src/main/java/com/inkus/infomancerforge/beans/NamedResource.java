package com.inkus.infomancerforge.beans;


/**
 * A named resource can only have one of it's type defined in the system. One type per class.
 * 
 * So for example Gob's need a unique name but that name need not be unique to other named resources.
 * 
 * @author Travis
 */
public interface NamedResource {
	public static final NamedResource empty=new NamedResourceEmpty();
	
	public String getUuid();
	public String getName();
	
	public boolean isNamed(); // This is needed for DataInstnaces where some are named but others are not.
	
	static class NamedResourceEmpty implements NamedResource{
		private NamedResourceEmpty(){
		}
		
		@Override
		public String getUuid() {
			return null;
		}
		
		@Override
		public String getName() {
			return "~None~";
		}
		
		public boolean isNamed() {
			return true;
		}
		
	}
}
