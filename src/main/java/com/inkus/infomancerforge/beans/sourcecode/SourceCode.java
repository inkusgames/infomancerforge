package com.inkus.infomancerforge.beans.sourcecode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.io.FilenameUtils;

import com.inkus.infomancerforge.beans.FileGameObject;
import com.inkus.infomancerforge.beans.NamedResource;

public class SourceCode implements FileGameObject,NamedResource{
	private static final long serialVersionUID = 1L;
	
	private File sourceFile;
	private String extension;
	private String name;
	private String projectPath; // Full project path
	private String code;
	private String uuid;
	private String newCode;

	transient List<SourceErrors> errors=new ArrayList<>();

	public SourceCode() {
	}

	public SourceCode(File sourceFile,String projectPath, String extension, String code, String uuid) {
		super();
		this.projectPath = projectPath;
		this.extension = extension;
		this.code = code;
		this.uuid = uuid;
		setMyFile(sourceFile);
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public String getCode() {
		return hasChanges()?newCode:code;
	}

	public void setCode(String code) {
		this.newCode = code;
	}

	public void setProjectPath(String projectPath) {
		this.projectPath = projectPath;
	}

	public String getProjectPath() {
		return projectPath;
	}

	@Override
	public String getName() {
		return name+"."+extension;
	}

	@Override
	public boolean isNamed() {
		return true;
	}
	
	@Override
	public File getMyFile() {
		return sourceFile;
	}

	@Override
	public void setMyFile(File myFile) {
		name=FilenameUtils.getBaseName(myFile.getName());
		this.sourceFile = myFile;
	}

	@Override
	public String getFileResourceName() {
		return FilenameUtils.getBaseName(sourceFile.getName());
	}
	
	@Override
	public boolean renameFileResource(File tofile) {
		if (sourceFile.renameTo(tofile)) {
			setMyFile(tofile);
			//name=FilenameUtils.getBaseName(tofile.getName());
			return true;
		}
		return false;
	}

	@Override
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid=uuid;
	}

	@Override
	public boolean hasChanges() {
		return newCode!=null && !newCode.equals(code);
	}

	@Override
	public void touch() {
		// This wont do anything unless you change newcode.
	}

	public File getSourceFile() {
		return sourceFile;
	}

	public void setSourceFile(File sourceFile) {
		this.sourceFile = sourceFile;
	}

	public List<SourceErrors> getErrors() {
		return errors;
	}

	public void setErrors(List<SourceErrors> errors) {
		this.errors = errors;
	}

	@Override
	public void saved() {
		if (hasChanges()) {
			code=newCode;
		}
		newCode=null;
	}

	@Override
	public int hashCode() {
		return Objects.hash(sourceFile);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SourceCode other = (SourceCode) obj;
		return Objects.equals(sourceFile, other.sourceFile);
	}

}
