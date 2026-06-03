package entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.Random;
import java.util.Stack;
import java.util.concurrent.ThreadLocalRandom;
import main.Game;
import main.Textures;
import pathfinding.AStar;
import pathfinding.Node;

public class Ghost extends GameObject
{
	public String direction;
	private Textures textures;
	public String state;
	public String prevState;
	public String name;
	public BufferedImage icon;
	private int iconState;
	public Game game;
	int[] temp;
	public boolean inTunnel;
	public AStar aS;
	public boolean isEaten = false;
	public boolean isGrid = false;

	public int cruiseElroy = 0;

	public int phase;

	// AI Variables
	public int[] target = new int[2];
	public Stack<Node> path = new Stack<Node>();

	// Counters done via number of ticks
	public int dormantDirectionTickCount = 20; // Number of ticks for switching between up and down when dormant
	public int dormantTickCount = 0; // Number of ticks to stay dormant

	private int iconStateCount = 10; // Number of ticks between changing icons for animation
	public int scatterTickCount; // Number of ticks to stay in scatter
	public int chaseTickCount; // Number of ticks to stay in chase
	private int frightenedTickCount; // Number of ticks to stay in frightened
	public int infectionCount = 540; // Number of ticks until Stinky goes to infect a Power Pellet

	private int blueGhost = 0;
	private int whiteGhost = 0;

	public boolean secondaryTick = false;

	private int explodedState = 0; // Indicates which sprite to use for the explosion animation

	double prevX, prevY;

	public Ghost(String name, String state, String direction, double x, double y, int i, int j, double speed,
			Textures textures, Game game)
	{
		super(x, y, speed);
		this.name = name;
		this.game = game;
		this.state = state;
		this.direction = direction;
		this.textures = textures;
		this.icon = getNextIcon();
		this.i = i;
		this.j = j;

		phase = 0;
		resetScatterTicks();
		resetChaseTicks();
		resetFrightenedTicks();
	}

	public void tick()
	{
		checkPacManCollision(); // Check if this Ghost has collided with Pac-Man

		
		
		// Critical to Ghosts working properly. If their coordinates `mod` 24
		// both equal 0, they're definitely on an exact grid space
		isGrid = checkGrid();
		inTunnel = checkTunnel();
		
		if(isGrid && state == "returning" && i == 6 && j == 14 && direction == "right")
			newReturningPath();
		else if(isGrid && state == "returning" && i == 21 && j == 14 && direction == "left")
			newReturningPath();

		// If this Ghost is outside the bounds of the map, they must be using
		// the tunnel. Move them over to the other side
		if (x >= 1200)
			x = 360;
		else if (x <= 360)
			x = 1200;

		// Tick this entity based on its current state
		if (state == "dormant")
			dormantTick();

		else if (state == "released")
			releasedTick();

		else if (state == "infecting")
			infectingTick();

		else if (state == "returning" || state == "returned")
		{
			icon = getNextIcon();
			returningTick();
		}

		else if (state == "scatter")
		{
			if (scatterTickCount > 0)
			{
				scatterTick();
				if (!secondaryTick)
					scatterTickCount--;
			} else
			{
				getScatterTickCount();
				if (prevState != "frightened")
					direction = oppositeDir();
				prevState = state;
				state = "chase";
				icon = getNextIcon();
				newChasePath();
			}
		}

		else if (state == "frightened")
		{
			if (frightenedTickCount == 0)
			{
				iconStateCount = 10;
				resetFrightenedTicks();
				state = prevState;
				prevState = "frightened";
				icon = getNextIcon();
				speed = 1;
				game.ghostPoints = 200;

				if (state == "scatter")
					newScatterPath();
				else if (state == "chase")
					newChasePath();
			}

			else if (frightenedTickCount > 0 && !secondaryTick)
			{
				frightenedTickCount--;
				if (isGrid && !inTunnel)
					direction = randomDirection();
			}
		}

		else if (state == "chase" && !inTunnel)
		{
			if (chaseTickCount > 0)
			{
				newChasePath();
				if (!secondaryTick)
					chaseTickCount--;
			} else
			{
				if (phase < 3)
					phase++;
				getChaseTickCount();
				if (prevState != "frightened")
					direction = oppositeDir();
				prevState = state;
				state = "scatter";
				icon = getNextIcon();
				newScatterPath();
			}
		}

		if (isGrid)
		{
			if (state != "frightened")
			{
				try
				{
					if (i == target[0] && j == target[1])
					{
						target[0] = path.peek().i;
						target[1] = path.pop().j;
					}
					if(!inTunnel)
					{
						if (target[0] > i)
							direction = "right";
						else if (target[1] > j)
							direction = "down";
						else if (target[0] < i)
							direction = "left";
						else if (target[1] < j)
							direction = "up";
					}
				} catch (EmptyStackException | NullPointerException e)
				{
					System.out.println(name + ": EMPTY STACK");
				}
			}
		}

		prevX = x;
		prevY = y;

		if (state == "scatter" || state == "chase" || state == "frightened" || state == "returning"
				|| state == "infecting")
			move();

		// Now that we've done the tick, change the icon state to "animate" the entity
		if (state == "exploded" && iconStateCount > 4)
			iconStateCount = 4;

		if (iconStateCount <= 0)
		{
			if(iconState == 0)
				iconState = 1;
			else if(iconState == 1)
				iconState = 0;
			
			if (frightenedTickCount > 120)
				icon = getNextIcon();
			if (state != "exploded")
				iconStateCount = 10;
			else
			{
				if (explodedState < 7)
				{
					explodedState++;
					iconStateCount = 4;
				}
				else
				{
					explodedState = 0;
					iconStateCount = 10;
					if(x+4 < 456 && j == 14)
					{
						direction = "right";
						state = "returning";
					}
					else if(x+4 > 1128 && j == 14)
					{
						direction = "left";
						state = "returning";
					}
					else
					{
						state = "returning";
						newReturningPath();
					}
				}
			}
		}

		// Ghost are about to become normal. Make them flash!
		if (state == "frightened" && frightenedTickCount <= 120)
		{
			if (whiteGhost == 0 && blueGhost == 0)
				whiteGhost = 30;

			if (iconState == 0 && whiteGhost > 0)
			{
				whiteGhost--;
				if (whiteGhost == 0)
					blueGhost = 30;
				icon = textures.whiteGhost0;
			}

			else if (iconState == 1 && whiteGhost > 0)
			{
				whiteGhost--;
				if (whiteGhost == 0)
					blueGhost = 30;
				icon = textures.whiteGhost1;
			}

			else if (iconState == 0 && blueGhost > 0)
			{
				blueGhost--;
				if (blueGhost == 0)
					whiteGhost = 30;
				icon = textures.blueGhost0;
			}

			else if (iconState == 1 && blueGhost > 0)
			{
				blueGhost--;
				if (blueGhost == 0)
					whiteGhost = 30;
				icon = textures.blueGhost1;
			}
		}

		if (!secondaryTick)
			iconStateCount--;
		if (name == "stinky" && state != "infecting")
		{
			infectionCount--;
			if (infectionCount == 0 && game.infectedPowerPellets < 4)
			{
				infect();
				state = "infecting";
			}
		}
	}

