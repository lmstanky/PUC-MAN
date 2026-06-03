package main;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

public class Textures
{
	public ArrayList<BufferedImage> mazeTiles = new ArrayList<BufferedImage>();
	public ArrayList<BufferedImage> mazeTilesWhite = new ArrayList<BufferedImage>();
	
	// Pac-Man Sprites (2 for each individual direction plus 1 "neutral" image that applies to all
	public BufferedImage rPlayer0, rPlayer1, rPlayer2;
	public BufferedImage dPlayer0, dPlayer1;
	public BufferedImage lPlayer0, lPlayer1;
	public BufferedImage uPlayer0, uPlayer1;
	
	// Pac-Man Death Sprites
	public BufferedImage pacDeath0, pacDeath1, pacDeath2, pacDeath3, pacDeath4, pacDeath5, pacDeath6, pacDeath7, pacDeath8, pacDeath9;
	
	// Ghost Eyes Sprites (For when they've been eaten and returning to the house)
	public BufferedImage rightEyes, downEyes, leftEyes, upEyes;
	
	// Blinky Sprites (2 for each individual direction)
	public BufferedImage rBlinky0, rBlinky1;
	public BufferedImage dBlinky0, dBlinky1;
	public BufferedImage lBlinky0, lBlinky1;
	public BufferedImage uBlinky0, uBlinky1;
	
	
	// Pinky Sprites (2 for each individual direction)
	public BufferedImage rPinky0, rPinky1;
	public BufferedImage dPinky0, dPinky1;
	public BufferedImage lPinky0, lPinky1;
	public BufferedImage uPinky0, uPinky1;
	
	
	// Inky Sprites (2 for each individual direction)
	public BufferedImage rInky0, rInky1;
	public BufferedImage dInky0, dInky1;
	public BufferedImage lInky0, lInky1;
	public BufferedImage uInky0, uInky1;
	
	
	// Inky Sprites (2 for each individual direction)
	public BufferedImage rClyde0, rClyde1;
	public BufferedImage dClyde0, dClyde1;
	public BufferedImage lClyde0, lClyde1;
	public BufferedImage uClyde0, uClyde1;
	
	
	// Stinky Sprites (2 for each individual direction)
	public BufferedImage rStinky0, rStinky1;
	public BufferedImage dStinky0, dStinky1;
	public BufferedImage lStinky0, lStinky1;
	public BufferedImage uStinky0, uStinky1;
	
	
	// "Frightened" Ghost Sprites (Neutral direction, but 2x2 for flashing between Blue and White
	public BufferedImage blueGhost0, blueGhost1;
	public BufferedImage whiteGhost0, whiteGhost1;
	
	
	// Miscellaneous
	public BufferedImage bulletVert, bulletHoriz, bulletDiagUpRight, bulletDiagUpLeft;
	public BufferedImage cursor, smallCursor, leftArrow, rightArrow, upArrow, downArrow;
	public BufferedImage rawMazeTiles, rawMazeTilesWhite;
	public BufferedImage fs, modeSelect;
	public BufferedImage ready, player1;
	public BufferedImage gameOver;
	public BufferedImage powerup, bomb0, bomb1, bomb2;
	public BufferedImage explosion0, explosion1, explosion2, explosion3, explosion4, explosion5, explosion6, explosion7;
	public BufferedImage centipedeHead;
	public BufferedImage ufo;
	public BufferedImage greenPellet;
	
	
	// Fruit
	public BufferedImage cherry, strawberry, peach, apple, grape, galaxian, bell, key;
	
	// Numbers
	public BufferedImage oneHundred, threeHundred, fiveHundred, sevenHundred, oneThousand, twoThousand, threeThousand, fiveThousand;
	public BufferedImage twoHundred, fourHundred, eightHundred, sixteenHundred;
	
	
	// Bomb Explosion
	public BufferedImage bombExplosionEndRight, bombExplosionEndDown, bombExplosionEndLeft, bombExplosionEndUp;
	public BufferedImage bombExplosionMiddleHoriz, bombExplosionMiddleVert;
	public BufferedImage bombExplosionTRight, bombExplosionTDown, bombExplosionTLeft, bombExplosionTUp;
	public BufferedImage bombExplosionCornerTRight, bombExplosionCornerBRight, bombExplosionCornerBLeft, bombExplosionCornerTLeft;
	public BufferedImage bombExplosionCross;
	
	
	// Lasers
	public BufferedImage laser;
	public BufferedImage bigLaser;
	
	
	// Donkey Kong
	public BufferedImage dkNormal1, dkNormal2, dkBarrel1, dkBarrel2;
	
	// Barrel
	public BufferedImage barrelHoriz0, barrelHoriz1;
	public BufferedImage barrelVert0, barrelVert1;
	
