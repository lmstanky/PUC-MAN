package mainPackage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import actors.Ghost;
import actors.PacMan;

public class TextGame implements Runnable
{
		public static int dotCount = 248;
		public static int score = 0;
		public static int rows = 31;
		public static int cols = 29;
		public int edibleTimer = 6000;
		public static int edibleScore = 200;
	    public static TextGameFrame frame;
	    
	    public static PacMan player = new PacMan(23, 14, "left", "M");
	    
	    public static Ghost blinky = new Ghost("blinky", 11, 14, "left", "B");
	    public static Ghost pinky = new Ghost("pinky", 11, 11, "left", "P");
	    public static Ghost inky = new Ghost("inky", 11, 9, "down", "I");
	    public static Ghost clyde = new Ghost("clyde", 11, 17, "left", "C");
	    
	    public static inedibleGhostsTask inedible;
		
		public static Ghost[] Ghosts = {blinky, pinky, inky, clyde};
		Timer timer = new Timer();
		
		public static volatile boolean cancelled;
		public static map maze = new map(rows, cols);
		
		public TextGame() throws IOException
		{			
			frame = new TextGameFrame();
			try
			{
				loadMap();
			} catch (NumberFormatException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			printGame(); //Display the map in the frame			
			loadEntities();
		}
		
		public void loadEntities() throws IOException
		{
			int i;
			for(i = 0; i < Ghosts.length; i++)
				map.layout[Ghosts[i].x][Ghosts[i].y].setText(Ghosts[i].icon);
			// Only two of the ghosts have working paths
			Ghosts[0].random = false;
			Ghosts[0].getPath(player.x, player.y, player.getSpaceInFront().get(0), player.getSpaceInFront().get(1));
			Ghosts[1].random = false;
			Ghosts[1].getPath(player.x, player.y, player.getSpaceBehind().get(0), player.getSpaceBehind().get(1));
		}

		public void loadMap() throws NumberFormatException, IOException
		{
			//Initialise the map by adding a JLabel to each index
			for( int i = 0; i < rows; i++)
				for(int j = 0; j < cols; j++)
				{
					map.layout[i][j] = new JLabel(" ");
					map.layout[i][j].setHorizontalAlignment(SwingConstants.CENTER);
				}
			
			//Go through each index and add walls to construct the walls
			map.populate("PacMap.txt");
		}
		
		public static void printGame()
		{
			for(int i = 0; i < rows; i++)
			{
				for(int j = 0; j < cols; j++)
					frame.mapPanel.add(map.layout[i][j]);
			}
			frame.mapPanel.revalidate();
			frame.mapPanel.repaint();
		}

		@SuppressWarnings("static-access")
		@Override
		public void run()
		{
			int i;
			int j;
			// Game loop
			while(player.lives > 0)
			{	
				Ghosts[0].getPath(player.x, player.y, player.getSpaceInFront().get(0), player.getSpaceInFront().get(1));
				Ghosts[1].getPath(player.x, player.y, player.getSpaceBehind().get(0), player.getSpaceBehind().get(1));
				if(dotCount == 0)
				{
					try
					{
						Thread.sleep(2500);
						for(i = 0; i < 31; i++)
							for(j = 0; j < 29; j++)
								if(map.layout[i][j].getText() != "\u25A0")
									map.layout[i][j].setText(" ");
						Thread.sleep(2000);
						resetPositions();
						map.populate("PacMap.txt");
						dotCount = 244;
						Thread.sleep(2000);
					} catch (InterruptedException | NumberFormatException | IOException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				try
				{
					Thread.sleep(250);
				} catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				// 1. Remove all Entities from the game
				for(i = 0; i < Ghosts.length; i++)
				{
					if(Ghosts[i].redot)
					{
						map.layout[Ghosts[i].x][Ghosts[i].y].setText(Ghosts[i].redotVal);
						Ghosts[i].redot = false;
					}
					
					else
						map.layout[Ghosts[i].x][Ghosts[i].y].setText(" ");
				}		
				map.layout[player.x][player.y].setText(" ");
				
				// 2. Select any new directions for the Ghosts, if they need it
				for(i = 0; i < Ghosts.length; i++)
					Ghosts[i].direction = possibleDirections(Ghosts[i].x, Ghosts[i].y, Ghosts[i].direction);
				
				// 3. Update every entity (i.e. move them)
				for(i = 0; i < Ghosts.length; i++)
				{
					
					if(!Ghosts[i].random && !Ghosts[i].ghostPath.isEmpty())
					{
						Ghosts[i].x = Ghosts[i].ghostPath.get(0).x;
						Ghosts[i].y = Ghosts[i].ghostPath.remove(0).y;
					}
					
					else
						Ghosts[i].move(Ghosts[i].direction);
					
					if(map.layout[Ghosts[i].x][Ghosts[i].y].getText().equals("."))
						Ghosts[i].redot = true;
				}
				
				/* 4. Determine if the Ghost is on a space there'd normally be a dot/power pellet. If so, remind the Thread
				 * to put it back in next time all the Ghosts move. They're not supposed to eat the dots!
				 */
				for(i = 0; i < Ghosts.length; i++)
					if(map.layout[Ghosts[i].x][Ghosts[i].y].getText().equals("."))
					{
						Ghosts[i].redotVal = ".";
						Ghosts[i].redot = true;
					}
				
					else if(map.layout[Ghosts[i].x][Ghosts[i].y].getText().equals("\u25CF"))
					{
						Ghosts[i].redotVal = "\u25CF";
						Ghosts[i].redot = true;
					}
						
					else
						Ghosts[i].redot = false;
					
				
				if(player.checkDirection(player.x, player.y, player.direction))
					player.move(player.direction);
				
				// 5. Get the player's new position
				// If there's a dot in the player's new position, add 10 points
				if(map.layout[player.x][player.y].getText().equals("."))
				{
					score += 10;
					TextGameFrame.scoreText.setText("Score: " + Integer.toString(score));
					dotCount--;
				}
				
				else if(map.layout[player.x][player.y].getText().equals("\u25CF"))
				{
					inedible = new inedibleGhostsTask();
					TextGame.edibleScore = 200;
					score += 50;
					TextGameFrame.scoreText.setText("Score: " + Integer.toString(score));
					dotCount--;
					for(i = 0; i < Ghosts.length; i++)
					{
						Ghosts[i].edible = true;
						Ghosts[i].icon = "!";
					}
					timer.schedule(inedible, 6000);
				}
				
				// 6. Redraw all entities
				for(i = 0; i < Ghosts.length; i++)
					map.layout[Ghosts[i].x][Ghosts[i].y].setText(Ghosts[i].icon);
					
				map.layout[player.x][player.y].setText(player.icon);
				
				/* 7. Check for collision between player and any Ghost.
				 * If so, kill the player and simulate the real game by sleeping for the same amount of time
				 * as the animation, then reset the map.
				 * Otherwise, the Ghost is edible. Add points!
				 */
				for(i = 0; i < Ghosts.length; i++)
					if(Ghosts[i].x == player.x && Ghosts[i].y == player.y && Ghosts[i].edible == false)
					{
						try
						{
							Thread.sleep(1025);
						} catch (InterruptedException e)
						{
							e.printStackTrace();
						}
						player.die();
						
						try
						{
							Thread.sleep(2500);
							resetPositions();
							for(i = 0; i < Ghosts.length; i++)
								map.layout[Ghosts[i].x][Ghosts[i].y].setText(Ghosts[i].icon);
							map.layout[player.x][player.y].setText(player.icon);
							for(i = 0; i < 31; i++)
								for(j = 0; j < 29; j++)
									map.layout[i][j].setVisible(true);
							Thread.sleep(2000);
						} catch (InterruptedException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				
					else if(Ghosts[i].x == player.x && Ghosts[i].y == player.y && Ghosts[i].edible == true)
					{
						score += edibleScore;
						edibleScore *= 2;
						TextGameFrame.scoreText.setText("Score: " + Integer.toString(score));
						try
						{
							Thread.sleep(500);
						} catch (InterruptedException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						Ghosts[i].x = 11;
						Ghosts[i].y = 14;
						if(i == 0)
							Ghosts[i].direction = "B";
						else if(i == 1)
							Ghosts[i].direction = "P";
						else if(i == 2)
							Ghosts[i].direction = "I";
						else if(i == 3)
							Ghosts[i].direction = "C";
					}
			}
		}
		
		public static String possibleDirections(int x, int y, String current)
		{
			int index;
			Random rand = new Random();
			
			//Construct the list of possible directions to move
			ArrayList<String> possibles = new ArrayList<String>();
			if(map.layout[x-1][y].getText().equals(" ") ||
					map.layout[x-1][y].getText().equals(".") ||
					map.layout[x-1][y].getText().equals("P") ||
					map.layout[x-1][y].getText().equals("G") ||
					map.layout[x-1][y].getText().equals("\u25CF"))
				possibles.add("up");
			if((y+1 > 28) || 
					map.layout[x][y+1].getText().equals(" ") ||
					map.layout[x][y+1].getText().equals(".") ||
					map.layout[x][y+1].getText().equals("P") ||
					map.layout[x][y+1].getText().equals("G") ||
					map.layout[x][y+1].getText().equals("\u25CF")) //If the Ghost can move right
				possibles.add("right");
			if(map.layout[x+1][y].getText().equals(" ") ||
					map.layout[x+1][y].getText().equals(".") ||
					map.layout[x+1][y].getText().equals("P") ||
					map.layout[x+1][y].getText().equals("G") ||
					map.layout[x+1][y].getText().equals("\u25CF"))//If the Ghost can move down
				possibles.add("down");
			if((y-1 < 0) || 
					map.layout[x][y-1].getText().equals(" ") ||
					map.layout[x][y-1].getText().equals(".") ||
					map.layout[x][y-1].getText().equals("P") ||
					map.layout[x][y-1].getText().equals("G") ||
					map.layout[x][y-1].getText().equals("\u25CF"))//If the Ghost can move left
				possibles.add("left");
			possibles.remove(oppositeDir(current));
			index = rand.nextInt(possibles.size());
			return possibles.get(index);
		}
		
		private static String oppositeDir(String current)
		{
			String opposite = null;
			if(current == "up")
				opposite = "down";
			else if(current == "right")
				opposite = "left";
			else if(current == "down")
				opposite = "up";
			else if(current == "left")
				opposite = "right";
			return opposite;
			
		}
		
		public static boolean getDirectionCheck(String dir)
		{
			return player.checkChange(dir);
		}
		
		@SuppressWarnings("static-access")
		public void resetPositions()
		{
			map.layout[player.x][player.y].setText(" ");
			player.x = 23;
			player.y = 14;
			player.direction = "left";
			player.lives--;
			TextGameFrame.livesText.setText("Lives: " + Integer.toString(player.lives));
			
			blinky.x = 11;
			blinky.y = 14;
			blinky.direction = "left";
			
			pinky.x = 11;
			pinky.y = 11;
			pinky.direction = "left";
			
			inky.x = 11;
			inky.y = 9;
			inky.direction = "down";
			
			clyde.x = 11;
			clyde.y = 17;
			clyde.direction = "right";
		}
}