	private String randomDirection()
	{
		ArrayList<String> temp = new ArrayList<String>();
		if (game.maze.map[j][i + 1].isBlank || game.maze.map[j][i + 1].isDot)
			temp.add("right");
		if ((game.maze.map[j + 1][i].isBlank || game.maze.map[j + 1][i].isDot) && i <= 27 && i >= 0)
			temp.add("down");
		if (game.maze.map[j][i - 1].isBlank || game.maze.map[j][i - 1].isDot)
			temp.add("left");
		if ((game.maze.map[j - 1][i].isBlank || game.maze.map[j - 1][i].isDot) && i <= 27 && i >= 0)
			temp.add("up");
		temp.remove(oppositeDir());

		Random r = new Random();
		int index = r.nextInt(temp.size());
		return temp.get(index);
	}

	private void infectingTick()
	{
		if (i == aS.ti && j == aS.tj)
		{
			if (game.maze.map[j][i].isDot)
			{
				game.maze.map[j][i].isInfected = true;
				game.maze.map[j][i].icon = textures.greenPellet;
				game.audio.soundEffects.get("infect").playAsSoundEffect(1.0f, 1.0f, false);
				game.infectedPowerPellets++;
			}

			state = "scatter";
			aS = new AStar(i, j, 13, 17, game, this);
			path = aS.algorithm();
			try
			{
				target[0] = path.peek().i;
				target[1] = path.pop().j;
			} catch (EmptyStackException e)
			{
				System.out.println("Stinky: FAILED");
			}
			infectionCount = 540;

		}
	}

	private void releasedTick()
	{
		if (x < 776)
		{
			speed = 1;
			direction = "right";
			x++;
		} else if (x > 776)
		{
			speed = 1;
			direction = "left";
			x--;
		} else if (y > 356)
		{
			speed = 1;
			direction = "up";
			y--;
		} else
		{
			direction = "left";
			state = "scatter";
			icon = getNextIcon();
			i = 13;
			j = 11;
			newScatterPath();
			speed = 2;
		}
	}

	private void dormantTick()
	{
		if (dormantDirectionTickCount == 0 && direction == "up")
		{
			direction = "down";
			icon = getNextIcon();
			dormantDirectionTickCount = 40;
		} else if (dormantDirectionTickCount == 0 && direction == "down")
		{
			direction = "up";
			icon = getNextIcon();
			dormantDirectionTickCount = 40;
		}

		if (direction == "up")
			y--;
		else if (direction == "down")
			y++;

		dormantDirectionTickCount--;
		if (dormantTickCount > 0)
		{
			if ((name == "inky" && game.dotCount <= 214) || (name == "clyde" && game.dotCount <= 161))
				dormantTickCount--;
		}

	}

	private void returningTick()
	{

		if (i == 13 && j == 11 && x < 776)
			x++;
		else if (i == 14 && j == 11 && x > 776)
			x--;
		else if (x == 776 && j == 11 && y <= 427)
		{
			speed = 1;
			direction = "down";
			state = "returned";
			y++;
		} else if ((name == "blinky" || name == "pinky") && x == 776 && y == 428)
		{
			speed = 1;
			state = "released";
			resetFrightenedTicks();
			direction = "up";
			iconStateCount = 10;
			iconState = 0;
			icon = getNextIcon();
		}

		// While Blinky and Pinky would just go to the middle of the house, Inky
		// and Clyde go back to their side
		else if (name == "inky" && state == "returned" && x > 728 && y == 428)
		{
			j = 14;
			speed = 1;
			direction = "left";
			x--;
		}

		else if (name == "inky" && state == "returned")
		{
			speed = 1;
			direction = "right";
			state = "released";
			resetFrightenedTicks();
			iconStateCount = 10;
			iconState = 0;
			icon = getNextIcon();
		}

		else if (name == "clyde" && state == "returned" && x < 826 && y == 428)
		{
			j = 14;
			speed = 1;
			direction = "right";
			x++;
		}

		else if (name == "clyde" && state == "returned")
		{
			speed = 1;
			state = "released";
			resetFrightenedTicks();
			direction = "left";
			iconStateCount = 10;
			iconState = 0;
			icon = getNextIcon();
		}
	}

