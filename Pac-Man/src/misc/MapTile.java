package misc;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class MapTile
{
	public int index;
	public int x;
	public int y;
	public boolean isInfected; // Checks if the power pellet in this space is infected so Pac-Man can be slowed
	public boolean isDot; // Checks if this tile contains a standard dot
	public boolean isBlank;
	public boolean isWall; // Checks if this is a space that is blank, but also reachable by the player so they can reach the powerup
	public boolean dotRestore; // Checks if the dot needs to be restored upon resetting the map
	public boolean powerRestore; // Checks if the power pellet needs to be restored upon resetting the map
	public BufferedImage icon;
	public Rectangle bounds;
	
	public MapTile(int x, int y, Rectangle bounds, BufferedImage icon, boolean isInfected, boolean isDot, boolean isBlank, int index, boolean dotRestore, boolean powerRestore)
	{
		this.x = x;
		this.y = y;
		this.bounds = bounds;
		this.icon = icon;
		this.isDot = isDot;
		this.isBlank = isBlank;
		this.index = index;
		this.dotRestore = dotRestore;
		this.powerRestore = powerRestore;
	}
}
