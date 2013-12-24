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

package neon.ui.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import javax.swing.*;
import javax.swing.border.*;
import neon.entities.Creature;
import neon.entities.Player;
import neon.magic.Effect;
import neon.resources.RSpell;
import neon.ui.UserInterface;

public class SpellMakerDialog {
	private JDialog frame;
	private JPanel panel, options;
	private Player player;
	private JComboBox<Effect> effectBox;
	private JSpinner sizeSpinner, rangeSpinner, durationSpinner;
	private JTextField nameField;
	private UserInterface ui;
	
	public SpellMakerDialog(UserInterface ui) {
		this.ui = ui;
		JFrame parent = ui.getWindow();
		frame = new JDialog(parent, true);
		frame.setPreferredSize(new Dimension(parent.getWidth() - 100, parent.getHeight() - 100));
		frame.setUndecorated(true);
		
		panel = new JPanel(new BorderLayout());
		panel.setBorder(new CompoundBorder(new EtchedBorder(EtchedBorder.RAISED), new EmptyBorder(10,10,10,10)));		

		JLabel instructions = new JLabel("Use tab to switch between the available options, press enter to purchase spell, esc to cancel.");
    	instructions.setBorder(new CompoundBorder(new TitledBorder("Instructions"), new EmptyBorder(0,5,10,5)));
        panel.add(instructions, BorderLayout.PAGE_END);
		
		options = new JPanel();
		options.setBorder(new TitledBorder("Make your spell"));
		GroupLayout layout = new GroupLayout(options);
		options.setLayout(layout);
		layout.setAutoCreateGaps(true);

		JLabel nameLabel = new JLabel("Name: ");
		JLabel targetLabel = new JLabel("Target: ");
		JLabel effectLabel = new JLabel("Effect: ");
		JLabel sizeLabel = new JLabel("Size: ");
		JLabel rangeLabel = new JLabel("Range: ");
		JLabel durationLabel = new JLabel("Duration: ");
		nameField = new JTextField(20);
		effectBox = new JComboBox<Effect>(neon.magic.Effect.values());
		sizeSpinner = new JSpinner(new SpinnerNumberModel(0,0,100,1));
		rangeSpinner = new JSpinner(new SpinnerNumberModel(0,0,100,1));
		durationSpinner = new JSpinner(new SpinnerNumberModel(0,0,100,1));
		layout.setVerticalGroup(
				layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(nameLabel).addComponent(nameField))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(targetLabel))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(effectLabel).addComponent(effectBox))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(sizeLabel).addComponent(sizeSpinner))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(rangeLabel).addComponent(rangeSpinner))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(durationLabel).addComponent(durationSpinner)));
		layout.setHorizontalGroup(
				layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(nameLabel).addComponent(targetLabel).addComponent(effectLabel)
						.addComponent(sizeLabel).addComponent(rangeLabel).addComponent(durationLabel))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
						.addComponent(nameField)
						.addComponent(effectBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(sizeSpinner).addComponent(rangeSpinner).addComponent(durationSpinner)));
		
		panel.add(options, BorderLayout.CENTER);

		Action ok = new OkAction();
		Action cancel = new CancelAction();
		panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ENTER"), "enter");
        panel.getActionMap().put("enter", ok);
        panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "esc");
        panel.getActionMap().put("esc", cancel);
        
        
        frame.setContentPane(panel);
        try {
        	frame.setOpacity(0.9f);
        } catch(UnsupportedOperationException e) {
        	System.out.println("setOpacity() not supported.");
        }
	}
	
	public void show(Player player, Creature enchanter) {
		this.player = player;
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		nameField.requestFocus();
	}
	
	private RSpell createSpell() {
		return new RSpell(nameField.getText(), (Integer)rangeSpinner.getValue(), (Integer)durationSpinner.getValue(),
				effectBox.getSelectedItem().toString(), (Integer)sizeSpinner.getValue(), (Integer)sizeSpinner.getValue(), "spell");
	}
	
	private boolean isValid() {
		return(nameField.getText() != null && !nameField.getText().isEmpty());
	}
	
	@SuppressWarnings("serial")
	private class OkAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			if(isValid()) {
				player.getMagicComponent().addSpell(createSpell());
				frame.dispose();
			} else {
				ui.showMessage("Please fill in all required fields.", 2);
			}
		}
	}
	
	@SuppressWarnings("serial")
	private class CancelAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			frame.dispose();
		}
	}
}