	private void scatterTick()
	{
		if (name == "blinky" && i == 25 && j == 1 && isGrid)
		{
			// path.clear();
			direction = "right";
			aS = new AStar(i, j, 26, 2, game, this);
			path = aS.algorithm();
			try
			{
				target[0] = path.peek().i;
				target[1] = path.pop().j;
			} catch (EmptyStackException e)
			{
				System.out.println("Caught Pac-Man");
			}
		}

		else if (name == "blinky" && i == 26 && j == 2 && isGrid)
		{
			// path.clear();
			direction = "down";
			aS = new AStar(i, j, 25, 1, game, this);
			path = aS.algorithm();
			try
			{
				target[0] = path.peek().i;
				target[1] = path.pop().j;
			} catch (EmptyStackException e)
			{
				System.out.println("Caught Pac-Man");
			}
		}

		else if (name == "pinky" && i == 2 && j == 1 && isGrid)
		{
			// path.clear();
			direction = "left";
			aS = new AStar(i, j, 1, 2, game, this);
			path = aS.algorithm();
			try
			{
				target[0] = path.peek().i;
				target[1] = path.pop().j;
			} catch (EmptyStackException e)
			{
				System.out.println("Caught Pac-Man");
			}
		}

		else if (name == "pinky" && i == 1 && j == 2 && isGrid)
		{
			// path.clear();
			direction = "down";
			aS = new AStar(i, j, 2, 1, game, this);
			path = aS.algorithm();
			try
			{
				target[0] = path.peek().i;
				target[1] = path.pop().j;
			} catch (EmptyStackException e)
			{
				System.out.println("Caught Pac-Man");
			}
		}

		else if (name == "inky" && i == 26 && j == 29 && isGrid)
		{
			// path.clear();
			direction = "left";
			aS = new AStar(i, j, 23, 29, game, this);
			path = aS.algorithm();
			try
			{
				target[0] = path.peek().i;
				target[1] = path.pop().j;
			} catch (EmptyStackException e)
			{
				System.out.println("Inky: EMPTY PATH");
			}
		}

		else if (name == "inky" && i == 23 && j == 29 && isGrid)
		{
			// path.clear();
			direction = "left";
			aS = new AStar(i, j, 26, 29, game, this);
			path = aS.algorithm();
			try
			{
				target[0] = path.peek().i;
				target[1] = path.pop().j;
			} catch (EmptyStackException e)
			{
				System.out.println("Inky: EMPTY PATH");
			}
		}

		else if (name == "clyde" && i == 1 && j == 29 && isGrid)
		{
			// path.clear();
			direction = "down";
			aS = new AStar(i, j, 5, 29, game, this);
			path = aS.algorithm();
			try
			{
				target[0] = path.peek().i;
				target[1] = path.pop().j;
			} catch (EmptyStackException e)
			{
				System.out.println("Clyde: EMPTY STACK");
			}
		}

		else if (name == "clyde" && i == 5 && j == 29 && isGrid)
		{
			// path.clear();
			direction = "right";
			aS = new AStar(i, j, 1, 29, game, this);
			path = aS.algorithm();
			try
			{
				target[0] = path.peek().i;
				target[1] = path.pop().j;
			} catch (EmptyStackException e)
			{
				System.out.println("Clyde: EMPTY STACK");
			}
		}

		else if (name == "stinky" && i == 14 && i == 17 && isGrid)
		{
			direction = "left";
			aS = new AStar(i, j, 15, 23, game, this);
			path = aS.algorithm();
			try
			{
				target[0] = path.peek().i;
				target[1] = path.pop().j;
			} catch (EmptyStackException e)
			{
				System.out.println("Stinky: EMPTY STACK");
			}
		}

		else if (name == "stinky" && i == 15 && i == 23 && isGrid)
		{
			direction = "up";
			aS = new AStar(i, j, 13, 17, game, this);
			path = aS.algorithm();
			try
			{
				target[0] = path.peek().i;
				target[1] = path.pop().j;
			} catch (EmptyStackException e)
			{
				System.out.println("Stinky: EMPTY STACK");
			}
		}
	}

	private void newChasePath()
	{
		if (name == "blinky")
			blinkyPath();
		else if (name == "pinky")
			pinkyPath();
		else if (name == "inky")
			inkyPath();
		else if (name == "clyde")
			clydePath();
	}

	public void newScatterPath()
	{
		if (name == "blinky")
		{
			if (cruiseElroy == 1 || cruiseElroy == 2)
				aS = new AStar(i, j, game.pacMan.i, game.pacMan.j, game, this);
			else
				aS = new AStar(i, j, 25, 1, game, this);
		} else if (name == "pinky")
			aS = new AStar(i, j, 2, 1, game, this);
		else if (name == "inky")
			aS = new AStar(i, j, 26, 28, game, this);
		else if (name == "clyde")
			aS = new AStar(i, j, 1, 29, game, this);
		else if (name == "stinky")
			aS = new AStar(i, j, 13, 17, game, this);
		path = aS.algorithm();
		try
		{
			target[0] = path.peek().i;
			target[1] = path.pop().j;
		} catch (EmptyStackException | NullPointerException e)
		{
			System.out.println("Caught Pac-Man");
		}
	}

