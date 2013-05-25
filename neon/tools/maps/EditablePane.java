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

package neon.tools.maps;

import javax.swing.*;
import java.awt.*;
import neon.graphics.*;
import neon.objects.resources.RTerrain;
import neon.tools.Editor;
import neon.tools.ObjectTransferHandler;
import java.awt.event.*;
import org.jdom2.Element;
import neon.tools.resources.IContainer;
import neon.tools.resources.IDoor;
import neon.tools.resources.IObject;
import neon.tools.resources.IRegion;
import neon.tools.resources.Instance;

/**
 * @author	mdriesen
 */
@SuppressWarnings("serial")
public class EditablePane extends JScrollPane implements MouseMotionListener, MouseListener, MouseWheelListener {
	private static SelectionFilter filter = new Filter();

	private Scene scene;
	private int layer;
	private ZoneTreeNode node;
	private Instance cut;
	private Instance copy;
	private Point position;
	private Dimension delta;
	private JVectorPane pane;
	private IRegion newRegion;
	
	/**
	 * Initializes this <code>EditablePane</code>.
	 */
	public EditablePane(ZoneTreeNode node, float width, float height) {
		if(node.getZone().getScene() == null) {
			node.getZone().map.load();			
		}
		layer = -1;
		this.node = node;
		setBackground(Color.black);
		pane = new JVectorPane();
		scene = node.getZone().getScene();	// scene na pane, anders loopt repaint mis
		setViewportView(pane);
		pane.setEditable(true);
		pane.setPreferredSize(new Dimension((int)(scene.getWidth()*pane.getZoom()), 
				(int)(scene.getHeight()*pane.getZoom())));
		
		// listeners en dingen
		pane.addMouseListener(this);
		pane.addMouseMotionListener(this);
		pane.addMouseWheelListener(this);
		pane.setTransferHandler(new ObjectTransferHandler(node.getZone(), this));
		pane.setSelectionFilter(filter);

		InputMap map = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		map.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "del");
		getActionMap().put("del", new KeyAction("del"));
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				repaint();	// omdat er anders iets misloopt met nieuwe panes
			}
		});
	}
	
	public void repaint() {
		if(scene != null) {	// JScrollPane repaint() al in de constructor
			pane.setRenderables(scene.getElements(pane.getVisibleRectangle()));
		}
		super.repaint();
	}
	
	public ZoneTreeNode getNode() {
		return node;
	}
	
	public void setLayer(int layer) {
		this.layer = layer;
	}

	public void toggleView(boolean view) {
//		System.out.println("toggle");
		for(Layer layer : scene.getLayers()) {
			if(layer.getDepth() == this.layer) {
				layer.setVisible(true);
			} else {
				layer.setVisible(view);
			}
		}
	}
	
	public void mouseClicked(MouseEvent e) {
		Renderable selected = pane.getSelectedObject();
		if(e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1 && selected != null) {
			if(selected instanceof IDoor) {
				new DoorInstanceEditor((IDoor)selected, Editor.getFrame()).show();
			} else if(selected instanceof IContainer) {
				new ContainerInstanceEditor((IContainer)selected, Editor.getFrame(), node).show();
			}
		} else if(e.getButton() == MouseEvent.BUTTON3) {
			JPopupMenu menu = new JPopupMenu();
			menu.add(new ClickAction("Cut", selected != null));
			menu.add(new ClickAction("Copy", selected != null));
			menu.add(new ClickAction("Paste", this.cut != null || this.copy != null, e.getX(), e.getY()));
			menu.add(new ClickAction("Delete", selected != null));
			menu.addSeparator();
			menu.add(new ClickAction("Properties...", selected instanceof IContainer || 
					selected instanceof IDoor || selected instanceof IRegion));
			menu.show(e.getComponent(), e.getX(), e.getY());			
		}
	}
	
	public void mouseEntered(MouseEvent arg0) {}
	public void mouseExited(MouseEvent arg0) {}
	public void mouseDragged(MouseEvent e) {
		Instance selected = (Instance)pane.getSelectedObject();
		if(MapEditor.drawMode()) {
			newRegion.width =(int)(e.getX()/pane.getZoom()) - newRegion.getX();
			newRegion.height = (int)(e.getY()/pane.getZoom()) - newRegion.getY();
			repaint();			
		} else if(selected != null) {
			selected.setX((int)(e.getX()/pane.getZoom() + delta.width));
			selected.setY((int)(e.getY()/pane.getZoom() + delta.height));
			scene.removeElement(selected);
			scene.addElement(selected, selected.getBounds(), selected.z);
			repaint();
		}
	}
	
	public void mouseMoved(MouseEvent e) {
		Editor.getStatusBar().setMessage("x: " + (int)(e.getX()/pane.getZoom()) + ", y: " + (int)(e.getY()/pane.getZoom()));
	}
	
	public void mousePressed(MouseEvent e) {
		if(MapEditor.drawMode()) {
			if(MapEditor.getSelectedTerrain() !=  null) {
				Element region = new Element("region");
				region.setAttribute("text", MapEditor.getSelectedTerrain());
				region.setAttribute("x", Integer.toString((int)(e.getX()/pane.getZoom())));
				region.setAttribute("y", Integer.toString((int)(e.getY()/pane.getZoom())));
				region.setAttribute("w", "1");
				region.setAttribute("h", "1");
				region.setAttribute("l", Integer.toString(layer));
				newRegion = new IRegion(region);
				scene.addElement(newRegion, newRegion.getBounds(), newRegion.z);
			}
		} else {
			Renderable selected = pane.getSelectedObject();
			if(selected != null) {
				position = new Point(selected.getBounds().x, selected.getBounds().y);
				delta = new Dimension(selected.getBounds().x - (int)(e.getX()/pane.getZoom()), 
						selected.getBounds().y - (int)(e.getY()/pane.getZoom()));
			}
		}
	}
	
	public void mouseReleased(MouseEvent e) {
		Instance selected = (Instance)pane.getSelectedObject();
		if(MapEditor.drawMode()) {
			scene.removeElement(newRegion);
			if(newRegion.width < 0) {
				newRegion.width = -newRegion.width;
				newRegion.setX((int)(e.getX()/pane.getZoom()));
			}
			if(newRegion.height < 0) {
				newRegion.width = -newRegion.height;				
				newRegion.setY((int)(e.getY()/pane.getZoom()));
			}
			scene.addElement(newRegion, newRegion.getBounds(), newRegion.z);
			repaint();
		} else if(selected != null) {
			MapEditor.setUndoAction(new UndoAction.Move(selected, scene, position.x, position.y));
			repaint();
		}
	}
	
	private class ClickAction extends AbstractAction {
		private int x, y;
		
		public ClickAction(String name, boolean enabled) {
			super(name);
			setEnabled(enabled);
		}
		
		public ClickAction(String name, boolean enabled, int x, int y) {
			super(name);
			setEnabled(enabled);
			this.x = x; 
			this.y = y;
		}
		
		private Instance clone(Instance original) {
			Instance clone = null;
			Element originalElement = copy.toElement();
			Element cloneElement = new Element(originalElement.getName());
			cloneElement.setAttribute("x", Integer.toString((int)(x/pane.getZoom())));
			cloneElement.setAttribute("y", Integer.toString((int)(y/pane.getZoom())));
			cloneElement.setAttribute("uid", Integer.toString(node.getZone().map.createUID(cloneElement)));
			if(original instanceof IRegion) {
				cloneElement.setAttribute("text", originalElement.getAttributeValue("text"));
				cloneElement.setAttribute("w", originalElement.getAttributeValue("w"));
				cloneElement.setAttribute("h", originalElement.getAttributeValue("h"));
				cloneElement.setAttribute("l", originalElement.getAttributeValue("l"));
				clone = new IRegion((RTerrain)original.resource, (int)(x/pane.getZoom()), (int)(y/pane.getZoom()), original.getZ(), 
						original.width, original.height);
			} else if(original instanceof IObject) {
				// state van deuren en containers meekopieren -> nee?
				cloneElement.setAttribute("id", originalElement.getAttributeValue("id"));
				clone = new IObject(original.resource, (int)(x/pane.getZoom()), (int)(y/pane.getZoom()), original.getZ(), 0);
			}
			return clone;
		}
		
		public void actionPerformed(ActionEvent e) {
			Instance selected = (Instance)pane.getSelectedObject();

			if(e.getActionCommand().equals("Cut")) {
				if(selected != null) {
					copy = null;
					cut = selected;
					cut.setCut(true);
				}
			} else if(e.getActionCommand().equals("Copy")) {
				if(selected != null) {
					cut = null;
					copy = selected;
				}
			} else if(e.getActionCommand().equals("Paste")) {
				if(copy != null) {
					Instance clone = clone(copy);
					scene.addElement(clone, clone.getBounds(), clone.getZ());
				} else if(cut != null) {
					cut.setX((int)(x/pane.getZoom()));
					cut.setY((int)(y/pane.getZoom()));
					scene.removeElement(cut);
					scene.addElement(cut, cut.getBounds(), cut.z);
					cut.setCut(false);
					cut = null;
				}
			} else if(e.getActionCommand().equals("Delete")) {
				if(selected != null) {
					scene.removeElement(selected);	// ding verwijderen
					if(selected instanceof IObject) {	// UID weg indien object
						node.getZone().map.removeObjectUID(((IObject)selected).uid);
						if(selected instanceof IContainer) {	// inhoud van container ook verwijderen
							for(IObject io : ((IContainer)selected).contents) {
								node.getZone().map.removeObjectUID(io.uid);
							}
						}
					}
				}
			} else if(e.getActionCommand().equals("Properties...")) {
				if(selected instanceof IDoor) {
					new DoorInstanceEditor((IDoor)selected, Editor.getFrame()).show();
				} else if(selected instanceof IContainer) {
					new ContainerInstanceEditor((IContainer)selected, Editor.getFrame(), node).show();
				} else if(selected instanceof IRegion) {
					new RegionInstanceEditor((IRegion)selected, Editor.getFrame(), node).show();
				}
			}
			repaint();
		}
	}

	public float getZoom() {
		return pane.getZoom();
	}
	
	public void setZoom(float zoom) {
		float ratio = zoom/this.pane.getZoom();
		int dx = (int)(ratio > 1 ? viewport.getWidth()/ratio : -viewport.getWidth()*ratio/2);
		int dy = (int)(ratio > 1 ? viewport.getHeight()/ratio : -viewport.getHeight()*ratio/2);
		final int x = Math.max(0, (int)(viewport.getViewPosition().x*ratio + dx));
		final int y = Math.max(0, (int)(viewport.getViewPosition().y*ratio + dy));
		pane.setZoom(zoom);
		// omdat preferredSize van de viewport niet onmiddelijk verandert
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				viewport.setViewPosition(new Point(x, y));				
			}
		});
	}
	
	public void mouseWheelMoved(MouseWheelEvent mwe) {
		Point position = viewport.getViewPosition();
		int notches = (int)(mwe.getWheelRotation()*100/Math.sqrt(pane.getZoom()));
		if(mwe.isControlDown()) {
			if(notches > 0) {
				setZoom(pane.getZoom()/2);
			} else if(notches < 0) {
				setZoom(pane.getZoom()*2);				
			}
		} else if(mwe.isShiftDown()) {
			viewport.setViewPosition(new Point(Math.max(0, position.x + notches), position.y));
		} else {
			viewport.setViewPosition(new Point(position.x, Math.max(0, position.y + notches)));
		}
	}

	private static class Filter implements SelectionFilter {
		/**
		 * @param r
		 * @return	whether the given instance can be selected
		 */
		public boolean isSelectable(Renderable r) {
			if(r instanceof IRegion && Editor.tEdit.isSelected()) {
				return true;
			} else if(r instanceof IObject && Editor.oEdit.isSelected()) {
				return true;
			} else {
				return false;
			}
		}		
	}
	
	private class KeyAction extends AbstractAction {
		public KeyAction(String command) {
			putValue(ACTION_COMMAND_KEY, command);
		}
		
		public void actionPerformed(ActionEvent ae) {
			if(ae.getActionCommand().equals("del")) {
				Instance selected = (Instance)pane.getSelectedObject();
				if(selected != null) {
					scene.removeElement(selected);
					if(selected instanceof IObject) {	// UID weg indien object
						node.getZone().map.removeObjectUID(((IObject)selected).uid);
						if(selected instanceof IContainer) {	// inhoud van container ook verwijderen
							for(IObject io : ((IContainer)selected).contents) {
								node.getZone().map.removeObjectUID(io.uid);
							}
						}
					}
				}
				repaint();
			}
		}
	}
}