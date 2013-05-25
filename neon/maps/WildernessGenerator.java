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

package neon.maps;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.Collection;
import org.jdom2.Element;
import org.texgen.signals.AnimalStripe;
import neon.core.Engine;
import neon.objects.EntityFactory;
import neon.objects.entities.Container;
import neon.objects.entities.Creature;
import neon.objects.entities.Item;
import neon.objects.property.Habitat;
import neon.objects.resources.RItem;
import neon.objects.resources.RRegionTheme;
import neon.objects.resources.RTerrain;
import neon.util.Dice;

/**
 * Generates a piece of wilderness. The following types are supported:
 * <ul>
 * 	<li>plain
 * 	<li>ridges
 * 	<li>terraces
 * 	<li>chaotic
 * </ul>
 * 
 * @author mdriesen
 */
public class WildernessGenerator {
	private Zone zone;
	private String[][] terrain;	// algemene terrain info
	
	/**
	 * Constuctor used by the engine.
	 * 
	 * @param zone
	 */
	public WildernessGenerator(Zone zone) {
		this.zone = zone;
	}
	
	/**
	 * Constructor used by the editor.
	 * 
	 * @param terrain
	 */
	public WildernessGenerator(String[][] terrain) {
		this.terrain = terrain;
	}
	
	/**
	 * Generates a piece of wilderness using the supplied parameters.
	 */
	public void generate(Region region, RRegionTheme theme) {
		// kijken of er bovenop deze region al andere regions liggen
		Collection<Region> regions = zone.getRegions(region.getBounds());
		if(!isOnTop(region, regions)) {	// als er nog regions boven deze region liggen
			decompose(region, regions, theme);
		} else if(region.width > 100 || region.height > 100) {	// kijken of region niet te groot is
			divide(region, theme);
		} else {	// indien klein genoeg, region genereren
			terrain = new String[region.height + 2][region.width + 2];	// [rijen][kolommen]
			if(region.y > 0) {	// bovenkant van map
				for(int i = 0; i < region.width; i++) {
					terrain[0][i+1] = zone.getRegion(new Point(region.x + i, region.y - 1)).getTextureType();
				}
			}
			if(region.y + region.height < zone.getHeight() - 1) {	// onderkant
				for(int i = 0; i < region.width; i++) {
					terrain[region.height + 1][i+1] = 
							zone.getRegion(new Point(region.x + i, region.y + region.height)).getTextureType();
				}
			}
			if(region.x > 0) {	// links
				for(int i = 0; i < region.height; i++) {
					terrain[i+1][0] = zone.getRegion(new Point(region.x - 1, region.y + i)).getTextureType();
				}
			}
			if(region.x + region.width < zone.getWidth() - 1) {	// rechts
				for(int i = 0; i < region.height; i++) {
					terrain[i+1][region.width + 1] = 
							zone.getRegion(new Point(region.x + region.width, region.y + i)).getTextureType();
				}
			}
					
			// terrain genereren
			generateTerrain(region.width, region.height, theme, region.getTextureType());
			
			// planten toevoegen indien nodig
			addVegetation(region.width, region.height, theme, region.id);

			// creatures toevoegen
			addCreatures(region.x, region.y, region.width, region.height, theme, region.id);

			// alle info in terrain omzetten in regions
			generateEngineContent(region);
		}
	}

	public String[][] generate(Rectangle r, RRegionTheme theme, String base) {
		// terrain genereren
		generateTerrain(r.width, r.height, theme, base);
		
		// fauna genereren
		addVegetation(r.width, r.height, theme, base);
		return terrain;
	}
	
	private boolean isOnTop(Region region, Collection<Region> regions) {
		for(Region r : regions) {
			if(r.z > region.z) {
				return false;
			}
		}
		return true;
	}
	
