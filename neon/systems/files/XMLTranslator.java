/*
 *	Neon, a roguelike engine.
 *	Copyright (C) 2010 - Maarten Driesen
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

import org.jdom2.*;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.*;
import java.io.*;

/**
 * This class can load, save and translate an xml file from disk.
 * 
 * @author mdriesen
 */
public class XMLTranslator implements Translator<Document> {
	public Document translate(InputStream input) {
		Document doc = new Document();
		
		try {
			doc = new SAXBuilder().build(input);
			input.close();
		} catch (IOException e) {
			System.out.println("IOException in XMLTranslator");
		} catch(JDOMException e) {
			System.out.println("JDOMException in XMLTranslator");
		} 
		
		return doc;
	}
	
	public ByteArrayOutputStream translate(Document output) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
		try {
			outputter.output(output, out);
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return out;
	}
}

