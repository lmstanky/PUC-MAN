package actors;

import java.util.ArrayList;

import mainPackage.map;

public class PacMan extends Entity
{
	public static String direction;
	public static int lives = 3;
	public PacMan(int inX, int inY, String inDir, String inIcon)
	{
		x = inX;
		y = inY;
		direction = inDir;
		icon = inIcon;
	}
	
	public boolean checkChange(String direction)
	{
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
	
	public void die()
	{
		// There's no animation or anything right now, so this whole process is just to simulate what happens in the real game
		for(int i = 0; i < 31; i++)
			for(int j = 0; j < 29; j++)
				if(map.layout[i][j].getText().equals("B") ||
				   map.layout[i][j].getText().equals("P") ||
				   map.layout[i][j].getText().equals("I") ||
				   map.layout[i][j].getText().equals("C"))
					map.layout[i][j].setText(" ");
		
		for(int k = 0; k < 31; k++)
			for(int l = 0; l < 29; l++)
				if(map.layout[k][l].getText().equals("."))
					map.layout[k][l].setVisible(false);
	}
	
	public ArrayList<Integer> getSpaceInFront()
	{
		ArrayList<Integer> space = new ArrayList<Integer>();
		if(direction == "up")
		{
			space.add(x-1);
			space.add(y);
			return space;
		}
		
		else if(direction == "right")
		{
			space.add(x);
			space.add(y+1);
			return space;
		}
		
		else if(direction == "down")
		{
			space.add(x+1);
			space.add(y);
			return space;
		}
		
		else
		{
			space.add(x);
			space.add(y-1);
			return space;
		}	
	}
	
	public ArrayList<Integer> getSpaceBehind()
	{
		ArrayList<Integer> space = new ArrayList<Integer>();
		if(direction == "up")
		{
			space.add(x+1);
			space.add(y);
			return space;
		}
		
		else if(direction == "right")
		{
			space.add(x);
			space.add(y-1);
			return space;
		}
		
		else if(direction == "down")
		{
			space.add(x-1);
			space.add(y);
			return space;
		}
		
		else
		{
			space.add(x);
			space.add(y+1);
			return space;
		}	
	}
}
