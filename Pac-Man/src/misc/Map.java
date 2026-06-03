package misc;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import collision.EntityC;
import main.Game;
import main.Textures;

public class Map implements EntityC
{
	Textures textures;
	String mapFile;
	BufferedImage maze;
	String[][] mapLayout;
	public MapTile[][] map;
	public ArrayList<BufferedImage> tiles = null;
	int w;
	int h;
	
	public Map(String mapFile, Textures textures) throws IOException
	{
		this.textures = textures;
		tiles = textures.mazeTiles;
		InputStream in = Map.class.getResourceAsStream(mapFile);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String delim = "\\s+";
		int numRows = 31;
		int numCols = 28;
		map = new MapTile[numRows][numCols];
		mapLayout = new String[numRows][numCols];		
		
		for(int i = 0; i < numRows; i++)
		{
			String line = br.readLine();
			String[] temp = line.split(delim);
			for(int j = 0; j < numCols; j++)
				mapLayout[i][j] = temp[j];
		}
		w = numCols*24;
		h = numRows*24;
		constructMaze();
	}
	
	public void constructMaze()
	{
		for(int i = 0; i < 31; i++)
			for(int j = 0; j < 28; j++)
			{
				int index = Integer.parseInt(mapLayout[i][j]);
				BufferedImage temp = new BufferedImage(24, 24, BufferedImage.TYPE_INT_RGB);
				if(index == 50)
					temp = tiles.get(36);
				else temp = tiles.get(index);
				map[i][j] = new MapTile(0, 0, null, temp, false, false, false, index, false, false);
				if(index == 29)
				{
					map[i][j].isDot = true;
					map[i][j].dotRestore = true;
				}
				else if(index == 38)
				{
					map[i][j].isDot = true;
					map[i][j].powerRestore = true;
				}
				else if(index == 36)
					map[i][j].isBlank = true;
				
				else if(index == 50)
				{
					map[i][j].isWall = true;
					map[i][j].icon = tiles.get(36);
				}
				
			}
		
		int x = 456;
		int y = 96;
		
		for(int i = 0; i < 31; i++)
		{
			for(int j = 0; j < 28; j++)
			{
				map[i][j].x = x;
				map[i][j].y = y;
				map[i][j].bounds = new Rectangle(x, y, 24, 24);
				x+=24;
			}
			x=456;
			//x=144;
			y+=24;
		}
	}

	public void render(Graphics g)
	{
		
		
		for(int i = 0; i < h/24; i++)
		{
			for(int j = 0; j < w/24; j++)
			{
				g.drawImage(map[i][j].icon, (int)map[i][j].x, (int)map[i][j].y, null);
				g.setColor(Color.WHITE);
				if(Game.DEBUG)
					g.drawRect((int)map[i][j].x, (int)map[i][j].y, 24, 24);
			}
			
		}			
	}
}