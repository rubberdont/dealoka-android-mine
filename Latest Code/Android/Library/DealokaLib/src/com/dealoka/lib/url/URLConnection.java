package com.dealoka.lib.url;

import java.util.ArrayList;

public class URLConnection {
	public static final int MAX_CONNECTIONS = 5;
	private ArrayList<Runnable> active = new ArrayList<Runnable>();
	private ArrayList<Runnable> queue = new ArrayList<Runnable>();
	private static URLConnection instance;
	public static URLConnection getInstance() {
		if(instance == null)
			instance = new URLConnection();
		return instance;
	}
	public void push(Runnable runnable) {
		queue.add(runnable);
		if(active.size() < MAX_CONNECTIONS)
			startNext();
	}
	private void startNext() {
		if(!queue.isEmpty()) {
			Runnable next = queue.get(0);
			queue.remove(0);
			active.add(next);
			Thread thread = new Thread(next);
            thread.start();
        }
    }
	public void didComplete(Runnable runnable) {
		active.remove(runnable);
        startNext();
    }
}