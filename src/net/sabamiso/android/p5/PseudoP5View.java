//
//	PseudoP5View.java - a view implementation like PApplet class in processing.org.  
//
//  example;
//     public class SendOSCView extends PseudoP5View {
//         public SendOSCView(Context context) {
//             super(context);
//         }
//         
//         @Override
//         protected void setup() {
//             size(640, 480);
//             frameRate(30);
//         }
//     
//         @Override
//         protected void draw() {
//             background(0, 0, 0);
//             
//             noStroke();
//             fill(255, 255, 255);
//     
//             for (int i = 0; i < 100; ++i) {
//                 float x = random(width);
//                 float y = random(height);
//                 ellipse(x, y, 20, 20);
//             }
//         }
//     }
//
//  license:
//      Copyright (c) 2015 yoggy <yoggy0@gmail.com>
//      Released under the MIT license
//      http://opensource.org/licenses/mit-license.php
//
package net.sabamiso.android.p5;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public abstract class PseudoP5View extends SurfaceView implements
		SurfaceHolder.Callback {

	/////////////////////////////////////////////////////////////////////////////
	//
	// processing like variables (protected)
	//
	/////////////////////////////////////////////////////////////////////////////
	protected int width;
	protected int height;
	protected int frameCount;
	protected float frameRate;
	protected float mouseX;
	protected float mouseY;
	protected boolean mousePressed;

	public static final float PI = (float) Math.PI;
	public static final int CENTER = 1;

	/////////////////////////////////////////////////////////////////////////////
	//
	// processing like functions (protected)
	//
	/////////////////////////////////////////////////////////////////////////////

	protected abstract void setup();
	protected abstract void draw();

	//
	// for events
	//
	protected void mousePressed() {
	}

	protected void mouseReleased() {
	}

	//
	// for sketch settings
	//
	protected void size(int width, int height) {
		this.width = width;
		this.height = height;
	}

	protected void frameRate(int fps) {
		frame_rate = fps;

		frameRate = fps;
		frame_rate_counter_ = 0;
	}

	//
	// for draw
	//
	protected void background(int color) {
		if (cc == null)
			return;

		resetSketchScale();

		Paint p = new Paint();
		p.setColor(color | 0xff000000);
		cc.drawRect(new Rect(0, 0, getWidth(), getHeight()), p);

		adjustSketchScale();
	}

	protected void background(int r, int g, int b) {
		background(rgb2Int(r, g, b));
	}

	protected void strokeWeight(float weight) {
		current_stroke_w = weight;
	}

	protected void noFill() {
		enable_fill = false;
	}

	protected void fill(int c) {
		fill(c, c, c, 255);
	}

	protected void fill(int c, int a) {
		fill(c, c, c, a);
	}

	protected void fill(int r, int g, int b) {
		fill(r, g, b, 255);
	}

	protected void fill(int r, int g, int b, int a) {
		fillImpl(rgb2Int(r, g, b), a);
	}

	protected void noStroke() {
		enable_stroke = false;
	}

	protected void stroke(int c) {
		stroke(c, c, c, 255);
	}

	protected void stroke(int r, int g, int b) {
		stroke(r, g, b, 255);
	}

	protected void stroke(int c, int a) {
		stroke(c, c, c, a);
	}

	protected void stroke(int r, int g, int b, int a) {
		strokeImpl(rgb2Int(r, g, b), a);
	}

	protected void line(int x1, int y1, int x2, int y2) {
		if (enable_stroke) {
			cc.drawLine(x1, y1, x2, y2, getStrokePaint());
		}
	}

	protected void line(float x1, float y1, float x2, float y2) {
		if (enable_stroke) {
			cc.drawLine(x1, y1, x2, y2, getStrokePaint());
		}
	}

	protected void rect(float x, float y, float w, float h) {
		if (enable_fill) {
			cc.drawRect(createRectF(x, y, w, h), getFillPaint());
		}
		if (enable_stroke) {
			cc.drawRect(createRectF(x, y, w, h), getStrokePaint());
		}
	}

	protected void rect(float x, float y, float w, float h, float r) {
		if (enable_fill) {
			cc.drawRoundRect(createRectF(x, y, w, h), r, r, getFillPaint());
		}
		if (enable_stroke) {
			cc.drawRoundRect(createRectF(x, y, w, h), r, r, getStrokePaint());
		}
	}

	protected void ellipse(float x, float y, float w, float h) {
		RectF r = createRectF(x - w / 2, y - h / 2, w, h);

		if (enable_fill) {
			cc.drawOval(r, getFillPaint());
		}
		if (enable_stroke) {
			cc.drawOval(r, getStrokePaint());
		}
	}

	protected void text(String str, float x, float y) {
		Paint p = getFillPaint();
		p.setTextSize(12);
		cc.drawText(str, x, y, p);
	}
	
	// matrix operations
	protected void pushMatrix() {
		cc.save();
	}

	protected void popMatrix() {
		cc.restore();
	}

	protected void translate(float dx, float dy) {
		cc.translate(dx, dy);
	}

	protected void rotate(float rad) {
		float deg = rad / (float) Math.PI * 180.0f;
		cc.rotate(deg);
	}

	protected void scale(float s) {
		scale(s, s);
	}

	protected void scale(float sx, float sy) {
		cc.scale(sx, sy);
	}

	//
	// utilities
	//
	protected String nfs(int val, int digit) {
		String zero = "";
		for (int i = 0; i < digit; ++i) {
			zero += 0;
		}

		String tmp = zero + Integer.toString(val);
		String result = tmp.substring(tmp.length() - digit, tmp.length());
		return result;
	}

	protected int unhex(String str) {
		return Integer.parseInt(str, 16);
	}

	protected float random(float range) {
		float r = (float) (Math.random() * range);
		return r;
	}

	public float random(float v0, float v1) {
		float diff = v1 - v0;
		float rv = random(diff) + v0;
		return rv;
	}

	protected float cos(float t) {
		return (float) Math.cos(t);
	}

	protected float sin(float t) {
		return (float) Math.sin(t);
	}

	protected float abs(float val) {
		return Math.abs(val);
	}

	protected void println(String str) {
		Log.d(getClass().getName(), str);
	}

	protected void print(String str) {
		Log.d(getClass().getName(), str);
	}

	/////////////////////////////////////////////////////////////////////////////
	//
	// private variables
	//
	/////////////////////////////////////////////////////////////////////////////
	protected Canvas cc;

	int frame_rate = 30;
	int frame_rate_counter_;
	long frame_rate_counter_start_time_;
	float screen_offset_scale;
	float screen_offset_x;
	float screen_offset_y;

	Matrix initial_matrix;

	boolean enable_stroke;
	int current_stroke_argb = 0xffffffff;
	float current_stroke_w = 1;

	boolean enable_fill;
	int current_fill_argb = 0xffffffff;

	boolean is_timer_enable = false;
	Thread draw_thread = null;

	/////////////////////////////////////////////////////////////////////////////
	//
	// constructor
	//
	/////////////////////////////////////////////////////////////////////////////
	public PseudoP5View(Context context) {
		super(context);
		getHolder().addCallback(this);
	}

	/////////////////////////////////////////////////////////////////////////////
	//
	// surface holder operations
	//
	/////////////////////////////////////////////////////////////////////////////
	SurfaceHolder surface_holder = null;

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		surface_holder = holder;
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		surface_holder = null;
	}

	/////////////////////////////////////////////////////////////////////////////
	//
	// draw thread (internal use only)
	//
	/////////////////////////////////////////////////////////////////////////////
	public void onResume() {
		timerStart();
	}

	public void onPause() {
		timerStop();
		mousePressed = false;
	}

	void timerStart() {
		is_timer_enable = true;

		draw_thread = new Thread(draw_task, "pseudo_p5_view_thread");
		draw_thread.start();
	}

	void timerStop() {
		is_timer_enable = false;
		try {
			draw_thread.join();
		} catch (InterruptedException e) {
		}
		draw_thread = null;
	}

	Runnable draw_task = new Runnable() {
		@Override
		public void run() {
			long st, draw_t;
			while (is_timer_enable) {
				if (surface_holder != null) {
					Canvas c = surface_holder.lockCanvas();
					st = System.currentTimeMillis();
					drawInternal(c);
					draw_t = System.currentTimeMillis() - st;
					surface_holder.unlockCanvasAndPost(c);
				} else {
					draw_t = 0;
				}

				long interval_t = (long) (1000 / frame_rate);
				long diff = interval_t - draw_t;
				if (diff > 0) {
					try {
						Thread.sleep(diff);
					} catch (InterruptedException e) {
					}
				}
			}
		}
	};

	protected void drawInternal(Canvas canvas) {
		this.cc = canvas;

		boolean rv = adjustSketchScale();
		if (rv == true) {
			draw();
		}
		cc.restore();

		frameCount++;

		frame_rate_counter_++;
		if (frame_rate_counter_ == 100) {
			double dt = (System.currentTimeMillis() - frame_rate_counter_start_time_) / 1000.0;
			double t = dt / 100;
			frameRate = (float) (1 / t);

			frame_rate_counter_ = 0;
			frame_rate_counter_start_time_ = System.currentTimeMillis();
		}
	}

	private boolean adjustSketchScale() {
		cc.save();

		int view_w = getWidth();
		int view_h = getHeight();

		if (view_w == 0 || view_h == 0)
			return false;
		if (width == 0 || height == 0)
			return false;

		float aspect_view = view_w / (float) view_h;
		float aspect_sketch = width / (float) height;

		if (aspect_view > aspect_sketch) {
			screen_offset_scale = view_h / (float) height;
			screen_offset_x = (view_w - width * screen_offset_scale) / 2;
			screen_offset_y = 0.0f;
		} else {
			screen_offset_scale = view_w / (float) width;
			screen_offset_x = 0.0f;
			screen_offset_y = (view_h - height * screen_offset_scale) / 2;
		}

		Matrix mat_trans = new Matrix();
		mat_trans.postTranslate(screen_offset_x, screen_offset_y);

		Matrix mat_scale = new Matrix();
		mat_scale.postScale(screen_offset_scale, screen_offset_scale);

		cc.concat(mat_trans);
		cc.concat(mat_scale);

		return true;
	}

	void resetSketchScale() {
		cc.restore();
	}

	/////////////////////////////////////////////////////////////////////////////
	//
	// events
	//
	/////////////////////////////////////////////////////////////////////////////
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent evt) {
		float screen_x = evt.getX();
		float screen_y = evt.getY();

		mouseX = (screen_x - screen_offset_x) / screen_offset_scale;
		mouseY = (screen_y - screen_offset_y) / screen_offset_scale;

		if (evt.getAction() == android.view.MotionEvent.ACTION_DOWN) {
			mousePressed = true;
			mousePressed();
		} else if (evt.getAction() == android.view.MotionEvent.ACTION_UP) {
			mousePressed = false;
			mouseReleased();
		}

		return true;
	}

	/////////////////////////////////////////////////////////////////////////////
	//
	// utils
	//
	/////////////////////////////////////////////////////////////////////////////
	void fillImpl(int c, int a) {
		enable_fill = true;
		current_fill_argb = c | ((a << 24) & 0xff000000);
	}

	void strokeImpl(int c, int a) {
		enable_stroke = true;
		current_stroke_argb = c | ((a << 24) & 0xff000000);
	}

	int rgb2Int(int r, int g, int b) {
		int c = ((r << 16) & 0x00ff0000) | ((g << 8) & 0x0000ff00)
				| ((b << 0) & 0x000000ff) | 0x00000000;
		return c;
	}

	RectF createRectF(float x, float y, float w, float h) {
		RectF r = new RectF(x, y, x + w, y + h);
		return r;
	}

	Paint getFillPaint() {
		Paint p = new Paint();
		p.setStyle(Style.FILL);
		p.setColor(current_fill_argb);
		p.setAntiAlias(true);
		return p;
	}

	Paint getStrokePaint() {
		Paint p = new Paint();
		p.setStyle(Style.STROKE);
		p.setStrokeWidth(current_stroke_w);
		p.setColor(current_stroke_argb);
		p.setAntiAlias(true);
		return p;
	}
}