	public void newReturningPath()
	{
		
		if(x+4 >= 456 && x+4 <= 576 && j == 14)
			direction = "right";
		else if(x+4 <= 1128 && x+4 >= 1008 && j == 14)
			direction = "left";

		if (x+4 <= 776)
			aS = new AStar(i, j, 13, 11, game, this);
		else if (x+4 > 776)
			aS = new AStar(i, j, 14, 11, game, this);
		path = aS.algorithm();
		try
		{
		target[0] = path.peek().i;
		target[1] = path.pop().j;
		
			
			if(y+4 > game.maze.map[j][i].y && (Math.abs(y - game.maze.map[j][i].y) < Math.abs(y - game.maze.map[j+1][i].y)))
				direction = "up";
			else if(y+4 < game.maze.map[j][i].y && (Math.abs(y - game.maze.map[j][i].y) > Math.abs(y - game.maze.map[j-1][i].y)))
				direction = "down";
			else if(x+4 > game.maze.map[j][i].x && (Math.abs(x - game.maze.map[j][i].x) < Math.abs(x - game.maze.map[j][i-1].x)))
				direction = "left";
			else if(x+4 < game.maze.map[j][i].x && (Math.abs(x - game.maze.map[j][i].x) > Math.abs(x - game.maze.map[j][i+1].x)))
				direction = "right";
		

		} catch (EmptyStackException e)
		{
			state = "returned";
		} catch (NullPointerException e)
		{
			System.out.println("Returning Path: Null Pointer. " + name + " may be outside the map bounds");
		} catch(ArrayIndexOutOfBoundsException e)
		{
			System.out.println("Returning Path: Index out of bounds");
		}
	}

	private void move()
	{
		
		if(isGrid)
		{
			try
			{
				if (direction == "right" && (game.maze.map[j][i + 1].isDot || game.maze.map[j][i + 1].isBlank))
					x++;
				else if (direction == "down" && (game.maze.map[j + 1][i].isDot || game.maze.map[j + 1][i].isBlank))
					y++;
				else if (direction == "left" && (game.maze.map[j][i - 1].isDot || game.maze.map[j][i - 1].isBlank))
					x--;
				else if (direction == "up" && (game.maze.map[j - 1][i].isDot || game.maze.map[j - 1][i].isBlank))
					y--;
			}catch(ArrayIndexOutOfBoundsException e)
			{
				if (direction == "right")
					x++;
				else if (direction == "down")
					y++;
				else if (direction == "left")
					x--;
				else if (direction == "up")
					y--;
			}
		}
		
		else
		{
			if (direction == "right")
				x++;
			else if (direction == "down")
				y++;
			else if (direction == "left")
				x--;
			else if (direction == "up")
				y--;
		}

	}

	private void infect()
	{
		/*
		 * Use two temporary, 2D arrays to store the two Xs and two Ys where the
		 * Power Pellets are normally placed. i.e. 1 and 26 for the two Xs, and
		 * 3 and 23 for the two Ys.
		 * 
		 * From these, a random index will be generated for each, and the
		 * resulting permutation of X and Y will be cross-referenced. If the
		 * permutation equals a grid reference where a (currently uninfected)
		 * Power Pellet still resides, this will be used for Stinky's new path
		 * target. Otherwise, just run in circles; the remaining power pellets
		 * are either all gone, or all infected.
		 */
		int[][] tempXs = new int[2][1];
		int[][] tempYs = new int[2][1];
		tempXs[0][0] = 1;
		tempXs[1][0] = 26;
		tempYs[0][0] = 3;
		tempYs[1][0] = 23;

		int iterationCount = 4;

		while (state != "infecting" && iterationCount > 0)
		{

			int randomX = ThreadLocalRandom.current().nextInt(0, 2);
			int randomY = ThreadLocalRandom.current().nextInt(0, 2);

			if (game.maze.map[tempYs[randomY][0]][tempXs[randomX][0]].isDot
					&& !game.maze.map[tempYs[randomY][0]][tempXs[randomX][0]].isInfected)
			{
				aS = new AStar(i, j, tempXs[randomX][0], tempYs[randomY][0], game, this);
				path = aS.algorithm();
				path.pop();
				target[0] = path.peek().i;
				target[1] = path.pop().j;
				state = "infecting";
			}
			iterationCount--;
		}
	}

