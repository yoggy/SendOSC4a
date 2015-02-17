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
		boolean last_pressed_status;

		public Button(int idx, int x, int y, int w, int h) {
			this.idx = idx;
			this.x = x;
			this.y = y;
			this.w = w;
			this.h = h;
			
			last_pressed_status = false;
		}

		public boolean isInner(float x, float y) {
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

		public boolean isPressed() {
			if (mousePressed == false) return false;
			return isInner(mouseX, mouseY);
		}

		void process() {
			boolean current_pressed_status = isPressed();
			
			if (current_pressed_status == true && last_pressed_status == false) {
				osc.sendPressedMessage(idx);
			}
			else if (current_pressed_status == false && last_pressed_status == true) {
				osc.sendReleasedMessage(idx);
			}
			
			last_pressed_status = current_pressed_status;
		}
		
		public void draw() {
			strokeWeight(5);
			stroke(255);
			if (isPressed()) {
				fill(255);
			} else {
				fill(64);
			}
			rect(x, y, w, h, 20);

			fill(128);
			text("" + idx, x + 14, y + 26);
			text("P:" + osc.getButtonPressedConfig(idx), x + 14, y + 44);
			text("R:" + osc.getButtonReleasedConfig(idx), x + 14, y + 62);
		}
	}

	Vector<Button> v = new Vector<Button>();
	long last_pressed_t;
	OSC osc;

	public SendOSCView(Context context) {
		super(context);
	}

	@Override
	protected void setup() {
		size(360, 640);
		frameRate(30);

		createButtons();
		
		osc = OSC.getInstance();
	}

	@Override
	public void onResume() {
		super.onResume();
		osc.reload();
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

	@Override
	protected void draw() {
		background(0, 0, 0);

		for (int i = 0; i < v.size(); ++i) {
			Button b = v.get(i);
			b.process();
			b.draw();
		}

		fill(128);
		text("long press : show setting activity", 90, 635);

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
	}
	
}
