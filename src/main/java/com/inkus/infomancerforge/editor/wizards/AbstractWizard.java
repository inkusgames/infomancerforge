package com.inkus.infomancerforge.editor.wizards;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.io.Serializable;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;

import com.formdev.flatlaf.extras.components.FlatButton;

import snap.swing.Form;

public abstract class AbstractWizard<bean extends Serializable> extends JDialog {
	private static final long serialVersionUID = 1L;

	private bean bean;
	
	private CardLayout centerCardLayout;
	private JPanel centerCardPanel;
	private Form<bean>[] forms;
	
	private NextPage nextPage=new NextPage(); 
	private PrevPage prevPage=new PrevPage();
	
	private FlatButton prevButton;
	
	private String completedButtonText="Finish";
	
	private int currentPage=0;
	
	private Point screenPos=null;
	
	protected AbstractWizard(String title,int minWidth) {
		super();
		setTitle(title);
		setModal(true);
		
		setMinimumSize(new Dimension(minWidth, 100));
	}
	
	public Point getScreenPos() {
		return screenPos;
	}

	public void setScreenPos(Point screenPos) {
		this.screenPos = screenPos;
	}

	protected void setCompleteButtonText(String completedButtonText) {
		this.completedButtonText=completedButtonText;
	}
	
	protected void setFormsAndStart(bean bean,Form<bean>[] forms) {
		this.bean=bean;
		this.forms=forms;
		build();
		nextPage.refresh();
		pack();
		if (screenPos!=null) {
			//setLocation(screenPos);
			var s=this.getGraphicsConfiguration().getBounds();
//			System.out.println("Bounds("+s.x+","+s.y+","+s.width+","+s.height+")");
			int x=screenPos.x;
			int y=screenPos.y;
//			System.out.println("Location("+x+","+y+","+getWidth()+","+getHeight()+")");
			if (x<s.x) {
				x=s.x;
			}
			if (x+getWidth()>s.width) {
				x=s.width-getWidth();
			}
			if (y<s.y) {
				y=s.y;
			}
			if (y+getHeight()>s.height) {
				y=s.height-getHeight();
			}
			setLocation(x, y);
//			System.out.println("Location("+x+","+y+")");
		} else {
			setLocationRelativeTo(null);
		}
		setVisible(true);
	}
	
	protected abstract boolean completeWizard();
	
	private JComponent buildCenter() {
		centerCardLayout=new CardLayout();
		centerCardPanel=new JPanel(centerCardLayout);
		int f=0;
		for (var form:forms) {
			form.setCurrentBean(bean);
			centerCardPanel.add(form, ""+f++);
		}
		return centerCardPanel;
	}
	
	private JComponent buildButtons() {
		JPanel panel=new JPanel(new BorderLayout());

		FlatButton nextButton=new FlatButton();
		nextButton.setAction(nextPage);
		
		prevButton=new FlatButton();
		prevButton.setAction(prevPage);
		prevButton.setVisible(false);
		
		panel.add(prevButton,BorderLayout.WEST);
		panel.add(nextButton,BorderLayout.EAST);
		
		return panel;
	}

	private void build() {
		JPanel main=new JPanel(new BorderLayout());
		
		main.add(buildCenter(),BorderLayout.CENTER);
		main.add(buildButtons(),BorderLayout.SOUTH);
		
		getContentPane().add(main);
	}
	
	private class NextPage extends AbstractAction {
		private static final long serialVersionUID = 1L;

		private NextPage(){
			putValue(NAME, "Next");
		}
		
		public void refresh() {
			prevButton.setVisible(currentPage>0);
			if (currentPage==forms.length-1) {
				putValue(NAME, completedButtonText);
			} else {
				putValue(NAME, "Next");
			}
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			forms[currentPage].guiIntoBean();
			if (currentPage==forms.length-1) {
				if (completeWizard()) {
					setVisible(false);
				}
			} else {
				currentPage++;
				centerCardLayout.show(centerCardPanel,""+currentPage);
			}
			refresh();
		}
		
	}
	
	private class PrevPage extends AbstractAction {
		private static final long serialVersionUID = 1L;

		private PrevPage(){
			putValue(NAME, "Previous");
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			currentPage--;
			centerCardLayout.show(centerCardPanel,""+currentPage);
			prevButton.setVisible(currentPage>0);
		}
		
	}
	
}
