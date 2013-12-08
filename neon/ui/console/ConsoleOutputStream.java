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
import java.io.Writer;

/**
 * Data written to this will be displayed into the console. 
 * 
 * This code was adapted from the following forum post: 
 * http://www.javaprogrammingforums.com/java-swing-tutorials/4907-java-tip-jul-29-2010-swing-console-component.html
 * 
 * @author Andrew
 * @author mdriesen
 */
public class ConsoleOutputStream extends Writer {
	private JConsole console;

	/**
	 * @param console
	 */
	public ConsoleOutputStream(JConsole console) {
		this.console = console;
	}

	@Override
	public synchronized void close() throws IOException {
		console = null;
	}

	@Override
	public void flush() throws IOException {
		// no extra flushing needed
	}

	@Override
	public synchronized void write(char[] cbuf, int off, int len) throws IOException {
		StringBuilder temp = new StringBuilder(console.getText());
		for(int i = off; i < off + len; i++) {
			temp.append(cbuf[i]);
		}
		console.setText(temp.toString() + "\n");
	}
}
