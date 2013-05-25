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

package neon.ui.console;

import java.io.IOException;
import java.io.Reader;

/**
 * Data written into this is data from the console
 * 
 * This code was adapted from the following forum post: 
 * http://www.javaprogrammingforums.com/java-swing-tutorials/4907-java-tip-jul-29-2010-swing-console-component.html
 *
 * @author Andrew
 */
public class ConsoleInputStream extends Reader {
	private StringBuilder stream;

	/**
	 * @param console
	 */
	public ConsoleInputStream(JConsole console) {
		stream = new StringBuilder();
	}

	/**
	 * @param text
	 */
	public void addText(String text) {
		synchronized (stream) {
			stream.append(text);
		}
	}

	@Override
	public synchronized void close() throws IOException {
		stream = null;
	}

	@Override
	public int read(char[] buf, int off, int len) throws IOException {
		int count = 0;
		boolean doneReading = false;
		for (int i = off; i < off + len && !doneReading; i++) {
			// determine if we have a character we can read
			// we need the lock for stream
			int length = 0;
			while (length == 0) {
				// sleep this thread until there is something to read
				try {
					Thread.sleep(100);
				} catch(InterruptedException e) {
					e.printStackTrace();
				}
				synchronized (stream) {
					length = stream.length();
				}
			}
			synchronized (stream) {
				// get the character
				buf[i] = stream.charAt(0);
				// delete it from the buffer
				stream.deleteCharAt(0);
				count++;
				if (buf[i] == '\n') {
					doneReading = true;
				}
			}
		}
		return count;
	}
}
