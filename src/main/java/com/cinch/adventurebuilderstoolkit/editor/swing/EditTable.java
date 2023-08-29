package com.cinch.adventurebuilderstoolkit.editor.swing;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

import com.formdev.flatlaf.extras.components.FlatTable;

public class EditTable extends FlatTable {
	private static final long serialVersionUID = 1L;

	public EditTable() {
	}

    public boolean editCellAt(int row, int column, EventObject e) {
        boolean result = super.editCellAt(row, column, e);
        final Component editor = getEditorComponent();

        if (editor != null && editor instanceof JTextComponent) {
            //((JTextComponent)editor).selectAll();
            editor.requestFocusInWindow();

            if (e == null) {
                //((JTextComponent)editor).selectAll();
                editor.requestFocusInWindow();
            } else if (e instanceof MouseEvent) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        //((JTextComponent)editor).selectAll();
                        editor.requestFocusInWindow();
                    }
                });
            }
        }

        return result;
    }

}