	// Donkey Kong Platform
	public BufferedImage platform;
	
	public BufferedImage rawDKTiles;
	public BufferedImage rawExplosionTiles;
	public BufferedImage bigBomb;
	public BufferedImage banner;
	public BufferedImage bigPac;
	
	private SpriteSheet ss;
	private SpriteSheet mapSS;
	private SpriteSheet whiteMapSS;
	private SpriteSheet dkSS;
	private SpriteSheet explosionSS;
	@SuppressWarnings("unused")
	private Game game;
	
	public Textures(Game game)
	{
		this.game = game;
		ss = new SpriteSheet(game.getSpriteSheet());
		BufferedImageLoader loader = new BufferedImageLoader();
		try
		{
			rawMazeTiles = loader.loadImage("/map_sprites.png");
			rawMazeTilesWhite = loader.loadImage("/map_sprites_white.png");
			rawDKTiles = loader.loadImage("/dk_sprites.png");
			ready = loader.loadImage("/gui/ready.png");
			gameOver = loader.loadImage("/gui/gameOver.png");
			rawExplosionTiles = loader.loadImage("/explosion.png");
			centipedeHead = loader.loadImage("/centipedeHead.png");
			platform = loader.loadImage("/platform.png");
			fs = loader.loadImage("/gui/fs.png");
			ufo = loader.loadImage("/ufo.png");
			bigLaser = loader.loadImage("/gui/bigLaser.png");
			bigBomb = loader.loadImage("/gui/bigBomb.png");
			banner = loader.loadImage("/gui/Banner.png");
			bigPac = loader.loadImage("/gui/bigPac.png");	
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mapSS = new SpriteSheet(rawMazeTiles);
		whiteMapSS = new SpriteSheet(rawMazeTilesWhite);
		dkSS = new SpriteSheet(rawDKTiles);
		explosionSS = new SpriteSheet(rawExplosionTiles);
		
		getTextures();
	}
	
	private void getTextures()
	{
		
		// Column number first, then row. They're NOT 0 indexed, and almost all of them are 32 x 32 pixels
		
		
		// Set Pac-Man Sprites
		rPlayer0 = ss.extractSprite(1, 1, 32, 32);
		rPlayer1 = ss.extractSprite(2, 1, 32, 32);
		rPlayer2 = ss.extractSprite(3, 1, 32, 32);
		dPlayer0 = ss.extractSprite(1, 2, 32, 32);
		dPlayer1 = ss.extractSprite(2, 2, 32, 32);
		lPlayer0 = ss.extractSprite(1, 3, 32, 32);
		lPlayer1 = ss.extractSprite(2, 3, 32, 32);
		uPlayer0 = ss.extractSprite(1, 4, 32, 32);
		uPlayer1 = ss.extractSprite(2, 4, 32, 32);
		
		
		// Set Pac-Death Sprites
		pacDeath0 = ss.extractSprite(1, 8, 32, 32);
		pacDeath1 = ss.extractSprite(2, 8, 32, 32);
		pacDeath2 = ss.extractSprite(3, 8, 32, 32);
		pacDeath3 = ss.extractSprite(4, 8, 32, 32);
		pacDeath4 = ss.extractSprite(5, 8, 32, 32);
		pacDeath5 = ss.extractSprite(6, 8, 32, 32);
		pacDeath6 = ss.extractSprite(7, 8, 32, 32);
		pacDeath7 = ss.extractSprite(8, 8, 32, 32);
		pacDeath8 = ss.extractSprite(9, 8, 32, 32);
		pacDeath9 = ss.extractSprite(10, 8, 32, 32);
		
		
		// Set Ghost Eyes Sprites
		rightEyes = ss.extractSprite(12, 3, 32, 32);
		downEyes = ss.extractSprite(13, 3, 32, 32);
		leftEyes = ss.extractSprite(14, 3, 32, 32);
		upEyes = ss.extractSprite(15, 3, 32, 32);
		
		
		// Set Blinky Sprites
		rBlinky0 = ss.extractSprite(4, 1, 32, 32);
		rBlinky1 = ss.extractSprite(5, 1, 32, 32);
		dBlinky0 = ss.extractSprite(4, 2, 32, 32);
		dBlinky1 = ss.extractSprite(5, 2, 32, 32);
		lBlinky0 = ss.extractSprite(4, 3, 32, 32);
		lBlinky1 = ss.extractSprite(5, 3, 32, 32);
		uBlinky0 = ss.extractSprite(4, 4, 32, 32);
		uBlinky1 = ss.extractSprite(5, 4, 32, 32);
		
		
		// Set Pinky Sprites
		rPinky0 = ss.extractSprite(6, 1, 32, 32);
		rPinky1 = ss.extractSprite(7, 1, 32, 32);
		dPinky0 = ss.extractSprite(6, 2, 32, 32);
		dPinky1 = ss.extractSprite(7, 2, 32, 32);
		lPinky0 = ss.extractSprite(6, 3, 32, 32);
		lPinky1 = ss.extractSprite(7, 3, 32, 32);
		uPinky0 = ss.extractSprite(6, 4, 32, 32);
		uPinky1 = ss.extractSprite(7, 4, 32, 32);
		
		
		// Set Inky Sprites
		rInky0 = ss.extractSprite(8, 1, 32, 32);
		rInky1 = ss.extractSprite(9, 1, 32, 32);
		dInky0 = ss.extractSprite(8, 2, 32, 32);
		dInky1 = ss.extractSprite(9, 2, 32, 32);
		lInky0 = ss.extractSprite(8, 3, 32, 32);
		lInky1 = ss.extractSprite(9, 3, 32, 32);
		uInky0 = ss.extractSprite(8, 4, 32, 32);
		uInky1 = ss.extractSprite(9, 4, 32, 32);
		
		
		// Set Clyde Sprites
		rClyde0 = ss.extractSprite(10, 1, 32, 32);
		rClyde1 = ss.extractSprite(11, 1, 32, 32);
		dClyde0 = ss.extractSprite(10, 2, 32, 32);
		dClyde1 = ss.extractSprite(11, 2, 32, 32);
		lClyde0 = ss.extractSprite(10, 3, 32, 32);
		lClyde1 = ss.extractSprite(11, 3, 32, 32);
		uClyde0 = ss.extractSprite(10, 4, 32, 32);
		uClyde1 = ss.extractSprite(11, 4, 32, 32);
		
		
		// Set Stinky Sprites
		rStinky0 = ss.extractSprite(5, 10, 32, 32);
		rStinky1 = ss.extractSprite(6, 10, 32, 32);
		dStinky0 = ss.extractSprite(5, 11, 32, 32);
		dStinky1 = ss.extractSprite(6, 11, 32, 32);
		lStinky0 = ss.extractSprite(5, 12, 32, 32);
		lStinky1 = ss.extractSprite(6, 12, 32, 32);
		uStinky0 = ss.extractSprite(5, 13, 32, 32);
		uStinky1 = ss.extractSprite(6, 13, 32, 32);
		
		
		// Set "Frightened" Sprites
		blueGhost0 = ss.extractSprite(12, 1, 32, 32);
		blueGhost1 = ss.extractSprite(13, 1, 32, 32);
		whiteGhost0 = ss.extractSprite(12, 2, 32, 32);
		whiteGhost1 = ss.extractSprite(13, 2, 32, 32);
		
		
		// Set Bullet Sprites
		bulletHoriz = ss.extractSprite(1, 12, 32, 32);
		bulletVert = ss.extractSprite(2, 12, 32, 32);
		bulletDiagUpRight = ss.extractSprite(3, 12, 32, 32);
		bulletDiagUpLeft = ss.extractSprite(4, 12, 32, 32);
		
		
		// Set Menu Cursor Sprite (It's just an enlarged Power Pellet)
		cursor = ss.extractSprite(3, 2, 32, 32);
		smallCursor = ss.extractSprite(1, 5, 32, 32);
		
		// Set Menu Arrows Sprites
		rightArrow = ss.extractSprite(3, 3, 32, 32);
		leftArrow = ss.extractSprite(3, 4, 32, 32);
		upArrow = ss.extractSprite(14, 1, 32, 32);
		downArrow = ss.extractSprite(14, 2, 32, 32);
		
		// Set Fruit icons
		cherry = ss.extractSprite(2, 5, 32, 32);
		strawberry = ss.extractSprite(3, 5, 32, 32);
		peach = ss.extractSprite(4, 5, 32, 32);
		apple = ss.extractSprite(5, 5, 32, 32);
		grape = ss.extractSprite(6, 5, 32, 32);
		galaxian = ss.extractSprite(7, 5, 32, 32);
		bell = ss.extractSprite(8, 5, 32, 32);
		key = ss.extractSprite(9, 5, 32, 32);
		
		// Set Number icons
		oneHundred = ss.extractSprite(1, 6, 32, 32);
		threeHundred = ss.extractSprite(2, 6, 32, 32);
		fiveHundred = ss.extractSprite(3, 6, 32, 32);
		sevenHundred = ss.extractSprite(4, 6, 32, 32);
		oneThousand = ss.extractSprite(5, 6, 32, 32);
		twoThousand = ss.extractSprite(7, 6, 32, 32);
		threeThousand = ss.extractSprite(7, 6, 32, 32);
		fiveThousand = ss.extractSprite(8, 6, 32, 32);
		
		twoHundred = ss.extractSprite(1, 7, 32, 32);
		fourHundred = ss.extractSprite(2, 7, 32, 32);
		eightHundred = ss.extractSprite(3, 7, 32, 32);
		sixteenHundred = ss.extractSprite(4, 7, 32, 32);
		
		
		// Add powerup Sprites
		powerup = ss.extractSprite(1, 10, 32, 32);
		bomb0 = ss.extractSprite(1, 11, 32, 32);
		bomb1 = ss.extractSprite(2, 11, 32, 32);
		bomb2 = ss.extractSprite(3, 11, 32, 32);
		laser = ss.extractSprite(4, 11, 32, 32);
		
		
		
		// Add Normal, blue Map Tile Sprites
		for(int i = 0; i < 3; i++)
			for(int j = 0; j < 12; j++)
				mazeTiles.add(mapSS.extractSprite(j+1, i+1, 24, 24));
		mazeTiles.add(mapSS.extractSprite(1, 4, 24, 24));
		mazeTiles.add(mapSS.extractSprite(2, 4, 24, 24));
		mazeTiles.add(mapSS.extractSprite(3, 4, 24, 24));
		
		
		// Add white Map Tile Sprites
		for(int i = 0; i < 3; i++)
			for(int j = 0; j < 12; j++)
				mazeTilesWhite.add(whiteMapSS.extractSprite(j+1, i+1, 24, 24));
		mazeTilesWhite.add(whiteMapSS.extractSprite(1, 4, 24, 24));
		mazeTilesWhite.add(whiteMapSS.extractSprite(2, 4, 24, 24));
		mazeTilesWhite.add(whiteMapSS.extractSprite(3, 4, 24, 24));
		
		
		// Set Donkey Kong Sprites
		dkNormal1 = dkSS.extractSprite(1, 1, 100, 70);
		dkNormal2 = dkSS.extractSprite(2, 1, 100, 70);
		dkBarrel1 = dkSS.extractSprite(3, 1, 100, 70);
		dkBarrel2 = dkSS.extractSprite(4, 1, 100, 70);
		
		// Set Barrel Sprites
		barrelVert0 = ss.extractSprite(1, 9, 32, 32);
		barrelVert1 = ss.extractSprite(2, 9, 32, 32);
		barrelHoriz0 = ss.extractSprite(3, 9, 32, 32);
		barrelHoriz1 = ss.extractSprite(4, 9, 32, 32);
		
		
		// Set Centipede Explosion Sprites
		explosion0 = explosionSS.extractSprite(1, 1, 64, 64);
		explosion1 = explosionSS.extractSprite(2, 1, 64, 64);
		explosion2 = explosionSS.extractSprite(3, 1, 64, 64);
		explosion3 = explosionSS.extractSprite(4, 1, 64, 64);
		explosion4 = explosionSS.extractSprite(5, 1, 64, 64);
		explosion5 = explosionSS.extractSprite(6, 1, 64, 64);
		explosion6 = explosionSS.extractSprite(7, 1, 64, 64);
		explosion7 = explosionSS.extractSprite(8, 1, 64, 64);
		
		
		// Set Bomb Explosion Sprites
		bombExplosionEndRight = ss.extractSprite(1, 13, 32, 32);
		bombExplosionEndDown = ss.extractSprite(2, 13, 32, 32);
		bombExplosionEndLeft = ss.extractSprite(3, 13, 32, 32);
		bombExplosionEndUp = ss.extractSprite(4, 13, 32, 32);
		
		bombExplosionMiddleHoriz = ss.extractSprite(1, 14, 32, 32);
		bombExplosionMiddleVert = ss.extractSprite(2, 14, 32, 32);
		bombExplosionCross = ss.extractSprite(3, 14, 32, 32);
		
		bombExplosionTUp = ss.extractSprite(4, 14, 32, 32);
		bombExplosionTRight = ss.extractSprite(5, 14, 32, 32);
		bombExplosionTDown = ss.extractSprite(6, 14, 32, 32);
		bombExplosionTLeft = ss.extractSprite(7, 14, 32, 32);
		
		bombExplosionCornerTLeft = ss.extractSprite(1, 15, 32, 32);
		bombExplosionCornerTRight = ss.extractSprite(2, 15, 32, 32);
		bombExplosionCornerBRight = ss.extractSprite(3, 15, 32, 32);
		bombExplosionCornerBLeft = ss.extractSprite(4, 15, 32, 32);
		
		
		// Set Green Power Pellet Sprite
		greenPellet = ss.extractSprite(7, 10, 32, 32);
	}
}
