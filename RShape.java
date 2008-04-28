package geomerative ;
import processing.core.*;


/**
 * RShape is a reduced interface for creating, holding and drawing complex Shapes. Shapes are groups of one or more subshapes (RSubshape).  Shapes can be selfintersecting and can contain holes.  This interface also allows you to transform shapes into polygons by segmenting the curves forming the shape.
 * @eexample RShape
 * @usage Geometry
 * @related RSubshape
 */
public class RShape extends RGeomElem
{
	/**
	* @invisible
	*/
	public int type = RGeomElem.SHAPE;

	/**
	* Array of RSubshape objects holding the subshapes of the polygon. 
 	* @eexample subshapes
 	* @related RSubshape
 	* @related countSubshapes ( )
 	* @related addSubshape ( )
 	*/
	public RSubshape[] subshapes;
	int currentSubshape = 0;

   	// ----------------------
   	// --- Public Methods ---
   	// ----------------------
	
	/**
	 * Use this method to create a new empty shape.
	 * @eexample RShape
	 */
	public RShape(){
		this.subshapes= null;
		type = RGeomElem.SHAPE;
	}

	public RShape(RSubshape newsubshape){
		this.append(newsubshape);
		type = RGeomElem.SHAPE;
	}

	public RShape(RShape s){
		for(int i=0;i<s.countSubshapes();i++){
			this.append(new RSubshape(s.subshapes[i]));
		}
		type = RGeomElem.SHAPE;
                this.id = s.id;
                this.texture = s.texture;
                this.fillColour = s.fillColour;
                this.strokeColour = s.strokeColour;

	}

	/**
	* Use this method to create a new circle shape. 
 	* @eexample createRect
 	* @param x float, the x position of the rectangle
 	* @param y float, the y position of the rectangle
 	* @param w float, the width of the rectangle
 	* @param h float, the height of the rectangle
 	* @return RShape, the rectangular shape just created
 	*/
        static public RShape createRect(float x, float y, float w, float h){
		RShape rect = new RShape();
		rect.addMoveTo(x, y);
		rect.addLineTo(x+w, y);
		rect.addLineTo(x+w, y+h);
		rect.addLineTo(x, y+h);
		rect.addLineTo(x, y);
		return rect;
	}
        
        /**
	* Use this method to create a new elliptical shape. 
 	* @eexample createEllipse
 	* @param x float, the x position of the ellipse
 	* @param y float, the y position of the ellipse
 	* @param rx float, the horizontal radius of the ellipse
 	* @param ry float, the vertical radius of the ellipse
 	* @return RShape, the elliptical shape just created
 	*/
        static public RShape createEllipse(float x, float y, float rx, float ry){
		RPoint center = new RPoint(x,y);
		RShape circle = new RShape();
		float kx = (((8F/(float)Math.sqrt(2F))-4F)/3F) * rx;
		float ky = (((8F/(float)Math.sqrt(2F))-4F)/3F) * ry;
		circle.addMoveTo(center.x, center.y - ry);
		circle.addBezierTo(center.x+kx, center.y-ry, center.x+rx, center.y-ky, center.x+rx, center.y);
		circle.addBezierTo(center.x+rx, center.y+ky, center.x+kx, center.y+ry, center.x, center.y+ry);
		circle.addBezierTo(center.x-kx, center.y+ry, center.x-rx, center.y+ky, center.x-rx, center.y);
		circle.addBezierTo(center.x-rx, center.y-ky, center.x-kx, center.y-ry, center.x, center.y-ry);
                circle.addClose();
		return circle;
	}

        static public RShape createCircle(float x, float y, float r){
          return createEllipse(x, y, r, r);
        }

  /**
   * Use this method to get the centroid of the element.
   * @eexample RGroup_getCentroid
   * @return RPoint, the centroid point of the element
   * @related getBounds ( )
   * @related getCenter ( )
   */
  public RPoint getCentroid(){
    RPoint bestCentroid = new RPoint();
    float bestArea = Float.NEGATIVE_INFINITY;
    if(subshapes != null){
      for(int i=0;i<subshapes.length;i++)
        {
          float area = Math.abs(subshapes[i].getArea());
          if(area > bestArea){
            bestArea = area;
            bestCentroid = subshapes[i].getCentroid();
          }
        }
      return bestCentroid;
    }
    return null;
  }
	
	/**
	* Use this method to count the number of subshapes in the polygon. 
 	* @eexample countSubshapes
 	* @return int, the number countours in the polygon.
 	* @related addSubshape ( )
 	*/
	public int countSubshapes(){
		if(this.subshapes==null){
			return 0;
		}
		
		return this.subshapes.length;
	}
	
