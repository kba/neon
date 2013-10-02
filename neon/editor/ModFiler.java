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

package neon.editor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import neon.editor.resources.*;
import neon.resources.RCraft;
import neon.resources.RCreature;
import neon.resources.RDungeonTheme;
import neon.resources.RItem;
import neon.resources.RMod;
import neon.resources.RPerson;
import neon.resources.RQuest;
import neon.resources.RRecipe;
import neon.resources.RRegionTheme;
import neon.resources.RScript;
import neon.resources.RSign;
import neon.resources.RSpell;
import neon.resources.RTattoo;
import neon.resources.RTerrain;
import neon.resources.RZoneTheme;
import neon.systems.files.FileSystem;
import neon.systems.files.StringTranslator;
import neon.systems.files.XMLTranslator;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

public class ModFiler {
	private FileSystem files;
	private DataStore store;
	private Editor editor;
	private JFrame frame;
	
	public ModFiler(JFrame frame, FileSystem files, DataStore store, Editor editor) {
		this.frame = frame;
		this.files = files;
		this.store = store;
		this.editor = editor;
	}
	
	void loadMod() {
		// louche manier om de filechooser in de game dir te laten beginnen
		JFileChooser chooser = new JFileChooser(new File("neon.ini"));
		chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		chooser.setDialogTitle("Choose module");
		if(chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
			load(chooser.getSelectedFile(), true);
		} 
	}
	
	void load(File file, boolean active) {
		String path = file.getPath();
		try {
			path = files.mount(path);
			if(!isMod(path)) {	// kijken of dit wel mod is
				JOptionPane.showMessageDialog(frame, "Selected file is not a valid mod.");
				files.removePath(path);
			} else {
				if(isExtension(path)) {	// als extensie: alle masters laden
					Document doc = files.getFile(new XMLTranslator(), path, "main.xml");
					for(Object master : doc.getRootElement().getChildren("master")) {
						String id = ((Element)master).getText();
						Document ini = new Document();
						try {	// kijken in neon.ini welke mods er zijn
							FileInputStream in = new FileInputStream("neon.ini");
							ini = new SAXBuilder().build(in);
							in.close();
						} catch(JDOMException e) {}

						// kijken of er een mod is met de juist id
						for(Element mod : ini.getRootElement().getChild("files").getChildren()) {
							if(!mod.getText().equals(path)) {	// zien dat huidige mod niet nog eens geladen wordt
								System.out.println(mod.getText() + ", " + path);
								files.mount(mod.getText());
								Document d = files.getFile(new XMLTranslator(), mod.getText(), "main.xml");
								if(d.getRootElement().getAttributeValue("id").equals(id)) {
									store.loadData(mod.getText(), false, false);
								} else {
									files.removePath(mod.getText());
								}
							}
						}
					}
				} 

				frame.setTitle("Neon Editor: " + path);
				store.loadData(path, active, isExtension(path));
				editor.mapEditor.loadMaps(Editor.resources.getResources(RMap.class), path);
				editor.enableEditing(file.isDirectory());
				frame.pack();				
			}
		} catch (IOException e1) {
			JOptionPane.showMessageDialog(frame, "Selected file is not a valid mod.");
		}
	}