	public BufferedImage getNextIcon()
	{
		if (state == "exploded")
		{
			if (explodedState == 0)
				return textures.explosion0;
			else if (explodedState == 1)
				return textures.explosion1;
			else if (explodedState == 2)
				return textures.explosion2;
			else if (explodedState == 3)
				return textures.explosion3;
			else if (explodedState == 4)
				return textures.explosion4;
			else if (explodedState == 5)
				return textures.explosion5;
			else if (explodedState == 6)
				return textures.explosion6;
			else if (explodedState == 7)
				return textures.explosion7;
			else
				explodedState = 0;

		}

		else if (state == "returned" || state == "returning")
		{
			if (direction == "right")
				return textures.rightEyes;
			else if (direction == "down")
				return textures.downEyes;
			else if (direction == "left")
				return textures.leftEyes;
			else if (direction == "up")
				return textures.upEyes;
		}

		else if (state != "frightened" || state == "released")
		{
			if (this.direction == "right")
			{
				if (this.name == "blinky")
				{
					if (iconState == 0)
						return textures.rBlinky0;
					else if (iconState == 1)
						return textures.rBlinky1;
				}

				else if (name == "pinky")
				{
					if (iconState == 0)
						return textures.rPinky0;
					else if (iconState == 1)
						return textures.rPinky1;
				}

				else if (name == "inky")
				{
					if (iconState == 0)
						return textures.rInky0;
					else if (iconState == 1)
						return textures.rInky1;
				}

				else if (name == "clyde")
				{
					if (iconState == 0)
						return this.textures.rClyde0;
					else if (iconState == 1)
						return textures.rClyde1;
				}

				else
				{
					if (iconState == 0)
						return this.textures.rStinky0;
					else if (iconState == 1)
						return textures.rStinky1;
				}
			}

			else if (this.direction == "down")
			{
				if (name == "blinky")
				{
					if (iconState == 0)
						return textures.dBlinky0;
					else if (iconState == 1)
						return textures.dBlinky1;
				}

				else if (name == "pinky")
				{
					if (iconState == 0)
						return textures.dPinky0;
					else if (iconState == 1)
						return textures.dPinky1;
				}

				else if (name == "inky")
				{
					if (iconState == 0)
						return textures.dInky0;
					else if (iconState == 1)
						return textures.dInky1;
				}

				else if (name == "clyde")
				{
					if (iconState == 0)
						return textures.dClyde0;
					else if (iconState == 1)
						return textures.dClyde1;
				}

				else
				{
					if (iconState == 0)
						return textures.dStinky0;
					else if (iconState == 1)
						return textures.dStinky1;
				}
			}

			else if (direction == "left")
			{
				if (name == "blinky")
				{
					if (iconState == 0)
						return textures.lBlinky0;
					else if (iconState == 1)
						return textures.lBlinky1;
				}

				else if (name == "pinky")
				{
					if (iconState == 0)
						return textures.lPinky0;
					else if (iconState == 1)
						return textures.lPinky1;
				}

				else if (name == "inky")
				{
					if (iconState == 0)
						return textures.lInky0;
					else if (iconState == 1)
						return textures.lInky1;
				}

				else if (name == "clyde")
				{
					if (iconState == 0)
						return textures.lClyde0;
					else if (iconState == 1)
						return textures.lClyde1;
				}

				else
				{
					if (iconState == 0)
						return textures.lStinky0;
					else if (iconState == 1)
						return textures.lStinky1;
				}

			}

			else
			{
				if (name == "blinky")
				{
					if (iconState == 0)
						return textures.uBlinky0;
					else if (iconState == 1)
						return textures.uBlinky1;
				}

				else if (name == "pinky")
				{
					if (iconState == 0)
						return textures.uPinky0;
					else if (iconState == 1)
						return textures.uPinky1;
				}

				else if (name == "inky")
				{
					if (iconState == 0)
						return textures.uInky0;
					else if (iconState == 1)
						return textures.uInky1;
				}

				else if (name == "clyde")
				{
					if (iconState == 0)
						return textures.uClyde0;
					else
						return textures.uClyde1;
				}

				else
				{
					if (iconState == 0)
						return textures.uStinky0;
					else
						return textures.uStinky1;
				}
			}
		}

		else
		{
			if (iconState == 0)
				return textures.blueGhost0;
			else
				return textures.blueGhost1;

		}
		// Won't ever happen, but this is to satisfy the return type (or it
		// throws its dummy out the pram and gives an error)
		return null;
	}

	public void render(Graphics g)
	{
		if (state != "exploded")
			g.drawImage(this.icon, (int) this.x, (int) this.y, null);
		else if (state == "exploded")
			g.drawImage(this.icon, (int) this.x - 14, (int) this.y - 14, null);
		if (Game.DEBUG)
		{
			drawCollisionBoxes(g);
		}
	}

	private void drawCollisionBoxes(Graphics g)
	{
		g.setColor(Color.RED);
		if (this.name == "blinky")
			g.setColor(Color.cyan);
		g.drawRect((int) x + 4, (int) y + 4, 24, 24);
	}

	public Rectangle getBounds()
	{
		return new Rectangle((int) x + 4, (int) y + 4, 24, 24);
	}

	public int[] getSpaceBehind()
	{
		if (this.direction == "right")
		{
			temp[0] = i - 1;
			temp[1] = j;
			return temp;
		}

		else if (this.direction == "down")
		{
			temp[0] = i;
			temp[1] = j - 1;
			return temp;
		}

		else if (this.direction == "left")
		{
			temp[0] = i + 1;
			temp[1] = j;
			return temp;
		}

		else
		{
			temp[0] = i;
			temp[1] = j + 1;
			return temp;
		}

	}

	public void blinkyPath()
	{
		aS = new AStar(i, j, game.pacMan.i, game.pacMan.j, game, this);
		
		path = aS.algorithm();
		try
		{
			target[0] = path.peek().i;
			target[1] = path.pop().j;
		} catch (EmptyStackException | NullPointerException e)
		{
			System.out.println("Blinky: FAILED");
		}
	}

