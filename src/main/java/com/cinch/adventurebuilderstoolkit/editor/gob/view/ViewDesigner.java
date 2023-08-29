package com.cinch.adventurebuilderstoolkit.editor.gob.view;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kordamp.ikonli.fluentui.FluentUiFilledAL;
import org.kordamp.ikonli.fluentui.FluentUiFilledMZ;
import org.kordamp.ikonli.fluentui.FluentUiRegularAL;
import org.kordamp.ikonli.fluentui.FluentUiRegularMZ;

import com.cinch.adventurebuilderstoolkit.ImageUtilities;
import com.cinch.adventurebuilderstoolkit.beans.views.GobView;
import com.cinch.adventurebuilderstoolkit.beans.views.RightDragHandler;
import com.cinch.adventurebuilderstoolkit.beans.views.View;
import com.cinch.adventurebuilderstoolkit.beans.views.View.GridType;
import com.cinch.adventurebuilderstoolkit.beans.views.ViewDrawable;
import com.cinch.adventurebuilderstoolkit.editor.AdventureProjectModel;
import com.cinch.adventurebuilderstoolkit.editor.actions.BaseViewAction;
import com.cinch.adventurebuilderstoolkit.editor.actions.LinkGobInstanceToView;
import com.cinch.adventurebuilderstoolkit.editor.actions.NewGobInstanceToView;
import com.cinch.adventurebuilderstoolkit.editor.gob.ViewEditor;
import com.cinch.adventurebuilderstoolkit.editor.property.PropertyEditor;
import com.cinch.adventurebuilderstoolkit.editor.property.PropertyValues;
import com.cinch.adventurebuilderstoolkit.editor.property.gob.PropertyValuesGobInstance;
import com.formdev.flatlaf.extras.components.FlatButton;
import com.formdev.flatlaf.extras.components.FlatButton.ButtonType;
import com.formdev.flatlaf.extras.components.FlatComboBox;
import com.formdev.flatlaf.extras.components.FlatMenuItem;
import com.formdev.flatlaf.extras.components.FlatPopupMenu;
import com.formdev.flatlaf.extras.components.FlatToggleButton;
import com.formdev.flatlaf.extras.components.FlatToolBar;

public class ViewDesigner extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener  {
	@SuppressWarnings("unused")
	static private final Logger log = LogManager.getLogger(ViewDesigner.class);
	private static final long serialVersionUID = 1L;

	private ViewEditor viewEditor;
	private View view;
	private AdventureProjectModel adventureProjectModel;

	// These are used for actions to know where they were initially invoked
	private int activeMouseX;
	private int activeMouseY;
	private int activeMouseViewX;
	private int activeMouseViewY;
	private int activeMouseScreenX;
	private int activeMouseScreenY;

	private JScrollPane scrollPane;
	private EditView editView;
	private JSplitPane split;
	
	private List<BaseViewAction> standardMenu = new ArrayList<>();

	private Map<String,ViewDrawable> selectedMap = new HashMap<>();
	private Map<String,Point> draggedMap = new HashMap<>();
	private ViewDrawable highlighted = null;
	private ViewDrawable dragSelected = null;
	private RightDragHandler dragRightSelected = null;
	private int dragSelectedXoff;
	private int dragSelectedYoff;
	private PropertyEditor propertyEditor;
	private ToggleProperties toggleProperties;

	public ViewDesigner(ViewEditor viewEditor, View view, AdventureProjectModel adventureProjectModel) {
		super(new BorderLayout());
		this.viewEditor = viewEditor;
		this.view = view;
		this.adventureProjectModel = adventureProjectModel;
		view.init();
		
		addMouseListener(this);
		standardMenu.add(new NewGobInstanceToView(viewEditor,null,null));
		standardMenu.add(new LinkGobInstanceToView(viewEditor,null,null));
		
		addComponentListener(new ComponentListener() {
			
			@Override
			public void componentShown(ComponentEvent e) {
			}
			
			@Override
			public void componentResized(ComponentEvent e) {
				updatePropertiesSeen();
			}
			
			@Override
			public void componentMoved(ComponentEvent e) {
			}
			
			@Override
			public void componentHidden(ComponentEvent e) {
			}
		});

		build();
	}