	public void save() {
		XMLBuilder builder = new XMLBuilder(store);
		RMod active = store.getActive();
		saveFile(new Document(store.getActive().getMainElement()), "main.xml");
		saveFile(new Document(store.getActive().getCCElement()), "cc.xml");
		saveFile(builder.getResourceDoc(Editor.resources.getResources(RItem.class), "items", active), "objects", "items.xml");
		saveFile(builder.getListDoc(Editor.resources.getResources(RFaction.class), "factions", active), "factions.xml");
		saveFile(builder.getListDoc(Editor.resources.getResources(RRecipe.class), "recipes", active), "objects", "alchemy.xml");
		saveFile(builder.getEventsDoc(), "events.xml");
		saveFile(builder.getListDoc(Editor.resources.getResources(RPerson.class), "people", active), "objects", "npc.xml");
		saveFile(builder.getResourceDoc(Editor.resources.getResources(RCreature.class), "monsters", active), "objects", "monsters.xml");
		saveFile(builder.getResourceDoc(Editor.resources.getResources(RSpell.class), "spells", active), "spells.xml");
		saveFile(builder.getListDoc(Editor.resources.getResources(RTerrain.class), "terrain", active), "terrain.xml");
		saveFile(builder.getListDoc(Editor.resources.getResources(RCraft.class), "items", active), "objects", "crafting.xml");
		saveFile(builder.getListDoc(Editor.resources.getResources(RSign.class), "signs", active), "signs.xml");
		saveFile(builder.getListDoc(Editor.resources.getResources(RTattoo.class), "tattoos", active), "tattoos.xml");
		saveFile(builder.getListDoc(Editor.resources.getResources(RZoneTheme.class), "themes", active), "themes", "zones.xml");
		saveFile(builder.getListDoc(Editor.resources.getResources(RDungeonTheme.class), "themes", active), "themes", "dungeons.xml");
		saveFile(builder.getListDoc(Editor.resources.getResources(RRegionTheme.class), "themes", active), "themes", "regions.xml");
		saveMaps();
		saveQuests();
		saveScripts();
	}
	
	private void saveMaps() {
		for(String name : files.listFiles(store.getActive().getPath()[0], "maps")) {
			String map = name.substring(name.lastIndexOf(File.separator) + 1, name.length() - 4);	// -4 voor ".xml"
			if(Editor.resources.getResource(map, "maps") == null) {
				files.delete(name);
			}
		}
		for(RMap map : editor.mapEditor.getActiveMaps()) {
			Document doc = new Document().setRootElement(map.toElement());
			saveFile(doc, "maps", map.id + ".xml");
		}
	}
	
	private void saveQuests() {
		for(String name : files.listFiles(store.getActive().getPath()[0], "quests")) {
			String quest = name.substring(name.lastIndexOf(File.separator) + 1, name.length() - 4);	// -4 voor ".xml"
			if(Editor.resources.getResource(quest, "quest") == null) {
				files.delete(name);
			}
		}
		for(RQuest quest : Editor.resources.getResources(RQuest.class)) {
			saveFile(new Document(quest.toElement()), "quests", quest.id + ".xml");
		}
	}
	
	private void saveScripts() {
		for(String name : files.listFiles(store.getActive().getPath()[0], "scripts")) {
			String script = name.substring(name.lastIndexOf(File.separator) + 1, name.length() - 3);	// -3 voor ".js"
			if(!store.getScripts().containsKey(script)) {
				files.delete(name);
			}
		}
		for(RScript script : store.getScripts().values()) {
			saveFile(script.script, "scripts", script.id + ".js");
		}
	}
	
	private void saveFile(String text, String... file) {
		String[] fullPath = new String[file.length + 1];
		System.arraycopy(file, 0, fullPath, 1, file.length);
		fullPath[0] = store.getActive().getPath()[0];
		files.saveFile(text, new StringTranslator(), fullPath);
	}

	private void saveFile(Document doc, String... file) {
		String[] fullPath = new String[file.length + 1];
		System.arraycopy(file, 0, fullPath, 1, file.length);
		fullPath[0] = store.getActive().getPath()[0];
		files.saveFile(doc, new XMLTranslator(), fullPath);
	}

	private boolean isExtension(String path) {
		Document doc = files.getFile(new XMLTranslator(), path, "main.xml");
		return doc.getRootElement().getName().equals("extension");
	}
	
	private boolean isMod(String path) {
		try {	// main.xml moet bestaan en valid xml zijn
			return files.getFile(new XMLTranslator(), path, "main.xml") != null;
		} catch(NullPointerException e) {
			return false;
		}
	}
}
