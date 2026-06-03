package main;

import java.awt.image.BufferedImage;

public class SpriteSheet
{
	private BufferedImage image;
	
	public SpriteSheet(BufferedImage ss)
	{
		this.image = ss;
	}
	
	public BufferedImage extractSprite(int col, int row, int width, int height)
	{
		BufferedImage sprite = image.getSubimage((col * width) - width, (row * height) - height, width, height);
		return sprite;
	}
}
