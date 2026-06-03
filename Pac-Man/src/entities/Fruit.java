package entities;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import main.Game;

public class Fruit extends GameObject
{
	private Game game;
	public String name;
	private BufferedImage icon;
	private Rectangle bounds;
	private Rectangle pacBounds;
	
	
	
	public Fruit(Game game, String name, double x, double y, double speed, BufferedImage icon)
	{
		super(x, y, speed);
		this.icon = icon;
		this.name = name;
		this.game = game;
		bounds = getBounds(4, 24, 24);
	}

	public void render(Graphics g)
	{
		g.drawImage(icon, (int)x, (int)y, null);
	}
	
	public void tick()
	{
		pacBounds = new Rectangle((int)game.pacMan.x+8, (int)game.pacMan.y+8, 14, 14);
		if(bounds.intersects(pacBounds) && game.fruitActive)
		{
			game.audio.soundEffects.get("fruit").playAsSoundEffect(1.0f, 1.0f, false);
			if(name == "cherry")
			{
				game.score+=100;
				game.livesPoints-=100;
				icon = game.textures.oneHundred;
			}
			else if(name == "strawberry")
			{
				game.score+=300;
				game.livesPoints-=300;
				icon = game.textures.threeHundred;
			}
			else if(name == "peach")
			{
				game.score+=500;
				game.livesPoints-=500;
				icon = game.textures.fiveHundred;
			}
			else if(name == "apple")
			{
				game.score+=700;
				game.livesPoints-=700;
				icon = game.textures.sevenHundred;
			}
			else if(name == "grape")
			{
				game.score+=1000;
				game.livesPoints-=1000;
				icon = game.textures.oneThousand;
			}
			else if(name == "galaxian")
			{
				game.score+=2000;
				game.livesPoints-=2000;
				icon = game.textures.twoThousand;
			}
			else if(name == "bell")
			{
				game.score+=3000;
				game.livesPoints-=3000;
				icon = game.textures.threeThousand;
			}
			else if(name == "key")
			{
				game.score+=5000;
				game.livesPoints-=5000;
				icon = game.textures.fiveThousand;
			}
			game.checkExtraLife();
			game.fruitActiveCount = 90;
			bounds.x = 0;
			bounds.y = 0;
		}
	}
	
	
	
}
