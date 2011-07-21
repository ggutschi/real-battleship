package at.ac.uniklu.mobile.util;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;
import at.ac.uniklu.mobile.ChallengeStartActivity;
import at.ac.uniklu.mobile.db.Challenge;
import at.ac.uniklu.mobile.db.Ship;
import at.ac.uniklu.mobile.db.ShipPosition;

import com.google.android.maps.MapView;

public class GridOverlay extends com.google.android.maps.Overlay {
	
	public final static float GRID_LINE_WIDTH = 3;
	
	public final static int GRID_COLOR = Color.WHITE;
	
	public final static int GRID_TRANSPARENCY = 200;	// value between 0 and 255
	
	public final static int UNCOVERED_CELL_COLOR = Color.WHITE;
	
	public final static int UNCOVERED_SHIP_COLOR = Color.BLACK;
	
	public final static int UNCOVERED_SHIPPOSITION_COLOR = Color.RED;
	
	public final static int UNCOVERED_NO_SHIPPOSITION_COLOR = Color.WHITE;

	public final static int UNCOVERED_SHIP_TRANSPARENCY = 150;	// value between 0 and 255
	
	private MapView mapView;
	private Challenge currentChallenge;
	
	public GridOverlay(MapView mapView, Challenge currentChallenge) {
		super();
    	Log.d(Constants.LOG_TAG, "GridOverlay constructor Challenge: " + this.currentChallenge);
	    	
		
		this.mapView = mapView;
		this.currentChallenge = currentChallenge;
	}

    @Override
    public void draw(Canvas canvas, MapView mapView, boolean shadow) {
        super.draw(canvas, mapView, shadow);


        if (!shadow) {

            Point point1 = new Point();
            Point point2 = new Point();
            
        	Paint paint = new Paint();
        	
        	paint.setColor(GRID_COLOR);
        	paint.setStrokeWidth(GRID_LINE_WIDTH);
        	paint.setAlpha(GRID_TRANSPARENCY);
        	
        	//Log.d(Constants.LOG_TAG, "currentChallenge: " + currentChallenge);
            
            mapView.getProjection().toPixels(currentChallenge.getLocationLeftTop(), point1);
            mapView.getProjection().toPixels(currentChallenge.getLocationRightBottom(), point2);
            
            float diffX = (point2.x - point1.x) / currentChallenge.getCellsX();	            
            float diffY = (point2.y - point1.y) / currentChallenge.getCellsY();
            
            if (diffX < 0)
            	diffX *= (-1);

            if (diffY < 0)
            	diffY *= (-1);
            
            // draw vertical lines
            for (float f = point1.x; f <= point2.x; f += diffX)
            	canvas.drawLine(f, point1.y, f, point2.y, paint);

            // draw horizontal lines
            for (float f = point1.y; f >= point2.y; f += diffY)
            	canvas.drawLine(point1.x, f, point2.x, f, paint);

    		paint.setColor(UNCOVERED_CELL_COLOR);
            paint.setAlpha(UNCOVERED_SHIP_TRANSPARENCY);
            
            // draw uncovered cells
            for (int i = 0; i < currentChallenge.getCellsY(); i++)
            	for (int j = 0; j < currentChallenge.getCellsX(); j++)
            		if (currentChallenge.isUncovered(j, i))
            			canvas.drawRect(point1.x + j * diffX, point1.y + i * diffY, point1.x + (j + 1) * diffX, point1.y + (i + 1) * diffY, paint);

            			
            for (Ship s : currentChallenge.getShips()) {
            	if (s.isDestroyed()) {
            		paint.setColor(UNCOVERED_SHIP_COLOR);
            	} else {
            		paint.setColor(UNCOVERED_SHIPPOSITION_COLOR);
            	}
                
                paint.setAlpha(UNCOVERED_SHIP_TRANSPARENCY);
            
            	for (ShipPosition sp: s.getShipPositions()) {
            		if (sp.isUncovered()) {
            			canvas.drawRect(point1.x + sp.getColumn() * diffX, point1.y + sp.getRow() * diffY, point1.x + (sp.getColumn() + 1) * diffX, point1.y + (sp.getRow() + 1) * diffY, paint);

            		}
            	}
            }
        }
    }
}
