package main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.JFrame;

import collision.Entity;
import collision.Physics;
import entities.DK;
import entities.Fruit;
import entities.Ghost;
import entities.Player;
import entities.Powerup;
import entities.UFO;
import entities.CentipedeHead;
import misc.GameController;
import misc.Map;
import misc.SoundSystem;
import pathfinding.AStar;
import states.CustomGame;
import states.HighScore;
import states.MainMenu;
import states.ModeSelect;

import org.newdawn.slick.util.ResourceLoader;


@SuppressWarnings("serial")
public class Game extends Canvas implements Runnable
{
	public static final double FPS = 60.0;
	public static final int WIDTH = 1366;
	public static final int HEIGHT = 768;
	public static final boolean DEBUG = false;
	public final String TITLE = "Puc-Man";
	private boolean isCustomGame = false;
	public boolean gameOver = false;
	public int gameOverTickCount = 240;
	double delta = 0;
	Font font20;
	
	public int infectedPowerPellets = 0;
	public int dotCount = 244;
	private boolean introEnable = true;
	private int introTickCount = 245;
	
	public int[][] scatterPhaseTickCounts = new int[3][4];
	public int[][] chasePhaseTickCounts = new int[3][4];
	public int[] frightenedTickCounts = new int[6];
	public int level = 0;
	
	public boolean resetLevel = false;
	public boolean nextLevel = false;
	private int resetLevelCount = 120;
	private int nextLevelCount = 240;
	
	public Fruit fruit;
	public boolean fruitFlag = false; // Boolean to check if the fruit has already been run on this level
	public boolean fruitActive = false; // Boolean to check if the fruit is currently active
	public int fruitActiveCount = 540; // Number of ticks to run the fruit for (Which is 9 seconds)
	public BufferedImage[] fruitHUD = new BufferedImage[7];
	
	public Powerup powerUp = null;
	public boolean powerUpFlag = false;
	public boolean powerUpActive = false;
	public int powerUpActiveCount = 630;
	public int powerUpSpawnTime = ThreadLocalRandom.current().nextInt(50, 150);
	public boolean bombActive = false;
	public int bombExplosionCount = 60;
	
	public CentipedeHead ch = null;
	public int centipedeSpawnTime = ThreadLocalRandom.current().nextInt(50, 150);
	public boolean centipedeActive = false;
	public boolean centipedeFlag = false;
	
	public boolean ghostEaten = false; // boolean to check if any of the ghosts have been eaten
	public int ghostEatenCount = 60;  // How long to pause the game when Pac-Man eats a frightened Ghost (1 second)
	

	public SoundSystem audio = new SoundSystem();
	public boolean backgroundNoiseIsPlaying = false;
	
	private boolean isShooting;
	
	public DK dk;
	public UFO ufo;
	public int ufoSpawnTime = ThreadLocalRandom.current().nextInt(25, powerUpSpawnTime);
	public boolean ufoActive = false;
	public boolean ufoFlag = false;
	
	private BufferedImage bg = new BufferedImage(HEIGHT, WIDTH, BufferedImage.TYPE_INT_RGB);
	private BufferedImage spriteSheet = null;
	
	int dirCooldown = 0;
	
	public Map maze = null;
	
	private MainMenu mainMenu;
	private ModeSelect modeSelect;
	public CustomGame customGame;
	public HighScore highScoreState;
	
	public Player pacMan;
	public Ghost inky;
	public Ghost pinky;
	public Ghost blinky;
	public Ghost clyde;
	public Ghost stinky;
	private int ghostExtraTickCount = 30;
	private int playerExtraTickCount = 5;
	
	public int ghostPoints = 200;
	public boolean pacDeath = false;
	private int pacDeathCount = 120;
	public boolean barrelDeath = false;
	public boolean laserDeath = false;
	
	private boolean levelCleared = false;
	private int levelClearedTransitionCount = 60;
	private int blueMapCount = 0;
	private int whiteMapCount = 30;
	private boolean isBlue = true; // Flag to say if the map tiles need changing to white
	private boolean isWhite  = false; // Flag to say if the map tiles need changing to blue
	
	public Textures textures;
	
	public GameController controls = new GameController();
	float right;
	float down;
	float left;
	float up;
	boolean isPressed = false;
	int tickCount = 0;
	
	private enum STATE
	{
		MAIN_MENU,
		MODE_SELECT,
		CUSTOM_MENU,
		GAME,
		HIGH_SCORE,
		PAUSED
	};
	
	private STATE State = STATE.MAIN_MENU;
	
	private boolean running = false;
	private Thread thread;
	
	public MainControl c;
	
	// HUD Elements
	public int score = 0;
	public int highScore = 0;
	private String initials;
	public int lives = 3;
	public int displayLives = lives;
	public int livesPoints = 10000;
	
