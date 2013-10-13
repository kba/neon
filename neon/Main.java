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

package neon;

import neon.core.Engine;
import neon.systems.io.LocalPort;
import neon.ui.Client;

/**
 * The main class of the neon roguelike engine.
 * 
 * @author	mdriesen
 */
public class Main {
	private static final String version = "0.4.2";	// huidige versie

	/**
     * The application's main method. This method creates an {@code Engine} and 
     * a {@code UserInterface} instance and connects them.
     * 
     * @param args	the command line arguments
     */
	public static void main(String[] args) {
		// poorten aanmaken en verbinden
		LocalPort cPort = new LocalPort();
		LocalPort sPort = new LocalPort();
		cPort.connect(sPort);
		sPort.connect(cPort);
		
		// engine en ui aanmaken
		Engine engine = new Engine(sPort);
		Client client = new Client(cPort, version);
		
		// custom look and feels zijn soms wat strenger als gewone, blijkbaar
		// is het grootste probleem dat in modules als main menu delen van de ui buiten 
		// de swing thread worden aangemaakt. Daarom alles maar op event-dispatch thread.
		javax.swing.SwingUtilities.invokeLater(client);
		engine.run();
	}
}