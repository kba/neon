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

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.text.NumberFormat;
import java.util.Enumeration;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import neon.maps.WildernessGenerator;
import neon.objects.resources.RRegionTheme;
import neon.objects.resources.RScript;
import neon.objects.resources.RTerrain;
import neon.tools.Editor;
import neon.tools.resources.IRegion;
import neon.tools.resources.Instance;
import neon.tools.resources.RZone;
import org.jdom2.Element;
import java.awt.event.*;

public class RegionInstanceEditor implements ActionListener, MouseListener {
	private IRegion region;
	private JDialog frame;
	private JTextField labelField;
	private JFormattedTextField xField, yField, wField, hField;
	private JComboBox<RRegionTheme> themeBox;
	private JComboBox<RTerrain> terrainBox;
	private JList<RScript> scriptList;
	private DefaultListModel<RScript> scriptListModel;
	private RZone zone;
	private JSpinner zSpinner;
	
	public RegionInstanceEditor(IRegion r, JFrame parent, ZoneTreeNode zone) {
		this.zone = zone.getZone();
		region = r;
		frame = new JDialog(parent, "Region instance editor: " + region.resource.id);
		JPanel content = new JPanel(new BorderLayout());
		frame.setContentPane(content);
				
		JPanel buttons = new JPanel();
		content.add(buttons, BorderLayout.PAGE_END);
		JButton ok = new JButton("Ok");
		ok.addActionListener(this);
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(this);
		JButton apply = new JButton("Apply");
		apply.addActionListener(this);
		buttons.add(ok);
		buttons.add(cancel);
		buttons.add(apply);
		
		JPanel sizePanel = new JPanel();
		sizePanel.setBorder(new TitledBorder("Geometry"));
		GroupLayout destLayout = new GroupLayout(sizePanel);
		sizePanel.setLayout(destLayout);
		destLayout.setAutoCreateGaps(true);
		
		JLabel xLabel = new JLabel("X: ");
		JLabel yLabel = new JLabel("Y: ");
		JLabel wLabel = new JLabel("Width: ");
		JLabel hLabel = new JLabel("Height: ");
		JLabel terrainLabel = new JLabel("Terrain: ");
		JLabel zLabel = new JLabel("Layer: ");
		JLabel labelLabel = new JLabel("Label: ");
		xField = new JFormattedTextField(NumberFormat.getIntegerInstance());
		xField.setColumns(10);
		yField = new JFormattedTextField(NumberFormat.getIntegerInstance());
		wField = new JFormattedTextField(NumberFormat.getIntegerInstance());
		wField.setColumns(10);
		hField = new JFormattedTextField(NumberFormat.getIntegerInstance());
		terrainBox = new JComboBox<RTerrain>(Editor.resources.getResources(RTerrain.class));
		zSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 124, 1));
		labelField = new JTextField();
		destLayout.setVerticalGroup(
				destLayout.createSequentialGroup()
				.addGroup(destLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(xLabel).addComponent(xField).addComponent(wLabel).addComponent(wField))
				.addGroup(destLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(yLabel).addComponent(yField).addComponent(hLabel).addComponent(hField))
				.addGroup(destLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(terrainLabel).addComponent(terrainBox)
						.addComponent(zLabel).addComponent(zSpinner))
				.addGroup(destLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(labelLabel).addComponent(labelField)));
		destLayout.setHorizontalGroup(
				destLayout.createSequentialGroup()
				.addGroup(destLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(xLabel)
						.addComponent(yLabel).addComponent(terrainLabel).addComponent(labelLabel))
				.addGroup(destLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
						.addComponent(xField).addComponent(yField).addComponent(terrainBox).addComponent(labelField))
				.addGroup(destLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(wLabel).addComponent(hLabel).addComponent(zLabel))
				.addGroup(destLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
						.addComponent(wField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(hField).addComponent(zSpinner)));
		
		// scripts
		scriptListModel = new DefaultListModel<RScript>();
		scriptList = new JList<RScript>(scriptListModel);
		scriptList.addMouseListener(this);
		JScrollPane scroller = new JScrollPane(scriptList);
		scroller.setBorder(new TitledBorder("Scripts"));
		
		//random generation
		JPanel randomPanel = new JPanel();
		themeBox = new JComboBox<RRegionTheme>();
		themeBox.addItem(null);
		for(RRegionTheme theme : Editor.resources.getResources(RRegionTheme.class)) {
			themeBox.addItem(theme);
		}
		JButton randomButton = new JButton("Generate randomly");
		randomButton.setActionCommand("Random");
		randomButton.addActionListener(this);
		randomPanel.add(themeBox);
		randomPanel.add(randomButton);
		randomPanel.setBorder(new TitledBorder("Random generation"));

		JPanel otherPanel = new JPanel(new BorderLayout());
		if(!this.zone.map.isDungeon()) {	// alleen outdoor random regions
			otherPanel.add(randomPanel, BorderLayout.PAGE_START);
		}
		otherPanel.add(scroller, BorderLayout.CENTER);
		content.add(sizePanel, BorderLayout.PAGE_START);
		content.add(otherPanel, BorderLayout.CENTER);

		initRegion();
	}

	public void show() {
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	public void initRegion() {
		xField.setValue(region.x);
		yField.setValue(region.y);
		wField.setValue(region.width);
		hField.setValue(region.height);
		zSpinner.setValue(region.z);
		terrainBox.setSelectedItem(region.resource);
		themeBox.setSelectedItem(region.theme);
		labelField.setText(region.label);
		
		for(RScript script : region.scripts) {
			scriptListModel.addElement(script);
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		if ("Ok".equals(e.getActionCommand())) {
			save();
			frame.dispose();
		} else if ("Cancel".equals(e.getActionCommand())){
			frame.dispose();
		} else if ("Apply".equals(e.getActionCommand())){
			save();
		} else if("Random".equals(e.getActionCommand())) {
			String type = themeBox.getSelectedItem().toString();
			if(type.startsWith("town")) {
//				new TownGenerator(zone).generate(region.x, region.y, region.width, region.height, type, region.z);
			} else {
				String[][] terrain = new String[region.height + 2][region.width + 2];	// [rijen][kolommen]
				// laten overvloeien in naburig region
				if(region.y > 0) {	// bovenkant van map
					for(int i = 0; i < region.width; i++) {
						terrain[0][i+1] = zone.getRegion(region.x + i, region.y - 1).resource.id;
					}
				}
				if(region.y + region.height < zone.getScene().getHeight() - 1) {	// onderkant
					for(int i = 0; i < region.width; i++) {
						terrain[region.height + 1][i+1] = 
								zone.getRegion(region.x + i, region.y + region.height).resource.id;
					}
				}
				if(region.x > 0) {	// links
					for(int i = 0; i < region.height; i++) {
						terrain[i+1][0] = zone.getRegion(region.x - 1, region.y + i).resource.id;
					}
				}
				if(region.x + region.width < zone.getScene().getWidth() - 1) {	// rechts
					for(int i = 0; i < region.height; i++) {
						terrain[i+1][region.width + 1] = 
								zone.getRegion(region.x + region.width, region.y + i).resource.id;
					}
				}
				String[][] content = new WildernessGenerator(terrain).generate(region.getBounds(), region.theme, region.resource.id);
				makeContent(content);
			}
			themeBox.setSelectedItem(null);
		} 
	}
	
	public void save() {
		region.x = Integer.parseInt(xField.getValue().toString());
		region.y = Integer.parseInt(yField.getValue().toString());
		region.z = (Integer)zSpinner.getValue();
		region.width = Integer.parseInt(wField.getValue().toString());
		region.height = Integer.parseInt(hField.getValue().toString());
		region.resource = (RTerrain)terrainBox.getSelectedItem();
		
		region.scripts.clear();
		for(Enumeration<RScript> e = scriptListModel.elements(); e.hasMoreElements();) {
			region.scripts.add(e.nextElement());
		}
		
		region.theme = (RRegionTheme)themeBox.getSelectedItem();
		region.label = labelField.getText();
	}
	
	public void mouseExited(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseClicked(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON3) {
			JPopupMenu menu = new JPopupMenu();
			menu.add(new ScriptListAction("Add script"));
			menu.add(new ScriptListAction("Delete script"));
			menu.show(e.getComponent(), e.getX(), e.getY());
			scriptList.setSelectedIndex(scriptList.locationToIndex(e.getPoint()));
		}
	}
	
	@SuppressWarnings("serial")
	private class ScriptListAction extends AbstractAction {
		public ScriptListAction(String name) {
			super(name);
		}
		
		public void actionPerformed(ActionEvent e) {
			if(e.getActionCommand().equals("Add script")) {
				Object[] spells = Editor.getStore().getScripts().values().toArray();
				RScript rs = (RScript)JOptionPane.showInputDialog(neon.tools.Editor.getFrame(), "Add script:",
						"Add script", JOptionPane.PLAIN_MESSAGE, null, spells, 0);
				scriptListModel.addElement(rs);
			} else if(e.getActionCommand().equals("Delete script")) {
				scriptListModel.remove(scriptList.getSelectedIndex());
			} 
		}
	}
	
	private void makeContent(String[][] tiles) {
		for(int x = 0; x < region.width; x++) {
			for(int y = 0; y < region.height; y++) {
				if(tiles[y+1][x+1] != null) {
					String data[] = tiles[y+1][x+1].split(";");
					for(String entry : data) {
						if(entry.startsWith("i:")) {
							String id = entry.replace("i:", "");
							Element e = new Element("item");
							e.setAttribute("x", Integer.toString(x + region.x));
							e.setAttribute("y", Integer.toString(y + region.y));
							e.setAttribute("id", id);
							e.setAttribute("uid", Integer.toString(zone.map.createUID(e)));
							Instance instance = RZone.getInstance(e, zone);
							zone.getScene().addElement(instance, instance.getBounds(), instance.z);
						} else if(entry.startsWith("c:")) {
						} else if(!entry.isEmpty()) {
							Element element = new Element("region");
							element.setAttribute("text", entry);
							element.setAttribute("x", Integer.toString(x + region.x));
							element.setAttribute("y", Integer.toString(y + region.y));
							element.setAttribute("w", "1");
							element.setAttribute("h", "1");
							element.setAttribute("l", Integer.toString(region.z + 1));
							Instance instance = new IRegion(element);
							zone.getScene().addElement(instance, new Rectangle(x + region.x, y + region.y, 1, 1), region.z + 1);							
						}
					}
				}
			}
		}
	}
}
