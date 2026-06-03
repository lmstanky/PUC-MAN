package actors;

import mainPackage.map;

public class Entity
{
	public int x;
	public int y;
	public String icon;
	
	public void move(String dir)
	{
		if(dir == "left")
			y--;
		else if(dir == "right")
			y++;
		else if(dir == "up")
			x--;
		else if(dir == "down")
			x++;
		
		// Is this entity using the tunnel? If so, move them over to the other side of the maze
		if(y < 0)
			y = 28;
		else if(y > 28)
			y = 0;
	}
	
	public static boolean checkDirection(int x, int y, String direction)
	{
		if(y-1 < 0 || y+1 > 28)
			return true;
		if(direction == "up" && map.layout[x-1][y].getText().equals("\u25A0"))
			return false;
		else if(direction == "down" && map.layout[x+1][y].getText().equals("\u25A0"))
			return false;
		else if(direction == "left" && map.layout[x][y-1].getText().equals("\u25A0"))
			return false;
		else if(direction == "right" && map.layout[x][y+1].getText().equals("\u25A0"))
			return false;
		else
			return true;
	}
}
