package com.inkus.infomancerforge.beans.gobs;

import java.io.Serializable;
import java.util.Objects;

import com.inkus.infomancerforge.editor.AdventureProjectModel;

public class GOBReferance implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String typeUuid;
	private String uuid;
	
	private transient GOB gob;
	private transient GOBInstance gobInstance;
	
	public GOBReferance() {
	}
	
	public GOBReferance(String typeUuid, String uuid) {
		super();
		this.typeUuid = typeUuid;
		this.uuid = uuid;
	}

	public GOBReferance(GOB gob,GOBInstance gobInstance) {
		this.gob=gob;
		this.gobInstance=gobInstance;
		typeUuid=gobInstance.getGobType();
		uuid=gobInstance.getUuid();
	}

	public String getTypeUuid() {
		return typeUuid;
	}

	public void setTypeUuid(String typeUuid) {
		this.typeUuid = typeUuid;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	private void getData(AdventureProjectModel adventureProjectModel) {
		if (gobInstance==null) {
			synchronized (this) {
				if (gobInstance==null) {
					gob=adventureProjectModel.getNamedResourceByUuid(GOB.class, typeUuid);
					if (gob!=null) {
						gobInstance=adventureProjectModel.getGOBDataTableModel(gob).getRowByUUID(uuid);
					}
				}
			}
		}
	}

	public boolean stillExists(AdventureProjectModel adventureProjectModel) {
		gobInstance=null;
		return getGobInstance(adventureProjectModel)!=null;
	}
	
	public GOB getGob(AdventureProjectModel adventureProjectModel) {
		getData(adventureProjectModel);
		return gob;
	}
	
	public GOBInstance getGobInstance(AdventureProjectModel adventureProjectModel) {
		getData(adventureProjectModel);
		return gobInstance;
	}

	@Override
	public int hashCode() {
		return Objects.hash(typeUuid, uuid);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GOBReferance other = (GOBReferance) obj;
		return Objects.equals(typeUuid, other.typeUuid) && Objects.equals(uuid, other.uuid);
	}
	
}
