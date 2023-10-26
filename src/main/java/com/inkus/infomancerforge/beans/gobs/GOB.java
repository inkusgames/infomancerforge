package com.inkus.infomancerforge.beans.gobs;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;

import com.inkus.infomancerforge.Alignment;
import com.inkus.infomancerforge.StorageUtilities;
import com.inkus.infomancerforge.beans.FileGameObject;
import com.inkus.infomancerforge.beans.NamedResource;
import com.inkus.infomancerforge.beans.views.GobView.ViewMode;

public class GOB implements FileGameObject, NamedResource {
	private static final long serialVersionUID = 1L;

	public enum Type {
		Base, Linked, Embedded;

		public boolean isBase() {
			return this == Base;
		}

		public boolean isReferance() {
			return this != Embedded;
		}

	}
	
	public enum SummaryAlignment {
		Left{
			public Alignment getAlignment() {
				return Alignment.TopLeft;
			}
		},
		Center{
			public Alignment getAlignment() {
				return Alignment.Top;
			}
		},
		Right{
			public Alignment getAlignment() {
				return Alignment.TopRight;
			}
		};
		
		abstract public Alignment getAlignment();
	}

	private String uuid;
	private String name;
	private String parent;
	private Type type = Type.Base;
	private ViewMode defaultViewMode = ViewMode.Full;
	private boolean definitionOnly = false;
	private List<GOBPropertyDefinition> propertyDefinitions = new ArrayList<>();

	private String gobTableName;

	private Color colorBackground;
	private String summary;
	private SummaryAlignment summaryAlignment=SummaryAlignment.Center;
	
	private transient File myFile;

	private transient boolean changed = false;

	public GOB() {
		uuid = UUID.randomUUID().toString();
	}
	
	@Override
	public File getMyFile() {
		return myFile;
	}

	@Override
	public void setMyFile(File myFile) {
		name=FilenameUtils.getBaseName(myFile.getName());
		this.myFile = myFile;
	}

	@Override
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	@Override
	public boolean isNamed() {
		return type.isBase();
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public List<GOBPropertyDefinition> getPropertyDefinitions() {
		return propertyDefinitions;
	}

	public void setPropertyDefinitions(List<GOBPropertyDefinition> propertyDefinitions) {
		this.propertyDefinitions = propertyDefinitions;
	}

	@Override
	public String getFileResourceName() {
		return FilenameUtils.getBaseName(myFile.getName());
	}
	
	@Override
	public boolean renameFileResource(File tofile) {
		if (myFile.renameTo(tofile)) {
			setMyFile(tofile);
			StorageUtilities.saveGOB(this, tofile.getAbsolutePath());
			return true;
		}
		return false;
	}

	@Override
	public boolean hasChanges() {
		return changed;
	}

	@Override
	public void touch() {
		changed = true;
	}

	@Override
	public void saved() {
		changed = false;
	}

	public boolean isDefinitionOnly() {
		return definitionOnly;
	}

	public void setDefinitionOnly(boolean definitionOnly) {
		this.definitionOnly = definitionOnly;
	}

	public String getGobTableName() {
		return gobTableName;
	}

	public void setGobTableName(String gobTableName) {
		this.gobTableName = gobTableName;
	}

	public Color getColorBackground() {
		return colorBackground;
	}

	public void setColorBackground(Color colorBackground) {
		this.colorBackground = colorBackground;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public SummaryAlignment getSummaryAlignment() {
		return summaryAlignment;
	}

	public void setSummaryAlignment(SummaryAlignment summaryAlignment) {
		this.summaryAlignment = summaryAlignment;
	}

	public ViewMode getDefaultViewMode() {
		return defaultViewMode;
	}

	public void setDefaultViewMode(ViewMode defaultViewMode) {
		this.defaultViewMode = defaultViewMode;
	}

	@Override
	public int hashCode() {
		return Objects.hash(colorBackground, defaultViewMode, definitionOnly, gobTableName, name, parent,
				propertyDefinitions, summary, summaryAlignment, type, uuid);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GOB other = (GOB) obj;
		return Objects.equals(colorBackground, other.colorBackground) && defaultViewMode == other.defaultViewMode
				&& definitionOnly == other.definitionOnly && Objects.equals(gobTableName, other.gobTableName)
				&& Objects.equals(name, other.name) && Objects.equals(parent, other.parent)
				&& Objects.equals(propertyDefinitions, other.propertyDefinitions)
				&& Objects.equals(summary, other.summary) && summaryAlignment == other.summaryAlignment
				&& type == other.type && Objects.equals(uuid, other.uuid);
	}

}
