/*
 * This project and its source code is licensed under
 * Apache License
 * Version 2.0, January 2004
 * http://www.apache.org/licenses/
 *
 * Copyright (c) 2017 Mathias Lux, mathias@juggle.at
 */

package at.juggle.games.counting.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import at.juggle.games.counting.CountingGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		// System.setProperty("org.lwjgl.opengl.Display.allowSoftwareOpenGL", "true");
		config.width = 1280;
		config.height = 720;
		// config.width = 1920;
		// config.height = 1080;
		new LwjglApplication(new CountingGame(), config);
	}
}