	private void decompose(Region region, Collection<Region> regions, RRegionTheme theme) {
		// region in stukjes knippen die niet overlappen met bovenliggende regions
		zone.removeRegion(region);
		Area area = new Area(region.getBounds());
		for(Region r : regions) {
			if(r.z > region.z) {
				area.subtract(new Area(r.getBounds()));
			}
		}
		
		int i = 0;
		while(i < 5 && !area.isEmpty()) {
			i++;	// hopen dat de area niet al te ingewikkeld is
			Collection<Rectangle> pieces = Decomposer.split(area);
			for(Rectangle r : pieces) {
				if(area.contains(r)) {
					RTerrain rt = (RTerrain)Engine.getResources().getResource(region.getTextureType(), "terrain");
					zone.addRegion(new Region(region.getTextureType(), r.x, r.y, r.width, r.height, theme, region.z, rt));
					area.subtract(new Area(r));
				}
			}
		}		
	}
	
	private void divide(Region region, RRegionTheme theme) {
		String texture = region.getTextureType();
		
		// in kleinere non-fixed stukken van zelfde grootte splitsen
		int newWidth = (region.width > region.height) ? region.width/2 : region.width;
		int newHeight = (region.width > region.height) ? region.height : region.height/2;
		zone.removeRegion(region);
		
		RTerrain rt = (RTerrain)Engine.getResources().getResource(texture, "terrain");
		zone.addRegion(new Region(texture, region.x, region.y, newWidth, newHeight, theme, region.z, rt));
		
		int dw = region.width%2;
		int dh = region.height%2;
		
		if(region.width > region.height) {
			zone.addRegion(new Region(texture, region.x + newWidth, region.y, newWidth + dw, newHeight, theme, region.z, rt));
		} else {
			zone.addRegion(new Region(texture, region.x, region.y + newHeight, newWidth, newHeight + dh, theme, region.z, rt));				
		}
	}
	
	private void generateTerrain(int width, int height, RRegionTheme theme, String base) {
		// terrain en beplanting maken
		switch(theme.type) {
		case CHAOTIC: 
			generateSwamp(width, height, theme); 
			break;
		case PLAIN: 
			generateForest(width, height, theme); 
			break;
		case RIDGES: 
			generateRidges(width, height, theme); 
			break;
		case TERRACE: 
			generateTerraces(width, height, theme); 
			break;
		default:
			break;
		}
		
		// laten overvloeien in naburig region
		makeBorder(base);
		
		// features toevoegen
		addFeatures(width, height, theme);		
	}
	
	private void addFeatures(int width, int height, RRegionTheme theme) {
		double ratio = (width*height)/10000d;
		for(Element feature : theme.features) {
			int n = (int)Float.parseFloat(feature.getAttributeValue("n"))*100;
			if(n > 100) {
				n = MapUtils.random(0, (int)(n*ratio/100));
			} else {
				n = (MapUtils.random(0, (int)(n*ratio)) > 50) ? 1 : 0;
			}
			if(feature.getText().equals("lake")) {	// grote patch die gewoon alles overschrijft
				int size = 100/Integer.parseInt(feature.getAttributeValue("s"));
				ArrayList<Rectangle> lakes = BlocksGenerator.generateSparseRectangles(width, height, 
						width/size, height/size, 2, n);
				for(Rectangle r : lakes) {
					// meer inkwakken
					Polygon lake = MapUtils.randomPolygon(r, (r.width + r.height)/2);
					for(int x = 0; x < width; x++) {
						for(int y = 0; y < height; y++) {
							if(lake.contains(x,y)) {
								terrain[y+1][x+1] = feature.getAttributeValue("t");
							}
						}
					}
				}
			} 
		}
	}
	
