package entities;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import main.Game;

public class UFO
{
	BufferedImage icon;
	Game game;
	public int x = 408;
	public int shotCount = 120;
	public boolean isShot = false;
	
	public UFO(Game game)
	{
		this.game = game;
		icon = game.textures.ufo;
	}
	
	public void tick()
	{
		if(!game.audio.soundEffects.get("mysteryShip").isPlaying())
			game.audio.soundEffects.get("mysteryShip").playAsSoundEffect(1.0f, 1.0f, false);
		if(!isShot)
			x+=2;
		else if(isShot)
		{
			icon = game.textures.oneThousand;
			game.audio.soundEffects.get("mysteryShip").stop();
			shotCount--;
		}
	}
	
	public void render(Graphics g)
	{
		g.drawImage(icon, x, 10, null);
	}
}