	public void pinkyPath()
	{
		try
		{
			if (game.pacMan.getDirection() == "right" || game.pacMan.getDirection() == "dRight"
					|| game.pacMan.getDirection() == "uRight")
			{
				if (game.maze.map[game.pacMan.j][game.pacMan.i + 4].isBlank
						|| game.maze.map[game.pacMan.j][game.pacMan.i + 4].isDot && i + 4 < 28)
					aS = new AStar(i, j, game.pacMan.i + 4, game.pacMan.j, game, this);
				else if (game.maze.map[game.pacMan.j][game.pacMan.i + 3].isBlank
						|| game.maze.map[game.pacMan.j][game.pacMan.i + 3].isDot && i + 3 < 28)
					aS = new AStar(i, j, game.pacMan.i + 3, game.pacMan.j, game, this);
				else if (game.maze.map[game.pacMan.j][game.pacMan.i + 2].isBlank
						|| game.maze.map[game.pacMan.j][game.pacMan.i + 2].isDot && i + 2 < 28)
					aS = new AStar(i, j, game.pacMan.i + 2, game.pacMan.j, game, this);
				else if (game.maze.map[game.pacMan.j][game.pacMan.i + 1].isBlank
						|| game.maze.map[game.pacMan.j][game.pacMan.i + 1].isDot && i + 1 < 28)
					aS = new AStar(i, j, game.pacMan.i + 1, game.pacMan.j, game, this);
				else
					aS = new AStar(i, j, game.pacMan.i, game.pacMan.j, game, this);
			}

			else if (game.pacMan.getDirection() == "down" || game.pacMan.getDirection() == "lDown"
					|| game.pacMan.getDirection() == "rDown")
			{
				if (game.maze.map[game.pacMan.j + 4][game.pacMan.i].isBlank
						|| game.maze.map[game.pacMan.j + 4][game.pacMan.i].isDot && j + 4 < 30)
					aS = new AStar(i, j, game.pacMan.i, game.pacMan.j + 4, game, this);
				else if (game.maze.map[game.pacMan.j + 3][game.pacMan.i].isBlank
						|| game.maze.map[game.pacMan.j + 3][game.pacMan.i].isDot && j + 3 < 30)
					aS = new AStar(i, j, game.pacMan.i, game.pacMan.j + 3, game, this);
				else if (game.maze.map[game.pacMan.j + 2][game.pacMan.i].isBlank
						|| game.maze.map[game.pacMan.j + 2][game.pacMan.i].isDot && j + 2 < 30)
					aS = new AStar(i, j, game.pacMan.i, game.pacMan.j + 2, game, this);
				else if (game.maze.map[game.pacMan.j + 1][game.pacMan.i].isBlank
						|| game.maze.map[game.pacMan.j + 1][game.pacMan.i].isDot && j + 1 < 30)
					aS = new AStar(i, j, game.pacMan.i, game.pacMan.j + 1, game, this);
				else
					aS = new AStar(i, j, game.pacMan.i, game.pacMan.j, game, this);
			}

			else if (game.pacMan.getDirection() == "left" || game.pacMan.getDirection() == "dLeft"
					|| game.pacMan.getDirection() == "uLeft")
			{
				if (game.maze.map[game.pacMan.j][game.pacMan.i - 4].isBlank
						|| game.maze.map[game.pacMan.j][game.pacMan.i - 4].isDot && i - 4 > 0)
					aS = new AStar(i, j, game.pacMan.i - 4, game.pacMan.j, game, this);
				else if (game.maze.map[game.pacMan.j][game.pacMan.i - 3].isBlank
						|| game.maze.map[game.pacMan.j][game.pacMan.i - 3].isDot && i - 3 > 0)
					aS = new AStar(i, j, game.pacMan.i - 3, game.pacMan.j, game, this);
				else if (game.maze.map[game.pacMan.j][game.pacMan.i - 2].isBlank
						|| game.maze.map[game.pacMan.j][game.pacMan.i - 2].isDot && i - 2 > 0)
					aS = new AStar(i, j, game.pacMan.i - 2, game.pacMan.j, game, this);
				else if (game.maze.map[game.pacMan.j][game.pacMan.i - 1].isBlank
						|| game.maze.map[game.pacMan.j][game.pacMan.i - 1].isDot && i - 1 > 0)
					aS = new AStar(i, j, game.pacMan.i - 1, game.pacMan.j, game, this);
				else
					aS = new AStar(i, j, game.pacMan.i, game.pacMan.j, game, this);
			}

			else if (game.pacMan.getDirection() == "up" || game.pacMan.getDirection() == "rUp"
					|| game.pacMan.getDirection() == "lUp")
			{
				if (game.maze.map[game.pacMan.j - 4][game.pacMan.i].isBlank
						|| game.maze.map[game.pacMan.j - 4][game.pacMan.i].isDot && j - 4 > 0)
					aS = new AStar(i, j, game.pacMan.i, game.pacMan.j - 4, game, this);
				else if (game.maze.map[game.pacMan.j - 3][game.pacMan.i].isBlank
						|| game.maze.map[game.pacMan.j - 3][game.pacMan.i].isDot && j - 3 > 0)
					aS = new AStar(i, j, game.pacMan.i, game.pacMan.j - 3, game, this);
				else if (game.maze.map[game.pacMan.j - 2][game.pacMan.i].isBlank
						|| game.maze.map[game.pacMan.j - 2][game.pacMan.i].isDot && j - 2 > 0)
					aS = new AStar(i, j, game.pacMan.i, game.pacMan.j - 2, game, this);
				else if (game.maze.map[game.pacMan.j - 1][game.pacMan.i].isBlank
						|| game.maze.map[game.pacMan.j - 1][game.pacMan.i].isDot && j - 1 > 0)
					aS = new AStar(i, j, game.pacMan.i, game.pacMan.j - 1, game, this);
				else
					aS = new AStar(i, j, game.pacMan.i, game.pacMan.j, game, this);
			}
		} catch (ArrayIndexOutOfBoundsException e)
		{
			System.out.println("Pinky: FAILED");
			aS = new AStar(i, j, game.pacMan.i, game.pacMan.j, game, this);
		}
		path = aS.algorithm();
		try
		{
			target[0] = path.peek().i;
			target[1] = path.pop().j;
		} catch (NullPointerException e)
		{
			System.out.println("Pinky: NULL PATH");
		}
	}

