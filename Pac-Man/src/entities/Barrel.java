package entities;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import main.Game;

public class Barrel
{
	Game game;
	int x = 200;
	int y = 50;
	double tx, ty;
	
	int iconStateCount = 6;
	
	BufferedImage icon;
	public int iconState = 0;
	public String direction = "horizontal";
	
	public Barrel(Game game)
	{
		this.game = game;
		tx = game.pacMan.x;
		ty = 1110;
		icon = game.textures.barrelHoriz0;
	}
	
	public void tick()
	{
		checkPacManCollision();
		if(x < tx)
			x+=4;
		else if(y < ty)
		{
			direction = "vertical";
			y+=4;
		}
		else if(x == tx && y == ty)
		{
			game.dk.barrelThrown = false;
		}
		
		
		if(iconStateCount == 0)
		{
			iconStateCount = 10;
			if(iconState == 0)
			{
				if(direction == "horizontal")
					icon = game.textures.barrelHoriz0;
				else if(direction == "vertical")
					icon = game.textures.barrelVert0;
				iconState = 1;
			}
			
			else if(iconState == 1)
			{
				if(direction == "horizontal")
					icon = game.textures.barrelHoriz1;
				else if(direction == "vertical")
					icon = game.textures.barrelVert1;
				iconState = 0;
			}
		}		
		iconStateCount--;
	}
	
	public void render(Graphics g)
	{
		g.drawImage(icon, x, y, null);
	}
	
	public void checkPacManCollision()
	{
		Rectangle barrelBounds = new Rectangle(x+4, y+4, 24, 24);
		Rectangle pacBounds = new Rectangle((int)game.pacMan.x+16, (int)game.pacMan.y+16, 8, 8);
		
		if(barrelBounds.intersects(pacBounds))
		{
			game.stopBackgroundNoise();
			game.stopReturningNoise();
			game.stopFrightenedNoise();
			game.pacDeath = true;
			game.barrelDeath = true;
		}
	}
	
}
