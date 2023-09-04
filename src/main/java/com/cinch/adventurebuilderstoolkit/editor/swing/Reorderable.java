package com.cinch.adventurebuilderstoolkit.editor.swing;

public interface Reorderable {
	public void reorder(int fromIndex, int toIndex, int size);
	public boolean reorderable(int fromIndex, int toIndex, int size);
}