	/**
	* Use this method to add a new shape.  The subshapes of the shape we are adding will simply be added to the current shape.
	* @eexample addShape
	* @param s RShape, the shape to be added.
 	* @related setSubshape ( )
	* @related addMoveTo ( )
	* @invisible
 	*/
	public void addShape(RShape s){
		for(int i=0;i<s.countSubshapes();i++){
			this.append(s.subshapes[i]);
		}
	}

	/**
	* Use this method to create a new subshape.  The first point of the new subshape will be set to (0,0).  Use addMoveTo ( ) in order to add a new subshape with a different first point.
	* @eexample addSubshape
	* @param s RSubshape, the subshape to be added.
 	* @related setSubshape ( )
	* @related addMoveTo ( )
 	*/
	public void addSubshape(){
		this.append(new RSubshape());
	}
	
	public void addSubshape(RSubshape s){
		this.append(s);
	}
	
	/**
	* Use this method to set the current subshape. 
	* @eexample setSubshape
 	* @related addMoveTo ( )
 	* @related addLineTo ( )
	* @related addQuadTo ( )
 	* @related addBezierTo ( )
 	* @related addSubshape ( )
 	*/
	public void setSubshape(int indSubshape){
		this.currentSubshape = indSubshape;
	}
	
	/**
	* Use this method to add a new moveTo command to the shape.  The command moveTo acts different to normal commands, in order to make a better analogy to its borthers classes Polygon and Mesh.  MoveTo creates a new subshape in the shape.  It's similar to adding a new contour to a polygon.
	* @eexample addMoveTo
	* @param endx float, the x coordinate of the first point for the new subshape.
	* @param endy float, the y coordinate of the first point for the new subshape.
 	* @related addLineTo ( )
	* @related addQuadTo ( )
 	* @related addBezierTo ( )
 	* @related addSubshape ( )
 	* @related setSubshape ( )
 	*/
	public void addMoveTo(float endx, float endy){
		if (subshapes == null){
			this.append(new RSubshape(endx,endy));
		}else if(subshapes[currentSubshape].countCommands() == 0){
			this.subshapes[currentSubshape].lastPoint = new RPoint(endx,endy);
		}else{
			this.addClose();
			this.append(new RSubshape(endx,endy));
		}
	}

	/**
	* Use this method to add a new lineTo command to the current subshape.  This will add a line from the last point added to the point passed as argument.
	* @eexample addLineTo
	* @param endx float, the x coordinate of the ending point of the line.
	* @param endy float, the y coordinate of the ending point of the line.
 	* @related addMoveTo ( )
	* @related addQuadTo ( )
 	* @related addBezierTo ( )
 	* @related addSubshape ( )
 	* @related setSubshape ( )
 	*/
	public void addLineTo(float endx, float endy){
		if (subshapes == null) {
			this.append(new RSubshape());
		}
		this.subshapes[currentSubshape].addLineTo(endx, endy);
	}

	/**
	* Use this method to add a new quadTo command to the current subshape.  This will add a quadratic bezier from the last point added with the control and ending points passed as arguments.
	* @eexample addQuadTo
	* @param cp1x float, the x coordinate of the control point of the bezier.
	* @param cp1y float, the y coordinate of the control point of the bezier.
	* @param endx float, the x coordinate of the ending point of the bezier.
	* @param endy float, the y coordinate of the ending point of the bezier.
 	* @related addMoveTo ( )
 	* @related addLineTo ( )
 	* @related addBezierTo ( )
 	* @related addSubshape ( )
 	* @related setSubshape ( )
 	*/
	public void addQuadTo(float cp1x, float cp1y, float endx, float endy){
		if (subshapes == null) {
			this.append(new RSubshape());
		}
		this.subshapes[currentSubshape].addQuadTo(cp1x,cp1y,endx,endy);
	}

	/**
	* Use this method to add a new bezierTo command to the current subshape.  This will add a cubic bezier from the last point added with the control and ending points passed as arguments.
	* @eexample addArcTo
	* @param cp1x float, the x coordinate of the first control point of the bezier.
	* @param cp1y float, the y coordinate of the first control point of the bezier.
	* @param cp2x float, the x coordinate of the second control point of the bezier.
	* @param cp2y float, the y coordinate of the second control point of the bezier.
	* @param endx float, the x coordinate of the ending point of the bezier.
	* @param endy float, the y coordinate of the ending point of the bezier.
 	* @related addMoveTo ( )
 	* @related addLineTo ( )
 	* @related addQuadTo ( )
 	* @related addSubshape ( )
 	* @related setSubshape ( )
 	*/
	public void addBezierTo(float cp1x, float cp1y, float cp2x, float cp2y, float endx, float endy){
		if (subshapes == null) {
			this.append(new RSubshape());
		}
		this.subshapes[currentSubshape].addBezierTo(cp1x,cp1y,cp2x,cp2y,endx,endy);
	}
	
