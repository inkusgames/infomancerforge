package com.cinch.adventurebuilderstoolkit.display.factories.builders;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.OverlayLayout;
import javax.swing.tree.TreeNode;

import org.kordamp.ikonli.fluentui.FluentUiFilledMZ;

import com.cinch.adventurebuilderstoolkit.ImageUtilities;
import com.cinch.adventurebuilderstoolkit.display.factories.TreeDisplayBuilder;
import com.cinch.adventurebuilderstoolkit.editor.swing.CountLabel;
import com.cinch.adventurebuilderstoolkit.editor.swing.ErrorLabel;
import com.formdev.flatlaf.extras.components.FlatLabel;
import com.formdev.flatlaf.extras.components.FlatLabel.LabelType;

public abstract class TreeDisplayBuilderBase<type extends TreeNode> extends TreeDisplayBuilder<type> {

	private JPanel panel=new JPanel(new BorderLayout());
	private JLabel iconLabel=new JLabel();
	private CountLabel changedLabel=new CountLabel(ImageUtilities.TREE_NODE_CHANGED_COLOR);
	private ErrorLabel errorLabel=new ErrorLabel();
	
	private FlatLabel name=new FlatLabel(); 
	private Color flatLabelColor=name.getForeground();
	
	public TreeDisplayBuilderBase(){
		changedLabel.setForeground(Color.BLACK);
		changedLabel.setHorizontalTextPosition(JLabel.CENTER);

		JPanel iconPanel=new JPanel(new BorderLayout());
		iconPanel.setOpaque(false);
		iconPanel.add(iconLabel,BorderLayout.EAST);
		iconPanel.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 5));

		JPanel errorIconPanel=new JPanel(new BorderLayout());
		errorIconPanel.setOpaque(false);
		errorIconPanel.add(errorLabel,BorderLayout.EAST);
		errorIconPanel.add(new JLabel(),BorderLayout.CENTER);
		
		JPanel changedIconPanel=new JPanel(new BorderLayout());
		changedIconPanel.setOpaque(false);
		changedIconPanel.add(changedLabel,BorderLayout.EAST);
		changedIconPanel.add(new JLabel(),BorderLayout.CENTER);
		JPanel changedIconPanelFinal=new JPanel(new BorderLayout());
		changedIconPanelFinal.setOpaque(false);
		changedIconPanelFinal.add(changedIconPanel,BorderLayout.NORTH);
		changedIconPanelFinal.add(errorIconPanel,BorderLayout.SOUTH);
		
		JPanel imagePanel=new JPanel();
		OverlayLayout overlayLayout=new OverlayLayout(imagePanel);
		imagePanel.setLayout(overlayLayout);
		imagePanel.add(changedIconPanelFinal);
		imagePanel.add(iconPanel);
		imagePanel.setOpaque(false);
		
		name.setLabelType(LabelType.regular);
		panel.setOpaque(false);
		panel.add(name,BorderLayout.CENTER);
		panel.add(imagePanel,BorderLayout.WEST);
	}

	public abstract String getName(type node);
	public abstract Icon getIcon(type node,boolean expanded);
	
	@Override
	protected JPanel getDisplay(type t,boolean expanded,boolean leaf, boolean changed, int changedCount, boolean error) {
		iconLabel.setIcon(getIcon(t,expanded));
		name.setText(getName(t));
		errorLabel.setVisible(error);
		if (changed || changedCount>0) {
			name.setForeground(flatLabelColor.brighter());
			if (changedCount>0) {
				changedLabel.setIcon(null);
//				changedLabel.setIcon(IconUtilities.getIcon(FluentUiFilledAL.CIRCLE_24, IconUtilities.TREE_NODE_CHANGED_COLOR, IconUtilities.TREE_ICON_CHANGED_COUNT_SIZE));
				//changedLabel.setOpaque(true);
//				changedLabel.setBackground(IconUtilities.TREE_NODE_CHANGED_COLOR);
				if (changedCount>9) {
					changedLabel.setText(" 9+ ");
				} else {
					changedLabel.setText(" "+changedCount+" ");
				}
			} else {
				changedLabel.setIcon(ImageUtilities.getIcon(FluentUiFilledMZ.STAR_12, ImageUtilities.TREE_NODE_CHANGED_COLOR, ImageUtilities.TREE_ICON_CHANGED_SIZE));
				changedLabel.setText("");
//				changedLabel.setOpaque(false);
			}
		} else {
			name.setForeground(flatLabelColor);
			changedLabel.setIcon(null);
			//changedLabel.setOpaque(false);
			changedLabel.setText("");
		}
		return panel;
	}

}
