package net.sabamiso.android.sendosc4a;

import java.net.InetAddress;
import java.util.Locale;
import java.util.Vector;

import net.sabamiso.android.util.Config;
import android.os.StrictMode;
import android.util.Log;

import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortOut;

public class OSC {

	public static final int button_num = 8;
	
	public static OSC singleton;

	public static OSC getInstance() {
		if (singleton == null) {
			singleton = new OSC();
		}
		return singleton;
	}

	/////////////////////////////////////////////////////////////	
	Config cf;
	OSCPortOut sender;
	
	String host;
	int port;
	Vector<String> button_pressed_config = new Vector<String>();
	Vector<String> button_released_config = new Vector<String>();
	Vector<OSCMessage> button_pressed_messages = new Vector<OSCMessage>();
	Vector<OSCMessage> button_released_messages = new Vector<OSCMessage>();
	
	protected OSC() {
		cf = Config.getInstance();
		reload();

		// disable StrictMode
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
	}

	public void reload() {
		button_pressed_config.clear();
		button_released_config.clear();
		button_pressed_messages.clear();
		button_released_messages.clear();
		
		host = cf.getString("host", "192.168.1.100");
		port = cf.getInt("port", 7000);
		

		try {
			InetAddress addr = InetAddress.getByName(host);
			sender = new OSCPortOut(addr, port);
		} catch (Exception e) {
			Log.e("OSC", e.toString());
		}

		for (int i = 0; i < button_num; ++i) {
			String key_p = "osc_button_" + i + "_press";
			String key_r = "osc_button_" + i + "_release";
			String str_p = cf.getString(key_p, "/btn" + i + " i 1");
			String str_r = cf.getString(key_r, "/btn" + i + " i 0");
			
			button_pressed_config.add(str_p);
			button_released_config.add(str_r);
			
			OSCMessage msg_p = parseMessage(str_p);
			OSCMessage msg_r = parseMessage(str_r);
			
			button_pressed_messages.add(msg_p);
			button_released_messages.add(msg_r);
		}
	}
	
	OSCMessage parseMessage(String str) {
		// message string format
		//
		//	address type0 arg0 type1 arg1 type2 arg2....
		//
		//	ex:
		// 		/addr i 0 f 0.0 s string
		//
		//	type string:
		//		i:int
		//		f:float
		//		s:string
		//
		if (str == null || str.length() == 0) return null;

		// split by space
		String [] v = str.split("\\s+");

		// check arguments number
		if (v.length % 2 != 1) {
			Log.e("OSC", "message format error...str=" + str);
			return null;
		}
		
		// v[0] is address
		String addr = v[0];
		OSCMessage msg = new OSCMessage(addr);

		// parse arguments
		for (int i = 1; i < v.length; i += 2) {
			String type = v[i];
			type = type.toLowerCase(new Locale("us"));
			type = type.substring(0, 1);
			
			String val_str = v[i + 1];
			
			if ("i".equals(type)) {
				int val = Integer.parseInt(val_str);
				msg.addArgument(val);
			}
			else if ("f".equals(type)) {
				float val = Float.parseFloat(val_str);
				msg.addArgument(val);
			}
			else if ("s".equals(type)) {
				msg.addArgument(val_str);
			}
			else {
				Log.e("OSC", "message format error...str=" + str);
				return null;
			}
		}
		
		return msg;
	}
	
	public void sendPressedMessage(int idx) {
		OSCMessage msg = getButtonPressedMessage(idx);
		if (msg == null) return;		
		try {
			sender.send(msg);
		} catch (Exception e) {
			e.toString();
		}
	}
	
	public void sendReleasedMessage(int idx) {
		OSCMessage msg = getButtonReleasedMessage(idx);
		if (msg == null) return;		
		try {
			sender.send(msg);
		} catch (Exception e) {
			e.toString();
		}
	}

	public String getButtonPressedConfig(int idx) {
		if (idx < 0 || button_pressed_config.size() <= idx) return null;
		return button_pressed_config.get(idx);
	}
	
	public String getButtonReleasedConfig(int idx) {
		if (idx < 0 || button_released_config.size() <= idx) return null;
		return button_released_config.get(idx);
	}

	OSCMessage getButtonPressedMessage(int idx) {
		if (idx < 0 || button_pressed_messages.size() <= idx) return null;
		return button_pressed_messages.get(idx);
	}
	
	OSCMessage getButtonReleasedMessage(int idx) {
		if (idx < 0 || button_released_messages.size() <= idx) return null;
		return button_released_messages.get(idx);
	}
}