	public JComponent buildDesigner() {
		editView = new EditView();
		editView.addMouseListener(this);
		editView.addMouseMotionListener(this);
		editView.addMouseWheelListener(this);

		scrollPane = new JScrollPane(editView);
		scrollPane.setDoubleBuffered(false);
		scrollPane.setWheelScrollingEnabled(false);
		scrollPane.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
		scrollPane.getViewport().setViewPosition(new Point(view.getPosX(), view.getPosY()));

		scrollPane.getViewport().addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				var bounds = scrollPane.getViewport().getViewRect();
				view.setPosX((int) bounds.getMinX());
				view.setPosY((int) bounds.getMinY());
				viewEditor.changed();
			}
		});

		return scrollPane;
	}

	public JComponent buildPropertiesPanel() {
		propertyEditor=new PropertyEditor(adventureProjectModel);
		
		JPanel panel=new JPanel(new BorderLayout());
		panel.setMinimumSize(new Dimension(300,300));
		panel.setPreferredSize(new Dimension(300,300));
		
		panel.add(propertyEditor,BorderLayout.CENTER);
		
		return panel;
	}
	
	public JComponent buildToolBar() {
		JPanel topBar=new JPanel(new BorderLayout());
		
		FlatButton zoomOut = new FlatButton();
		zoomOut.setAction(new ZoomOut());
		zoomOut.setFocusable(false);

		FlatButton zoomOne = new FlatButton();
		zoomOne.setAction(new ZoomOne());
		zoomOne.setFocusable(false);

		FlatButton zoomIn = new FlatButton();
		zoomIn.setAction(new ZoomIn());
		zoomIn.setFocusable(false);

		FlatToolBar toolBar = new FlatToolBar();

		toolBar.addSeparator();
		toolBar.add(new JLabel(" Grid"));
		toolBar.add(new ToggleGrid());
		toolBar.add(new ToggleSnap());
		toolBar.add(new GridSize());
		toolBar.addSeparator();
		toolBar.add(new JLabel(" Zoom"));
		toolBar.add(zoomOut);
		toolBar.add(zoomOne);
		toolBar.add(zoomIn);
		toolBar.addSeparator();
		
		FlatToolBar propBar = new FlatToolBar();
		propBar.add(new JLabel(" Properties"));
		toggleProperties=new ToggleProperties();
		propBar.add(toggleProperties);

		
		topBar.add(toolBar,BorderLayout.WEST);
		topBar.add(propBar,BorderLayout.EAST);

		return topBar;
	}

	private void removeOldGobs() {
		List<GobView> removeGobs=new ArrayList<>();
		for (var d : view.getGobs()) {
			if (!d.getGobReferance().stillExists(adventureProjectModel)) {
				removeGobs.add(d);
			}
		}
		if (removeGobs.size()>0) {
			System.out.println("Removing "+removeGobs.size());
			view.getGobs().removeAll(removeGobs);
		}
	}
	
	public void build() {
		removeOldGobs();
		
		for (var d : view.getDrawables()) {
			d.recalcSize(adventureProjectModel);
		}

		JPanel main=new JPanel(new BorderLayout());
		main.add(buildToolBar(), BorderLayout.NORTH);
		main.add(buildDesigner(), BorderLayout.CENTER);
		
		split=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, main, buildPropertiesPanel());
		split.setResizeWeight(1.0);
		split.setEnabled(false);
		split.setDividerSize(0);

		add(split, BorderLayout.CENTER);
	}

	public int getActiveMouseX() {
		return activeMouseX;
	}

	public int getActiveMouseY() {
		return activeMouseY;
	}

	public int getActiveMouseScreenX() {
		return activeMouseScreenX;
	}

	public int getActiveMouseScreenY() {
		return activeMouseScreenY;
	}

	public void setSelected(ViewDrawable viewDrawable) {
		selectedMap.clear();
		addSelected(viewDrawable);
	}
	
	public void updatePropertiesEditor() {
		List<PropertyValues> allProperties=new ArrayList<>();
		for (var g:selectedMap.values()) {
			if (g instanceof GobView gobView) {
				allProperties.add(new PropertyValuesGobInstance(adventureProjectModel, gobView.getGobReferance().getGobInstance(adventureProjectModel)));
			}
		}
		propertyEditor.setPropertyValues(null,allProperties);
	}

	public void addSelected(ViewDrawable viewDrawable) {
		if (viewDrawable!=null && !selectedMap.containsKey(viewDrawable.getUuid())) {
			selectedMap.put(viewDrawable.getUuid(),viewDrawable);
			updatePropertiesEditor();
		}
		repaint();
	}

	private void showMenu(Point p,int offsetX, int offetrsetY) {
		List<BaseViewAction> actions = new ArrayList<>();

		// Find object we are over
		ViewDrawable over=findDrawableAtPoint(p);
		
		List<BaseViewAction> overActions=over!=null?over.getActions(viewEditor,adventureProjectModel):null;
		
		if (overActions!=null) {
			actions.addAll(overActions);
		}
		
		if (actions.size()==0) {
			actions.addAll(standardMenu);
		}
		showMenu(p,offsetX, offetrsetY,actions);
	}
	
	private void showMenu(Point p,int offsetX, int offetrsetY,List<BaseViewAction> actions) {

		FlatPopupMenu popupMenu = new FlatPopupMenu();

		for (var action : actions) {
			if (action == null) {
				popupMenu.addSeparator();
			} else {
				FlatMenuItem menuItem = new FlatMenuItem();
				menuItem.setAction(action);
				popupMenu.add(action);
			}
		}

		popupMenu.show(this, offsetX, offetrsetY);
	}

	private void setMousePositionFromEvent(MouseEvent e) {
		activeMouseX = (int) (e.getX() / view.getScale());
		activeMouseY = (int) (e.getY() / view.getScale());

		Rectangle currentView = scrollPane.getViewport().getViewRect();
		activeMouseViewX = (int) ((e.getX()) - currentView.x);
		activeMouseViewY = (int) ((e.getY()) - currentView.y);
		activeMouseScreenX = e.getXOnScreen();
		activeMouseScreenY = e.getYOnScreen();
	}

	public ViewDrawable findDrawableAtPoint(Point p) {
		for (var d : view.getDrawables()) {
			ViewDrawable over = d.findOver(p);
			if (over != null) {
				return over;
			}
		}
		return null;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (SwingUtilities.isRightMouseButton(e)) {
			// TODO: See if we over a drawable if so get it's menu
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					setMousePositionFromEvent(e);
					//					showMenu(standardMenu,activeMouseViewX,activeMouseViewY);
					showMenu(new Point(activeMouseX, activeMouseY),activeMouseViewX, activeMouseViewY);
				}
			});
		}
		if (SwingUtilities.isLeftMouseButton(e) && dragRightSelected==null) {
			// Select the view item we might be over
			setMousePositionFromEvent(e);
			if (e.isControlDown()) {
				addSelected(findDrawableAtPoint(new Point(activeMouseX, activeMouseY)));
			} else {
				setSelected(findDrawableAtPoint(new Point(activeMouseX, activeMouseY)));
			}
		}
	}

	private MouseEvent dragEvent = null;
	private boolean moving = false;
	private boolean draging = false;

	@Override
	public void mousePressed(MouseEvent event) {
		if (SwingUtilities.isLeftMouseButton(event) && dragRightSelected==null) {
			setMousePositionFromEvent(event);
			dragSelected = findDrawableAtPoint(new Point(activeMouseX, activeMouseY));
			if (dragSelected != null) {
				//				System.out.println("Dragging");
				//				dragEvent=event;
				draging = true;
				dragSelectedXoff = activeMouseX - dragSelected.bounds().x;
				dragSelectedYoff = activeMouseY - dragSelected.bounds().y;
				setCursor(new Cursor(Cursor.HAND_CURSOR));
				if (event.isControlDown()) {
					addSelected(dragSelected);
				} else {
					setSelected(dragSelected);
				}
				draggedMap.clear();
				for (var selected:selectedMap.values()) {
					draggedMap.put(selected.getUuid(), selected.bounds().getLocation());
				}
			}
		} else if (SwingUtilities.isMiddleMouseButton(event)) {
			dragEvent = event;
			moving = true;
			setCursor(new Cursor(Cursor.MOVE_CURSOR));
		} else if (SwingUtilities.isRightMouseButton(event)) {
			dragEvent = event;
			setMousePositionFromEvent(event);
			var rightOver=findDrawableAtPoint(new Point(activeMouseX, activeMouseY));
			if (rightOver!=null) {
				dragRightSelected=rightOver.getRightDragHandler();
			}
			
			if (dragRightSelected != null) {
				setSelected(dragRightSelected.getDrawable());
//				selected=dragRightSelected.getDrawable();
				//				System.out.println("Dragging");
				//				dragEvent=event;
				setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
			}
			
			//			connectEvent=event;
			//			connecting=true;
			//			connectTo=null;
		}
	}

	@Override
	public void mouseReleased(MouseEvent event) {
		if (SwingUtilities.isLeftMouseButton(event)) {
			draging = false;
		}
		
		if (SwingUtilities.isRightMouseButton(event)) {
			if (dragRightSelected!=null) {
				setMousePositionFromEvent(event);
				var rightOver=findDrawableAtPoint(new Point(activeMouseX, activeMouseY));
				if (rightOver!=null) {
					dragRightSelected.dragOnto(adventureProjectModel, rightOver);
				} else {
					
					var actions=dragRightSelected.draggedTo(viewEditor, adventureProjectModel, activeMouseX, activeMouseY, activeMouseViewY, activeMouseViewX, activeMouseScreenY, activeMouseScreenX);
					if (actions!=null && actions.size()>0){
						System.out.println("MenuIteams="+actions.size());
						showMenu(new Point(activeMouseX, activeMouseY),activeMouseViewX, activeMouseViewY, actions);
					}
				}
			}
			dragRightSelected = null;
			highlighted = null;
			repaint();
		}

		if (SwingUtilities.isMiddleMouseButton(event)) {
			dragEvent = null;
			moving = false;
		}
		if (!draging && !moving && dragRightSelected==null) {
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mouseDragged(MouseEvent event) {
		if (moving && SwingUtilities.isMiddleMouseButton(event)) {
			JViewport vp = scrollPane.getViewport();
			Point p = vp.getViewPosition();
			int dx = (int) p.getX() + dragEvent.getXOnScreen() - event.getXOnScreen();
			int dy = (int) p.getY() + dragEvent.getYOnScreen() - event.getYOnScreen();

			Dimension viewSize = vp.getViewSize();
			Dimension visableSize = vp.getExtentSize();
			if (dx > viewSize.getWidth() - visableSize.getWidth()) {
				dx = (int) (viewSize.getWidth() - visableSize.getWidth());
			}
			if (dy > viewSize.getHeight() - visableSize.getHeight()) {
				dy = (int) (viewSize.getHeight() - visableSize.getHeight());
			}

			if (dx < 0) {
				dx = 0;
			}
			if (dy < 0) {
				dy = 0;
			}

			view.setPosX(dx);
			view.setPosY(dy);
			scrollPane.getViewport().setViewPosition(new Point(dx, dy));
			dragEvent = event;
			viewEditor.changed();
		} else if (SwingUtilities.isRightMouseButton(event) && dragRightSelected!=null) {
			setMousePositionFromEvent(event);
			var rightOver=findDrawableAtPoint(new Point(activeMouseX, activeMouseY));
			if (dragRightSelected.canDragOnto(adventureProjectModel, rightOver)) {
				if (highlighted==null) {
					setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				}
				highlighted=rightOver;
			} else {
				if (highlighted!=null) {
					setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
					highlighted=null;
				}
			}

			repaint();
		} else {
			if (SwingUtilities.isLeftMouseButton(event) && dragSelected != null && draging) {
				
				int dx = (int) ((event.getX()) / view.getScale()) - dragSelectedXoff;
				int dy = (int) ((event.getY()) / view.getScale()) - dragSelectedYoff;
				
				int xAdjust=dx-draggedMap.get(dragSelected.getUuid()).x;
				int yAdjust=dy-draggedMap.get(dragSelected.getUuid()).y;
				
				for (var selected:selectedMap.values()) {
					
					Rectangle b = selected.bounds();
	
					int tx = draggedMap.get(selected.getUuid()).x+xAdjust;
					int ty = draggedMap.get(selected.getUuid()).y+yAdjust;
	
					// Snap
					if (view.isSnapGrid() && view.getGridType().gridSize() > 0) {
						int gridSize = view.getGridType().gridSize();
						tx = ((int) ((tx + gridSize / 2) / gridSize)) * gridSize;
						ty = ((int) ((ty + gridSize / 2) / gridSize)) * gridSize;
					}
	
					// Fit in view
					tx = Math.max(0, Math.min(tx, view.getCanvasWidth() - b.width));
					ty = Math.max(0, Math.min(ty, view.getCanvasHeight() - b.height));
	
					selected.moveTo(tx, ty);
				}
				viewEditor.changed();
				repaint();
			} else {
				draging = false;
			}
		}
		//					if (event.isControlDown()){
		//						if (currentDisplayable instanceof QuestDisplayable){
		//							// Move quest and objectives
		//							QuestDisplayable qd=(QuestDisplayable)currentDisplayable;
		//							for (Objective o:qd.getQuest().objectives){
		//								ObjectiveDisplayable od=o.getObjectiveDisplayable(qd.dialog);
		//								od.editorx-=xd;
		//								od.editory-=yd;
		//								if (od.editorx<0){
		//									od.editorx=0;
		//								}
		//								if (od.editorx+od.width>Editor.canvasSize){
		//									od.editorx=Editor.canvasSize-od.width;
		//								}
		//								if (od.editory<0){
		//									od.editory=0;
		//								}
		//								if (od.editory+od.width>Editor.canvasSize){
		//									od.editory=Editor.canvasSize-od.height;
		//								}
		//							}
		//						}else if (currentDisplayable instanceof Conversation){ 
		//							if (event.isShiftDown()){
		//								int dialogNumber=((Conversation)currentDisplayable).dialogNumber;
		//								// Move entire dialog
		//								for (Conversation c:dialogues.get(dialogNumber).conversations){
		//									if (c!=currentDisplayable){
		//										c.editorx-=xd;
		//										c.editory-=yd;
		//										if (c.editorx<0){
		//											c.editorx=0;
		//										}
		//										if (c.editorx+c.width>Editor.canvasSize){
		//											c.editorx=Editor.canvasSize-c.width;
		//										}
		//										if (c.editory<0){
		//											c.editory=0;
		//										}
		//										if (c.editory+c.width>Editor.canvasSize){
		//											c.editory=Editor.canvasSize-c.height;
		//										}
		//									}
		//									for (Option o:c.options){
		//										o.editorx-=xd;
		//										o.editory-=yd;
		//										if (o.editorx<0){
		//											o.editorx=0;
		//										}
		//										if (o.editorx+o.width>Editor.canvasSize){
		//											o.editorx=Editor.canvasSize-o.width;
		//										}
		//										if (o.editory<0){
		//											o.editory=0;
		//										}
		//										if (o.editory+o.width>Editor.canvasSize){
		//											o.editory=Editor.canvasSize-o.height;
		//										}
		//									}
		//								}
		//							}else{
		//								// Move conversation and options
		//								Conversation c=(Conversation)currentDisplayable;
		//								for (Option o:c.options){
		//									o.editorx-=xd;
		//									o.editory-=yd;
		//									if (o.editorx<0){
		//										o.editorx=0;
		//									}
		//									if (o.editorx+o.width>Editor.canvasSize){
		//										o.editorx=Editor.canvasSize-o.width;
		//									}
		//									if (o.editory<0){
		//										o.editory=0;
		//									}
		//									if (o.editory+o.width>Editor.canvasSize){
		//										o.editory=Editor.canvasSize-o.height;
		//									}
		//								}
		//							}
		//						}
		//					}
		//					dragEvent=event;
		//					repaint();
		//				}else {
		//					draging=(Math.abs(xd)+Math.abs(yd)>10);
		//				}
		//			}
		//		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent event) {
		if (!moving) {
			int notches = event.getWheelRotation();

			if (scrollPane.getViewport().getMousePosition() != null) {

				if (notches < 0) {
					// UP
					view.setScale(view.getScale() + 0.1f);
				} else {
					// Down
					view.setScale(view.getScale() - 0.1f);
				}
				if (view.getScale() < .25) {
					view.setScale(.25);
				}
				if (view.getScale() > 2) {
					view.setScale(2);
				}
				viewEditor.changed();
				setMousePositionFromEvent(event);
				repaint();
				event.consume();
			}
		}
	}

	public void refresh() {
		removeOldGobs();
		for (var g : view.getGobs()) {
			g.refreshConnectors(adventureProjectModel);
		}

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				repaint();
			}
		});
	}