  public void addClose(){
    if (subshapes == null) {
      this.append(new RSubshape());
    }
    this.subshapes[currentSubshape].addClose();
  }
	
	/**
	* Use this method to create a new mesh from a given polygon. 
	* @eexample toMesh
	* @return RMesh, the mesh made of tristrips resulting of a tesselation of the polygonization followd by tesselation of the shape.
 	* @related draw ( )
 	*/
	public RMesh toMesh(){
		RPolygon p = this.toPolygon();
		return p.toMesh();
	}

	/**
	* Use this method to create a new polygon from a given shape. 
	* @eexample toPolygon
	* @return RPolygon, the polygon resulting of the segmentation of the commands in each subshape.
 	* @related draw ( )
 	*/
	public RPolygon toPolygon(){
		int numSubshapes = countSubshapes();
		RPolygon result = new RPolygon();
		for(int i=0;i<numSubshapes;i++){
			RPoint[] newpoints = this.subshapes[i].getCurvePoints();
			result.addContour(new RContour(newpoints));
		}
                result.texture = this.texture;
                result.id = this.id;
		return result;
	}

	/**
	* @invisible
	*/
	public RShape toShape(){
		return this;
	}

	/**
	* Use this method to get the bounding box of the shape. 
	* @eexample getBounds
	* @return RContour, the bounding box of the shape in the form of a fourpoint contour
 	* @related getCenter ( )
 	*/
	public RContour getBounds(){
      		float xmin =  Float.MAX_VALUE ;
      		float ymin =  Float.MAX_VALUE ;
      		float xmax = -Float.MAX_VALUE ;
      		float ymax = -Float.MAX_VALUE ;
      		
      		for(int j=0;j<this.countSubshapes();j++){
      			for( int i = 0 ; i < this.subshapes[j].countCommands() ; i++ )
      			{
				RPoint[] points = this.subshapes[j].commands[i].getPoints();
				if(points!=null){
					for( int k = 0 ; k < points.length ; k++ ){
						float x = points[k].x;
						float y = points[k].y;
						if( x < xmin ) xmin = x;
						if( x > xmax ) xmax = x;
						if( y < ymin ) ymin = y;
						if( y > ymax ) ymax = y;
					}
				}
      			}
      		}
      			
      		RContour c = new RContour();
      		c.addPoint(xmin,ymin);
      		c.addPoint(xmin,ymax);
      		c.addPoint(xmax,ymax);
      		c.addPoint(xmax,ymin);
      		return c;
	}

	/**
	* Use this method to get the center point of the shape. 
	* @eexample RShape_getCenter
	* @return RPoint, the center point of the shape
 	* @related getBounds ( )
 	*/
	public RPoint getCenter(){
		RContour c = getBounds();
		return new RPoint((c.points[2].x + c.points[0].x)/2,(c.points[2].y + c.points[0].y)/2);
	}

	/**
	 * Use this to return the start, control and end points of the shape.  It returns the points in the way of an array of RPoint.
	 * @eexample RShape_getPoints
	 * @return RPoint[], the start, control and end points returned in an array.
	 * */
	public RPoint[] getPoints(){
          int numSubshapes = countSubshapes();
          if(numSubshapes == 0){
            return null;
          }
          
          RPoint[] result=null;
          RPoint[] newresult=null;
          for(int i=0;i<numSubshapes;i++){
            RPoint[] newPoints = subshapes[i].getPoints();
            if(newPoints!=null){
              if(result==null){
                result = new RPoint[newPoints.length];
                System.arraycopy(newPoints,0,result,0,newPoints.length);
              }else{
                newresult = new RPoint[result.length + newPoints.length];
                System.arraycopy(result,0,newresult,0,result.length);
                System.arraycopy(newPoints,0,newresult,result.length,newPoints.length);
                result = newresult;
              }
            }
          }
          return result;
	}

