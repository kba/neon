/*
 *	Neon, a roguelike engine.
 *	Copyright (C) 2012 - Maarten Driesen
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

/**
 * The main class of the neon roguelike engine.
 * 
 * @author	mdriesen
 */
public class Main {
    /**
     * The application's main method. Creates a new Engine instance and runs it. Everything must be run 
     * on the swing event-dispatch thread to avoid errors with the user interface.
     * 
     * @param args	the command line arguments
     */
	public static void main(String[] args) {
		// custom look and feels zijn soms wat strenger als gewone, blijkbaar
		// is het grootste probleem dat in modules als main menu delen van de ui buiten 
		// de swing thread worden aangemaakt. Daarom alles maar op event-dispatch thread.
		javax.swing.SwingUtilities.invokeLater(new neon.core.Engine());
	}
}