	public void inkyPath()
	{
		int diffX, diffY;
		try
		{
			if (game.blinky.state == "released" || game.blinky.state == "returning" || game.blinky.state == "returned")
				aS = new AStar(i, j, game.pacMan.i, game.pacMan.j, game, this);
			else if (game.pacMan.getDirection() == "right" || game.pacMan.getDirection() == "dRight"
					|| game.pacMan.getDirection() == "uRight")
			{
				int[] temp =
				{ game.pacMan.i + 2, game.pacMan.j };
				diffX = temp[0] - game.blinky.i;
				diffY = temp[1] - game.blinky.j;
				if (game.maze.map[temp[1] + diffY][temp[0] + diffX].isBlank
						|| game.maze.map[temp[1] + diffY][temp[0] + diffX].isDot)
					aS = new AStar(i, j, temp[0] + diffX, temp[1] + diffY, game, this);
				else if (game.maze.map[game.pacMan.j][game.pacMan.i + 2].isBlank
						|| game.maze.map[game.pacMan.j][game.pacMan.i + 2].isDot)
					aS = new AStar(i, j, game.pacMan.i + 2, game.pacMan.j, game, this);
				else
					aS = new AStar(i, j, game.pacMan.i, game.pacMan.j, game, this);
			}

			else if (game.pacMan.getDirection() == "down" || game.pacMan.getDirection() == "rDown"
					|| game.pacMan.getDirection() == "lDown")
			{
				int[] temp =
				{ game.pacMan.i, game.pacMan.j + 2 };
				diffX = temp[0] - game.blinky.i;
				diffY = temp[1] - game.blinky.j;
				if (game.maze.map[temp[1] + diffY][temp[0] + diffX].isBlank
						|| game.maze.map[temp[1] + diffY][temp[0] + diffX].isDot)
					aS = new AStar(i, j, temp[0] + diffX, temp[1] + diffY, game, this);
				else if (game.maze.map[game.pacMan.j + 2][game.pacMan.i].isBlank
						|| game.maze.map[game.pacMan.j + 2][game.pacMan.i].isBlank)
					aS = new AStar(i, j, game.pacMan.i, game.pacMan.j + 2, game, this);
				else
					aS = new AStar(i, j, game.pacMan.i, game.pacMan.j, game, this);
			}

			else if (game.pacMan.getDirection() == "left" || game.pacMan.getDirection() == "dLeft"
					|| game.pacMan.getDirection() == "uLeft")
			{
				int[] temp =
				{ game.pacMan.i - 2, game.pacMan.j };
				diffX = temp[0] - game.blinky.i;
				diffY = temp[1] - game.blinky.j;
				if (game.maze.map[temp[1] + diffY][temp[0] + diffX].isBlank
						|| game.maze.map[temp[1] + diffY][temp[0] + diffX].isDot)
					aS = new AStar(i, j, temp[0] + diffX, temp[1] + diffY, game, this);
				else if (game.maze.map[game.pacMan.j][game.pacMan.i - 2].isBlank
						|| game.maze.map[game.pacMan.j][game.pacMan.i - 2].isDot)
					aS = new AStar(i, j, game.pacMan.i - 2, game.pacMan.j, game, this);
				else
					aS = new AStar(i, j, game.pacMan.i, game.pacMan.j, game, this);
			}

			else if (game.pacMan.getDirection() == "up" || game.pacMan.getDirection() == "rUp"
					|| game.pacMan.getDirection() == "lUp")
			{
				int[] temp =
				{ game.pacMan.i, game.pacMan.j - 2 };
				diffX = temp[0] - game.blinky.i;
				diffY = temp[1] - game.blinky.j;
				if (game.maze.map[temp[1] + diffY][temp[0] + diffX].isBlank
						|| game.maze.map[temp[1] + diffY][temp[0] + diffX].isDot)
					aS = new AStar(i, j, temp[0] + diffX, temp[1] + diffY, game, this);
				else if (game.maze.map[game.pacMan.j - 2][game.pacMan.i].isBlank
						|| game.maze.map[game.pacMan.j - 2][game.pacMan.i].isBlank)
					aS = new AStar(i, j, game.pacMan.i, game.pacMan.j - 2, game, this);
				else
					aS = new AStar(i, j, game.pacMan.i, game.pacMan.j, game, this);
			}

		} catch (ArrayIndexOutOfBoundsException e)
		{
			aS = new AStar(i, j, game.pacMan.i, game.pacMan.j, game, this);
			System.out.println("Inky: FAILED");
		}
		path = aS.algorithm();
		try
		{
			target[0] = path.peek().i;
			target[1] = path.pop().j;
		} catch (NullPointerException e)
		{
			System.out.println("Inky: NULL PATH");
		}
	}

	public void clydePath()
	{
		int diffX = Math.abs(game.pacMan.i - i);
		int diffY = Math.abs(game.pacMan.j - j);
		if (diffX + diffY > 8)
		{
			aS = new AStar(i, j, game.pacMan.i, game.pacMan.j, game, this);
			path = aS.algorithm();
			try
			{
				target[0] = path.peek().i;
				target[1] = path.pop().j;
			} catch (NullPointerException e)
			{
				System.out.println("Clyde: FAILED");
			}
		}

		else
		{
			aS = new AStar(i, j, 1, 29, game, this);
			if (i == 1 && j == 29)
				aS = new AStar(i, j, 3, 29, game, this);
			path = aS.algorithm();
			try
			{
				target[0] = path.peek().i;
				target[1] = path.pop().j;
			} catch (NullPointerException e)
			{
				System.out.println("Clyde failed");
			}
		}
	}

	public String oppositeDir()
	{
		if (direction == "right")
			return "left";
		else if (direction == "down")
			return "up";
		else if (direction == "left")
			return "right";
		else
			return "down";
	}

	private boolean checkGrid()
	{
		if ((x + 4) % 24 == 0.0 && (y + 4) % 24 == 0.0)
		{
			i = (int)((x + 4) - 456) / 24;
			j = (int)((y + 4) - 96) / 24;
			return true;
		}
		return false;
	}

