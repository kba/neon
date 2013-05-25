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

package neon.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.*;
import javax.swing.border.*;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import neon.core.Configuration;
import neon.core.Engine;
import neon.core.KeyConfig;

public class OptionDialog {
	private JCheckBox audioBox;
	private JRadioButton numpad, qwerty, azerty, qwertz;
	private ButtonGroup group;	
	private JDialog frame;
	private JFrame parent;
	
	public OptionDialog(JFrame parent) {
		this.parent = parent;
		frame = new JDialog(parent, false);
		frame.setPreferredSize(new Dimension(parent.getWidth() - 100, parent.getHeight() - 100));
		frame.setUndecorated(true);
		frame.setTitle("Options");
		
		JPanel control = new JPanel();
		control.setLayout(new BoxLayout(control, BoxLayout.PAGE_AXIS));
		control.setBorder(new TitledBorder("Choose movement keys"));
		numpad = new JRadioButton("numpad");
		numpad.setMnemonic('n');
		numpad.setSelected(true);
		qwerty = new JRadioButton("qwerty");
		qwerty.setMnemonic('q');
		azerty = new JRadioButton("azerty");
		azerty.setMnemonic('a');
		qwertz = new JRadioButton("qwertz");
		qwertz.setMnemonic('z');
		ButtonAction numpadAction = new ButtonAction("numpad", "numpad");
		control.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("N"), "numpad");
		control.getActionMap().put("numpad", numpadAction);
		ButtonAction qwertyAction = new ButtonAction("qwerty", "qwerty");
		control.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("Q"), "qwerty");
		control.getActionMap().put("qwerty", qwertyAction);
		ButtonAction azertyAction = new ButtonAction("azerty", "azerty");
		control.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("A"), "azerty");
		control.getActionMap().put("azerty", azertyAction);
		ButtonAction qwertzAction = new ButtonAction("qwertz", "qwertz");
		control.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("Z"), "qwertz");
		control.getActionMap().put("qwertz", qwertzAction);
		
		group = new ButtonGroup();
		group.add(numpad);
		group.add(qwerty);
		group.add(qwertz);
		group.add(azerty);
		
		JPanel audio = new JPanel();
		audio.setBorder(new TitledBorder("Audio options"));
		audioBox = new JCheckBox("audio on");
		audioBox.setDisplayedMnemonicIndex(6);
		audioBox.setSelected(Configuration.audio);
		audio.add(audioBox);
		ButtonAction audioAction = new ButtonAction("audio", "audio");
		audio.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("O"), "audio");
		audio.getActionMap().put("audio", audioAction);
		
		JPanel buttons = new JPanel();
		Action okAction = new ButtonAction("Ok", "ok");
		JButton ok = new JButton(okAction);
		buttons.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ENTER"), "ok");
		buttons.getActionMap().put("ok", okAction);
		Action cancelAction = new ButtonAction("Cancel", "esc");
		JButton cancel = new JButton(cancelAction);
		buttons.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "esc");
		buttons.getActionMap().put("esc", okAction);
		
		control.add(numpad);
		control.add(azerty);
		control.add(qwerty);
		control.add(qwertz);
		buttons.add(ok);
		buttons.add(cancel);
		
		JPanel options = new JPanel(new BorderLayout());		
		options.add(control, BorderLayout.CENTER);
		options.add(audio, BorderLayout.PAGE_END);
		
    	JLabel instructions = new JLabel("Press enter to accept settings, esc to cancel.");
    	instructions.setBorder(new CompoundBorder(new TitledBorder("Instructions"), new EmptyBorder(0,5,10,5)));
		
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(instructions, BorderLayout.PAGE_START);
		panel.add(options);
		panel.add(buttons, BorderLayout.PAGE_END);
		panel.setBorder(new CompoundBorder(new EtchedBorder(EtchedBorder.RAISED), new EmptyBorder(10,10,10,10)));		
		frame.setContentPane(panel);
        try {
        	frame.setOpacity(0.9f);
        } catch(UnsupportedOperationException e) {
        	System.out.println("setOpacity() not supported.");
        }
	}
	
	public void show() {
		switch(KeyConfig.keys) {
		case KeyConfig.AZERTY: azerty.setSelected(true); break;
		case KeyConfig.QWERTY: qwerty.setSelected(true); break;
		case KeyConfig.QWERTZ: qwertz.setSelected(true); break;
		case KeyConfig.NUMPAD: numpad.setSelected(true); break;
		}
		frame.pack();
		frame.setLocationRelativeTo(parent);
		frame.setVisible(true);	
	}

	@SuppressWarnings("serial")
	private class ButtonAction extends AbstractAction {
		public ButtonAction(String text, String command) {
			super(text);
			putValue(ACTION_COMMAND_KEY, command);
		}
		
		public void actionPerformed(ActionEvent e) {
			if(e.getActionCommand().equals("numpad")) {
				numpad.setSelected(true);
			} else if(e.getActionCommand().equals("azerty")) {
				azerty.setSelected(true);
			} else if(e.getActionCommand().equals("qwerty")) {
				qwerty.setSelected(true);
			} else if(e.getActionCommand().equals("qwertz")) {
				qwertz.setSelected(true);
			} else if(e.getActionCommand().equals("audio")) {
				audioBox.setSelected(!audioBox.isSelected());
			} else if(e.getActionCommand().equals("ok")) {
				save();
				frame.dispose();
			} else if(e.getActionCommand().equals("esc")) {
				frame.dispose();
			}
		}
		
		private void save() {
			Document doc = new Document();
			try {
				FileInputStream in = new FileInputStream("neon.ini");
				doc = new SAXBuilder().build(in);
				in.close();
			} catch (Exception e) {
				Engine.getLogger().severe(e.getMessage());
			}
			
			Configuration.audio = audioBox.isSelected();
			Element ini = doc.getRootElement();
			if(group.isSelected(numpad.getModel())) {
				KeyConfig.setKeys(KeyConfig.NUMPAD);
				ini.getChild("keys").setText("numpad");
			} else if(group.isSelected(azerty.getModel())) {
				KeyConfig.setKeys(KeyConfig.AZERTY);
				ini.getChild("keys").setText("azerty");
			} else if(group.isSelected(qwerty.getModel())) {
				KeyConfig.setKeys(KeyConfig.QWERTY);
				ini.getChild("keys").setText("qwerty");
			} else if(group.isSelected(qwertz.getModel())) {
				KeyConfig.setKeys(KeyConfig.QWERTZ);
				ini.getChild("keys").setText("qwertz");
			}
			
			XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
			try {
				FileOutputStream out = new FileOutputStream("neon.ini");
				outputter.output(doc, out);
				out.close();
			} catch (IOException e) {
				Engine.getLogger().severe(e.getMessage());
			} 
		}
	}
}
