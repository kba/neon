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

package neon.tools;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import javax.swing.TransferHandler;
import neon.graphics.JVectorPane;
import neon.objects.resources.RItem.Type;
import neon.tools.maps.*;
import neon.tools.resources.Instance;
import neon.objects.resources.RItem;
import neon.tools.resources.RZone;
import org.jdom2.Element;

@SuppressWarnings("serial")
public class ObjectTransferHandler extends TransferHandler {
	private RZone zone;
	private EditablePane pane;
	
	public ObjectTransferHandler(RZone zone, EditablePane pane) {
		this.zone = zone;
		this.pane = pane;
	}
	
	public boolean canImport(TransferHandler.TransferSupport ts) {
		return true;
	}
	
	public boolean importData(TransferHandler.TransferSupport ts) {
		try {		
			String id = ts.getTransferable().getTransferData(DataFlavor.stringFlavor).toString();
			String type = "item";
			
			if(Editor.resources.getResource(id) instanceof RItem) {
				System.out.println(id);
				RItem item = (RItem)Editor.resources.getResource(id);
				if(item instanceof RItem.Door) {
					type = "door";
				} else if(item.type == Type.container) {
					type = "container";
				}
			} else {
				type = "creature"; 
			}

			float zoom = ((JVectorPane)ts.getComponent()).getZoom();
			
			DropLocation dl = ts.getDropLocation();
			int x = (int)(dl.getDropPoint().x/zoom);
			int y = (int)(dl.getDropPoint().y/zoom);
			
			Element e = new Element(type);
			e.setAttribute("x", Integer.toString(x));
			e.setAttribute("y", Integer.toString(y));
			e.setAttribute("id", id);
			e.setAttribute("uid", Integer.toString(zone.map.createUID(e)));
			
			Instance instance = RZone.getInstance(e, zone);
			zone.getScene().addElement(instance, instance.getBounds(), instance.z);
			
			UndoAction undo = new UndoAction.Drop(instance, zone.getScene());
			MapEditor.setUndoAction(undo);
			pane.repaint();
		} catch (UnsupportedFlavorException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}	
}
