package entities;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import main.Game;

public class DK
{
	Game game;
	
	public BufferedImage icon;
	public int iconState = 0;
	public int iconStateCount = 30;
	
	public Barrel barrel = null;
	public boolean barrelThrown = false;
	public int barrelThrowTickCount = 60;
	public boolean throwing = false;
	
	public DK(Game game)
	{
		this.game = game;
		icon = game.textures.dkNormal1;
	}

	public void tick()
	{
		if(barrelThrown && barrel != null)
			barrel.tick();
		
		if((game.pacMan.speed == 0 && !game.resetLevel && !game.pacDeath && !game.nextLevel) || barrelThrown)
			barrelThrowTickCount--;
		else if(game.pacMan.speed > 0 && !barrelThrown)
			barrelThrowTickCount = 60;
		
		if(barrelThrowTickCount <= 0 && !barrelThrown)
		{
			iconState = 2;
			icon = game.textures.dkBarrel1;
			if(barrelThrowTickCount == -30)
			{
				icon = game.textures.dkBarrel2;
				throwBarrel();
			}
		}
		
		if(barrelThrowTickCount == -120)
		{
			barrelThrowTickCount = 60;
			icon = game.textures.dkNormal1;
			iconState = 0;
			iconStateCount = 15;
		}
		
		
		
		
		if(iconStateCount == 0)
		{
			if(iconState == 0)
			{
				iconStateCount = 30;
				iconState = 1;
				icon = game.textures.dkNormal1;
			}
			
			else if(iconState == 1)
			{
				iconStateCount = 30;
				iconState = 0;
				icon = game.textures.dkNormal2;
			}
		}
		iconStateCount--;
	}

	private void throwBarrel()
	{
		barrel = new Barrel(game);
		barrelThrown = true;
	}

	public void render(Graphics g)
	{
		g.drawImage(icon, 100, 10, null);
		if(barrel != null)
			barrel.render(g);
	}
}
