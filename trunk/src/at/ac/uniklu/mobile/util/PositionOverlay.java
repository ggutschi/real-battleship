package at.ac.uniklu.mobile.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import at.ac.uniklu.mobile.R;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;


public class PositionOverlay extends com.google.android.maps.Overlay {
	
	private GeoPoint location;
	private Context context;
	
	public PositionOverlay(GeoPoint location, Context context) {
		super();
		
		this.location = location;
		this.context = context;
	}

    @Override
    public void draw(Canvas canvas, MapView mapView, boolean shadow) {
        super.draw(canvas, mapView, shadow);

        if (!shadow) {

            Point point = new Point();
            mapView.getProjection().toPixels(location, point);

            Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.fadenkreuz);
            
            int x = point.x - bmp.getWidth() / 2;
            int y = point.y - bmp.getHeight() / 2;
        
            canvas.drawBitmap(bmp, x, y, null);
        }
    }
}