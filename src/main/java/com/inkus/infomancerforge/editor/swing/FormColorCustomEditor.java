package com.inkus.infomancerforge.editor.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.Serializable;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.kordamp.ikonli.fluentui.FluentUiRegularAL;

import com.formdev.flatlaf.extras.components.FlatButton;
import com.formdev.flatlaf.extras.components.FlatToolBar;
import com.inkus.infomancerforge.ImageUtilities;

import snap.bean.ObjectMapper;
import snap.swing.CustomField;

public class FormColorCustomEditor extends JPanel implements CustomField, MouseListener {
	private static final long serialVersionUID = 1L;

	private Color color;
	private JLabel label=new JLabel();
	private String description="Pick Color";

	public FormColorCustomEditor() {
		super(new BorderLayout());
		
		FlatButton clearButton=new FlatButton();
		clearButton.setAction(new ClearColorAction());
		clearButton.setFocusable(false);
		
		FlatToolBar clearToolBar=new FlatToolBar();
		clearToolBar.add(clearButton);
		
		add(clearToolBar,BorderLayout.EAST);
		
		add(label,BorderLayout.CENTER);
		setColor(null);
		setMinimumSize(new Dimension(20, 24));
		setPreferredSize(new Dimension(200, 24));
		label.setHorizontalAlignment(JLabel.CENTER);
		label.addMouseListener(this);
		setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 3));
	}
	
	public FormColorCustomEditor(String description) {
		this();
		this.description=description;
	}

	public void setColor(Color c) {
		color=c;
		if (color==null) {
			label.setOpaque(false);
			label.setText("no color");
		}else {
			label.setOpaque(true);
			label.setBackground(color);
			label.setForeground(ImageUtilities.getSuitableTextColorForBackground(color));
			label.setText(ImageUtilities.colorRGBToHex(color));
		}
	}
	
	public Color getColor() {
		return color;
	}

	@Override
	public void intoGui(String field, Serializable bean, JComponent component) {
		Object value=ObjectMapper.getFieldValue(field, bean);
		if (value instanceof Color c) {
			setColor(c);
		}
	}

	@Override
	public void intoBean(String field, Serializable bean, JComponent component) {
		ObjectMapper.setFieldValue(field, bean, color);
	}

	@Override
	public JComponent getComponent(String field, Serializable bean) {
		return this;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (isEnabled() && SwingUtilities.isLeftMouseButton(e)) {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					JColorChooser chooserPane=new JColorChooser(color==null?Color.black:color);

					JColorChooser.createDialog(FormColorCustomEditor.this, description, true, chooserPane, 
						new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								setColor(chooserPane.getColor());
								FocusEvent focusEvent=new FocusEvent(FormColorCustomEditor.this, FocusEvent.FOCUS_LOST);
								for (var f:FormColorCustomEditor.this.getFocusListeners()) {
									f.focusLost(focusEvent);
								}
							}
						}, 
						new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
							}
						}
					).setVisible(true);
				}
			});
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	private class ClearColorAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		private ClearColorAction(){
			putValue(SMALL_ICON, ImageUtilities.getIcon(FluentUiRegularAL.BLOCK_16, ImageUtilities.BUTTON_ICON_COLOR, ImageUtilities.SMALL_BUTTON_ICON_SIZE));
			putValue(SHORT_DESCRIPTION, "Clear the current color.");
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			setColor(null);
			FocusEvent focusEvent=new FocusEvent(FormColorCustomEditor.this, FocusEvent.FOCUS_LOST);
			for (var f:FormColorCustomEditor.this.getFocusListeners()) {
				f.focusLost(focusEvent);
			}
		}
	}
	
}