	public void init()
	{	
		BufferedImageLoader loader = new BufferedImageLoader();
		audio.load();
		getCurrentHighScore();
		try
		{
			spriteSheet = loader.loadImage("/pac_sprites.png");
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		textures = new Textures(this);
		mainMenu = new MainMenu(textures);
		modeSelect = new ModeSelect(textures);
		customGame = new CustomGame(textures);
		
		dk = new DK(this);
		
		fruit = new Fruit(this, "cherry", 776, 500, 0, textures.cherry);
		fruitHUD[0] = textures.cherry;
		try
		{
			font20 = Font.createFont(Font.TRUETYPE_FONT, ResourceLoader.getResourceAsStream(("PressStart2P.ttf"))).deriveFont(Font.PLAIN, 20);
		} catch (FontFormatException | IOException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		};
		c = new MainControl(this);
		this.requestFocus();
		try
		{
		maze = new Map("/maps/default.txt", textures);
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		initialisePhaseTimers();
		
		//Landscape
		pacMan = new Player(776, 644, 1, textures, this, audio.getSound("pacDot_down"));
		c.entities.add((Entity) pacMan);
		
		// Create Ghost objects. Blinky's path is initialised immediately as he always starts in "scatter"
		inky = new Ghost("inky", "dormant", "up", 728, 428, 0, 0, 2.0, textures, this);
		pinky = new Ghost("pinky", "released", "up", 776, 428, 0, 0, 2.0, textures, this);
		blinky = new Ghost("blinky", "scatter", "left", 776, 356, 13, 11, 2.0, textures, this);
		blinky.resetFrightenedTicks();
		blinky.aS = new AStar(blinky.i, blinky.j, 25, 1, this, blinky);
		blinky.path = blinky.aS.algorithm();
		blinky.target[0] = blinky.path.peek().i;
		blinky.target[1] = blinky.path.pop().j;
		clyde = new Ghost("clyde", "dormant", "up", 826, 428, 0, 0, 2.0, textures, this);
		
		c.ghosts.add(blinky);
		c.ghosts.add(pinky);
		c.ghosts.add(inky);
		c.ghosts.add(clyde);
	}
	
	private synchronized void start()
	{
		if(running)
			return;
		running = true;
		thread = new Thread(this);
		thread.start();
	}
	
	private synchronized void stop()
	{
		if(!running)
			return;
		
		running = false;
		try
		{
			thread.join();
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.exit(0);
	}
	
	public static void main(String[] args)
	{
		Game pMan = new Game();
		pMan.setPreferredSize(new Dimension(HEIGHT, WIDTH));
		pMan.setMaximumSize(new Dimension(HEIGHT, WIDTH));
		pMan.setMinimumSize(new Dimension(HEIGHT, WIDTH));
		
		JFrame gameFrame = new JFrame();
		gameFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		gameFrame.setUndecorated(true);
		gameFrame.add(pMan);
		gameFrame.pack();
		gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gameFrame.setResizable(false);
		gameFrame.setVisible(true);
		
		// Hide cursor by setting it to a blank, custom image with 0 Alpha
		BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "");
		gameFrame.setCursor(blankCursor);
		
		pMan.start();
	}
	

	@Override
	public void run()
	{
		init();
		long lastTime = System.nanoTime();
		final double numberOfTicks = FPS;
		double ns = 1000000000 / numberOfTicks;
		long timeNow = System.nanoTime();
		delta = 0; // Calculate the amount of time passed
		
		while(running) // Game loop
		{
			if(controls.sanwa != null)
			{
				controls.sanwa.poll();
				checkController();
			}
			
			else if(controls.xbox != null)
			{
				controls.xbox.poll();
				checkXboxController();
			}
			
			else
			{
				System.out.println("Controller not connected");
				controls = new GameController();
			}
		
				
			if(dotCount <= 0)
			{
				levelCleared = true;
				stopBackgroundNoise();
				stopFrightenedNoise();
				stopReturningNoise();
			}
				
			if(State != STATE.GAME)
				delta = 0;
			
			timeNow = System.nanoTime();
			delta += (timeNow - lastTime) / ns;
			lastTime = timeNow;
			if(delta >= 1)
			{
				if(State != STATE.PAUSED)
					tick();
				if(State == STATE.PAUSED)
				{
					stopFrightenedNoise();
					stopBackgroundNoise();
					stopReturningNoise();
				}
				
				if(introEnable)
					delta = 0;
				else
					delta--;
			}
			render();
		}
		stop();
	}

	private void checkController()
	{	
		/* If any of the controls are pressed, don't allow any operation.
		 * This controls the cursor movement speed by requiring the player to let go of all buttons completely
		 * before being able to move the cursor or select an option again.
		 * Otherwise, the game polls the controller way too fast to accurately navigate the menus with any ease.*/
		if(controls.components[6].getPollData() == 0 &&
				controls.components[7].getPollData() == 0 &&
				controls.components[8].getPollData() == 0 &&
				controls.components[9].getPollData() == 0 &&
				controls.components[10].getPollData() == 0 &&
				controls.components[11].getPollData() == 0)
			isPressed = false;
		
		if(State == STATE.MAIN_MENU)
		{
			if(controls.components[6].getPollData() > 0 && mainMenu.selected == 0 && !isPressed)
			{
				isPressed = true;
				State = STATE.MODE_SELECT;
				audio.getSound("menu_scroll").playAsSoundEffect(1.0f, 1.0f, false);
			}
		}
		
		else if(State == STATE.MODE_SELECT)
		{
			if(controls.components[7].getPollData() > 0 && !isPressed)
			{
				isPressed = true;
				audio.getSound("menu_scroll").playAsSoundEffect(1.0f, 1.0f, false);
				State = STATE.MAIN_MENU;
			}
			
			else if(controls.components[6].getPollData() > 0 && modeSelect.selected == "classic" && !isPressed)
			{
				isPressed = true;
				State = STATE.GAME;
				audio.getSound("game_start").playAsSoundEffect(1.0f, 1.0f, false);
				dotCount = 244;
				//audio.getSound("menu_scroll").playAsSoundEffect(1.0f, 1.0f, false);
			}
			
			else if(controls.components[6].getPollData() > 0 && modeSelect.selected == "custom" && !isPressed)
			{
				isPressed = true;
				audio.getSound("menu_scroll").playAsSoundEffect(1.0f, 1.0f, false);
				State = STATE.CUSTOM_MENU;
			}
			
			else if(controls.components[7].getPollData() > 0 && !isPressed)
			{
				isPressed = true;
				mainMenu.selected = 0;
				State = STATE.MAIN_MENU;
				audio.getSound("menu_scroll").playAsSoundEffect(1.0f, 1.0f, false);
			}
			
			else if(controls.components[9].getPollData() > 0 && modeSelect.selected == "classic" && !isPressed)
			{
				isPressed = true;
				modeSelect.selected = "custom";
				audio.getSound("menu_scroll").playAsSoundEffect(1.0f, 1.0f, false);
			}
			
			else if(controls.components[11].getPollData() > 0 && modeSelect.selected == "custom" && !isPressed)
			{
				isPressed = true;
				modeSelect.selected = "classic";
				audio.getSound("menu_scroll").playAsSoundEffect(1.0f, 1.0f, false);
			}
		}
		
		
		else if(State == STATE.CUSTOM_MENU)
		{
			if(controls.components[7].getPollData() > 0 && !isPressed)
			{
				isPressed = true;
				audio.getSound("menu_scroll").playAsSoundEffect(1.0f, 1.0f, false);
				State = STATE.MODE_SELECT;
			}
			
			if(controls.components[6].getPollData() > 0 && customGame.menuSelected == 8 && !isPressed)
			{
				lives = customGame.startLivesOptions[customGame.startLivesSelected];
				displayLives = lives;
				livesPoints = customGame.extraLivesOptions[customGame.extraLivesSelected];
				audio.soundEffects.get("game_start").playAsSoundEffect(1.0f, 1.0f, false);
				if(customGame.stinkyEnabled)
				{
					stinky = new Ghost("stinky", "scatter", "left", 776, 500, 13, 17, 2, textures, this);
					stinky.aS = new AStar(stinky.i, stinky.j, 15, 22, this, stinky);
					stinky.path = stinky.aS.algorithm();
					stinky.target[0] = stinky.path.peek().i;
					stinky.target[1] = stinky.path.pop().j;
					c.ghosts.add(stinky);
				}
				State = STATE.GAME;
				isCustomGame = true;
			}
			
			else if(controls.components[8].getPollData() > 0 && !isPressed)
			{
				isPressed = true;
				if(customGame.menuSelected == 0 && customGame.startLivesSelected < 2){customGame.startLivesSelected++;}
				else if(customGame.menuSelected == 1 && customGame.extraLivesSelected < 3){customGame.extraLivesSelected++;}
				else if(customGame.menuSelected == 2 && !customGame.bomb){customGame.bomb = true;}
				else if(customGame.menuSelected == 3 && !customGame.laser){customGame.laser = true;}
				else if(customGame.menuSelected == 4 && !customGame.stinkyEnabled){customGame.stinkyEnabled = true;}
				else if(customGame.menuSelected == 5 && !customGame.DK){customGame.DK = true;}
				else if(customGame.menuSelected == 6 && !customGame.centipede){customGame.centipede = true;}
				else if(customGame.menuSelected == 7 && !customGame.ufo){customGame.ufo = true;}
			}
			
			else if(controls.components[9].getPollData() > 0 && customGame.menuSelected < 8 && !isPressed){isPressed = true; customGame.menuSelected++;}
			else if(controls.components[10].getPollData() > 0 && !isPressed)
			{
				isPressed = true;
				if(customGame.menuSelected == 0 && customGame.startLivesSelected > 0){customGame.startLivesSelected--;}
				else if(customGame.menuSelected == 1 && customGame.extraLivesSelected > 0){customGame.extraLivesSelected--;}
				else if(customGame.menuSelected == 2 && customGame.bomb){customGame.bomb = false;}
				else if(customGame.menuSelected == 3 && customGame.laser){customGame.laser = false;}
				else if(customGame.menuSelected == 4 && customGame.stinkyEnabled){customGame.stinkyEnabled = false;}
				else if(customGame.menuSelected == 5 && customGame.DK){customGame.DK = false;}
				else if(customGame.menuSelected == 6 && customGame.centipede){customGame.centipede = false;}
				else if(customGame.menuSelected == 7 && customGame.ufo){customGame.ufo = false;}
			}
			else if(controls.components[11].getPollData() > 0 && customGame.menuSelected > 0 &&!isPressed){isPressed = true; customGame.menuSelected--;}
		}
		
		
		else if(State == STATE.GAME)
		{				
			if(controls.components[7].getPollData() > 0 && !isPressed)
			{
				isPressed = true;
				State = STATE.PAUSED;
			}
			
			else if(controls.components[8].getPollData() > 0 && Physics.checkDirection("right", pacMan, maze))
			{
				pacMan.prevDirection = pacMan.getDirection();
				if(((int)(pacMan.x+4) % 24 <= 1 && (int)(pacMan.y+4) % 24 <= 1))
					pacMan.setDirection("right");
				else if(pacMan.prevDirection == "up" && pacMan.y+4 > maze.map[pacMan.j][pacMan.i+1].y)
					pacMan.setDirection("uRight");
				else if(pacMan.prevDirection == "down" && pacMan.y+4 < maze.map[pacMan.j][pacMan.i+1].y)
					pacMan.setDirection("dRight");
				
				// These conditionals check if Pac-Man has surpassed the junction, and will move back if within 12 pixels of it
				// i.e. Half a tile's width
				else if(pacMan.prevDirection == "up" && pacMan.y+4 > maze.map[pacMan.j][pacMan.i+1].y-12)
					pacMan.setDirection("dRight");
				
				else if(pacMan.prevDirection == "down" && pacMan.y+4 < maze.map[pacMan.j][pacMan.i+1].y+12)
					pacMan.setDirection("uRight");
			}
			
			else if(controls.components[9].getPollData() > 0 && Physics.checkDirection("down", pacMan, maze))
			{
				pacMan.prevDirection = pacMan.getDirection();
				if(((int)(pacMan.x+4) % 24 <= 1 && (int)(pacMan.y+4) % 24 <= 1))
					pacMan.setDirection("down");
				else if(pacMan.prevDirection == "left" && pacMan.x+4 > maze.map[pacMan.j+1][pacMan.i].x)
					pacMan.setDirection("lDown");
				else if(pacMan.prevDirection == "right" && pacMan.x+4 < maze.map[pacMan.j+1][pacMan.i].x)
					pacMan.setDirection("rDown");
				
				
				// These conditionals check if Pac-Man has surpassed the junction, and will move back if within 12 pixels of it
				// i.e. Half a tile's width
				else if(pacMan.prevDirection == "left" && pacMan.x+4 > maze.map[pacMan.j+1][pacMan.i].x-12)
					pacMan.setDirection("rDown");
				else if(pacMan.prevDirection == "right" && pacMan.x+4 < maze.map[pacMan.j+1][pacMan.i].x+12)
					pacMan.setDirection("lDown");
			}
			
			else if(controls.components[10].getPollData() > 0 && Physics.checkDirection("left", pacMan, maze))
			{
				pacMan.prevDirection = pacMan.getDirection();
				if(((int)(pacMan.x+4) % 24 <= 1 && (int)(pacMan.y+4) % 24 <= 1))
					pacMan.setDirection("left");
				else if(pacMan.prevDirection == "up" && pacMan.y+4 > maze.map[pacMan.j][pacMan.i-1].y)
					pacMan.setDirection("uLeft");
				else if(pacMan.prevDirection == "down" && pacMan.y+4 < maze.map[pacMan.j][pacMan.i-1].y)
					pacMan.setDirection("dLeft");
				
				// These conditionals check if Pac-Man has surpassed the junction, and will move back if within 12 pixels of it
				// i.e. Half a tile's width
				else if(pacMan.prevDirection == "up" && pacMan.y+4 > maze.map[pacMan.j][pacMan.i-1].y-12)
					pacMan.setDirection("dLeft");
				else if(pacMan.prevDirection == "down" && pacMan.y+4 < maze.map[pacMan.j][pacMan.i-1].y+12)
					pacMan.setDirection("uLeft");
			}
			
			else if(controls.components[11].getPollData() > 0 && Physics.checkDirection("up", pacMan, maze))
			{
				pacMan.prevDirection = pacMan.getDirection();
				if(((int)(pacMan.x+4) % 24 <= 1 && (int)(pacMan.y+4) % 24 <= 1))
					pacMan.setDirection("up");
				else if(pacMan.prevDirection == "right" && pacMan.x+4 < maze.map[pacMan.j-1][pacMan.i].x)
					pacMan.setDirection("rUp");
				else if(pacMan.prevDirection == "left" && pacMan.x+4 > maze.map[pacMan.j-1][pacMan.i].x)
					pacMan.setDirection("lUp");
				
				// These conditionals check if Pac-Man has surpassed the junction, and will move back if within 12 pixels of it
				// i.e. Half a tile's width
				else if(pacMan.prevDirection == "right" && pacMan.x+4 < maze.map[pacMan.j-1][pacMan.i].x+12)
					pacMan.setDirection("lUp");
				else if(pacMan.prevDirection == "left" && pacMan.x+4 > maze.map[pacMan.j-1][pacMan.i].x-12)
					pacMan.setDirection("rUp");
			}
			
			if(controls.components[6].getPollData() == 0 && isShooting)
				isShooting = false;
			
			if(controls.components[6].getPollData() > 0 && !isShooting && !introEnable && !nextLevel && !resetLevel && !pacDeath && !gameOver)
			{
				isShooting = true;
				pacMan.fire();
			}
		}
		
		else if(!isPressed && State == STATE.HIGH_SCORE)
		{
			if(controls.components[9].getPollData() > 0)
			{
				isPressed = true;
				if(highScoreState.selectedInitial == 0)
				{
					if(highScoreState.selectedValue0 == 25)
						highScoreState.selectedValue0 = 0;
					else
						highScoreState.selectedValue0++;
				}
				
				else if(highScoreState.selectedInitial == 1)
				{
					if(highScoreState.selectedValue1 == 25)
						highScoreState.selectedValue1 = 0;
					else
						highScoreState.selectedValue1++;
				}
				
				else if(highScoreState.selectedInitial == 2)
				{
					if(highScoreState.selectedValue2 == 25)
						highScoreState.selectedValue2 = 0;
					else
						highScoreState.selectedValue2++;
				}
			}
			
			else if(controls.components[8].getPollData() > 0)
			{
				isPressed = true;
				if(highScoreState.selectedInitial < 2)
					highScoreState.selectedInitial++;
			}
			
			else if(controls.components[10].getPollData() > 0)
			{
				isPressed = true;
				if(highScoreState.selectedInitial > 0)
					highScoreState.selectedInitial--;
			}
			
			else if(controls.components[11].getPollData() > 0)
			{
				isPressed = true;
				if(highScoreState.selectedInitial == 0)
				{
					if(highScoreState.selectedValue0 == 0)
						highScoreState.selectedValue0 = 25;
					else
						highScoreState.selectedValue0--;
				}
				
				else if(highScoreState.selectedInitial == 1)
				{
					if(highScoreState.selectedValue1 == 0)
						highScoreState.selectedValue1 = 25;
					else
						highScoreState.selectedValue1--;
				}
				
				else if(highScoreState.selectedInitial == 2)
				{
					if(highScoreState.selectedValue2 == 0)
						highScoreState.selectedValue2 = 25;
					else
						highScoreState.selectedValue2--;
				}
			}
			
			else if(controls.components[6].getPollData() > 0)
				highScoreState.saveScore();
		}
		
		if(State == STATE.HIGH_SCORE && highScoreState.saving)
		{
			if(highScoreState.savingCount > 0)
				highScoreState.savingCount--;
			else
			{
				resetGame();
				highScoreState.savingCount = 180;
				State = STATE.MAIN_MENU;
				audio.soundEffects.get("highScore").stop();
			}
		}
		
		else if(State == STATE.PAUSED)
		{
			if(controls.components[7].getPollData() > 0 && !isPressed)
			{
				isPressed = true;
				State = STATE.GAME;
			}
		}
	}

	private void checkXboxController()
	{
		if(controls.components[5].getPollData() == 0 &&
				controls.components[6].getPollData() == 0 &&
				controls.components[7].getPollData() == 0 &&
				controls.components[8].getPollData() == 0)
			isPressed = false;
		
		
		if(State == STATE.MAIN_MENU)
		{
			if(controls.components[6].getPollData() > 0 && mainMenu.selected == 0 && !isPressed)
			{
				isPressed = true;
				State = STATE.MODE_SELECT;
				audio.getSound("menu_scroll").playAsSoundEffect(1.0f, 1.0f, false);
			}			
			
			// Do Stuff with rest of controls here...
		}
		
		else if(!isPressed && State == STATE.MODE_SELECT)
		{
			if(controls.components[6].getPollData() > 0 && modeSelect.selected == "classic")
			{
				isPressed = true;
				delta = 0;
				State = STATE.GAME;
				audio.getSound("game_start").playAsSoundEffect(1.0f, 1.0f, false);
				dotCount = 244;
			}
			
			else if(controls.components[6].getPollData() > 0 && modeSelect.selected == "custom")
			{
				isPressed = true;
				audio.getSound("menu_scroll").playAsSoundEffect(1.0f, 1.0f, false);
				State = STATE.CUSTOM_MENU;
			}
			
			else if(controls.components[7].getPollData() > 0)
			{
				isPressed = true;
				mainMenu.selected = 0;
				State = STATE.MAIN_MENU;
				audio.getSound("menu_scroll").playAsSoundEffect(1.0f, 1.0f, false);
			}
			
			else if(controls.components[5].getPollData() > 0 && modeSelect.selected == "classic")
			{
				isPressed = true;
				modeSelect.selected = "custom";
				audio.getSound("menu_scroll").playAsSoundEffect(1.0f, 1.0f, false);
			}
			
			else if(controls.components[8].getPollData() > 0 && modeSelect.selected == "custom")
			{
				isPressed = true;
				modeSelect.selected = "classic";
				audio.getSound("menu_scroll").playAsSoundEffect(1.0f, 1.0f, false);
			}
		}
		
		else if(!isPressed && State == STATE.CUSTOM_MENU)
		{
			if(controls.components[6].getPollData() > 0)
			{
				isPressed = true;
				if(customGame.menuSelected == 0 && customGame.startLivesSelected < 2){customGame.startLivesSelected++;}
				else if(customGame.menuSelected == 1 && customGame.extraLivesSelected < 3){customGame.extraLivesSelected++;}
				else if(customGame.menuSelected == 2 && !customGame.bomb){customGame.bomb = true;}
				else if(customGame.menuSelected == 3 && !customGame.laser){customGame.laser = true;}
				else if(customGame.menuSelected == 4 && !customGame.stinkyEnabled){customGame.stinkyEnabled = true;}
				else if(customGame.menuSelected == 5 && !customGame.DK){customGame.DK = true;}
				else if(customGame.menuSelected == 6 && !customGame.centipede){customGame.centipede = true;}
				else if(customGame.menuSelected == 7 && !customGame.ufo){customGame.ufo = true;}
				else if(customGame.menuSelected == 8)
				{
					lives = customGame.startLivesOptions[customGame.startLivesSelected];
					displayLives = lives;
					livesPoints = customGame.extraLivesOptions[customGame.extraLivesSelected];
					audio.soundEffects.get("game_start").playAsSoundEffect(1.0f, 1.0f, false);
					if(customGame.stinkyEnabled)
					{
						stinky = new Ghost("stinky", "scatter", "left", 776, 500, 13, 17, 2, textures, this);
						stinky.aS = new AStar(stinky.i, stinky.j, 15, 22, this, stinky);
						stinky.path = stinky.aS.algorithm();
						stinky.target[0] = stinky.path.peek().i;
						stinky.target[1] = stinky.path.pop().j;
						c.ghosts.add(stinky);
					}
					State = STATE.GAME;
				}
			}
			
			else if(controls.components[5].getPollData() > 0 && customGame.menuSelected < 8){isPressed = true; customGame.menuSelected++;}
			else if(controls.components[7].getPollData() > 0 && !isPressed)
			{
				isPressed = true;
				if(customGame.menuSelected == 0 && customGame.startLivesSelected > 0){customGame.startLivesSelected--;}
				else if(customGame.menuSelected == 1 && customGame.extraLivesSelected > 0){customGame.extraLivesSelected--;}
				else if(customGame.menuSelected == 2 && customGame.bomb){customGame.bomb = false;}
				else if(customGame.menuSelected == 3 && customGame.laser){customGame.laser = false;}
				else if(customGame.menuSelected == 4 && customGame.stinkyEnabled){customGame.stinkyEnabled = false;}
				else if(customGame.menuSelected == 5 && customGame.DK){customGame.DK = false;}
				else if(customGame.menuSelected == 6 && customGame.centipede){customGame.centipede = false;}
				else if(customGame.menuSelected == 7 && customGame.ufo){customGame.ufo = false;}
			}
			else if(controls.components[8].getPollData() > 0 && customGame.menuSelected > 0 && !isPressed){isPressed = true; customGame.menuSelected--;}
		}
		
		else if(State == STATE.GAME)
		{
			if(controls.components[6].getPollData() > 0 && Physics.checkDirection("right", pacMan, maze))
			{
				pacMan.prevDirection = pacMan.getDirection();
				if(((int)(pacMan.x+4) % 24 <= 1 && (int)(pacMan.y+4) % 24 <= 1))
					pacMan.setDirection("right");
				else if(pacMan.prevDirection == "up" && pacMan.y+4 > maze.map[pacMan.j][pacMan.i+1].y)
					pacMan.setDirection("uRight");
				else if(pacMan.prevDirection == "down" && pacMan.y+4 < maze.map[pacMan.j][pacMan.i+1].y)
					pacMan.setDirection("dRight");
				
				// These conditionals check if Pac-Man has surpassed the junction, and will move back if within 12 pixels of it
				// i.e. Half a tile's width
				else if(pacMan.prevDirection == "up" && pacMan.y+4 > maze.map[pacMan.j][pacMan.i+1].y-12)
					pacMan.setDirection("dRight");
				
				else if(pacMan.prevDirection == "down" && pacMan.y+4 < maze.map[pacMan.j][pacMan.i+1].y+12)
					pacMan.setDirection("uRight");
			}
			
			else if(controls.components[5].getPollData() > 0 && Physics.checkDirection("down", pacMan, maze))
			{
				pacMan.prevDirection = pacMan.getDirection();
				if(((int)(pacMan.x+4) % 24 <= 1 && (int)(pacMan.y+4) % 24 <= 1))
					pacMan.setDirection("down");
				else if(pacMan.prevDirection == "left" && pacMan.x+4 > maze.map[pacMan.j+1][pacMan.i].x)
				{
					pacMan.setDirection("lDown");
				}
				else if(pacMan.prevDirection == "right" && pacMan.x+4 < maze.map[pacMan.j+1][pacMan.i].x)
				{
					pacMan.setDirection("rDown");
				}
				
				// These conditionals check if Pac-Man has surpassed the junction, and will move back if within 12 pixels of it
				// i.e. Half a tile's width
				else if(pacMan.prevDirection == "left" && pacMan.x+4 > maze.map[pacMan.j+1][pacMan.i].x-12)
				{
					pacMan.setDirection("rDown");
				}
				
				else if(pacMan.prevDirection == "right" && pacMan.x+4 < maze.map[pacMan.j+1][pacMan.i].x+12)
				{
					pacMan.setDirection("lDown");
				}
			}
			else if(controls.components[7].getPollData() > 0 && Physics.checkDirection("left", pacMan, maze))
			{
				pacMan.prevDirection = pacMan.getDirection();
				if(((int)(pacMan.x+4) % 24 <= 1 && (int)(pacMan.y+4) % 24 <= 1))
					pacMan.setDirection("left");
				else if(pacMan.prevDirection == "up" && pacMan.y+4 > maze.map[pacMan.j][pacMan.i-1].y)
					pacMan.setDirection("uLeft");
				else if(pacMan.prevDirection == "down" && pacMan.y+4 < maze.map[pacMan.j][pacMan.i-1].y)
					pacMan.setDirection("dLeft");
				
				
				else if(pacMan.prevDirection == "up" && pacMan.y+4 > maze.map[pacMan.j][pacMan.i-1].y-12)
					pacMan.setDirection("dLeft");
				else if(pacMan.prevDirection == "down" && pacMan.y+4 < maze.map[pacMan.j][pacMan.i-1].y+12)
					pacMan.setDirection("uLeft");
			}
			
			else if(controls.components[8].getPollData() > 0 && Physics.checkDirection("up", pacMan, maze))
			{
				pacMan.prevDirection = pacMan.getDirection();
				if(((int)(pacMan.x+4) % 24 <= 1 && (int)(pacMan.y+4) % 24 <= 1))
					pacMan.setDirection("up");
				else if(pacMan.prevDirection == "right" && pacMan.x+4 < maze.map[pacMan.j-1][pacMan.i].x)
					pacMan.setDirection("rUp");
					
				else if(pacMan.prevDirection == "left" && pacMan.x+4 > maze.map[pacMan.j-1][pacMan.i].x)
					pacMan.setDirection("lUp");
				
				//
				else if(pacMan.prevDirection == "right" && pacMan.x+4 < maze.map[pacMan.j-1][pacMan.i].x+12)
				{
					dirCooldown = 0;
					pacMan.setDirection("lUp");
				}
				else if(pacMan.prevDirection == "left" && pacMan.x+4 > maze.map[pacMan.j-1][pacMan.i].x-12)
					pacMan.setDirection("rUp");
			}
			
			if(controls.components[11].getPollData() == 0 && isShooting)
				isShooting = false;
			
			if(controls.components[11].getPollData() > 0 && !isShooting && !isShooting && !introEnable && !nextLevel && !resetLevel && !pacDeath && !gameOver)
			{
				isShooting = true;
				pacMan.fire();
			}
		}
		
		else if(!isPressed && State == STATE.HIGH_SCORE)
		{
			if(controls.components[5].getPollData() > 0)
			{
				isPressed = true;
				if(highScoreState.selectedInitial == 0)
				{
					if(highScoreState.selectedValue0 == 25)
						highScoreState.selectedValue0 = 0;
					else
						highScoreState.selectedValue0++;
				}
				
				else if(highScoreState.selectedInitial == 1)
				{
					if(highScoreState.selectedValue1 == 25)
						highScoreState.selectedValue1 = 0;
					else
						highScoreState.selectedValue1++;
				}
				
				else if(highScoreState.selectedInitial == 2)
				{
					if(highScoreState.selectedValue2 == 25)
						highScoreState.selectedValue2 = 0;
					else
						highScoreState.selectedValue2++;
				}
			}
			
			else if(controls.components[6].getPollData() > 0)
			{
				isPressed = true;
				if(highScoreState.selectedInitial < 2)
					highScoreState.selectedInitial++;
			}
			
			else if(controls.components[7].getPollData() > 0)
			{
				isPressed = true;
				if(highScoreState.selectedInitial > 0)
					highScoreState.selectedInitial--;
			}
			
			else if(controls.components[8].getPollData() > 0)
			{
				isPressed = true;
				if(highScoreState.selectedInitial == 0)
				{
					if(highScoreState.selectedValue0 == 0)
						highScoreState.selectedValue0 = 25;
					else
						highScoreState.selectedValue0--;
				}
				
				else if(highScoreState.selectedInitial == 1)
				{
					if(highScoreState.selectedValue1 == 0)
						highScoreState.selectedValue1 = 25;
					else
						highScoreState.selectedValue1--;
				}
				
				else if(highScoreState.selectedInitial == 2)
				{
					if(highScoreState.selectedValue2 == 0)
						highScoreState.selectedValue2 = 25;
					else
						highScoreState.selectedValue2--;
				}
			}
			
			else if(controls.components[11].getPollData() > 0)
				highScoreState.saveScore();
			
			if(State == STATE.HIGH_SCORE && highScoreState.saving)
			{
				if(highScoreState.savingCount > 0)
					highScoreState.savingCount--;
				else
				{
					resetGame();
					highScoreState.savingCount = 180;
					State = STATE.MAIN_MENU;
					audio.soundEffects.get("highScore").stop();
				}
			}
		}			
	}
	
	// Update everything in the game
	private void tick()
	{	
		if(State == STATE.MAIN_MENU)
		{
			if(!audio.soundEffects.get("titleMusic").isPlaying())
				audio.soundEffects.get("titleMusic").playAsMusic(1.0f, 1.0f, true);
		}
		
		else if(State == STATE.GAME)
		{			
			if(audio.soundEffects.get("titleMusic").isPlaying())
				audio.soundEffects.get("titleMusic").stop();
			
			if(levelCleared)
			{
				// A level has been cleared. Make the maze flash blue/white for a few seconds before resetting it for the next level
				if(levelClearedTransitionCount <= 0)
				{
					if(blueMapCount > 0)
					{
						whiteMapCount = 0;
						if(!isBlue)
						{
							isWhite = false;
							isBlue = true;
							int temp;
							for(int i = 0; i < 31; i++)
								for(int j = 0; j < 28; j++)
								{
									temp = maze.map[i][j].index;
									if(temp == 38 || temp == 29 || temp == 50)
										continue;
									maze.map[i][j].icon = textures.mazeTiles.get(temp);
								}
						}
						blueMapCount--;
						if(blueMapCount == 0)
							whiteMapCount = 30;
					}
					
					else if(whiteMapCount > 0)
					{
						blueMapCount = 0;
						if(!isWhite)
						{
							isBlue = false;
							isWhite = true;
							int temp;
							for(int i = 0; i < 31; i++)
								for(int j = 0; j < 28; j++)
								{
									temp = maze.map[i][j].index;
									if(temp == 38 || temp == 29 || temp == 50)
										continue;
									else maze.map[i][j].icon = textures.mazeTilesWhite.get(temp);
								}
						}
						whiteMapCount--;
						if(whiteMapCount == 0)
							blueMapCount = 30;
					}
				}
				levelClearedTransitionCount--;
				if(levelClearedTransitionCount == -240)
				{
					levelClearedTransitionCount = 60;
					if(isBlue)
						levelCleared = false;
					nextLevel();
					nextLevel = true;
					blinky.icon = blinky.getNextIcon();
					pinky.icon = pinky.getNextIcon();
					inky.icon = inky.getNextIcon();
					clyde.icon = clyde.getNextIcon();
				}
					
			}
			
			else if(gameOver)
			{
				if(gameOverTickCount == 0)
				{
					if(score > highScore && !isCustomGame)
					{
						highScoreState = new HighScore(textures, audio, score);
						State = STATE.HIGH_SCORE;
					}
					else
					{
						resetGame();
						State = STATE.MAIN_MENU;
					}
				}
				gameOverTickCount--;
			}
			
			// Normal tick - don't let all the conditionals get confusing
			else if(!introEnable && !levelCleared && !pacDeath && !ghostEaten && !resetLevel && !gameOver && !nextLevel)
			{	
				// If any ghosts are frightened or exploded, stop the background noise
				if(frightenedGhost() || explodedGhost())
				{
					stopBackgroundNoise();
					
					if((returningGhost() || explodedGhost()) && !audio.soundEffects.get("ghostReturning").isPlaying())
					{
						stopFrightenedNoise();
						audio.soundEffects.get("ghostReturning").playAsSoundEffect(1.0f, 1.0f, true);
					}
					else if((!explodedGhost() && !returningGhost()) && !audio.soundEffects.get("ghostsFrightened").isPlaying())
					{
						stopReturningNoise();
						audio.soundEffects.get("ghostsFrightened").playAsSoundEffect(1.0f, 1.0f, true);
					}
				}
				
				else if(!frightenedGhost() && audio.soundEffects.get("ghostsFrightened").isPlaying())
					audio.soundEffects.get("ghostsFrightened").stop();
				
				else if(!frightenedGhost() && !returningGhost())
					playBackgroundNoise();
				
				c.tick();
				
				if(dotCount == powerUpSpawnTime && !powerUpFlag && (customGame.bomb || customGame.laser))
				{
					powerUp = new Powerup(this, 0, 0, 0);
					powerUpActive = true;
					powerUpFlag = true;
					
					int randomI = ThreadLocalRandom.current().nextInt(1, 28);
					int randomJ = ThreadLocalRandom.current().nextInt(1, 30);
					
					while(!(maze.map[randomJ][randomI].isBlank || maze.map[randomJ][randomI].isDot) || maze.map[randomJ][randomI].isWall)
					{
						// Can't spawn where it suggested. Try another grid reference
						randomI = ThreadLocalRandom.current().nextInt(1, 28);
						randomJ = ThreadLocalRandom.current().nextInt(1, 30);
					}
					
					powerUp.x = maze.map[randomJ][randomI].x;
					powerUp.y = maze.map[randomJ][randomI].y;
				}
				
				if(dotCount == centipedeSpawnTime && customGame.centipede)
				{
					ch = new CentipedeHead(this);
					centipedeActive = true;
					centipedeFlag = true;
				}
				
				if(dotCount == ufoSpawnTime && customGame.ufo)
				{
					ufo = new UFO(this);
					ufoActive = true;
					ufoFlag = true;
				}
				
				if(dotCount == 174 && !fruitFlag)
				{
					fruitActive = true;
					fruitFlag = true;
				}
				if(dotCount <= 214 && inky.dormantTickCount == 0)
				{
					inky.speed = 1;
					inky.state = "released";
					inky.dormantTickCount = 120;
				}
				else if(dotCount <= 161 && clyde.dormantTickCount == 0)
				{
					clyde.speed = 1;
					clyde.state = "released";
					clyde.dormantTickCount = 240;
				}
				
				if(fruitActive)
				{
					fruit.tick();
					if(fruitActiveCount > 0)
						fruitActiveCount--;
					else
						fruitActive = false;
				}
				
				if(powerUpActive)
				{
					powerUp.tick();
					if(powerUpActiveCount > 0)
						powerUpActiveCount--;
					else
						powerUpActive = false;
				}
				
				if(playerExtraTickCount == 0)
				{
					pacMan.tick();
					if(dotCount <= 16)
						playerExtraTickCount = 7;
					playerExtraTickCount = 5;
				}
				else
					playerExtraTickCount--;
				
				if(ghostExtraTickCount == 0)
				{
					if(dotCount > 128)
						ghostExtraTickCount = 7;
					else if(dotCount > 64)
						ghostExtraTickCount = 6;
					else if(dotCount > 32)
						ghostExtraTickCount = 5;
					else if(dotCount > 16)
						ghostExtraTickCount = 4;
					else if(dotCount > 0)
						ghostExtraTickCount = 3;
					
					if(blinky.state != "frightened")
					{
						blinky.secondaryTick = true;
						blinky.tick();						
						blinky.secondaryTick = false;
					}
					
					if(pinky.state != "frightened")
					{
						pinky.secondaryTick = true;
						pinky.tick();
						pinky.secondaryTick = false;
					}
					
					if(inky.state != "frightened")
					{
						inky.secondaryTick = true;
						inky.tick();
						inky.secondaryTick = false;
					}
					
					if(clyde.state != "frightened")
					{
						clyde.secondaryTick = true;
						clyde.tick();
						clyde.secondaryTick = false;
					}
				}				
				else ghostExtraTickCount--;
			}
			
			
			
			else if(pacDeath)
			{
				if(pacDeathCount > 0)
					pacDeathCount--;
				else if(pacDeathCount >= -1)
				{
					if(barrelDeath)
						audio.soundEffects.get("pacDeath_dk").playAsSoundEffect(1.0f, 1.0f, false);
					else if(laserDeath)
						audio.soundEffects.get("pacDeath_laser").playAsSoundEffect(1.0f, 1.0f, false);
					else
						audio.soundEffects.get("pacDeath").playAsSoundEffect(1.0f, 1.0f, false);
					barrelDeath = false;
				}
				
				// Animation for Pac-Man's death
				// Instead of resetting the tick count, just run into negatives
				if(pacDeathCount <= 0)
				{
					if(!laserDeath)
					{
						if(pacDeathCount > -8)
						{
							fruitActive = false;
							pacMan.icon = textures.pacDeath0;
						}
						else if(pacDeathCount > -16)
							pacMan.icon = textures.pacDeath1;
						else if(pacDeathCount > -24)
							pacMan.icon = textures.pacDeath2;
						else if(pacDeathCount > -32)
							pacMan.icon = textures.pacDeath3;
						else if(pacDeathCount > -40)
							pacMan.icon = textures.pacDeath4;
						else if(pacDeathCount > -48)
							pacMan.icon = textures.pacDeath5;
						else if(pacDeathCount > -56)
							pacMan.icon = textures.pacDeath6;
						else if(pacDeathCount > -64)
							pacMan.icon = textures.pacDeath7;
						else if(pacDeathCount > -72)
							pacMan.icon = textures.pacDeath8;
						else if(pacDeathCount > -80)
							pacMan.icon = textures.pacDeath9;
						else if(pacDeathCount < -96)
							pacMan.icon = null;
					}
					
					if(laserDeath)
					{
						if(pacDeathCount > -8)
						{
							pacMan.icon = textures.explosion0;
							fruitActive = false;
							if(pacDeathCount == 0)
							{
							pacMan.x -= 14;
							pacMan.y -= 14;
							}
						}
						
						else if(pacDeathCount > -12)
							pacMan.icon = textures.explosion1;
						else if(pacDeathCount > -16)
							pacMan.icon = textures.explosion2;
						else if(pacDeathCount > -20)
							pacMan.icon = textures.explosion3;
						else if(pacDeathCount > -24)
							pacMan.icon = textures.explosion4;
						else if(pacDeathCount > -28)
							pacMan.icon = textures.explosion5;
						else if(pacDeathCount > -32)
							pacMan.icon = textures.explosion6;
						else if(pacDeathCount > -36)
							pacMan.icon = textures.explosion7;
						else
							pacMan.icon = null;
						
					}
					
					if(pacDeathCount < -130)
					{
						pacDeathCount = 120;
						pacDeath = false;
						if(lives > 1)
						{
							resetLevel = true;
							resetLevel();
						}
						else
							gameOver = true;
					}
					pacDeathCount--;
				}
			}
			
			else if(ghostEaten)
			{
				if(ghostEatenCount == 0)
				{
					if(blinky.state == "eaten"){blinky.state = "returning";}
					if(pinky.state == "eaten"){pinky.state = "returning";}
					if(inky.state == "eaten"){inky.state = "returning";}
					if(clyde.state == "eaten"){clyde.state = "returning";}
					ghostEaten = false;
					ghostEatenCount = 60;
				}
				else
				{
					ghostEatenCount--;
				}
			}
			
			else if(resetLevel)
			{
				if(resetLevelCount == 0)
				{
					resetLevelCount = 120;
					resetLevel = false;
					nextLevel = false;
				}
				
				else
					resetLevelCount--;
			}
			
			if(nextLevel)
			{
				if(nextLevelCount > 0)
					nextLevelCount--;
				else
				{
					nextLevel = false;
					nextLevelCount = 240;
				}
			}
			
			if(customGame.DK && !pacDeath)
				dk.tick();
			if(centipedeActive && !pacDeath)
				ch.tick();
			if(ufoActive && !pacDeath)
			{
				ufo.tick();
				if(ufo.x == 1250)
				{
					ufoActive = false;
					ufoFlag = true;
					audio.soundEffects.get("mysteryShip").stop();
				}
				
				else if(ufo.shotCount == 0)
				{
					ufoActive = false;
					ufoFlag = true;
					ufo = null;
				}
			}
		}
	}
	
	// Render and re-render everything in the game
	private void render()
	{
		BufferStrategy bs = this.getBufferStrategy();
		if(bs == null)
		{
			// Create 3 buffers for triple buffering
			createBufferStrategy(3);
			return;
		}
		
		Graphics2D g = (Graphics2D) bs.getDrawGraphics();	
		
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, 768, 1366);
		g.drawImage(bg, 0, 0, WIDTH, HEIGHT, this);
		
		if(State == STATE.GAME || State == STATE.PAUSED)
		{
			g.setFont(font20);
			g.setColor(Color.white);
			if(!isCustomGame)
			{
				g.drawString("HI", 456, 65);
				g.drawString(initials + "  " + highScore, 471, 90);
			}
			g.drawString("SCORE", 800, 65);
			g.drawString(Integer.toString(score), 815, 90);
			
			// Draw HUD elements
			int tempX = 500;
			for(int i = 0; i < displayLives; i++)
			{
				g.drawImage(textures.lPlayer1, tempX, 850, null);
				tempX+=50;
			}
			
			tempX = 1050;
			for(int i = 0; i < fruitHUD.length; i++)
			{
				if(fruitHUD[i] != null)
					g.drawImage(fruitHUD[i], tempX, 850, null);
				tempX-=50;
			}
			
			if(pacMan.weapon == "bomb" && pacMan.ammo > 0)
			{
				g.setFont(font20);
				g.drawImage(textures.bomb0, 1300, 668, null);
				g.drawString("Ammo: " + pacMan.ammo, 1332, 700);
			}
			
			else if(pacMan.weapon == "laser" && pacMan.ammo > 0)
			{
				g.setFont(font20);
				g.drawImage(textures.laser, 1300, 668, null);
				g.drawString("Ammo: " + pacMan.ammo, 1332, 700);
			}
			
			maze.render(g);
			if(gameOver)
				g.drawImage(textures.gameOver, 667, 503, null);
			
			else if(levelCleared && levelClearedTransitionCount == 0)
				maze.render(g);
					
			else if(introEnable)
			{
				g.drawImage(textures.ready, 704, 500, null);
				if(introTickCount == 0){displayLives--;}
				if(introTickCount <= 0)
					c.render(g);
				
				if(introTickCount <= -240)
					introEnable = false;
				introTickCount--;
			}
			
			else if(!introEnable && !pacDeath && !ghostEaten && !nextLevel ) // Normal tick render
			{
				if(powerUpActive)
					powerUp.render(g);
				if(fruitActive)
					fruit.render(g);
				c.render(g);
				
				g.setColor(Color.black);
				g.fillRect(360, 48, 96, 768);
				g.fillRect(1128, 48, 120, 768);
				
				if(customGame.DK)
					dk.render(g);
				
				if(centipedeActive)
					ch.render(g);
				
				if(ufoActive)
					ufo.render(g);
								
			}
			
			else if(pacDeath)
			{
				pacMan.render(g);
				if(pacDeathCount > 0)
				{
					c.render(g);
					if(customGame.stinkyEnabled)
						stinky.render(g);
				}
				if(fruitActive)
					fruit.render(g);
				maze.render(g);
				g.setColor(Color.black);
				g.fillRect(360, 48, 96, 768);
				g.fillRect(1128, 48, 120, 768);
				if(customGame.DK)
				{
					dk.barrel = null;
					dk.render(g);
				}
				if(centipedeActive)
					ch.render(g);
			}
			
			else if(ghostEaten)
			{
				maze.render(g);
				c.render(g);
				if(customGame.stinkyEnabled)
					stinky.render(g);
				if(fruitActive)
					fruit.render(g);
				g.setColor(Color.black);
				g.fillRect(360, 48, 96, 768);
				g.fillRect(1128, 48, 120, 768);
				
				if(centipedeActive)
					ch.render(g);
				
				if(customGame.DK)
					dk.render(g);
				
				
			}
			
			else if(nextLevel)
			{
				maze.render(g);
				c.render(g);
				if(customGame.DK)
					dk.render(g);
			}
			
			if(bombActive)
				pacMan.bomb.render(g);
			
			if(DEBUG)
			{
				g.setColor(Color.white);
				g.drawString("BLINKY: ", 1300, 200);
				g.drawString("State: " + blinky.state, 1300, 250);
				g.drawString("i, j: " + blinky.i + ", " + blinky.j, 1300, 300);
				g.drawString("target i, j: " + blinky.target[0] + ", " + blinky.target[1], 1300, 350);
				g.drawString("isGrid: " + blinky.isGrid, 1300, 400);
				g.drawString("Phase: " + blinky.phase, 1300, 450);
				
				g.drawString("PINKY: ", 50, 200);
				g.drawString("State: " + pinky.state, 50, 250);
				g.drawString("i, j: " + pinky.i + ", " + pinky.j, 50, 300);
				g.drawString("target i, j: " + pinky.target[0] + ", " + pinky.target[1], 50, 350);
				g.drawString("isGrid: " + pinky.isGrid, 50, 400);
				g.drawString("Phase: " + pinky.phase, 50, 450);
				
				g.drawString("CLYDE: ", 50, 650);
				g.drawString("State: " + clyde.state, 50, 700);
				g.drawString("i, j: " + clyde.i + ", " + clyde.j, 50, 750);
				g.drawString("target i, j: " + clyde.target[0] + ", " + clyde.target[1], 50, 800);
				g.drawString("isGrid: " + clyde.isGrid, 50, 850);
				g.drawString("Phase: " + clyde.phase, 50, 900);
				
				g.drawString("INKY: ", 1300, 650);
				g.drawString("State: " + inky.state, 1300, 700);
				g.drawString("i, j: " + inky.i + ", " + inky.j, 1300, 750);
				g.drawString("target i, j: " + inky.target[0] + ", " + inky.target[1], 1300, 800);
				g.drawString("isGrid: " + inky.isGrid, 1300, 850);
				g.drawString("Phase: " + inky.phase, 1300, 900);
			}
			
			if(State == STATE.PAUSED)
			{
				Color trans = new Color(0, 0, 0, 128);
				g.setColor(trans);
				g.fillRect(0,  0, 1715, 970);
			}
		}
		
		else if(State == STATE.MAIN_MENU)
		{
			try
			{
				mainMenu.render(g);
			} catch (FontFormatException | IOException e)
			{e.printStackTrace();}		
		}
		
		else if(State == STATE.MODE_SELECT)
		{
			try
			{
				modeSelect.render(g);
			} catch (FontFormatException | IOException e)
			{e.printStackTrace();}			
		}
		
		else if(State == STATE.CUSTOM_MENU)
		{
			try
			{
				customGame.render(g);
			} catch (FontFormatException | IOException e)
			{e.printStackTrace();}
		}
		
		else if(State == STATE.HIGH_SCORE)
		{
			highScoreState.render(g);
		}
		
		g.dispose();
		bs.show();
	}
	
	private boolean frightenedGhost()
	{
		if(blinky.state == "frightened" || pinky.state == "frightened" || inky.state == "frightened" || clyde.state == "frightened")
			return true;
		return false;
	}
	
	private boolean returningGhost()
	{
		if(blinky.state == "returning" || blinky.state == "returned" ||
				pinky.state == "returning" || pinky.state == "returned" ||
				inky.state == "returning" || inky.state == "returned" ||
				clyde.state == "returning" || clyde.state == "returned")
			return true;
		else
			return false;
	}
	
	private boolean explodedGhost()
	{
		if(blinky.state == "exploded" ||
				pinky.state == "exploded" ||
				inky.state == "exploded" ||
				clyde.state == "exploded")
			return true;
		else
			return false;
	}
	
	private void initialisePhaseTimers()
	{
		// 24 ticks = 1/60th second
		// 300 ticks = 5 seconds
		// 420 ticks = 7 seconds
		// 1200 = 20 seconds
		// 61980 ticks = 1033 seconds
		// 62220 ticks = 1037
		
		// Initialise Scatter Phase Timers - [Leve][Phase]
		
		// Level 1
		scatterPhaseTickCounts[0][0] = 420;
		scatterPhaseTickCounts[0][1] = 420;
		scatterPhaseTickCounts[0][2] = 300;
		scatterPhaseTickCounts[0][3] = 300;
		
		// Levels 2, 3 and 4
		scatterPhaseTickCounts[1][0] = 420;
		scatterPhaseTickCounts[1][1] = 420;
		scatterPhaseTickCounts[1][2] = 300;
		scatterPhaseTickCounts[1][3] = 24;
		
		// Level 5 and onwards
		scatterPhaseTickCounts[2][0] = 300;
		scatterPhaseTickCounts[2][1] = 300;
		scatterPhaseTickCounts[2][2] = 300;
		scatterPhaseTickCounts[2][3] = 24;
		
		
		
		// Initialise Chase Phase Timers
		// Level 1
		chasePhaseTickCounts[0][0] = 1200;
		chasePhaseTickCounts[0][1] = 1200;
		chasePhaseTickCounts[0][2] = 1200;
		chasePhaseTickCounts[0][3] = 1000000000; // Actually supposed to be infinite, but this works as an alternative :)
		
		// Levels 2, 3 and 4
		chasePhaseTickCounts[1][0] = 1200;
		chasePhaseTickCounts[1][1] = 1200;
		chasePhaseTickCounts[1][2] = 62220;
		chasePhaseTickCounts[1][3] = 1000000000;
		
		// Level 5 onwards
		chasePhaseTickCounts[2][0] = 1200;
		chasePhaseTickCounts[2][1] = 1200;
		chasePhaseTickCounts[2][2] = 1200;
		chasePhaseTickCounts[2][3] = 1000000000;
		
		
		
		// Initialise Frighten Counters
		// Level 1 (6 Seconds)
		frightenedTickCounts[0] = 360;
		
		// Levels 2, 6 and 10 (5 Seconds)
		frightenedTickCounts[1] = 300;
		
		// Level 3 (4 Seconds)
		frightenedTickCounts[2] = 240;
		
		// Levels 4 and 14 (3 Seconds)
		frightenedTickCounts[3] = 180;
		
		// Levels 5, 7, 8 and 11 (2 Seconds)
		frightenedTickCounts[4] = 120;
		
		// Levels 9, 12, 13, 15, 16 and 18 (1 Second)
		frightenedTickCounts[5] = 60;
		
		// Levels 17 and 19 are instant and have a 0 second timer, so the Ghost will just be made to immediately change to the opposite direction in this case.
		
	}
	
	private void  nextLevel()
	{
		level++;
		inky.x = 728;	inky.y = 428;	inky.direction = "up";	inky.state = "dormant"; inky.dormantDirectionTickCount = 20; inky.icon = inky.getNextIcon();
		inky.speed = 2; inky.phase = 0; if(inky.path != null || !inky.path.isEmpty()){inky.path.clear();}
		inky.resetChaseTicks(); inky.resetFrightenedTicks(); inky.resetScatterTicks();
		
		blinky.x = 776;	blinky.y = 356;	blinky.direction = "left";	blinky.state = "scatter"; blinky.dormantDirectionTickCount = 20;
		blinky.i = 13; blinky.j = 11; if(blinky.path != null || !blinky.path.isEmpty())blinky.path.clear(); blinky.newScatterPath(); blinky.speed = 2; blinky.phase = 0;
		blinky.resetChaseTicks(); blinky.resetFrightenedTicks(); blinky.resetScatterTicks();
		
		pinky.x = 776; pinky.y = 428; 	pinky.direction = "down";	pinky.state = "released"; pinky.dormantDirectionTickCount = 20; 
		pinky.speed = 1; pinky.phase = 0; if(pinky.path != null || !pinky.path.isEmpty())pinky.path.clear();
		pinky.resetChaseTicks(); pinky.resetFrightenedTicks(); pinky.resetScatterTicks();
		
		clyde.x = 826;	clyde.y = 428;	clyde.direction = "up";	clyde.state = "dormant"; clyde.path.clear(); clyde.dormantDirectionTickCount = 20;
		clyde.icon = clyde.getNextIcon(); if(clyde.path != null || !clyde.path.isEmpty())clyde.path.clear(); clyde.speed = 1; clyde.phase = 0; clyde.path.clear();
		clyde.resetChaseTicks(); clyde.resetFrightenedTicks(); clyde.resetScatterTicks();
		
		if(customGame.stinkyEnabled)
		{
			c.ghosts.remove(stinky);
			stinky = new Ghost("stinky", "scatter", "left", 776, 500, 13, 17, 2, textures, this);
			stinky.aS = new AStar(stinky.i, stinky.j, 15, 22, this, stinky);
			stinky.path = stinky.aS.algorithm();
			stinky.target[0] = stinky.path.peek().i;
			stinky.target[1] = stinky.path.pop().j;
			c.ghosts.add(stinky);
		}
		infectedPowerPellets = 0;
		
		pacMan.x = 776;
		pacMan.y = 644;
		pacMan.icon = textures.rPlayer2;
		pacMan.setDirection("left");
		pacMan.slowed = false;
		
		dk.barrelThrowTickCount = 60;
		dk.barrel = null;
		dk.icon = textures.dkNormal1;
		dk.throwing = false;
		
		// Remove all currently active Bullets and Bombs
		c.entities.clear();
		c.entities.add(pacMan);
		
		centipedeActive = false;
		centipedeSpawnTime = ThreadLocalRandom.current().nextInt(50, 150);
		ch = null;
		
		fruitActiveCount = 630;
		fruitActive = false;
		fruitFlag = false;
		if(level == 1)
		{
			fruitHUD[1] = textures.strawberry;
			fruit = new Fruit(this, "strawberry", 776, 500, 0, textures.strawberry);
		}
		else if(level == 2)
		{
			fruitHUD[2] = textures.peach;
			fruit = new Fruit(this, "peach", 776, 500, 0, textures.peach);
		}
		else if(level == 3)
		{
			fruitHUD[3] = textures.peach;
			fruit = new Fruit(this, "peach", 776, 500, 0, textures.peach);
		}
		else if(level == 4)
		{
			fruitHUD[4] = textures.apple;
			fruit = new Fruit(this, "apple", 776, 500, 0, textures.apple);
		}
		else if(level == 5)
		{
			fruitHUD[5] = textures.apple;
			fruit = new Fruit(this, "apple", 776, 500, 0, textures.apple);
		}
		else if(level == 6)
		{
			fruitHUD[6] = textures.grape;
			fruit = new Fruit(this, "grape", 776, 500, 0, textures.grape);
		}
		else if(level == 7)
		{
			fruitHUDShift();
			fruitHUD[6] = textures.grape;
		}
		else if(level == 8)
		{
			fruitHUDShift();
			fruitHUD[6] = textures.galaxian;
			fruit = new Fruit(this, "galaxian", 776, 500, 0, textures.galaxian);
		}
		else if(level == 9)
		{
			fruitHUDShift();
			fruitHUD[6] = textures.galaxian;
		}
		else if(level == 10)
		{
			fruitHUDShift();
			fruitHUD[6] = textures.bell;
			fruit = new Fruit(this, "bell", 776, 500, 0, textures.bell);
		}
		else if(level == 11)
		{
			fruitHUDShift();
			fruitHUD[6] = textures.bell;
		}
		else if(level >= 12 && level < 20)
		{
			fruitHUDShift();
			fruitHUD[6] = textures.key;
			fruit = new Fruit(this, "key", 776, 500, 0, textures.strawberry);
		}
		
		powerUpFlag = false;
		powerUpActive = false;
		powerUpActiveCount = 630;
		powerUpSpawnTime = ThreadLocalRandom.current().nextInt(50, 150);
		
		ufo = new UFO(this);
		ufoActive = false;
		ufoFlag = false;
		
		dotCount = 244;
		infectedPowerPellets = 0;
		
		// Repopulate map
		for(int i = 0; i < 31; i++)
			for(int j = 0; j < 28; j++)
			{
				maze.map[i][j].isInfected = false;
				if(maze.map[i][j].dotRestore)
				{
					maze.map[i][j].icon = textures.mazeTiles.get(29);
					maze.map[i][j].isDot = true;
					maze.map[i][j].isBlank = false;
				}
				else if(maze.map[i][j].powerRestore)
				{
					maze.map[i][j].icon = textures.mazeTiles.get(38);
					maze.map[i][j].isDot = true;
					maze.map[i][j].isBlank = false;
				}
			}
		
	}
	
	public void resetLevel()
	{
		displayLives--;
		lives--;
		inky.x = 728;	inky.y = 428;	inky.direction = "up";	inky.state = "dormant"; inky.dormantDirectionTickCount = 20;
		inky.resetScatterTicks(); inky.resetChaseTicks(); inky.resetFrightenedTicks(); inky.icon = inky.getNextIcon();
		
		blinky.x = 776;	blinky.y = 356;	blinky.direction = "left";	blinky.state = "scatter"; blinky.dormantDirectionTickCount = 20;
		blinky.resetScatterTicks(); blinky.resetChaseTicks(); blinky.resetFrightenedTicks();	blinky.i = 13; blinky.j = 11; blinky.newScatterPath(); blinky.speed = 2;
		blinky.icon= blinky.getNextIcon();
		
		pinky.x = 776; pinky.y = 428; 	pinky.direction = "down";	pinky.state = "released"; pinky.dormantDirectionTickCount = 20;
		pinky.resetScatterTicks(); pinky.resetChaseTicks(); pinky.resetFrightenedTicks(); pinky.speed = 1; pinky.icon = pinky.getNextIcon();
		pinky.speed = 1; pinky.icon= pinky.getNextIcon();
		
		clyde.x = 826;	clyde.y = 428;	clyde.direction = "up";	clyde.state = "dormant"; clyde.dormantDirectionTickCount = 20;
		clyde.resetScatterTicks(); clyde.resetChaseTicks(); clyde.resetFrightenedTicks(); clyde.icon = clyde.getNextIcon();
		
		if(customGame.stinkyEnabled)
		{
			c.ghosts.remove(stinky);
			stinky = new Ghost("stinky", "scatter", "left", 776, 500, 13, 17, 2, textures, this);
			stinky.aS = new AStar(stinky.i, stinky.j, 15, 22, this, stinky);
			stinky.path = stinky.aS.algorithm();
			stinky.target[0] = stinky.path.peek().i;
			stinky.target[1] = stinky.path.pop().j;
			c.ghosts.add(stinky);
		}
		
		pacMan.x = 776;	pacMan.y = 644;	pacMan.setDirection("left"); pacMan.icon = textures.rPlayer2;
		pacMan.slowed = false;
		
		if(customGame.DK)
		{
			dk.barrel = null;
			dk.barrelThrown = false;
		}
		
		// Remove all currently active Bullets and Bombs
		c.entities.clear();
		c.entities.add(pacMan);
		
		centipedeActive = false;
		ch = null;
	}
	
	public void resetGame()
	{
		isCustomGame = false;
		level = 0;
		livesPoints = 10000;
		getCurrentHighScore();
		// There's a lot of stuff to do here
		// 1. Reset Ghosts and player by replacing them with new instances
		c.entities.clear();
		c.ghosts.clear();
		
		inky = new Ghost("inky", "dormant", "up", 728, 428, 0, 0, 2.0, textures, this);
		pinky = new Ghost("pinky", "released", "up", 776, 428, 0, 0, 2.0, textures, this);
		blinky = new Ghost("blinky", "scatter", "left", 776, 356, 13, 11, 2.0, textures, this);
		blinky.resetFrightenedTicks();
		blinky.aS = new AStar(blinky.i, blinky.j, 25, 1, this, blinky);
		blinky.path = blinky.aS.algorithm();
		blinky.target[0] = blinky.path.peek().i;
		blinky.target[1] = blinky.path.pop().j;
		clyde = new Ghost("clyde", "dormant", "up", 826, 428, 0, 0, 2.0, textures, this);
		
		c.ghosts.add(inky);
		c.ghosts.add(pinky);
		c.ghosts.add(blinky);
		c.ghosts.add(clyde);
		
		fruit = new Fruit(this, "cherry", 776, 500, 0, textures.cherry);
		fruitActiveCount = 630;
		
		
		pacMan = new Player(776, 644, 1, textures, this, audio.getSound("pacDot_down"));
		c.entities.add(pacMan);
		
		if(customGame.stinkyEnabled)
		{
			c.ghosts.remove(stinky);
			stinky = null;
		}
		
		infectedPowerPellets = 0;
		
		
		// 2. Reset the maze
		
		// Reset dot count
		dotCount = 244;
		
		// Repopulate Map
		for(int i = 0; i < 31; i++)
			for(int j = 0; j < 28; j++)
			{
				maze.map[i][j].isInfected = false;
				if(maze.map[i][j].dotRestore)
				{
					maze.map[i][j].icon = textures.mazeTiles.get(29);
					maze.map[i][j].isDot = true;
					maze.map[i][j].isBlank = false;
				}
				else if(maze.map[i][j].powerRestore)
				{
					maze.map[i][j].icon = textures.mazeTiles.get(38);
					maze.map[i][j].isDot = true;
					maze.map[i][j].isBlank = false;
				}
			}
		
		
		// 3. Reset other game aspects and tick counts
		// Reset Lives
		lives = 3;
		displayLives = lives;
		
		// Reset score;
		score = 0;
		
		// Reset Ghost eaten points
		ghostPoints = 200;
		
		
		// Reset all the tick counters and boolean flags
		introTickCount = 245;
		pacDeathCount = 120;
		levelClearedTransitionCount = 60;
		gameOverTickCount = 240;
		whiteMapCount = 30;
		blueMapCount = 0;
		ghostEatenCount = 60;
		
		introEnable = true;
		isBlue = true;
		isWhite = false;
		gameOver = false;
		ghostEaten = false;
		pacDeath = false;
		resetLevel = false;
		nextLevel = false;
		fruitActive = false;
		fruitFlag = false;
		customGame.bomb = false;
		customGame.laser = false;
		pacMan.ammo = 0;
		customGame.stinkyEnabled = false;
		customGame.DK = false;
		customGame.centipede = false;
		customGame.ufo = false;
		customGame.extraLivesSelected = 1;
		customGame.startLivesSelected = 1;
		customGame.stinkyEnabled = false;
		
		centipedeSpawnTime = ThreadLocalRandom.current().nextInt(50, 150);
		centipedeActive = false;
		centipedeFlag = false;
		
		powerUpFlag = false;
		powerUpActive = false;
		powerUpActiveCount = 630;
		powerUpSpawnTime = ThreadLocalRandom.current().nextInt(50, 150);
		
		ufoSpawnTime = ThreadLocalRandom.current().nextInt(25, powerUpSpawnTime);
		ufoActive = false;
		ufoFlag = false;
		
		
		customGame.menuSelected = 0;
		
		modeSelect.selected = "classic";
		
		// Reset Fruit HUD (The level indicator)
		for(int i = 0; i < fruitHUD.length; i++)
			fruitHUD[i] = null;
		fruitHUD[0] = textures.cherry;
		
		delta = 0;
		
	}
	
	public void stopFrightenedNoise()
	{
		if(audio.soundEffects.get("ghostsFrightened").isPlaying())
			audio.soundEffects.get("ghostsFrightened").stop();
	}
	
	public void stopReturningNoise()
	{
		if(audio.soundEffects.get("ghostReturning").isPlaying())
			audio.soundEffects.get("ghostReturning").stop();
	}
	
	private void playBackgroundNoise()
	{
		stopFrightenedNoise();
		stopReturningNoise();		
		
		if(dotCount > 128 && !audio.soundEffects.get("bg1").isPlaying())
			audio.soundEffects.get("bg1").playAsSoundEffect(1.0f, 0.6f, true);
		else if(dotCount > 64 && dotCount <= 128  && !audio.soundEffects.get("bg2").isPlaying())
		{
			audio.soundEffects.get("bg1").stop();
			audio.soundEffects.get("bg2").playAsSoundEffect(1.0f, 0.6f, true);
		}
		else if(dotCount > 32 && dotCount <= 64 && !audio.soundEffects.get("bg3").isPlaying())
		{
			audio.soundEffects.get("bg2").stop();
			audio.soundEffects.get("bg3").playAsSoundEffect(1.0f, 0.6f, true);
		}
		else if(dotCount > 16 && dotCount <= 32 && !audio.soundEffects.get("bg4").isPlaying())
		{
			audio.soundEffects.get("bg3").stop();
			audio.soundEffects.get("bg4").playAsSoundEffect(1.0f, 0.6f, true);
		}
		else if(dotCount > 0 && dotCount <= 16 && !audio.soundEffects.get("bg5").isPlaying())
		{
			audio.soundEffects.get("bg4").stop();
			audio.soundEffects.get("bg5").playAsSoundEffect(1.0f, 0.6f, true);
		}
	}
	
	public void stopBackgroundNoise()
	{
		if(audio.soundEffects.get("bg1").isPlaying())
			audio.soundEffects.get("bg1").stop();
		else if(audio.soundEffects.get("bg2").isPlaying())
			audio.soundEffects.get("bg2").stop();
		else if(audio.soundEffects.get("bg3").isPlaying())
			audio.soundEffects.get("bg3").stop();
		else if(audio.soundEffects.get("bg4").isPlaying())
			audio.soundEffects.get("bg4").stop();
		else if(audio.soundEffects.get("bg5").isPlaying())
			audio.soundEffects.get("bg5").stop();
	}
	
	private void fruitHUDShift()
	{
		for(int i = 0; i < fruitHUD.length-1; i++)
			fruitHUD[i] = fruitHUD[i+1];
	}
	
	public void checkExtraLife()
	{
		if(livesPoints <= 0 && customGame.extraLivesSelected != 0)
		{
			livesPoints = customGame.extraLivesOptions[customGame.extraLivesSelected];
			if(lives < 5)
			{
				lives++;
				displayLives++;
			}
			audio.soundEffects.get("extraLife").playAsSoundEffect(1.0f, 1.0f, false);
		}
	}
	
	private void getCurrentHighScore()
	{
		try
		{
			File file = new File("");
			String path = file.getAbsolutePath(); // Get the path to the current directory
			InputStream in = new FileInputStream(path + "/highScore.txt");
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String delim = "\\s+"; // Set the delimiter to be a space character
			String line = null;
			try{line = br.readLine();} catch (IOException e){e.printStackTrace();}
			String[] temp = line.split(delim); // Split every string separated by a space into an individual index
			initials = temp[0];
			highScore = Integer.parseInt(temp[1]);
			br.close();
		} catch (IOException e2)
		{
			highScore = 0;
			e2.printStackTrace();
		}	
	}
	
	public void keyReleased(KeyEvent e){isShooting = false;}
	public BufferedImage getSpriteSheet(){return spriteSheet;}
	public Map getMap(){return maze;}

}
