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

package neon.systems.files;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Scanner;

/**
 * This class can load and save strings from/to files.
 * 
 * @author mdriesen
 */
public class StringTranslator implements Translator<String> {
	public String translate(InputStream input) {
	    try {
	        return new Scanner(input, "UTF-8").useDelimiter("\\A").next();
	    } catch (java.util.NoSuchElementException e) {
	        return "";
	    }
	}
	
	public ByteArrayOutputStream translate(String output) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte buffer[] = output.getBytes(Charset.forName("UTF-8"));
		try {
			out.write(buffer);
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return out;
	}
}