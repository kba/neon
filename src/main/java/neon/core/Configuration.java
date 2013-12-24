/*
 *	Neon, a roguelike engine.
 *	Copyright (C) 2013 - Maarten Driesen
 * 
 *	This program is free software; you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation; either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	This program is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package neon.core;

import java.io.*;
import java.util.*;
import java.util.logging.*;
import neon.resources.CServer;
import neon.resources.ResourceManager;

public class Configuration {
	public static boolean audio = false;	// audio aan of uit?
	public static boolean gThread = true;	// terrain generation threaded of niet?

	private HashMap<String, String> properties = new HashMap<>();
	
	/**
	 * Loads all kinds of stuff.
	 */
	public Configuration(ResourceManager resources) {
		// ini file inladen
		CServer config = (CServer)resources.getResource("ini", "config");

		// logging
		Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
		logger.setLevel(Level.parse(config.getLogLevel()));
		try {
			Handler handler = new FileHandler("neon.log");
			handler.setFormatter(new SimpleFormatter());
			logger.addHandler(handler);
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}

		// threading
		gThread = config.isMapThreaded();
		logger.config("Map generation thread: " + gThread);
	}
	
	/**
	 * @param property
	 * @return	the requested property
	 */
	public String getProperty(String property) {
		return properties.get(property);
	}
	
	/**
	 * Sets the string value of the given property.
	 * 
	 * @param property
	 * @param value
	 */
	public void setProperty(String property, String value) {
		properties.put(property, value);
	}
}