	private void addCreatures(int rx, int ry, int width, int height, RRegionTheme theme, String base) {
		double ratio = (double)width*height/10000;
		for(String id : theme.creatures.keySet()) {
			for(int i = (int)(Dice.roll(1, theme.creatures.get(id), 0)*ratio); i > 0; i--) {
				int x = Dice.roll(1, width, -1);
				int y = Dice.roll(1, height, -1);
				
				String t = terrain[y+1][x+1] == null ? base : terrain[y+1][x+1].split(";")[0];
				String region = t.isEmpty() ? base : t;
				
				Creature creature = EntityFactory.getCreature(id, rx + x, ry +y, 
						Engine.getStore().createNewEntityUID());
				RTerrain terrain = (RTerrain)Engine.getResources().getResource(region, "terrain");
				if(terrain.modifier == Region.Modifier.SWIM && 
						creature.species.habitat == Habitat.LAND) {
					Engine.getStore().addEntity(creature);
					zone.addCreature(creature);
				}
			}
		}		
	}
	
	private void generateEngineContent(Region region) {
		// kleinere stukken terrein
		for(int i = 0; i < region.getWidth(); i++) {
			for(int j = 0; j < region.getHeight(); j++) {
				if(terrain[j+1][i+1] != null) {
//					System.out.println(terrain[j+1][i+1]);
					String[] data = terrain[j+1][i+1].split(";");
					for(String entry : data) {
						if(entry.startsWith("i:")) {
							String id = entry.replace("i:", "");
							long uid = Engine.getStore().createNewEntityUID();
							Item item = EntityFactory.getItem(id, region.x + i, region.y + j, uid);
							Engine.getStore().addEntity(item);
							if(item instanceof Container) {
								for(String s : ((RItem.Container)item.resource).contents) {
									Item content = EntityFactory.getItem(s, Engine.getStore().createNewEntityUID());
									((Container)item).addItem(content.getUID());
									Engine.getStore().addEntity(content);
								}
							}
							zone.addItem(item);
						} else if(entry.startsWith("c:")) {
							String id = entry.replace("c:", "");
							long uid = Engine.getStore().createNewEntityUID();
							Creature creature = EntityFactory.getCreature(id, region.x + i, region.y + j, uid);
							Engine.getStore().addEntity(creature);
							zone.addCreature(creature);							
						} else if(!entry.isEmpty() && entry != region.getTextureType()) {
							RTerrain terrain = (RTerrain)Engine.getResources().getResource(entry, "terrain");
							zone.addRegion(new Region(entry, region.x + i, region.y + j, 1, 1, null, region.z + 1, terrain));
						}
					}
				}
			}
		}
	}
	
