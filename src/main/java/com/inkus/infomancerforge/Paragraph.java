package com.inkus.infomancerforge;

import java.awt.font.TextLayout;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class Paragraph {

	public Alignment overrideAlignment=null;
	
	public float maxWidth=0;
	public float maxHeight=0;
	public float fontSize=0;
	
	public List<Float> lineAscent=new ArrayList<>();
	public List<Float> lineLeading=new ArrayList<>();
	public List<Point2D> linePositions=new ArrayList<>();
	public List<TextLayout> textLayouts=new ArrayList<>();

	public Paragraph() {
	}
}