	/**
	 * Use this to return the start, control and end points of the shape.  It returns the points in the way of an array of RPoint.
	 * @eexample RShape_getCurvePoints
	 * @return RPoint[], the start, control and end points returned in an array.
	 * */
	public RPoint[] getCurvePoints(){
          int numSubshapes = countSubshapes();
          if(numSubshapes == 0){
            return null;
          }
          
          RPoint[] result=null;
          RPoint[] newresult=null;
          for(int i=0;i<numSubshapes;i++){
            RPoint[] newPoints = subshapes[i].getCurvePoints();
            if(newPoints!=null){
              if(result==null){
                result = new RPoint[newPoints.length];
                System.arraycopy(newPoints,0,result,0,newPoints.length);
              }else{
                newresult = new RPoint[result.length + newPoints.length];
                System.arraycopy(result,0,newresult,0,result.length);
                System.arraycopy(newPoints,0,newresult,result.length,newPoints.length);
                result = newresult;
              }
            }
          }
          return result;
	}


	/**
	* Use this method to get the type of element this is.
	* @eexample RShape_getType
	* @return int, will allways return RGeomElem.SHAPE
	*/
	public int getType(){
		return type;
	}

        public void print(){
          System.out.println("subshapes: ");
          for(int i=0;i<subshapes.length;i++)
            {
              System.out.println("--- subshape "+i+" ---");
              subshapes[i].print();
              System.out.println("---------------");
            }
        }

	/**
	* Use this method to draw the shape. 
	* @eexample drawShape
	* @param g PGraphics, the graphics object on which to draw the shape
	*/
	public void draw(PGraphics g){
          int numSubshapes = countSubshapes();

          if(numSubshapes!=0){
            if(isIn(g)) {
              // By default always drawy with an ADAPTATIVE segmentator
              int lastSegmentator = RCommand.segmentType;
              RCommand.setSegmentator(RCommand.ADAPTATIVE);            

              // Check whether to draw the fill or not
              if(g.fill){
                // Since we are drawing the different tristrips we must turn off the stroke or make it the same color as the fill
                // NOTE: there's currently no way of drawing the outline of a mesh, since no information is kept about what vertices are at the edge
		
                // Save the information about the current stroke color and turn off
                boolean stroking = g.stroke;
                g.noStroke();
		
                // Save smoothing state and turn off
                boolean smoothing = g.smooth;
                try{
                  if(smoothing){
                    g.noSmooth();
                  }
                }catch(Exception e){
                }
		
                RMesh tempMesh = this.toMesh();
                tempMesh.draw(g);
		
                // Restore the old stroke color
                if(stroking) g.stroke(g.strokeColor);
                
                // Restore the old smoothing state
                try{
                  if(smoothing){
                    g.smooth();
                  }
                }catch(Exception e){
                }
              }
              // Check whether to draw the stroke or not
              if(g.stroke){
                boolean beforeFill = g.fill;
                g.noFill();
                for(int i=0;i<numSubshapes;i++){
                  subshapes[i].draw(g);
                }
                if(beforeFill)
                  g.fill(g.fillColor);
              }
             
              // Restore the user set segmentator
              RCommand.setSegmentator(lastSegmentator);                    
            }            
          }
	}

        public void draw(PApplet g){
          int numSubshapes = countSubshapes();
          
          if(numSubshapes!=0){
            if(isIn(g)) {
              // By default always drawy with an ADAPTATIVE segmentator
              int lastSegmentator = RCommand.segmentType;
              RCommand.setSegmentator(RCommand.ADAPTATIVE);

              // Check whether to draw the fill or not
              if(g.g.fill){
                // Since we are drawing the different tristrips we must turn off the stroke or make it the same color as the fill
                // NOTE: there's currently no way of drawing the outline of a mesh, since no information is kept about what vertices are at the edge
		
                // Save the information about the current stroke color and turn off
                boolean stroking = g.g.stroke;
                g.noStroke();
		
                // Save smoothing state and turn off
                boolean smoothing = g.g.smooth;
                try{
                  if(smoothing){
                    g.noSmooth();
                  }
                }catch(Exception e){
                }
		
                RMesh tempMesh = this.toMesh();
                tempMesh.draw(g);
		
                // Restore the old stroke color
                if(stroking) g.stroke(g.g.strokeColor);
                
                // Restore the old smoothing state
                try{
                  if(smoothing){
                    g.smooth();
                  }
                }catch(Exception e){
                }
              }
              // Check whether to draw the stroke or not
              if(g.g.stroke){
                boolean beforeFill = g.g.fill;
                g.noFill();
                for(int i=0;i<numSubshapes;i++){
                  subshapes[i].draw(g);
                }
                if(beforeFill)
                  g.fill(g.g.fillColor);
              }
              
              // Restore the user set segmentator
              RCommand.setSegmentator(lastSegmentator);
            }
          }
	}

