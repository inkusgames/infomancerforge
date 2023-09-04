package com.inkus.infomancerforge.editor.swing;

import org.kordamp.ikonli.fluentui.FluentUiFilledMZ;

import com.formdev.flatlaf.extras.components.FlatLabel;
import com.inkus.infomancerforge.ImageUtilities;

public class ErrorLabel extends FlatLabel {
	private static final long serialVersionUID = 1L;

	public ErrorLabel() {
		super.setLabelType(LabelType.small);
		setIcon(ImageUtilities.getIcon(FluentUiFilledMZ.WARNING_16, ImageUtilities.TREE_NODE_ERROR_COLOR, ImageUtilities.TREE_ICON_ERROR_SIZE));
	}

}
