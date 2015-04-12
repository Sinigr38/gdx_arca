package com.sinigr.arca.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.sinigr.arca.ScreenController;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Arca";
	    config.width = 1920;
	    config.height = 1080;
	    config.fullscreen = false;
	    config.vSyncEnabled = true;
		new LwjglApplication(new ScreenController(), config);
	}
}
