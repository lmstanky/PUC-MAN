package states;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.newdawn.slick.util.ResourceLoader;

import main.Textures;

public class CustomGame
{
	
	Font font30 = null;
	Font font20 = null;
	Font font50 = null;
	Font font40 = null;
	
	// Main selection options
	public String[] customOptions = {"startLives", "extraLives", "bomb", "laser", "stinky", "DK", "centipedePlayer", "UFO", "go"};
	public int menuSelected = 0;
	
	// Selection sub-options
	public int[] startLivesOptions = {1, 3, 5};
	public int startLivesSelected = 1;
	
	public int[] extraLivesOptions = {0, 10000, 15000, 20000};
	public int extraLivesSelected = 1;
	
	public boolean bomb = false;
	public boolean laser = false;
	public boolean stinkyEnabled = false;
	public boolean DK = false;
	public boolean centipede = false;
	public boolean ufo = false;
	
	
	public String selected = "startLives";
	
	private BufferedImage cursor, frame, splash, leftArrow, rightArrow;
	
	public CustomGame(Textures textures)
	{
		this.leftArrow = textures.leftArrow;
		this.rightArrow = textures.rightArrow;
		this.cursor = textures.smallCursor;
	}
	
	public void render(Graphics g) throws FontFormatException, IOException
	{		
		if(frame == null)
			frame = ImageIO.read(getClass().getResource("/gui/modeselect.png"));
		if(font30 == null)
			font30 = Font.createFont(Font.TRUETYPE_FONT, ResourceLoader.getResourceAsStream(("PressStart2P.ttf"))).deriveFont(Font.PLAIN, 30);
		if(font20 == null)
			font20 = Font.createFont(Font.TRUETYPE_FONT, ResourceLoader.getResourceAsStream(("PressStart2P.ttf"))).deriveFont(Font.PLAIN, 20);
		if(font50 == null)
			font50 = Font.createFont(Font.TRUETYPE_FONT, ResourceLoader.getResourceAsStream(("PressStart2P.ttf"))).deriveFont(Font.PLAIN, 50);
		if(font40 == null)
			font40 = Font.createFont(Font.TRUETYPE_FONT, ResourceLoader.getResourceAsStream(("PressStart2P.ttf"))).deriveFont(Font.PLAIN, 40);
		g.setFont(font30);
		g.setColor(Color.yellow);
		
		g.drawImage(frame, 0, 0, null);
		
		if(menuSelected == 0)
		{
			g.drawImage(cursor, 10, 42, null);
			g.setFont(font50);
			g.drawString("Number of lives", 900, 600);
			g.drawString("to start the", 900, 700);
			g.drawString("game with", 900, 800);
			g.setFont(font30);
		}
		else if(menuSelected == 1)
		{
			g.drawImage(cursor, 10, 92, null);
			g.setFont(font50);
			g.drawString("Score points to", 925, 600);
			g.drawString("earn extra", 925, 700);
			g.drawString("lives", 925, 800);
			g.setFont(font30);
		}
		else if(menuSelected == 2)
		{
			g.drawImage(cursor, 10, 192, null);
			splash = ImageIO.read(getClass().getResource("/gui/bigBomb.png"));
			g.drawImage(splash, 1150, 125, null);
			g.setFont(font50);
			g.drawString("Explodes to", 925, 600);
			g.drawString("kill the", 925, 700);
			g.drawString("Ghosts", 925, 800);
			g.setFont(font30);
		}
		
		else if(menuSelected == 3)
		{
			g.drawImage(cursor, 10, 242, null);
			splash = ImageIO.read(getClass().getResource("/gui/bigLaser.png"));
			g.drawImage(splash, 1150, 125, null);
			g.setFont(font50);
			g.drawString("Shoot the", 925, 600);
			g.drawString("Ghosts. Get", 925, 700);
			g.drawString("the points!", 925, 800);
			g.setFont(font30);
		}
		else if(menuSelected == 4)
		{
			g.drawImage(cursor, 10, 342, null);
			splash = ImageIO.read(getClass().getResource("/gui/stinky.png"));
			g.drawImage(splash, 1200, 150, null);
			g.setFont(font20);
			g.drawString("PUC-MAN, Michael Tonks (2018)", 1000, 400);
			g.setFont(font50);
			g.drawString("Infects your", 950, 600);
			g.drawString("Power Pellets", 950, 700);
			g.drawString("to render", 1050, 800);
			g.drawString("them useless!", 950, 900);
			g.setFont(font30);
		}
		else if(menuSelected == 5)
		{
			g.drawImage(cursor, 10, 392, null);
			splash = ImageIO.read(getClass().getResource("/gui/dk.png"));
			g.drawImage(splash, 1150, 150, null);
			g.setFont(font20);
			g.drawString("DONKEY KONG, Nintendo (1981)", 1000, 400);
			g.setFont(font50);
			g.drawString("Keep moving, ", 990, 600);
			g.drawString("or DK will", 1025, 700);
			g.drawString("start throwing", 925, 800);
			g.drawString("barrels!", 1100, 900);
			g.setFont(font30);
		}
		
		else if(menuSelected == 6)
		{
			g.drawImage(cursor, 10, 442, null);
			splash = ImageIO.read(getClass().getResource("/gui/centipede.png"));
			g.drawImage(splash, 1200, 120, null);
			g.setFont(font20);
			g.drawString("CENTIPEDE (Rev. 3), Atari (1980)", 950, 400);
			g.setFont(font40);
			g.drawString("CENTIPEDE returns..", 900, 625);
			g.drawString("Avoid the shooting", 900, 725);
			g.drawString("lasers!", 1100, 825);
			g.setFont(font30);
		}
		
		else if(menuSelected == 7)
		{
			g.drawImage(cursor, 10, 492, null);
			splash = ImageIO.read(getClass().getResource("/gui/ufo.png"));
			g.drawImage(splash, 1150, 150, null);
			g.setFont(font20);
			g.drawString("SPACE INVADERS, Taito (1978)", 1000, 400);
			g.setFont(font40);
			g.drawString("Got a laser to hand?", 875, 625);
			g.drawString("Shoot this for extra", 875, 725);
			g.drawString("points!", 1100, 825);
			g.setFont(font30);
		}
		
		else if(menuSelected == 8)
			g.drawImage(cursor, 325, 710, null);
		
		populate(g);
		
	}
	
