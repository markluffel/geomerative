import geomerative.*;

RShape shp1;
RShape shp2;
RShape shp3;
RShape cursorShape;

void setup()
{
  size(400, 400);
  smooth();

  RGeomerative.init(this);

  shp1 = RShape.createRing(0, 0, 120, 50);
  shp2 = RShape.createStar(0, 0, 100.0, 80.0, 20);
}

void draw()
{
  background(255);    
  translate(width/2,height/2);

  cursorShape = new RShape(shp2);
  cursorShape.translate(mouseX - width/2, mouseY - height/2);
  
  // Only intersection() does not work for shapes with more than one subshape
  shp3 = shp1.diff( cursorShape );
  
  strokeWeight( 3 );

  if(mousePressed){
    fill( 220 , 0 , 0 , 30 );
    stroke( 120 , 0 , 0 );
    cursorShape.draw();

    fill( 0 , 220 , 0 , 30 );
    stroke( 0 , 120 , 0 );
    shp1.draw();
  }
  else{
    fill( 220 );
    stroke( 120 );
    shp3.draw();
  }
}