//	
//	@Override
//	public void fileGameObjectChanged(Object source, FileGameObject fileGameObject) {
//		boolean isViewed = fileGameObject.getUuid().equals(view.getUuid());
//
//		for (var g : view.getGobs()) {
//			if (isViewed) {
//				break;
//			}
//			isViewed = fileGameObject.getUuid().equals(g.getGobReferance().getTypeUuid());
//			if (isViewed) {
//				g.refreshConnectors(adventureProjectModel);
//			}
//		}
//		if (isViewed) {
//			SwingUtilities.invokeLater(new Runnable() {
//				@Override
//				public void run() {
//					
//					repaint();
//				}
//			});
//		}
//	}

	class EditView extends JPanel {
		private static final long serialVersionUID = 1L;
		private double scale = 0;

		EditView() {
			setMinimumSize(new Dimension((int) (view.getCanvasWidth() / view.getScale()),
					(int) (view.getCanvasHeight() / view.getScale())));
			setPreferredSize(new Dimension((int) (view.getCanvasWidth() / view.getScale()),
					(int) (view.getCanvasHeight() / view.getScale())));
			setMaximumSize(new Dimension((int) (view.getCanvasWidth() / view.getScale()),
					(int) (view.getCanvasHeight() / view.getScale())));
		}

		private void drawViewDrawables(Graphics2D g2, Collection<? extends ViewDrawable> viewDrawables) {
			for (var d : viewDrawables) {
				d.paintDrawable(adventureProjectModel, g2, selectedMap.containsKey(d.getUuid()), d == highlighted, viewEditor);
				var children = d.getChildren();
				if (children != null) {
					drawViewDrawables(g2, children);
				}
			}
		}

		private void drawObjects(Graphics2D g2) {
			drawViewDrawables(g2, view.getDrawables());
		}

		public void paintComponent(Graphics g) {
			paintComponent(g, true);
		}

		public void paintComponent(Graphics g, boolean drawBackground) {
			if (scale == 0) {
				scale = Math.max(.25, Math.min(2, view.getScale()));
			}
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);

			if (drawBackground) {

				// clear
				Dimension visableSize = scrollPane.getViewport().getExtentSize();
				Point topLeft = scrollPane.getViewport().getViewPosition();
				g.setColor(ImageUtilities.VIEW_BACKGROUND);
				g.fillRect((int) topLeft.getX(), (int) topLeft.getY(), (int) visableSize.getWidth(),
						(int) visableSize.getHeight());

				// Also adjust for zoom
				g2.scale(scale, scale);

				g.setColor(ImageUtilities.VIEW_BACKGROUND);
				g.fillRect(0, 0, view.getCanvasWidth(), view.getCanvasHeight());
				if (view.isDrawGrid()) {
					g.setColor(ImageUtilities.VIEW_GRID);
					for (int t = 0; t < view.getCanvasWidth(); t += view.getGridType().gridSize()) {
						g.drawLine(t, 0, t, view.getCanvasHeight());
					}
					for (int t = 0; t < view.getCanvasHeight(); t += view.getGridType().gridSize()) {
						g.drawLine(0, t, view.getCanvasWidth(), t);
					}
				}
			}

			Rectangle currentView = scrollPane.getViewport().getViewRect();
			currentView = new Rectangle((int) (currentView.getX() / scale), (int) (currentView.getY() / scale),
					(int) (currentView.getWidth() / scale), (int) (currentView.getHeight() / scale));

			drawObjects(g2);

			if (dragRightSelected!=null) {
				float gap=(float)(4f/viewEditor.getView().getScale());

				g2.setColor(ImageUtilities.VIEW_SELECTED_OUTLINE_COLOUR);
				
				g2.setStroke(new BasicStroke(gap/2,BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL,0, new float[]{gap}, gap));
				g2.drawLine((int)dragRightSelected.getDrawable().bounds().getCenterX(), (int)dragRightSelected.getDrawable().bounds().getCenterY(), activeMouseX, activeMouseY);
			}
			
			if (drawBackground) {
				// Scale changed
				if (scale != view.getScale()) {
					final JViewport vp = scrollPane.getViewport();

					// We want to center on the mouse cursor
					int ofx = vp.getViewRect().width / 2;
					int ofy = vp.getViewRect().height / 2;
					Point mousePoint = vp.getMousePosition();
					if (mousePoint != null) {
						ofx = (int) vp.getMousePosition().getX();
						ofy = (int) vp.getMousePosition().getY();
					}

					// Work out center
					final int centerX = (int) ((vp.getViewRect().x + ofx) / scale * view.getScale());
					final int centerY = (int) ((vp.getViewRect().y + ofy) / scale * view.getScale());

					scale = view.getScale();

					setMinimumSize(new Dimension((int) (view.getCanvasWidth() * scale),
							(int) (view.getCanvasHeight() * scale)));
					setPreferredSize(new Dimension((int) (view.getCanvasWidth() * scale),
							(int) (view.getCanvasHeight() * scale)));
					setMaximumSize(new Dimension((int) (view.getCanvasWidth() * scale),
							(int) (view.getCanvasHeight() * scale)));

					Dimension viewSize = new Dimension((int) (view.getCanvasWidth() * scale),
							(int) (view.getCanvasHeight() * scale));
					vp.setViewSize(viewSize);

					int px = centerX - ofx;
					int py = centerY - ofy;

					Dimension visableSize = vp.getExtentSize();

					if (px > viewSize.getWidth() - visableSize.getWidth()) {
						px = (int) (viewSize.getWidth() - visableSize.getWidth());
					}
					if (py > viewSize.getHeight() - visableSize.getHeight()) {
						py = (int) (viewSize.getHeight() - visableSize.getHeight());
					}

					if (px < 0) {
						px = 0;
					}
					if (py < 0) {
						py = 0;
					}
					view.setPosX(px);
					view.setPosY(py);
					vp.setViewPosition(new Point(px, py));
					viewEditor.changed();
					repaint();
				}
			}
		}
	}

	class GridSize extends FlatComboBox<View.GridType> {
		private static final long serialVersionUID = 1L;

		GridSize() {
			for (var gt : View.GridType.values()) {
				addItem(gt);
			}
			setFocusable(false);
			this.setToolTipText("Select the size of the grid.");
			setPreferredSize(new Dimension(100, 30));
			setMaximumSize(new Dimension(100, 30));
			setSelectedItem(view.getGridType());
			addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					view.setGridType((GridType) getSelectedItem());
					viewEditor.changed();
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							viewEditor.repaint();
						}
					});
				}
			});
		}
	}

	class ToggleGrid extends FlatToggleButton {
		private static final long serialVersionUID = 1L;

		ToggleGrid() {
			this.setButtonType(ButtonType.borderless);
			this.setSelectedIcon(ImageUtilities.getIcon(FluentUiFilledAL.BORDER_ALL_24,
					ImageUtilities.TOOLBAR_ICON_COLOR, ImageUtilities.TOOL_ICON_SIZE));
			this.setIcon(ImageUtilities.getIcon(FluentUiRegularAL.BORDER_ALL_24,
					ImageUtilities.TOOLBAR_ICON_COLOR_MUTED, ImageUtilities.TOOL_ICON_SIZE));
			this.setToolTipText("Toggles the display of the grid.");
			this.setFocusable(false);

			this.setSelected(view.isDrawGrid());
			addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					view.setDrawGrid(isSelected());
					viewEditor.changed();
				}
			});
		}

	}

	class ToggleSnap extends FlatToggleButton {
		private static final long serialVersionUID = 1L;

		ToggleSnap() {
			this.setButtonType(ButtonType.borderless);
			this.setSelectedIcon(ImageUtilities.getIcon(FluentUiFilledMZ.OFFICE_APPS_28,
					ImageUtilities.TOOLBAR_ICON_COLOR, ImageUtilities.TOOL_ICON_SIZE));
			this.setIcon(ImageUtilities.getIcon(FluentUiFilledMZ.OFFICE_APPS_28,
					ImageUtilities.TOOLBAR_ICON_COLOR_MUTED, ImageUtilities.TOOL_ICON_SIZE));
			this.setToolTipText("Toggles snapping to the grid.");
			this.setFocusable(false);

			this.setSelected(view.isSnapGrid());
			addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					view.setSnapGrid(isSelected());
					viewEditor.changed();
				}
			});
		}

	}

	
	private void updatePropertiesSeen() {
		if (toggleProperties.isSelected()) {
			split.setDividerLocation(split.getWidth()-300);
		} else {
			split.setDividerLocation(split.getWidth());
		}
	}
	
	class ToggleProperties extends FlatToggleButton {
		private static final long serialVersionUID = 1L;

		ToggleProperties() {
			this.setButtonType(ButtonType.borderless);
			this.setSelectedIcon(ImageUtilities.getIcon(FluentUiFilledAL.CARET_RIGHT_24,
					ImageUtilities.TOOLBAR_ICON_COLOR, ImageUtilities.TOOL_ICON_SIZE));
			this.setIcon(ImageUtilities.getIcon(FluentUiRegularAL.CARET_LEFT_24,
					ImageUtilities.TOOLBAR_ICON_COLOR, ImageUtilities.TOOL_ICON_SIZE));
			this.setToolTipText("Toggles the properties editor panel.");
			this.setFocusable(false);

			this.setSelected(view.isDrawGrid());
			addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					updatePropertiesSeen();
//					
//					if (split.getDividerLocation()==split.getWidth()) {
//						split.setDividerLocation(split.getWidth()-300);
//					} else {
//						split.setDividerLocation(split.getWidth());
//					}
				}
			});
		}

	}
	
	class ZoomIn extends AbstractAction {
		private static final long serialVersionUID = 1L;

		ZoomIn() {
			putValue(SMALL_ICON, ImageUtilities.getIcon(FluentUiRegularMZ.ZOOM_IN_24, ImageUtilities.TOOLBAR_ICON_COLOR,
					ImageUtilities.TOOL_ICON_SIZE));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			view.setScale(view.getScale() + 0.1f);

			if (view.getScale() > 2) {
				view.setScale(2);
			}
			viewEditor.changed();
			repaint();
		}

	}

	class ZoomOne extends AbstractAction {
		private static final long serialVersionUID = 1L;

		ZoomOne() {
			putValue(SMALL_ICON, ImageUtilities.getIcon(FluentUiRegularMZ.SEARCH_24, ImageUtilities.TOOLBAR_ICON_COLOR,
					ImageUtilities.TOOL_ICON_SIZE));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			view.setScale(1);
			viewEditor.changed();
			repaint();
		}

	}

	class ZoomOut extends AbstractAction {
		private static final long serialVersionUID = 1L;

		ZoomOut() {
			putValue(SMALL_ICON, ImageUtilities.getIcon(FluentUiRegularMZ.ZOOM_OUT_24,
					ImageUtilities.TOOLBAR_ICON_COLOR, ImageUtilities.TOOL_ICON_SIZE));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			// Down
			view.setScale(view.getScale() - 0.1f);
			if (view.getScale() < .25) {
				view.setScale(.25);
			}
			viewEditor.changed();
			repaint();
		}

	}

}