	public void defaults()
	{
		menuSelected = 0;
		startLivesSelected = 1;
		extraLivesSelected = 1;
		bomb = false;
		laser = false;
		stinkyEnabled = false;
		DK = false;
		centipede = false;
		ufo = false;
	}
	
	public void populate(Graphics g)
	{
		// Starting Lives Options
				g.drawString("Starting Lives", 50, 75);
				if(startLivesSelected != 0)
					g.drawImage(leftArrow, 550, 40, null);
				g.drawString(Integer.toString(startLivesOptions[startLivesSelected]), 590, 75);
				if(startLivesSelected != 2)
					g.drawImage(rightArrow, 630, 40, null);
				
				// Extra Lives Options
				g.drawString("Extra Lives", 50, 125);
				if(extraLivesSelected != 0)
					g.drawImage(leftArrow, 550, 90, null);
				g.drawString(Integer.toString(extraLivesOptions[extraLivesSelected]), 590, 125);
				if(extraLivesSelected != 3)
					g.drawImage(rightArrow, 750, 90, null);
				
				// Bomb Options
				g.drawString("Bomb", 50, 225);
				if(bomb)
				{
					g.drawImage(leftArrow, 550, 190, null);
					g.drawString("ON", 595, 225);
				}
				else if(!bomb)
				{
					g.drawString("OFF", 595, 225);
					g.drawImage(rightArrow, 695, 190, null);
				}
				
				// Laser Options
				g.drawString("Laser", 50, 275);
				if(laser)
				{
					g.drawImage(leftArrow, 550, 240, null);
					g.drawString("ON", 595, 275);
				}
				else if(!laser)
				{
					g.drawString("OFF", 595, 275);
					g.drawImage(rightArrow, 695, 240, null);
				}
				
				// Stinky Options
				g.drawString("Stinky", 50, 375);
				if(stinkyEnabled)
				{
					g.drawImage(leftArrow, 550, 340, null);
					g.drawString("ON", 595, 375);
				}
				else if(!stinkyEnabled)
				{
					g.drawString("OFF", 595, 375);
					g.drawImage(rightArrow, 695, 340, null);
				}
				
				// Donkey Kong
				g.drawString("Donkey Kong", 50, 425);
				if(DK)
				{
					g.drawImage(leftArrow, 550, 390, null);
					g.drawString("ON", 595, 425);
				}
				else if(!DK)
				{
					g.drawString("OFF", 595, 425);
					g.drawImage(rightArrow, 695, 390, null);
				}

				// Centipede Player Options
				g.drawString("Centipede Player", 50, 475);
				if(centipede)
				{
					g.drawImage(leftArrow, 550, 440, null);
					g.drawString("ON", 595, 475);
				}
				else if(!centipede)
				{
					g.drawString("OFF", 595, 475);
					g.drawImage(rightArrow, 695, 440, null);
				}
				
				// UFO Options
				g.drawString("Mystery Ship", 50, 525);
				if(ufo)
				{
					g.drawImage(leftArrow, 550, 490, null);
					g.drawString("ON", 595, 525);
				}
				else if(!ufo)
				{
					g.drawString("OFF", 595, 525);
					g.drawImage(rightArrow, 695, 490, null);
				}
				
				g.setFont(font50);
				g.drawString("GO!", 375, 750);
	}
}
