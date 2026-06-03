package collision;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import entities.GameObject;
import misc.Map;
import main.Game;

public class Physics
{	
	
	static Graphics2D g;
	
	public static boolean mapCollision(GameObject go, String name, String direction, Map map, Game game)
	{
		Rectangle bounds = null; // Pac-Man's main hitbox which is set below, depending on his direction
		Rectangle dotBounds = go.getBounds(8, 14, 14); // Pac-Man's smaller hitbox specifically for dots
		if(direction == "right")
		{
			bounds = go.getBounds(4, 24, 24);
			bounds.x+=1;
		}
			
		else if(direction == "down")
		{
			bounds = go.getBounds(4, 24, 24);
			bounds.y +=1;
		}
		else if(direction == "left")
		{
			bounds = go.getBounds(4, 24, 24);
			bounds.x -=1;
		}
		else if(direction == "up")
		{
			bounds = go.getBounds(4, 24, 24);
			bounds.y-=1;
		}
		
		else if(direction == "rUp" || direction == "uRight")
		{
			bounds = new Rectangle((int)go.x+23, (int)go.y+3,6,6);
		}
		
		else if(direction == "lUp" || direction == "uLeft")
		{
			bounds = new Rectangle((int)go.x+3, (int)go.y+3,6,6);
		}
		
		else if(direction == "dRight" || direction == "rDown")
		{
			bounds = new Rectangle((int)go.x+23, (int)go.y+23,6,6);
		}
		
		else if(direction == "dLeft" || direction == "lDown")
		{
			bounds = new Rectangle((int)go.x+3, (int)go.y+23,6,6);
		}
		
		
			
			
		for(int i = 0; i < 31; i++)
			for(int j = 0; j < 28; j++)
			{
				Rectangle temp = new Rectangle((int)map.map[i][j].x, (int)map.map[i][j].y, 24, 24);
				Rectangle dotBox = new Rectangle((int)map.map[i][j].x+4, (int)map.map[i][j].y+4, 10, 10);
				if(name == "pac-man" && dotBounds.intersects(dotBox) && map.map[i][j].isDot)
				{
					map.map[i][j].isDot = false;
					map.map[i][j].isBlank = true;
					if(map.map[i][j].icon == game.textures.mazeTiles.get(38) || map.map[i][j].isInfected)
					{
						map.map[i][j].icon = game.textures.mazeTiles.get(36);
						game.score+=50;
						game.infectedPowerPellets++;
						game.dotCount-=1;
						game.livesPoints-=50;
						game.checkExtraLife();
						
						if(map.map[i][j].isInfected)
							game.pacMan.slowed = true;
						
						game.blinky.resetFrightenedTicks();
						game.pinky.resetFrightenedTicks();
						game.inky.resetFrightenedTicks();
						game.clyde.resetFrightenedTicks();
						
						if(!map.map[i][j].isInfected)
						{
							game.blinky.frighten();
							game.pinky.frighten();
							game.inky.frighten();
							game.clyde.frighten();
							game.ghostPoints = 200;
						}
					}
					else
					{
						map.map[i][j].icon = game.textures.mazeTiles.get(36);
						game.dotCount-=1;
						game.score+=10;
						game.livesPoints-=10;
						game.checkExtraLife();
					}
					
					
					if(game.pacMan.dotSound == 0)
					{
						game.pacMan.dotSound = 1;
						game.audio.getSound("pacDot_down").playAsSoundEffect(1.00f, 0.30f, false);
					}
					
					else if(game.pacMan.dotSound == 1)
					{
						game.pacMan.dotSound = 0;
						game.audio.getSound("pacDot_up").playAsSoundEffect(1.00f, 0.30f, false);
					}
				}
				
				if(bounds.intersects(temp) && (!map.map[i][j].isDot && !map.map[i][j].isBlank))
					return true;
				
			}
		return false;
	}
	
	public static boolean checkDirection(String checkDir, GameObject go, Map map)
	{
		try
		{
			if(checkDir == "right" && (map.map[go.j][go.i+1].isBlank || map.map[go.j][go.i+1].isDot))
				return true;
			else if(checkDir == "down" && (map.map[go.j+1][go.i].isBlank || map.map[go.j+1][go.i].isDot))
				return true;
			else if(checkDir == "left" && (map.map[go.j][go.i-1].isBlank || map.map[go.j][go.i-1].isDot))
				return true;
			else if(checkDir == "up" && (map.map[go.j-1][go.i].isBlank || map.map[go.j-1][go.i].isDot))
				return true;
			else
				return false;
		}catch(ArrayIndexOutOfBoundsException e)
		{
			System.out.println("Can't check there: Index of of bounds");
			return false;
		}
		
		
	}
}
