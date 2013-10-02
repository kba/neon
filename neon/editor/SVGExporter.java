/*
 *	Neon, a roguelike engine.
 *	Copyright (C) 2011 - Maarten Driesen
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

package neon.editor;

import java.util.ArrayList;
import java.util.Collections;

import neon.editor.maps.*;
import neon.editor.resources.IRegion;
import neon.systems.files.FileSystem;
import neon.systems.files.XMLTranslator;
import neon.ui.graphics.Renderable;
import neon.ui.graphics.ZComparator;
import neon.util.ColorFactory;
import org.jdom2.*;

public class SVGExporter {
	public static void exportToSVG(ZoneTreeNode node, FileSystem files, DataStore store) {
		if(node != null) {
			Namespace ns = Namespace.getNamespace("http://www.w3.org/2000/svg");
			Element svg = new Element("svg", ns);
			Document doc = new Document(svg);
			doc.setDocType(new DocType("svg", "-//W3C//DTD SVG 1.1//EN", "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd"));
			svg.setAttribute("width", "100%");
			svg.setAttribute("height", "100%");
			svg.setAttribute("version", "1.1");
			
			ArrayList<Renderable> regions = new ArrayList<Renderable>(node.getZone().getScene().getElements());
			Collections.sort(regions, new ZComparator());
			
			for(Renderable i : regions) {
				if(i instanceof IRegion) {
					IRegion ri = (IRegion)i;
					Element region = new Element("rect", ns);
					region.setAttribute("x", Integer.toString(ri.x));
					region.setAttribute("y", Integer.toString(ri.y));
					region.setAttribute("width", Integer.toString(ri.width));
					region.setAttribute("height", Integer.toString(ri.height));
					int red = ColorFactory.getColor(ri.resource.color).getRed();
					int green = ColorFactory.getColor(ri.resource.color).getGreen();
					int blue = ColorFactory.getColor(ri.resource.color).getBlue();
					region.setAttribute("style", "fill:rgb(" + red + "," + green + "," + blue + ")");
					svg.addContent(region);
				}
			}
			
			files.saveFile(doc, new XMLTranslator(), store.getActive().getPath()[0], "shots", node.getZone().map.id + ".svg");
		}
	}
}
