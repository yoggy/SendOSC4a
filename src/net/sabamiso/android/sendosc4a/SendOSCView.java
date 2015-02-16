package net.sabamiso.android.sendosc4a;

import java.util.Vector;

import net.sabamiso.android.p5.PseudoP5View;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;

public class SendOSCView extends PseudoP5View {

	class Button {
		int idx;
		float x, y, w, h;

		public Button(int idx, int x, int y, int w, int h) {
			this.idx = idx;
			this.x = x;
			this.y = y;
			this.w = w;
			this.h = h;
		}

		public boolean isHit(boolean pressed, float x, float y) {
			if (pressed == false)
				return false;

			if (x < this.x)
				return false;
			if (y < this.y)
				return false;
			if (this.x + this.w < x)
				return false;
			if (this.y + this.h < y)
				return false;

			return true;
		}

		public void draw(boolean mousePressed, float mouseX, float mouseY) {
			strokeWeight(5);
			stroke(255);
			if (isHit(mousePressed, mouseX, mouseY)) {
				fill(255);
			} else {
				fill(64);
			}
			rect(x, y, w, h, 20);

			fill(128);
			text("" + idx, x + 16, y + 30);
		}
	}

	Vector<Button> v = new Vector<Button>();
	long last_pressed_t;

	public SendOSCView(Context context) {
		super(context);
	}

	@Override
	protected void setup() {
		size(360, 640);
		frameRate(30);

		createButtons();
	}

	@Override
	protected void draw() {
		background(0, 0, 0);

		for (int i = 0; i < v.size(); ++i) {
			Button b = v.get(i);
			b.draw(mousePressed, mouseX, mouseY);
		}

		fill(128);
		text("long press : show setting activity", 50, 635);

		// check long press
		long diff = System.currentTimeMillis() - last_pressed_t;
		if (mousePressed == true && diff > 2000) {
			
			Vibrator v = (Vibrator)getContext().getSystemService(Context.VIBRATOR_SERVICE);
			v.vibrate(100);
			
			Intent intent = new Intent(getContext(), SettingsActivity.class);
			getContext().startActivity(intent);
		}
	}

	@Override
	protected void mousePressed() {
		last_pressed_t = System.currentTimeMillis();

		int idx = getHitButtonIdx();
		if (idx >= 0) {
		}
	}

	void createButtons() {
		for (int i = 0; i < 8; ++i) {
			int bx = i % 2;
			int by = i / 2;
			int x = bx * 170 + 30;
			int y = by * 160 + 5;

			Button b = new Button(i, x, y, 130, 130);
			v.add(b);
		}
	}

	int getHitButtonIdx() {
		for (int i = 0; i < v.size(); ++i) {
			Button b = v.get(i);
			if (b.isHit(mousePressed, mouseX, mouseY) == true) {
				return i;
			}
		}
		return -1;
	}
}
