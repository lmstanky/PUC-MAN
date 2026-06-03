package actors;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import mainPackage.map;
import pathfinding.AStar;
import pathfinding.Node;

public class Ghost extends Entity
{
	private String name;
	public String direction;
	public boolean redot;
	public String redotVal;
	public boolean edible = false;
	public boolean random = true;
	public ArrayList<Integer> initPathX = new ArrayList<Integer>();
	public ArrayList<Integer> initPathY = new ArrayList<Integer>();
	public ArrayList<Node> ghostPath;
	
	public Ghost(String ghostName, int inX, int inY, String inDir, String inIcon)
	{
		x = inX;
		y = inY;
		direction = inDir;
		icon = inIcon;
		name = ghostName;
	}
	
	public void getPath(int pacX, int pacY, int pacBX, int pacBY)
	{
		AStar pathfind = new AStar(x, y, pacX, pacY, pacBX, pacBY);
		ghostPath = pathfind.algorithm();		
	}
	
	
	// The initial paths the Ghosts take before chasing Pac-Man are hard coded. However, only Blinky's has been done
	public void initialPath() throws IOException
	{
		int i;
		int j;
		String delim = "\\s+";
		if(this.name == "blinky")
		{
			InputStream in = map.class.getResourceAsStream("/initblinky.txt");
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			for(i = 0; i < 50; i++)
			{
				String line = br.readLine();
				String[] temp = line.split(delim);
				for(j = 0; j < 2; j++)
					if(j == 0)
						initPathX.add(Integer.parseInt(temp[j]));
					else if(j == 1)
						initPathY.add(Integer.parseInt(temp[j]));
			}	
		}
	}
}
