package net.sabamiso.android.util;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.util.Log;

public class Resolver {
	private static Resolver singleton;
	private static Context context;

	WifiManager wifi_manager;
	MulticastLock lock;
	JmDNS jmdns;

	HashMap<String, InetAddress> map = new HashMap<String, InetAddress>();
	
	public static void init(Context c) {
		context = c;
	}

	public static Resolver getInstance() {
		if (singleton == null) {
			singleton = new Resolver();
		}
		return singleton;
	}

	private Resolver() {
		wifi_manager = (android.net.wifi.WifiManager) context
				.getSystemService(android.content.Context.WIFI_SERVICE);
	}

	public void onResume() {
		enableMulticast();
		startJmDNS();
	}

	public void onPause() {
		stopJmDNS();
		disableMulticast();
	}

	String type = "_workstation._tcp.local.";
	ServiceListener listener;

	private void startJmDNS() {
		map.clear();
		try {
			int ip = wifi_manager.getConnectionInfo().getIpAddress();
			if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
				ip = Integer.reverseBytes(ip);
			}
			byte[] ip_byte_array = BigInteger.valueOf(ip).toByteArray();
			InetAddress addr = InetAddress.getByAddress(ip_byte_array);

			jmdns = JmDNS.create(addr);
			jmdns.addServiceListener(type, listener = new ServiceListener() {
				@Override
				public void serviceAdded(ServiceEvent evt) {
					// Log.d("Resolver", "Service removed: name=" +
					// evt.getName() + ",type=" + evt.getType());
					jmdns.requestServiceInfo(evt.getType(), evt.getName(), 1);
				}

				@Override
				public void serviceRemoved(ServiceEvent evt) {
					Log.d("Resolver", "Service removed: name=" + evt.getName());
				}

				@SuppressWarnings("deprecation")
				@Override
				public void serviceResolved(ServiceEvent evt) {
					ServiceInfo info = jmdns.getServiceInfo(evt.getType(),
							evt.getName());
					map.put(info.getServer(), info.getInetAddress());
					
					Log.d("Resolver",
							"Service resolved: " + info.getServer() + ",addr=" + info.getInetAddress());
				}
			});
			
			Thread.sleep(2000);
			
		} catch (Exception e) {
			Log.e("Resolver", e.toString());
		}
	}

	private void stopJmDNS() {
		map.clear();
		try {
			jmdns.removeServiceListener(type, listener);
			jmdns.close();
		} catch (Exception e) {
		}
		jmdns = null;
	}

	private void enableMulticast() {
		lock = wifi_manager.createMulticastLock("sendosc multicast lock");
		lock.setReferenceCounted(true);
		lock.acquire();
	}

	private void disableMulticast() {
		lock.release();
	}

	public InetAddress resolv(String host) {
		if (host == null || host.length() == 0) return null;
		
		// ipaddress string
		if (host.matches("^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$")) {
			InetAddress addr = null;
			try {
				addr = InetAddress.getByName(host);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
			return addr;
		}
		
		// nomal hostname string
		if (!host.contains(".local")) {
			InetAddress addr = null;
			try {
				addr = InetAddress.getByName(host);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
			return addr;
		}
		
		// resolv using mDNS
		Iterator<Entry<String, InetAddress>> it = map.entrySet().iterator();
		while(it.hasNext()) {
			Entry<String, InetAddress> ent = it.next();
			if (ent.getKey().contains(host)) {
				InetAddress addr = ent.getValue();
				return addr;
			}
		}
		
		return null;
	}
}