	protected void generateTerraces(int width, int height, RRegionTheme theme) {
		int[][] tiles = CaveGenerator.generateOpenCave(width, height, 3);

		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				if(tiles[x][y] > 0) {
					terrain[y+1][x+1] = theme.floor;
				}
			}
		}		
	}
	
	protected void generateForest(int width, int height, RRegionTheme theme) {

	}

	protected void generateRidges(int width, int height, RRegionTheme theme) {
        AnimalStripe stripe = new AnimalStripe(0);
			
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				double u = x;
				double v = y;
				if(stripe.getValue(u/100, v/30) > 0.5) {
					terrain[y+1][x+1] = theme.floor;
				}
			}
		}		
	}
	
	private void addVegetation(int width, int height, RRegionTheme theme, String base) {
		if(!theme.vegetation.isEmpty()) {
			String[][] fauna = new String[width][height];
			for(String id : theme.vegetation.keySet()) {
				int abundance = theme.vegetation.get(id);
				Item dummy = EntityFactory.getItem(id, 0);
				int size = dummy.bounds.width;	// size van boom in rekening brengen
				int ratio = (width/size)*(height/size);
				boolean[][] fill = generateIslands(width/size, height/size, abundance, 5, ratio/size);
				for(int i = 0; i < fill.length; i++) {
					for(int j = 0; j < fill[0].length; j++) {
						if(fill[i][j]) {
							fauna[i*size + MapUtils.random(0, size - 1)][j*size + MapUtils.random(0, size - 1)] = id;
						}
					}
				}
			}

			for(int i = 0; i < fauna.length; i++) {
				for(int j = 0; j < fauna[i].length; j++) {
					String region = terrain[j+1][i+1] == null ? base : terrain[j+1][i+1];
					RTerrain rt = (RTerrain)Engine.getResources().getResource(region, "terrain");
					if(fauna[i][j] != null && rt.modifier != Region.Modifier.SWIM) {
						String t = (terrain[j+1][i+1] != null ? terrain[j+1][i+1] : "");
						terrain[j+1][i+1] = t + ";i:" + fauna[i][j];
					}
				}
			}
		}
	}

	private void makeBorder(String type) {
		int width = terrain[0].length - 2;
		int height = terrain.length - 2;
		
		if(terrain[0][1] != null) {	// bovenkant
			// overlappen
			int h = 0;
			for(int i = 0; i < width; i++) {
				if(terrain[0][i+1] != type) {
					if(h > 0) {
						addTerrain(i+1, 1, 1, h, terrain[0][i+1]);
					}
					
					double c = Math.random();
					if(c > 0.7 && h < height/10) {
						h++;
					} else if(c < 0.3 && h > 0) {
						h--;
					}
				}
			}			
		}

		if(terrain[height + 1][1] != null) {	// onderkant
			// overlappen
			int h = 0;
			for(int i = 0; i < width; i++) {
				if(terrain[height + 1][i+1] != type) {
					if(h > 0) {
						addTerrain(i+1, height - h + 1, 1, h, terrain[height + 1][i+1]);
					}

					double c = Math.random();
					if(c > 0.7 && h < height/10) {
						h++;
					} else if(c < 0.3 && h > 0) {
						h--;
					}
				}
			}			
		}
		
		if(terrain[1][0] != null) {	// links
			// overlappen
			int w = 0;
			for(int i = 0; i < height; i++) {
				if(terrain[i+1][0] != type) {
					if(w > 0) {
						addTerrain(1, i+1, w, 1, terrain[i+1][0]);
					}
					
					double c = Math.random();
					if(c > 0.7 && w < width/10) {
						w++;
					} else if(c < 0.3 && w > 0) {
						w--;
					}
				}
			}			
		}

		if(terrain[1][width + 1] != null) {	// rechts
			// overlappen
			int w = 0;
			for(int i = 0; i < height; i++) {
				if(terrain[i][width + 1] != type) {
					if(w > 0) {
						addTerrain(width - w + 1, i+1, w, 1, terrain[i+1][width + 1]);
					}

					double c = Math.random();
					if(c > 0.7 && w < width/10) {
						w++;
					} else if(c < 0.3 && w > 0) {
						w--;
					}
				}
			}			
		}
	}
	
	private void addTerrain(int x, int y, int width, int height, String type) {
		for(int i = y; i < y + height; i++) {
			for(int j = x; j < x + width; j++) {
				terrain[i][j] = type;
			}
		}
	}
	
	// uit http://www.evilscience.co.uk/?p=53
	private boolean[][] generateIslands(int width, int height, int p, int n, int i) {
		boolean[][] map = new boolean[width][height];
		
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				// p: initiële kans dat een vakje iets bevat
				map[x][y] = (MapUtils.random(0, 100) < p);
			}
		}
		
		// i keer itereren
		for(; i > 0; i--) {
			int x = MapUtils.random(0, width - 1);
			int y = MapUtils.random(0, height - 1);
			// ongeveer conways game of life met n buren
			map[x][y] = (filledNeighbours(x, y, map) > n);
		}

		return map;
	}
	
	private int filledNeighbours(int x, int y, boolean[][] map) {
		int c = 0;
		for(int i = Math.max(0, x - 1); i < Math.min(x + 2, map.length); i++) {
			for(int j = Math.max(0, y - 1); j < Math.min(y + 2, map[0].length); j++) {
				if(map[i][j]) {
					c++;
				}
			}
		}
		return c;
	}
	
	protected void generateSwamp(int width, int height, RRegionTheme theme) {
		boolean[][] tiles = generateIslands(width, height, 20, 4, 5000);

		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				if(tiles[x][y]) {
					terrain[y+1][x+1] = theme.floor;
				}
			}
		}
	}
}
