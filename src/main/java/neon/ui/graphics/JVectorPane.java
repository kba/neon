/*
 *	Neon, a roguelike engine.
 *	Copyright (C) 2012-2013 - Maarten Driesen
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

package neon.ui.graphics;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import javax.swing.JComponent;
import neon.ui.graphics.event.VectorSelectionEvent;
import neon.ui.graphics.event.VectorSelectionListener;

/**
 * On this <code>JComponent</code>, vector drawings can be made.
 * 
 * @author mdriesen
 */
@SuppressWarnings("serial")
public class JVectorPane extends JComponent implements MouseListener {
	public static final int DEFAULT_ZOOM = 14;	// default zoom level
	
	private HashSet<Renderable> selection; // snelle look-up
	private boolean editable = false;
	private ZComparator comparator;
	private float zoom = DEFAULT_ZOOM;
	private BufferedImageOp filter;
	private int cx, cy; // camera
	private ArrayList<Renderable> renderables;
	private SelectionFilter selectionFilter;
	private RenderingHints hints;
	private BufferedImage image;

	public JVectorPane() {
		comparator = new ZComparator();
		selection = new HashSet<Renderable>();
		renderables = new ArrayList<Renderable>();
		addMouseListener(this);
		HashMap<RenderingHints.Key, Object> keys = new HashMap<>();
		keys.put(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
		keys.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
		keys.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
//		keys.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		hints = new RenderingHints(keys);
	}

	public void setRenderables(Collection<Renderable> list) {
		synchronized(renderables) {		
			renderables.clear();
			renderables.addAll(list);
		}
	}
	
	public void setFilter(BufferedImageOp filter) {
		this.filter = filter;
	}
	
	public void setSelectionFilter(SelectionFilter filter) {
		selectionFilter = filter;
	}
	
	public void setZoom(float zoom) {
		int w = (int)(getPreferredSize().width*zoom/this.zoom);
		int h = (int)(getPreferredSize().height*zoom/this.zoom);
		
		setPreferredSize(new Dimension(w, h));
		
		this.zoom = zoom;
		revalidate();
		repaint();
	}

	public void paintComponent(Graphics g) {
		synchronized(renderables) {
			Rectangle view = g.getClipBounds();
			// cx wordt 0 als pane in jscrollpane zit, view.x wordt 0 indien niet
			int x = (cx == 0 ? view.x : cx);
			int y = (cy == 0 ? view.y : cy);

			if(image == null || image.getWidth() != view.width || image.getHeight() != view.height) {
				image = getGraphicsConfiguration().createCompatibleImage(view.width, view.height);
			}
			
			Graphics2D buffer = image.createGraphics();
			buffer.addRenderingHints(hints);
			buffer.clearRect(0, 0, view.width, view.height);
			buffer.translate(-x, -y);
			
			Collections.sort(renderables, comparator);
			for(Renderable r : renderables) {
				r.paint(buffer, zoom, selection.contains(r));
			}
			buffer.dispose();
			((Graphics2D)g).drawImage(image, filter, view.x, view.y);
		}
	}

	public float getZoom() {
		return zoom;
	}

	public void updateCamera(Point camera) {
		cx = (int)(camera.x*zoom - getWidth()/2f);
		cy = (int)(camera.y*zoom - getHeight()/2f);
		revalidate();
	}
	
	/**
	 * @return	the visible part of the model, in model coordinates
	 */
	public Rectangle getVisibleRectangle() {
		Rectangle port = this.getBounds();
		return new Rectangle((int)(cx/zoom), (int)(cy/zoom), 
				(int)(port.width/zoom + 1), (int)(port.height/zoom + 1));
	}
	
	/**
	 * @return	whether this JVectorPane is editable or not
	 */
	public boolean isEditable() {
		return editable;
	}
	
	public void setEditable(boolean editable) {
		this.editable =  editable;
	}
	
	public void addVectorSelectionListener(VectorSelectionListener vsl) {
		listenerList.add(VectorSelectionListener.class, vsl);
	}
	
	public void removeVectorSelectionListener(VectorSelectionListener vsl) {
		listenerList.remove(VectorSelectionListener.class, vsl);
	}
	
	public void clearSelection() {
		selection.clear();
	}
	
	public Renderable getSelectedObject() {
		return selection.isEmpty() ? null : Collections.max(selection, comparator);
	}
	
	public Collection<Renderable> getSelectedObjects() {
		return selection;
	}
	
	public void mouseEntered(MouseEvent me) {}
	public void mouseExited(MouseEvent me) {}
	public void mousePressed(MouseEvent me) {
		selection.clear();
		for(Renderable r : renderables) {
			if(r.getBounds().contains((int)(me.getX()/zoom), (int)(me.getY()/zoom))) {
				if(selectionFilter == null || selectionFilter.isSelectable(r)) {
					selection.add(r);
				}
			}
		}
		repaint();
	}

	public void mouseReleased(MouseEvent me) {}	
	public void mouseClicked(MouseEvent me) {	
		for(VectorSelectionListener listener : listenerList.getListeners(VectorSelectionListener.class)) {
			listener.selectionChanged(new VectorSelectionEvent(this, new Rectangle()));
		}
	}
}