	/**
	 * Use this method to know if the shape is inside a graphics object. This might be useful if we want to delete objects that go offscreen.
	 * @eexample RShape_isIn
	 * @usage Geometry
	 * @param PGraphics g, the graphics object
	 * @return boolean, whether the shape is in or not the graphics object
	 */
	public boolean isIn(PGraphics g){
		RContour c = getBounds();
		float x0 = g.screenX(c.points[0].x,c.points[0].y);
		float y0 = g.screenY(c.points[0].x,c.points[0].y);
		float x1 = g.screenX(c.points[1].x,c.points[1].y);
		float y1 = g.screenY(c.points[1].x,c.points[1].y);
		float x2 = g.screenX(c.points[2].x,c.points[2].y);
		float y2 = g.screenY(c.points[2].x,c.points[2].y);
		float x3 = g.screenX(c.points[3].x,c.points[3].y);
		float y3 = g.screenY(c.points[3].x,c.points[3].y);

		float xmax = Math.max(Math.max(x0,x1),Math.max(x2,x3));
		float ymax = Math.max(Math.max(y0,y1),Math.max(y2,y3));
		float xmin = Math.min(Math.min(x0,x1),Math.min(x2,x3));
		float ymin = Math.min(Math.min(y0,y1),Math.min(y2,y3));

		return !((xmax < 0 || xmin > g.width) && (ymax < 0 || ymin > g.height));
	}

	public boolean isIn(PApplet g){
		RContour c = getBounds();
		float x0 = g.screenX(c.points[0].x,c.points[0].y);
		float y0 = g.screenY(c.points[0].x,c.points[0].y);
		float x1 = g.screenX(c.points[1].x,c.points[1].y);
		float y1 = g.screenY(c.points[1].x,c.points[1].y);
		float x2 = g.screenX(c.points[2].x,c.points[2].y);
		float y2 = g.screenY(c.points[2].x,c.points[2].y);
		float x3 = g.screenX(c.points[3].x,c.points[3].y);
		float y3 = g.screenY(c.points[3].x,c.points[3].y);

		float xmax = Math.max(Math.max(x0,x1),Math.max(x2,x3));
		float ymax = Math.max(Math.max(y0,y1),Math.max(y2,y3));
		float xmin = Math.min(Math.min(x0,x1),Math.min(x2,x3));
		float ymin = Math.min(Math.min(y0,y1),Math.min(y2,y3));

		return !((xmax < 0 || xmin > g.width) && (ymax < 0 || ymin > g.height));
	}
   	/**
	* Use this method to transform the shape.
	* @eexample RShape_transform
	* @param m RMatrix, the affine transformation to apply to the shape
 	* @related draw ( )
 	*/
        /*
	public void transform(RMatrix m){
          int numSubshapes = countSubshapes();
          if(numSubshapes!=0){
            for(int i=0;i<numSubshapes;i++){
              subshapes[i].transform(m);
            }
          }
	}
        */
	
	// ----------------------
	// --- Private Methods ---
	// ----------------------

	/**
	* Remove all of the subshapes.  Creates an empty shape.
	*/
   	void clear(){
   		this.subshapes = null;
	}
	
   	/**
	* Returns the bounding box of the polygon. 
 	*/
	/*
	RRectangle getBounds(){
		if( this.subshapes == null )
      		{
         		return new RRectangle();
      		}
      		else if( this.subshapes.length == 1 )
      		{
         		
      			float xmin =  Float.MAX_VALUE ;
      			float ymin =  Float.MAX_VALUE ;
      			float xmax = -Float.MAX_VALUE ;
      			float ymax = -Float.MAX_VALUE ;
      
      			for( int i = 0 ; i < this.subshapes[0].points.length ; i++ )
      			{
				float x = this.subshapes[0].points[i].getX();
         			float y = this.subshapes[0].points[i].getY();
         			if( x < xmin ) xmin = x;
         			if( x > xmax ) xmax = x;
         			if( y < ymin ) ymin = y;
         			if( y > ymax ) ymax = y;
      			}
      
      			return new RRectangle( xmin, ymin, (xmax-xmin), (ymax-ymin) );
      		}
      		else
      		{
         		throw new UnsupportedOperationException("getBounds not supported on complex poly.");
      		}
        }
	*/
   	void append(RSubshape nextsubshape)
	{
	  RSubshape[] newsubshapes;
	  if(subshapes==null){
	  	newsubshapes = new RSubshape[1];
	  	newsubshapes[0] = nextsubshape;
	  	currentSubshape = 0;
	  }else{
	  	newsubshapes = new RSubshape[this.subshapes.length+1];
	  	System.arraycopy(this.subshapes,0,newsubshapes,0,this.subshapes.length);
	  	newsubshapes[this.subshapes.length]=nextsubshape;
	  	currentSubshape++;
	  }
	  this.subshapes=newsubshapes;
	}
}