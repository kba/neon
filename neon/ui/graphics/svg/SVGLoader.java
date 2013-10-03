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

package neon.ui.graphics.svg;

import java.awt.Color;
import java.io.StringReader;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import neon.ui.graphics.shapes.*;
import neon.util.ColorFactory;

public class SVGLoader {
	public static JVShape loadShape(String shape) {
		StringReader stringReader = new StringReader(shape);
		SAXBuilder builder = new SAXBuilder();
		// doc al initialiseren, in geval builder.build faalt
		Document doc = new Document();
		try {
			doc = builder.build(stringReader);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Element root = doc.getRootElement();
		root.detach();
		return loadShape(root);
	}
	
	public static JVShape loadShape(Element shape) {
		Color color = ColorFactory.getColor(shape.getAttributeValue("fill"));
		if(shape.getAttribute("opacity") != null) {
			int opacity = (int)(Float.parseFloat(shape.getAttributeValue("opacity"))*255);
			color = new Color(color.getRed(), color.getGreen(), color.getBlue(), opacity);
		}

		if(shape.getName().equals("circle")) {
			int radius = Integer.parseInt(shape.getAttributeValue("r"));
			return new JVEllipse(radius, color);
		} else {
			return new JVRectangle(null, null);
		}
	}
}
