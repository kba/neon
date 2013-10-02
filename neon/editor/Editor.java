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

import javax.swing.*;

import neon.editor.help.HelpLabels;
import neon.editor.maps.*;
import neon.editor.resources.*;
import neon.resources.*;
import neon.systems.files.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.jar.JarFile;
import java.awt.event.*;
import neon.ui.HelpWindow;
import javax.swing.border.TitledBorder;
import javax.swing.tree.*;

public class Editor implements Runnable, ActionListener {
	public static JCheckBoxMenuItem tShow, tEdit, oShow, oEdit;
	public static FileSystem files;
	public final static ResourceManager resources = new ResourceManager();
	private static JFrame frame;
	private static DataStore store;
	private static JPanel toolPanel;
	private static StatusBar status;
	
	protected MapEditor mapEditor;
	private JTabbedPane mapTabbedPane;
	private JMenuBar menuBar;
	private JMenuItem pack, unpack, newMain, newExt, load, save, export, calculate;
	private JMenu make, edit, tools;
	private JPanel terrainPanel, objectPanel, resourcePanel;
	private JTree objectTree, resourceTree;
	private ModFiler filer;
	private JList<RTerrain> terrainList;
	private DefaultListModel<RTerrain> terrainListModel;
	private InfoEditor infoEditor;
	private CCEditor ccEditor;
	private ScriptEditor scriptEditor;
	private EventEditor eventEditor;

