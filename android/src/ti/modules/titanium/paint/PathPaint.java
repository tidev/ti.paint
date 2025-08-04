package ti.modules.titanium.paint;

import android.graphics.Path;
import android.graphics.Paint;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.util.TiConvert;

public class PathPaint {

	private Path myPath;
	private Paint myPaint;
	private Boolean isErease = false;

	public void setPaint(Paint p) {
			myPaint = p;
	}

	public Paint getPaint() {
			if (isErease) {
					myPaint.setAlpha(0xFF);
					myPaint.setColor(Color.TRANSPARENT);
					myPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
					myPaint.setColor(TiConvert.toColor("black", TiApplication.getAppCurrentActivity()));
			}
			return myPaint;
	}

	public Path getPath() {
			return myPath;
	}

	public void setPath(Path p) {
			myPath = p;
	}

	public Boolean getEarase() {
			return isErease;
	}

	public void setEarase(Boolean p) {
			isErease = p;
	}

	public PathPaint() {
			myPath = new Path();
			myPaint = new Paint();

			myPaint = new Paint();
			myPaint.setDither(true);
			myPaint.setAntiAlias(true);
			myPaint.setStyle(Paint.Style.STROKE);
			myPaint.setStrokeCap(Paint.Cap.ROUND);
			myPaint.setStrokeJoin(Paint.Join.ROUND);
			myPaint.setColor(TiConvert.toColor("black", TiApplication.getAppCurrentActivity()));
	}
}
