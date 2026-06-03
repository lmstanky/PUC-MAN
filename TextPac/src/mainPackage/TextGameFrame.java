package mainPackage;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import actors.PacMan;


public class TextGameFrame
{
	JFrame frame;
	
	JPanel mapPanel;
	JPanel infoPanel;
	JPanel controlPanel;
	JPanel livesDisplay;
	JPanel scoreDisplay;
	
	static JLabel livesText;
	static JLabel scoreText;
	
	public TextGameFrame()
	{
		frame = new JFrame("Pac-Man (Text Pre-Alpha v1.0)");
		frame.setLayout(new BorderLayout());
		
		livesText = new JLabel("Lives: " + Integer.toString(PacMan.lives));
		scoreText = new JLabel("Score: " + Integer.toString(TextGame.score));		
		
		infoPanel = new JPanel();
		infoPanel.setLayout(new BorderLayout());		
		
		livesDisplay = new JPanel();
		livesDisplay.setLayout(new BorderLayout());
		livesDisplay.add(livesText);
		
		scoreDisplay = new JPanel();
		scoreDisplay.setLayout(new BorderLayout());
		scoreDisplay.add(scoreText);
		
		
		infoPanel.add(livesDisplay, BorderLayout.WEST);
		infoPanel.add(scoreDisplay, BorderLayout.EAST);
		
		mapPanel = new JPanel();
		mapPanel.setLayout(new GridLayout(TextGame.rows, TextGame.cols));
		frame.add(mapPanel, BorderLayout.CENTER);
		frame.add(infoPanel, BorderLayout.NORTH);
		
		frame.addKeyListener(new KeyListener()
				{

					@Override
					public void keyPressed(KeyEvent e)
					{
						int key = e.getKeyCode();
						    if (key == KeyEvent.VK_LEFT)
						    {
						    	if(TextGame.getDirectionCheck("left"))
						    		PacMan.direction = "left";
						    }
				
						    if (key == KeyEvent.VK_RIGHT)
						    {
						    	if(TextGame.getDirectionCheck("right"))
						    		PacMan.direction = "right";
						    }
				
						    if (key == KeyEvent.VK_UP)
						    {
						    	if(TextGame.getDirectionCheck("up"))
						    		PacMan.direction = "up";
						    }
				
						    if (key == KeyEvent.VK_DOWN)
						    {
						    	if(TextGame.getDirectionCheck("down"))
						    		PacMan.direction = "down";
						    }
					}

					@Override
					public void keyReleased(KeyEvent arg0)
					{
						// TODO Auto-generated method stub
						
					}

					@Override
					public void keyTyped(KeyEvent arg0)
					{
						// TODO Auto-generated method stub
						
					}
					
				});
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setPreferredSize(new Dimension(600,600));
		frame.setResizable(false);
		frame.pack();
		frame.setVisible(true);
	}	
}
