package com.inkus.infomancerforge.beans.views;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import com.inkus.infomancerforge.beans.FileGameObject;
import com.inkus.infomancerforge.beans.NamedResource;
import com.inkus.infomancerforge.beans.gobs.GOBReferance;

public class View implements FileGameObject,NamedResource,Serializable {
	private static final long serialVersionUID = 1L;

	public enum GridType {
		Small(10),
		Normal(20),
		Large(40);
		
		int gridSize=20;
		
		GridType(int gridSize){
			this.gridSize=gridSize;
		}
		
		public int gridSize() {
			return gridSize;
		}
	}
	
	private String uuid;
	private String name;
	private transient boolean changed=false;
	
	private double scale=1;
	private int posX=0;
	private int posY=0;
	private int canvasWidth=5000;
	private int canvasHeight=5000;
	private boolean drawGrid=true;
	private boolean snapGrid=true;
	private GridType gridType=GridType.Normal;

	private List<GobView> gobs=new ArrayList<>();
	transient int gobViewsInitDone=-1;

	public View() {
		uuid=UUID.randomUUID().toString();
	}

	@Override
	public String getUuid() {
		return uuid;
	}

	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public boolean isNamed() {
		return true;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public boolean hasChanges() {
		return changed;
	}

	@Override
	public void touch() {
		changed=true;
	}

	@Override
	public void saved() {
		changed=false;
	}
	
	public void init() {
		if (gobViewsInitDone!=gobs.size()) {
			synchronized (this) {
				if (gobViewsInitDone!=gobs.size()) {
					System.out.println("Setting view");
					for (var g:gobs) {
						g.setView(this);
					}
					gobViewsInitDone=gobs.size();
				}				
			}
		}
	}
	
	public GobView findReferance(GOBReferance gobReferance) {
		for (var g:getGobs()) {
			if (g.getGobReferance().equals(gobReferance)) {
				return g;
			}
		}
		return null;
	}
	
	public List<GobView> getGobs() {
		init();
		return gobs;
	}

	public void setGobs(List<GobView> gobs) {
		this.gobs = gobs;
		gobViewsInitDone=-1;
		init();
	}

	public List<ViewDrawable> getDrawables() {
		init();
		return new ArrayList<>(gobs);
	}

	public double getScale() {
		return scale;
	}

	public void setScale(double scale) {
		this.scale = scale;
	}

	public int getPosX() {
		return posX;
	}

	public void setPosX(int posX) {
		this.posX = posX;
	}

	public int getPosY() {
		return posY;
	}

	public void setPosY(int posY) {
		this.posY = posY;
	}

	public boolean isDrawGrid() {
		return drawGrid;
	}

	public void setDrawGrid(boolean drawGrid) {
		this.drawGrid = drawGrid;
	}

	public boolean isSnapGrid() {
		return snapGrid;
	}

	public void setSnapGrid(boolean snapGrid) {
		this.snapGrid = snapGrid;
	}

	public GridType getGridType() {
		return gridType;
	}

	public void setGridType(GridType gridType) {
		this.gridType = gridType;
	}

	public int getCanvasWidth() {
		return canvasWidth;
	}

	public void setCanvasWidth(int canvasWidth) {
		this.canvasWidth = canvasWidth;
	}

	public int getCanvasHeight() {
		return canvasHeight;
	}

	public void setCanvasHeight(int canvasHeight) {
		this.canvasHeight = canvasHeight;
	}

	@Override
	public int hashCode() {
		return Objects.hash(canvasHeight, canvasWidth, drawGrid, gobs, gridType, name, posX, posY, scale, snapGrid,
				uuid);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		View other = (View) obj;
		return canvasHeight == other.canvasHeight && canvasWidth == other.canvasWidth && drawGrid == other.drawGrid
				&& Objects.equals(gobs, other.gobs) && gridType == other.gridType && Objects.equals(name, other.name)
				&& posX == other.posX && posY == other.posY
				&& Double.doubleToLongBits(scale) == Double.doubleToLongBits(other.scale) && snapGrid == other.snapGrid
				&& Objects.equals(uuid, other.uuid);
	}

	
}
