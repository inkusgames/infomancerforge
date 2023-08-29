package com.cinch.adventurebuilderstoolkit.editor.swing;

import org.kordamp.ikonli.fluentui.FluentUiFilledMZ;

import com.cinch.adventurebuilderstoolkit.ImageUtilities;
import com.formdev.flatlaf.extras.components.FlatLabel;

public class ErrorLabel extends FlatLabel {
	private static final long serialVersionUID = 1L;

	public ErrorLabel() {
		super.setLabelType(LabelType.small);
		setIcon(ImageUtilities.getIcon(FluentUiFilledMZ.WARNING_16, ImageUtilities.TREE_NODE_ERROR_COLOR, ImageUtilities.TREE_ICON_ERROR_SIZE));
	}

}