	public static void main(String[] args) {
		try {	// hier direct setten om problemen te voorkomen
			javax.swing.UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		javax.swing.SwingUtilities.invokeLater(new Editor());
	}

	public Editor() {
		// main window
		frame = new JFrame("Neon Editor");
		frame.getContentPane().setLayout(new BorderLayout());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setPreferredSize(new Dimension(1280, 800));
    	
		// dingen
    	files = new FileSystem();
    	store = new DataStore();

    	// menubalk
    	menuBar = new JMenuBar();
    	JMenu file = new JMenu("File");
    	make = new JMenu("New");
    	newMain = new JMenuItem("Master module...");
    	newExt = new JMenuItem("Extension module...");
    	load = new JMenuItem("Load...");
    	save = new JMenuItem("Save");
    	JMenuItem quit = new JMenuItem("Exit");
    	newMain.setActionCommand("newMain");
    	newExt.setActionCommand("newExt");
    	save.setActionCommand("save");
    	load.setActionCommand("load");
    	quit.setActionCommand("quit");
    	newMain.addActionListener(this);
    	newExt.addActionListener(this);
    	quit.addActionListener(this);
    	filer = new ModFiler(frame, files, store, this);
    	save.addActionListener(this);
    	load.addActionListener(this);
    	save.setEnabled(false);
    	make.add(newMain);
    	make.add(newExt);
    	file.add(make);
    	file.add(load);
    	file.addSeparator();
    	file.add(save);
    	file.addSeparator();
    	file.add(quit);
    	edit = new JMenu("Edit");
    	edit.setEnabled(false);
    	JMenuItem script = new JMenuItem("Scripts...");
    	JMenuItem events = new JMenuItem("Events...");
    	JMenuItem cc = new JMenuItem("Character creation...");
    	JMenuItem game = new JMenuItem("Game info...");
    	script.setActionCommand("script");
    	cc.setActionCommand("cc");
    	game.setActionCommand("game");
    	events.setActionCommand("events");
    	script.addActionListener(this);
    	cc.addActionListener(this);
    	game.addActionListener(this);
    	events.addActionListener(this);
    	edit.add(script);
    	edit.add(events);
    	edit.add(cc);
    	edit.add(game);
    	JMenu view = new JMenu("View");
    	tShow = new JCheckBoxMenuItem("Show terrain");
    	oShow = new JCheckBoxMenuItem("Show objects");
    	tEdit = new JCheckBoxMenuItem("Edit terrain");
    	oEdit = new JCheckBoxMenuItem("Edit objects");
    	tShow.setSelected(true);
    	oShow.setSelected(true);
    	tEdit.setSelected(true);
    	oEdit.setSelected(true);
    	view.add(tShow);
    	view.add(oShow);
    	view.addSeparator();
    	view.add(tEdit);
    	view.add(oEdit);
    	tools = new JMenu("Tools");
    	tools.setEnabled(false);
    	unpack = new JMenuItem("Unpack mod...");
    	unpack.setActionCommand("unpack");
    	unpack.addActionListener(this);
    	pack = new JMenuItem("Pack mod...");
    	pack.setActionCommand("pack");
    	pack.addActionListener(this);
    	tools.add(unpack);
    	tools.add(pack);
    	tools.addSeparator();
    	export = new JMenuItem("Export SVG...");
    	export.setActionCommand("svg");
    	export.addActionListener(this);
    	calculate = new JMenuItem("Challenge calculator...");
    	calculate.setActionCommand("calculate");
    	calculate.addActionListener(this);
    	tools.add(export);
    	tools.add(calculate);
    	JMenu help = new JMenu("Help");
    	JMenuItem introGuide = new JMenuItem("Getting started...");
    	introGuide.setActionCommand("intro");
    	introGuide.addActionListener(this);
    	help.add(introGuide);
    	JMenuItem scriptGuide = new JMenuItem("Scripting guide...");
    	scriptGuide.setActionCommand("scripting");
    	scriptGuide.addActionListener(this);
    	help.add(scriptGuide);
    	JMenuItem mapGuide = new JMenuItem("Map editing...");
    	mapGuide.setActionCommand("mapping");
    	mapGuide.addActionListener(this);
    	help.add(mapGuide);
    	JMenuItem resGuide = new JMenuItem("Resource editing...");
    	resGuide.setActionCommand("resources");
    	resGuide.addActionListener(this);
    	help.add(resGuide);
    	menuBar.add(file);
    	menuBar.add(edit);
    	menuBar.add(view);
    	menuBar.add(tools);
    	menuBar.add(help);
    	frame.setJMenuBar(menuBar);
    	
    	toolPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    	frame.add(toolPanel, BorderLayout.PAGE_START);
		
		// objecten
		objectTree = new JTree();
		objectTree.setRootVisible(false);
		objectTree.setShowsRootHandles(true);
		resourceTree = new JTree();
		resourceTree.setRootVisible(false);
		resourceTree.setShowsRootHandles(true);
		
    	// panels met maps
    	mapTabbedPane = new JTabbedPane();
		JPanel mapPanel = new JPanel(new BorderLayout());
		mapEditor = new MapEditor(mapTabbedPane, mapPanel);
    	
    	// panel met objecten en terrein
    	JTabbedPane editPanel = new JTabbedPane();
    	objectPanel = new JPanel(new BorderLayout());
    	objectPanel.setBorder(new TitledBorder("Objects"));
    	editPanel.add(objectPanel, "Objects");
    	terrainPanel = new JPanel(new BorderLayout());
    	terrainPanel.setBorder(new TitledBorder("Terrain"));
    	editPanel.add(terrainPanel, "Terrain");
    	resourcePanel = new JPanel(new BorderLayout());
    	resourcePanel.setBorder(new TitledBorder("Resources"));
    	editPanel.add(resourcePanel, "Resources");
    	
		// gefoefel met JSplitPanes om drie kolommen te krijgen
    	JSplitPane bigSplitPane = new JSplitPane();
    	JSplitPane smallSplitPane = new JSplitPane();
		smallSplitPane.setLeftComponent(mapPanel);
		smallSplitPane.setRightComponent(mapTabbedPane);
		bigSplitPane.setLeftComponent(smallSplitPane);
		bigSplitPane.setRightComponent(editPanel);
		
		// dit kan beter
		smallSplitPane.setDividerLocation(200);
		bigSplitPane.setDividerLocation(1000);
		frame.add(bigSplitPane, BorderLayout.CENTER);
		
		// statusbar
		status = new StatusBar();
		frame.add(status, BorderLayout.SOUTH);
	}

	public void run() {
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
		
	public static DataStore getStore() {
		return store;
	}
	
	public static JFrame getFrame() {
		return frame;
	}
	
	private void createMain() {
		JFileChooser chooser = new JFileChooser(new File("neon.ini"));
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setDialogTitle("Choose module directory");
		if(chooser.showDialog(frame, "Choose") == JFileChooser.APPROVE_OPTION) {
			// mod maken
			createMod(chooser.getSelectedFile());
			// editing enablen
			enableEditing(true);
			frame.pack();		
		} 
	}
	
	private void createMod(File file) {
		// path is hier de naam van de dir, enkel files weet volledig path
		String path = file.getName();	// dit wordt ook de mod id
		try {
			files.mount(file.getPath());
			// dirs genereren
			File objects = new File(file, "objects");
			objects.mkdir();
			File maps = new File(file, "maps");
			maps.mkdir();
			File quests = new File(file, "quests");
			quests.mkdir();
			File themes = new File(file, "themes");
			themes.mkdir();
			
			// load zorgt dat alle resources e.d. geïnitialiseerd zijn
			store.loadData(path, true, false);
			mapEditor.loadMaps(resources.getResources(RMap.class), path);		
		} catch (IOException e) {
			JOptionPane.showMessageDialog(frame, "Invalid mod directory: " + file + ".");
		}
	}
	
	protected void enableEditing(boolean unpacked) {
		// menu items enablen
		pack.setEnabled(unpacked);
		unpack.setEnabled(!unpacked);
		make.setEnabled(false);
		tools.setEnabled(true);
		edit.setEnabled(true);
		load.setEnabled(false);
		save.setEnabled(true);		

		// lijstjes klaarmaken
		initObjects();
		initResources();
		initTerrain();
	}
	
	private void createExtension() {
		JFileChooser chooser = new JFileChooser(new File("neon.ini"));
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setDialogTitle("Choose master module");
		if(chooser.showDialog(frame, "Master") == JFileChooser.APPROVE_OPTION) {
			File master = chooser.getSelectedFile();
			chooser.setDialogTitle("Choose extension directory");
			if(chooser.showDialog(frame, "Extension") == JFileChooser.APPROVE_OPTION) {
				// master laden
				filer.load(master, false);
				// extension aanmaken
				createMod(chooser.getSelectedFile());
				enableEditing(true);
				frame.pack();		
			}
		}
	}
	
	public static void addToolBar(JToolBar bar) {
		toolPanel.add(bar);
	}
	
	public static StatusBar getStatusBar() {
		return status;
	}
	
	private void initTerrain() {
		terrainListModel = new DefaultListModel<RTerrain>();
		for(RTerrain rt : resources.getResources(RTerrain.class)) {
			terrainListModel.addElement(rt);
		}
		terrainList = new JList<RTerrain>(terrainListModel);
		terrainList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		TerrainListener listener = new TerrainListener(mapEditor, terrainList);
		terrainList.addListSelectionListener(listener);
		terrainList.addMouseListener(listener);
		terrainPanel.add(new JScrollPane(terrainList));			
	}
	
	private void initResources() {
		DefaultMutableTreeNode top = new DefaultMutableTreeNode("Resources");
		
		// crafts
		ResourceNode craftNode = new ResourceNode("Crafting", ResourceNode.ResourceType.CRAFT);
		for(RCraft craft : resources.getResources(RCraft.class)) {
			craftNode.add(new ResourceNode(craft, ResourceNode.ResourceType.CRAFT));
		}
		top.add(craftNode);
		
		// factions
		ResourceNode factionNode = new ResourceNode("Factions", ResourceNode.ResourceType.FACTION);
		for(RFaction faction : resources.getResources(RFaction.class)) {
			factionNode.add(new ResourceNode(faction, ResourceNode.ResourceType.FACTION));
		}
		top.add(factionNode);
		
		// region themes
		ResourceNode regionNode = new ResourceNode("Region themes", ResourceNode.ResourceType.REGION);
		for(RRegionTheme region : resources.getResources(RRegionTheme.class)) {
			regionNode.add(new ResourceNode(region, ResourceNode.ResourceType.REGION));
		}
		top.add(regionNode);
		
		// zone themes
		ResourceNode zoneNode = new ResourceNode("Zone themes", ResourceNode.ResourceType.ZONE);
		for(RZoneTheme zone : resources.getResources(RZoneTheme.class)) {
			zoneNode.add(new ResourceNode(zone, ResourceNode.ResourceType.ZONE));
		}
		top.add(zoneNode);
		
		// dungeon themes
		ResourceNode dungeonNode = new ResourceNode("Dungeon themes", ResourceNode.ResourceType.DUNGEON);
		for(RDungeonTheme dungeon : resources.getResources(RDungeonTheme.class)) {
			dungeonNode.add(new ResourceNode(dungeon, ResourceNode.ResourceType.DUNGEON));
		}
		top.add(dungeonNode);
		
		// quests
		ResourceNode questNode = new ResourceNode("Quests", ResourceNode.ResourceType.QUEST);
		for(RQuest quest : resources.getResources(RQuest.class)) {
			questNode.add(new ResourceNode(quest, ResourceNode.ResourceType.QUEST));
		}
		top.add(questNode);
		
		// alchemy
		ResourceNode alchemyNode = new ResourceNode("Alchemy", ResourceNode.ResourceType.RECIPE);
		for(RRecipe rr : resources.getResources(RRecipe.class)) {
			alchemyNode.add(new ResourceNode(rr, ResourceNode.ResourceType.RECIPE));
		}
		top.add(alchemyNode);

		// tattoos
		ResourceNode tattooNode = new ResourceNode("Tattoos", ResourceNode.ResourceType.TATTOO);
		for(RTattoo rt : resources.getResources(RTattoo.class)) {
			tattooNode.add(new ResourceNode(rt, ResourceNode.ResourceType.TATTOO));
		}
		top.add(tattooNode);
		
		// spells
		ResourceNode spellNode = new ResourceNode("Spells", ResourceNode.ResourceType.SPELL);
		ResourceNode powerNode = new ResourceNode("Powers", ResourceNode.ResourceType.POWER);
		ResourceNode curseNode = new ResourceNode("Curses", ResourceNode.ResourceType.CURSE);
		ResourceNode poisonNode = new ResourceNode("Poison", ResourceNode.ResourceType.POISON);
		ResourceNode enchantNode = new ResourceNode("Enchantments", ResourceNode.ResourceType.ENCHANTMENT);
		ResourceNode diseaseNode = new ResourceNode("Diseases", ResourceNode.ResourceType.DISEASE);
		ResourceNode levelNode = new ResourceNode("Leveled spells", ResourceNode.ResourceType.LEVEL_SPELL);
		for(RSpell rs : resources.getResources(RSpell.class)) {
			if(rs instanceof LSpell) {
				levelNode.add(new ResourceNode(rs, ResourceNode.ResourceType.LEVEL_SPELL));
			} else {
				switch(rs.type) {
				case CURSE: curseNode.add(new ResourceNode(rs, ResourceNode.ResourceType.CURSE)); break;
				case DISEASE: diseaseNode.add(new ResourceNode(rs, ResourceNode.ResourceType.DISEASE));	break;
				case ENCHANT: enchantNode.add(new ResourceNode(rs, ResourceNode.ResourceType.ENCHANTMENT));	break;
				case POISON: poisonNode.add(new ResourceNode(rs, ResourceNode.ResourceType.POISON)); break;
				case POWER:	powerNode.add(new ResourceNode(rs, ResourceNode.ResourceType.POWER)); break;
				case SPELL:	spellNode.add(new ResourceNode(rs, ResourceNode.ResourceType.SPELL)); break;
				}
			}
		}
		top.add(spellNode);
		top.add(powerNode);
		top.add(curseNode);
		top.add(poisonNode);
		top.add(enchantNode);
		top.add(diseaseNode);
		top.add(levelNode);

		// signs
		ResourceNode signNode = new ResourceNode("Birth signs", ResourceNode.ResourceType.SIGN);		
		for(RSign rs : resources.getResources(RSign.class)) {
			signNode.add(new ResourceNode(rs, ResourceNode.ResourceType.SIGN));
		}
		top.add(signNode);

		resourceTree.setModel(new DefaultTreeModel(top));
		resourceTree.addMouseListener(new ResourceTreeListener(resourceTree, frame));
		resourcePanel.add(new JScrollPane(resourceTree));
	}
	
	private void initObjects() {
		DefaultMutableTreeNode top = new DefaultMutableTreeNode("Objects");
		
		//creatures;
		ObjectNode creatureNode = new ObjectNode("Creatures", ObjectNode.ObjectType.CREATURE);
		ObjectNode levelCreatureNode = new ObjectNode("Leveled creatures", ObjectNode.ObjectType.LEVEL_CREATURE);
		for(RCreature rc : resources.getResources(RCreature.class)) {
			if(rc instanceof LCreature) {
				levelCreatureNode.add(new ObjectNode(rc, ObjectNode.ObjectType.LEVEL_CREATURE));
			} else {
				creatureNode.add(new ObjectNode(rc, ObjectNode.ObjectType.CREATURE));
			}
		}
		top.add(creatureNode);
		top.add(levelCreatureNode);

		//NPCs
		ObjectNode npcNode = new ObjectNode("NPCs", ObjectNode.ObjectType.NPC);
		for(RPerson rp : resources.getResources(RPerson.class)) {
			npcNode.add(new ObjectNode(rp, ObjectNode.ObjectType.NPC));
		}
		top.add(npcNode);

		//items
		ObjectNode itemNode = new ObjectNode("Items", ObjectNode.ObjectType.ITEM);
		ObjectNode weaponNode = new ObjectNode("Weapons", ObjectNode.ObjectType.WEAPON);
		ObjectNode clothingNode = new ObjectNode("Clothing", ObjectNode.ObjectType.CLOTHING);
		ObjectNode armorNode = new ObjectNode("Armor", ObjectNode.ObjectType.ARMOR);
		ObjectNode lightNode = new ObjectNode("Light", ObjectNode.ObjectType.LIGHT);
		ObjectNode doorNode = new ObjectNode("Doors", ObjectNode.ObjectType.DOOR);
		ObjectNode containerNode = new ObjectNode("Containers", ObjectNode.ObjectType.CONTAINER);
		ObjectNode potionNode = new ObjectNode("Potions", ObjectNode.ObjectType.POTION);
		ObjectNode scrollNode = new ObjectNode("Scrolls", ObjectNode.ObjectType.SCROLL);
		ObjectNode bookNode = new ObjectNode("Books", ObjectNode.ObjectType.BOOK);
		ObjectNode coinNode = new ObjectNode("Money", ObjectNode.ObjectType.MONEY);
		ObjectNode foodNode = new ObjectNode("Food", ObjectNode.ObjectType.FOOD);
		ObjectNode levelItemNode = new ObjectNode("Leveled items", ObjectNode.ObjectType.LEVEL_ITEM);
		for(RItem ri : resources.getResources(RItem.class)) {
			if(ri instanceof LItem) {
				levelItemNode.add(new ObjectNode(ri, ObjectNode.ObjectType.LEVEL_ITEM));
			} else {
				switch(ri.type) {
				case armor: armorNode.add(new ObjectNode(ri, ObjectNode.ObjectType.ARMOR)); break;
				case book: bookNode.add(new ObjectNode(ri, ObjectNode.ObjectType.BOOK)); break;
				case clothing: clothingNode.add(new ObjectNode(ri, ObjectNode.ObjectType.CLOTHING)); break;
				case coin: coinNode.add(new ObjectNode(ri, ObjectNode.ObjectType.MONEY)); break;
				case container: containerNode.add(new ObjectNode(ri, ObjectNode.ObjectType.CONTAINER)); break;
				case door: doorNode.add(new ObjectNode(ri, ObjectNode.ObjectType.DOOR)); break;
				case food: foodNode.add(new ObjectNode(ri, ObjectNode.ObjectType.FOOD)); break;
				case light: lightNode.add(new ObjectNode(ri, ObjectNode.ObjectType.LIGHT)); break;
				case potion: potionNode.add(new ObjectNode(ri, ObjectNode.ObjectType.POTION)); break;
				case scroll: scrollNode.add(new ObjectNode(ri, ObjectNode.ObjectType.SCROLL)); break;
				case weapon: weaponNode.add(new ObjectNode(ri, ObjectNode.ObjectType.WEAPON)); break;
				default: itemNode.add(new ObjectNode(ri, ObjectNode.ObjectType.ITEM));
				}
			}
		}
		top.add(itemNode);
		top.add(weaponNode);
		top.add(clothingNode);
		top.add(armorNode);
		top.add(lightNode);
		top.add(doorNode);
		top.add(containerNode);
		top.add(potionNode);
		top.add(scrollNode);
		top.add(bookNode);
		top.add(coinNode);
		top.add(foodNode);
		top.add(levelItemNode);

		objectTree.setModel(new DefaultTreeModel(top));
		objectTree.addMouseListener(new ObjectTreeListener(objectTree, frame));
		objectPanel.add(new JScrollPane(objectTree));
		objectTree.setDragEnabled(true);
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("save")) {
			filer.save();
		} else if(e.getActionCommand().equals("load")) {
			filer.loadMod();
		} else if(e.getActionCommand().equals("quit")) {
			System.exit(0);
		} else if(e.getActionCommand().equals("newMain")) {
			createMain();
		} else if(e.getActionCommand().equals("newExt")) {
			createExtension();
		} else if(e.getActionCommand().equals("script")) {
			if(scriptEditor == null) {
				scriptEditor = new ScriptEditor(frame);
			}
			scriptEditor.show();
		} else if(e.getActionCommand().equals("cc")) {
			if(ccEditor == null) {
				ccEditor = new CCEditor(frame);
			}
			ccEditor.show();
		} else if(e.getActionCommand().equals("game")) {
			if(infoEditor == null) {
				infoEditor = new InfoEditor(frame);
			}
			infoEditor.show();
		} else if(e.getActionCommand().equals("events")) {
			if(eventEditor == null) {
				eventEditor = new EventEditor(frame);
			}
			eventEditor.show();
		} else if(e.getActionCommand().equals("pack")) {
			if(JOptionPane.showConfirmDialog(frame, "Do you wish to save the current data and pack it?", 
					"Pack mod", JOptionPane.YES_NO_OPTION) == 0) {
				pack();
			}
		} else if(e.getActionCommand().equals("unpack")) {
			unpack();
		} else if(e.getActionCommand().equals("svg")) {
			if((EditablePane)mapTabbedPane.getSelectedComponent() != null) {
				ZoneTreeNode node = ((EditablePane)mapTabbedPane.getSelectedComponent()).getNode();
				SVGExporter.exportToSVG(node, files, store);
			}
		} else if(e.getActionCommand().equals("calculate")) {
			new ChallengeCalculator().show();
		} else if(e.getActionCommand().equals("scripting")) {
			showHelp("scripting.html", "Scripting guide");
		} else if(e.getActionCommand().equals("intro")) {
			showHelp("intro.html", "Getting started");
		} else if(e.getActionCommand().equals("mapping")) {
			showHelp("maps.html", "Map editing");
		} else if(e.getActionCommand().equals("resources")) {
			showHelp("resources.html", "Resource editing");
		}
	}
	
	private void showHelp(String file, String title) {
		InputStream input = HelpLabels.class.getResourceAsStream(file);
		Scanner scanner = new Scanner(input, "UTF-8");
		String text = scanner.useDelimiter("\\A").next();
		scanner.close();
		new HelpWindow(frame).show(title, text);		
	}

	private void pack() {
		filer.save();
		try {
			JarFile jar = FileUtils.pack(store.getActive().getPath()[0], store.getActive().get("id"));
			System.out.println("attributes: " + jar.getManifest().getMainAttributes());
		} catch (IOException e) {
			JOptionPane.showMessageDialog(frame, "Packing failed");
		}
	}
	
	private void unpack() {
		FileUtils.unpack(store.getActive().getPath()[0]);
		JOptionPane.showMessageDialog(frame, "Please restart the editor and load the unpacked mod.", 
				"Unpack mod", JOptionPane.INFORMATION_MESSAGE);
	}	
}