	private boolean checkTunnel()
	{
		if (j == 14 && (x+4 < 600 || x+4 > 960))
		{
			if (state != "returning")
				speed = 1;
			else if(state == "returning")
				speed = 3;
			return true;
		}
		if (state == "returning" || state == "eaten")
			speed = 3;
		else if (state == "frightened")
			speed = 1;
		else
			speed = 2;
		return false;
	}

	private void getScatterTickCount()
	{
		if (game.level >= 4)
			scatterTickCount = game.scatterPhaseTickCounts[2][phase];
		else if (game.level > 0)
			scatterTickCount = game.scatterPhaseTickCounts[1][phase];
		else
			scatterTickCount = game.scatterPhaseTickCounts[0][phase];
	}

	private void getChaseTickCount()
	{
		if (game.level >= 4)
			chaseTickCount = game.chasePhaseTickCounts[2][phase];
		else if (game.level > 0)
			chaseTickCount = game.chasePhaseTickCounts[1][phase];
		else
			chaseTickCount = game.chasePhaseTickCounts[0][phase];
	}

	public void frighten()
	{
		if (game.level == 16 || game.level >= 18)
			direction = oppositeDir();
		else if ((state == "scatter" || state == "chase"))
		{
			prevState = state;
			state = "frightened";
			icon = getNextIcon();
			iconStateCount = 10;
			direction = oppositeDir();
			speed = 1;
		}
	}

	public void resetFrightenedTicks()
	{
		if (game.level == 0)
			frightenedTickCount = game.frightenedTickCounts[0];
		else if (game.level == 1 || game.level == 5 || game.level == 9)
			frightenedTickCount = game.frightenedTickCounts[1];
		else if (game.level == 2)
			frightenedTickCount = game.frightenedTickCounts[2];
		else if (game.level == 3 || game.level == 13)
			frightenedTickCount = game.frightenedTickCounts[3];
		else if (game.level == 4 || game.level == 6 || game.level == 7 || game.level == 9 || game.level == 10)
			frightenedTickCount = game.frightenedTickCounts[4];
		else if (game.level == 8 || game.level == 11 || game.level == 12 || game.level == 14 || game.level == 15
				|| game.level == 17)
			frightenedTickCount = game.frightenedTickCounts[5];
	}

	public void resetChaseTicks()
	{
		if (game.level == 0)
			chaseTickCount = game.chasePhaseTickCounts[0][phase];
		else if (game.level == 1 || game.level == 2 || game.level == 3)
			chaseTickCount = game.chasePhaseTickCounts[1][phase];
		else
			chaseTickCount = game.chasePhaseTickCounts[2][phase];
	}

	public void resetScatterTicks()
	{
		if (game.level == 0)
			scatterTickCount = game.scatterPhaseTickCounts[0][phase];
		else if (game.level == 1 || game.level == 2 || game.level == 3)
			scatterTickCount = game.scatterPhaseTickCounts[1][phase];
		else
			scatterTickCount = game.scatterPhaseTickCounts[2][phase];
	}

	private void checkPacManCollision()
	{
		Rectangle ghostBox = getBounds();
		Rectangle pacBox = new Rectangle((int) game.pacMan.x + 14, (int) game.pacMan.y + 14, 8, 8);

		if (ghostBox.intersects(pacBox) && state != "returning" && state != "returned" && !secondaryTick)
		{
			// Ghost has collided with Pac-Man and is not edible. Kill him.
			if (name != "stinky" && state != "frightened" && state != "returning" && state != "eaten" && state != "exploded")
			{
				game.stopBackgroundNoise();
				game.stopReturningNoise();
				game.stopFrightenedNoise();
				game.pacDeath = true;
				game.laserDeath = false;
				game.barrelDeath = false;
			}

			// Ghost has collided with Pac-Man, but the Ghost is edible. Add points!
			else if (name != "stinky" && state != "exploded")
			{
				if (inTunnel)
				{
					speed = 3;
					direction = oppositeDir();
				}

				if (game.ghostPoints == 200)
				{
					icon = textures.twoHundred;
					game.score += game.ghostPoints;
				}

				else if (game.ghostPoints == 400)
				{
					icon = textures.fourHundred;
					game.score += game.ghostPoints;
				}

				else if (game.ghostPoints == 800)
				{
					icon = textures.eightHundred;
					game.score += game.ghostPoints;
				}

				else if (game.ghostPoints == 1600)
				{
					icon = textures.sixteenHundred;
					game.score += game.ghostPoints;
				}
				game.livesPoints -= game.ghostPoints;
				game.checkExtraLife();
				state = "eaten";
				resetFrightenedTicks();
				iconStateCount = 10;
				explodedState = 0;
				iconStateCount = 10;
				if(x+4 < 456 && j == 14)
				{
					direction = "right";
					state = "returning";
				}
				else if(x+4 > 1128 && j == 14)
				{
					direction = "left";
					state = "returning";
				}
				else
					newReturningPath();
				
				game.audio.soundEffects.get("ghostEaten").playAsSoundEffect(1.0f, 1.0f, false);
				speed = 3;
				game.ghostEaten = true;

				if (game.ghostPoints < 1600)
					game.ghostPoints *= 2;
				else
					game.ghostPoints = 200;
			}
		}
	}

	public double nearest24(double x)
	{
		return Math.round(x / 24) * 24;
	}

	public void setDirection(String in)
	{
		direction = in;
	}

	public String getDirection()
	{
		return direction;
	}

	public double getX()
	{
		return x;
	}

	public double getY()
	{
		return y;
	}
}
