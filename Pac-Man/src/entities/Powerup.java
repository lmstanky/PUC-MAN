package entities;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.concurrent.ThreadLocalRandom;

import main.Game;

public class Powerup extends GameObject
{
	
	BufferedImage icon;
	Game game;
	Rectangle bounds;
	
	public Powerup(Game game, double x, double y, double speed)
	{
		super(x, y, speed);
		this.game = game;
		icon = game.textures.powerup;
	}

	public void tick()
	{
		Rectangle pacBounds = new Rectangle((int)game.pacMan.x+8, (int)game.pacMan.y+8, 14, 14);
		bounds = getBounds(4, 24, 24);
		if(pacBounds.intersects(bounds) && game.powerUpActive)
		{
			// Do powerup adding stuff here
			if(game.customGame.bomb && game.customGame.laser)
			{
				int temp = ThreadLocalRandom.current().nextInt(0, 2);
				if(temp == 0)
				{
					game.pacMan.weapon = "bomb";
					game.pacMan.ammo = 1;
				}
				
				else
				{
					game.pacMan.weapon = "laser";
					game.pacMan.ammo = 3;
				}
				
			}
			
			else if(game.customGame.bomb)
			{
				game.pacMan.weapon = "bomb";
				game.pacMan.ammo = 1;
			}
				
			else
			{
				game.pacMan.weapon = "laser";
				game.pacMan.ammo = 3;
			}
			
			game.audio.soundEffects.get("powerUp").playAsSoundEffect(1.0f, 1.0f, false);
			game.powerUpActive = false;
		}
	}
	
	public void render(Graphics g)
	{
		g.drawImage(icon, (int)x, (int)y, null);
	}
}
