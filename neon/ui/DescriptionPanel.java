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
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import neon.objects.entities.Armor;
import neon.objects.entities.Clothing;
import neon.objects.entities.Container;
import neon.objects.entities.Door;
import neon.objects.entities.Entity;
import neon.objects.entities.Item;
import neon.objects.entities.Weapon;
import neon.objects.resources.RClothing;
import neon.objects.resources.RSpell;
import neon.objects.resources.RWeapon;

@SuppressWarnings("serial")
public class DescriptionPanel extends JPanel {
	private JLabel label = new JLabel();
	private JPanel properties = new JPanel();
	private String coin;
	
	public DescriptionPanel(String coin) {
		this.coin = coin;
		
		setLayout(new BorderLayout());
    	setBorder(new TitledBorder("Description"));

    	label.setHorizontalAlignment(JLabel.CENTER);
    	label.setHorizontalTextPosition(JLabel.CENTER);
    	label.setVerticalTextPosition(JLabel.BOTTOM);
    	label.setBorder(new EmptyBorder(20, 0, 20, 0));

		BoxLayout layout = new BoxLayout(properties, BoxLayout.PAGE_AXIS);
		properties.setLayout(layout);
	}
	
	/**
	 * Updates the description.
	 * 
	 * @param entity	the {@code Entity} to describe
	 */
	public void update(Entity entity) {
		if(entity != null) {
			BufferedImage image = new BufferedImage(50, 60, BufferedImage.TYPE_INT_RGB);
			Graphics2D buffer = image.createGraphics();
			buffer.translate(-entity.bounds.x * 50, -entity.bounds.y * 50);
			entity.getRenderer().paint(buffer, 50, false);
			label.setIcon(new ImageIcon(image));
			label.setText(entity.toString());
			add(label, BorderLayout.PAGE_START);
			
			properties.removeAll();
			if(entity instanceof Door) {
				
			} else if(entity instanceof Container) {
				
			} else if(entity instanceof Item) {
				Item item = (Item)entity;
				if(item.resource.cost > 0) {
					properties.add(new JLabel("  Price: " + item.resource.cost + " " + coin));
				}
				if(item.resource.weight > 0) {
					properties.add(new JLabel("  Weight: " + item.resource.weight));
				}
				if(item.enchantment != null) {
					String def = item instanceof Item.Food ? "  Effect: " : "  Enchantment: ";
					RSpell spell = item.enchantment.getSpell();
					String text = (spell.name != null ? spell.name : spell.id);
					properties.add(new JLabel(def + text));
				}
				if(item instanceof Armor) {
					RClothing resource = (RClothing)item.resource;
					properties.add(new JLabel("  Type: " + resource.slot.toString().toLowerCase()));
					properties.add(new JLabel("  Armor class: " + resource.kind.toString().toLowerCase()));
					properties.add(new JLabel("  Armor rating: " + resource.rating));
					properties.add(new JLabel("  State: " + ((Armor)item).getState() + "%"));
				} else if(item instanceof Clothing) {
					RClothing resource = (RClothing)item.resource;
					properties.add(new JLabel("  Type: " + resource.slot.toString().toLowerCase()));
				} else if(item instanceof Weapon) {
					RWeapon resource = (RWeapon)item.resource;
					properties.add(new JLabel("  Type: " + resource.weaponType));
					properties.add(new JLabel("  Damage: " + resource.damage));
					properties.add(new JLabel("  State: " + ((Weapon)item).getState() + "%"));
					if(item.enchantment != null) {
						properties.add(new JLabel("  Magic charge: " + item.enchantment.getMana() + 
								"/" + item.enchantment.getBaseMana()));						
					}
				}
			}
			add(properties);
		} else {
			removeAll();
			label.setIcon(null);
			label.setText(null);
		}
		
		revalidate();
		repaint();
	}
}
