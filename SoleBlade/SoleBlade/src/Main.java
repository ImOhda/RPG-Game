import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import java.io.*;
//import sun.audio.*;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.Scanner;


/*
 * Name: Sibte Ali
 * Partner: Nipun Saini
 * Program: CPT Role Playing Game
 * Course Code: ICS3U1-03
 */

public class Main extends JPanel
{
	
	static int windowWidth = 808, windowHeight = 832; // This is the height and width of the window. This includes the bar on top.
	
	
	
	static int imagestotal = 321, loadingImageCount, attackanimation = 0, numberofrooms = 13; 
	// imagestotal is the total number of tiles that will be loaded. The tiles are found in the tile folder.
	// loadingImageCount is the value represented by the loading bar, and displayed above the loading bar. It is an integer that increases untill it is equal to imagestotal.
	// attackanimation is the current frame of the players attack animation, it goes from 0 to 6, because the attack animations are composed of 7 frames.
	
	public static int keysCollected = 0, monstersKilled = 0; // these two values record the keys you have collected and the monsters you have killed.
	
	static boolean loadingComplete = false, onlyonce = true, onlyonce2 = true, onlyonce3 = true, onlyonce4 = true, onlyonce5 = true, onlyonce6 = true, startCollisionTimer = false, startmenu = true, loadonlyonce = true, playerisinkeyroom = false, tutorial = true, doneloadingstartimage = false, restart = false;
	// loadingComplete is a boolean that represents whether the loading bar has been filled. 
	// all of the onlyonce booleans are used to make things happen only once. For example, if there is a check that happens constantly, but is required to trigger only once, then an only once boolean is used to make sure it does not trigger multiple times. 
	// startCollisionTimer is a boolean that starts the collision timer once it is set to true.
	// startmenu is a boolean that represents whether the user is on the start menu or not.
	// playerisinkeyroom is set to true when the player enters a key room.
	// tutorial is set to true at the start of the game, and displays the starting messages on how to play/where to go.
	// doneloadingstartimage is used to make sure that the image in the starting menu is not displayed before it is fully loaded into the game.
	
	int selection = 0, flametype = 0;
	// selection is the square the player has selected with their arrow keys at the start of the game.
	// flametype is a random number, either 0 or 1, which is used to decide which color of flame wil be displayed at the start screen.
	
	static int timeSeconds = 0, timeMins = 0;
	// this integers are used to hold the time that the player has been playing the game.
	
	public static Image[] spritesFull = new Image[imagestotal]; // this is an array that will hold all the tiles.
	public static Image input = null; // this is the image that will be displayed during the players attack animation. 
	
	Room r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13; // this is just initializing every room object.
	
	int startingScreenDisplayedFrame = 0; // this is which image will be displayed in the starting screen.
	ImageIcon[] startingScreen = new ImageIcon[22]; // this array will hold all the images of the starting screen background.
	
	double loadingFraction; // this decimal is used later on as a percentage of the loading bar, so what is displayed on the loading bar is dependent on this value.
	
	public boolean enableDeveloper = false; // boolean that you can enable that helps with development.
	
	static Room[] allrooms; // an array that will hold all the rooms.
	
	
	int currentRoomNumber = 1; // this is the room that you start in. This value changes as you move from room to room, and it is the code that a room is represented by.
	
	
	Rectangle[][] roomHitBoxes; // this is an array that holds an array of rectangles, each array of rectangles is associated with a room. These rectangles are the collidable walls and things the player cannot move through.
	Player p1; // just creating the player object.
	
	Font f = new Font("Times New Roman", Font.ROMAN_BASELINE, 32); // making a font that will be used in the start menu of the game.
	
	// main method:
	public static void main(String[] args)
	{
		new Main(); // new main, as required to set up the JFrame/JPanel.
	}

	// here is the actual main.
	Main()
	{
		// setting the background, layout, and focusable.
		setBackground(Color.black);
		setLayout(null);
		setFocusable(true);
		
		
		// making a new frame, setting it up.
		JFrame f = new JFrame();
		f.setContentPane(this);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setSize(windowWidth, windowHeight);
		f.setLocationRelativeTo(null);
		f.setResizable(false);
		// change log:
		// Cleaned up some of the commenting and removed some code that was only for use in making the game (creation tools, development tools etc). 
		// Also added some more control over the character movement, allowing the character to continue moving in a direction that
		// a key is pressed down in, the moment that direction is unobstructed by a wall. Makes movement controls feel smoother.
		f.setTitle("Sole Blade Ver. 2.5.1"); 
		f.setFocusable(false);
		f.setVisible(true);
		
		// initializing the player object, a random object, and setting the flame to a random number (0 or 1).
		p1 = new Player(40, 540);
		Random rnd = new Random();
		flametype = rnd.nextInt(2);
		
		// making a new DecimalFormat object.
		DecimalFormat df = new DecimalFormat("00");
		
		// iterating through all the images in the folder images/startingScreen and putting all those images in the image icon array startingScreen.
		for(int i = 0; i < 22; i++)
		{
			startingScreen[i] = new ImageIcon("images/startingScreen/frame_" + df.format(i) + "_delay-0.1s.gif");
			if(i == 21)
			{
				// setting this to true now that they are all done.
				doneloadingstartimage = true;
			}
		}
		
		// this timer counts the seconds and adds to the internal game clock.
		Timer tSecondCounter = new Timer(1000, new ActionListener() {
			public void actionPerformed(ActionEvent arg0)
			{
				timeSeconds++;
				if(timeSeconds == 60)
				{
					timeMins++;
					timeSeconds = 0;
				}
			}
		});
		
		// this timer iterates through the images for the background in the start screen.
		Timer tDisplayFrameStartingScreen = new Timer(1, new ActionListener()
		{

			public void actionPerformed(ActionEvent arg0)
			{
				startingScreenDisplayedFrame++;
				if(startingScreenDisplayedFrame == 22)
				{
					startingScreenDisplayedFrame = 0;
				}
			}
	
		});
		
		// this timer handles a player attack, it iterates through each image of the attack and sets input equal to the image it should be.
		// this is mostly how attacks work in this game.
		Timer tResetAttack = new Timer(70, new ActionListener() 
		{

			public void actionPerformed(ActionEvent e)
			{
				// add to the attack animation.
				attackanimation++;
				
				// if the animation is done, disable attacking etc, make it so that it is done.
				if(attackanimation == 6)
				{
					onlyonce = true;
					p1.setAttacking(false);
					attackanimation = 0;
					p1.setAttacknum(!p1.getAttacknum());
				}
				else
				{
					// if you are facing right, then set the input image to attack 1 or attack 2 based on which attack p1.getAttacknum returns.
					if(p1.getFacing() == 1)
					{
						if(p1.getAttacknum())
						{
							input = getScaledImage(new ImageIcon("images/playerSprites/Attack1/tile00" + attackanimation + ".png").getImage(), 200, 150);
						}
						else
						{
							input = getScaledImage(new ImageIcon("images/playerSprites/Attack2/tile00" + attackanimation + ".png").getImage(), 200, 150);
						}
					}
					else // if you are facing left, then set the input image to attack 1 L or attack 2 L based on which attack p1.getAttacknum returns.
					{
						if(p1.getAttacknum())
						{
							input = getScaledImage(new ImageIcon("images/playerSprites/Attack1L/tile00" + attackanimation + ".png").getImage(), 200, 150);
						}
						else
						{
							input = getScaledImage(new ImageIcon("images/playerSprites/Attack2L/tile00" + attackanimation + ".png").getImage(), 200, 150);
						}
					}

				}				
				
			}
		});
		
		// this timer checks all collisions. So that is, whether a monster collides with a wall or with another monster, all that happens here.
		Timer tCollisionChecker = new Timer(1, new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{				
				if(loadingComplete)
				{
					// iterate through all monsters in the room.
					for(int i = 0; i < allrooms[currentRoomNumber].getMonsterArray().length; i++)
					{
						// if the current monster collides with a wall.
						if(allrooms[currentRoomNumber].doesCollisionOccur(allrooms[currentRoomNumber].getMonsterArray()[i].getHitbox()))
						{
							allrooms[currentRoomNumber].getMonsterArray()[i].setxPos(allrooms[currentRoomNumber].getMonsterArray()[i].getPxPos());
							allrooms[currentRoomNumber].getMonsterArray()[i].setyPos(allrooms[currentRoomNumber].getMonsterArray()[i].getPyPos());
							// set its xpos and ypos to its pxpos and pypos, (previous xpos and previous ypos).
						}
						
						for(int i2 = 0; i2 < allrooms[currentRoomNumber].getMonsterArray().length; i2++)
						{
							// call doesMonsterCollisionOccur method on all of the slimes in the room against all of the other slimes in the room.
							doesMonsterCollisionOccur(allrooms[currentRoomNumber].getMonsterArray()[i], allrooms[currentRoomNumber].getMonsterArray()[i2]);
						}
					}
					
				}
				
			}
			
		});
		
		// this is the main timer of the game. Inside of this loop everything that needs to be checked occurs every second.
		Timer tMaintimer = new Timer(1, new ActionListener() 
		{
			public void actionPerformed(ActionEvent e)
			{
				// if the starting screen displayed frame is completely loaded, change the delay of the tDisplayFrameStartingScreen timer to 100.
				if(startmenu && startingScreenDisplayedFrame == 21)
				{
					tDisplayFrameStartingScreen.setDelay(100);
				}
				
				// if the player has collected all the keys show them the message!
				if(onlyonce6 && keysCollected == 3)
				{
					onlyonce6 = false;
					JOptionPane.showMessageDialog(null, "You have all the keys, Go find the boss room!", "Sole Blade", JOptionPane.INFORMATION_MESSAGE);
					
					// reset the player movement booleans so that they do not get stuck.
					p1.setPlrUp(false);
					p1.setPlrDown(false);
					p1.setPlrLeft(false);
					p1.setPlrRight(false);
				}
				
				// if the players health gets way too low, start the low hp music.
				if(p1.getHealth() <= 30 && onlyonce5 && !playerisinkeyroom)
				{
					//stopMusic();
					//playMusic(new File("audio/low_hp.wav"));
					onlyonce5 = false;
				}
				
				// if the player has run out of health, tell them they are dead and ask them if they wanna restart. If they say yes, restart the game, no, exit the game. 
				if(p1.getHealth() <= 0 && onlyonce4)
				{
					int r;
					// when the player wins, the player is killed and this dialogue does not show up. That is what restart boolean is for.
					if(!restart)
					{
						r = JOptionPane.showConfirmDialog(null, "You died! \n Would you like to play again?", "You loose!", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, new ImageIcon ("images/gameover.png"));
					}
					else
					{
						restart = false;
						r = 0;
					}
					
					if(r == 1)
					{
						JOptionPane.showMessageDialog(null, "Thanks for playing!", "Sole Blade", JOptionPane.INFORMATION_MESSAGE);
						System.exit(0);
					}
					else
					{
						// we have to reload the game, so we need to set this back to false and call the LoadingStart method again.
						loadingComplete = false;
						LoadingStart();
						p1 = new Player(40, 540); // remake the player.
						currentRoomNumber = 1; // set the player back to the starting room.
						
						//stopMusic(); // reset the music.
						//playMusic(new File("audio/main_menu.wav")); // play the menu music.
						onlyonce4 = false;
					}
				}
				
				// this starts off the loading screen once the player presses enter on play & they leave the start menu.
				if(loadonlyonce && !startmenu)
				{
					tSecondCounter.start();
					LoadingStart();
					loadonlyonce = false;
				}
				
				// once the loading is complete, the game can begin.
				if(loadingComplete)
				{
					// we can stop this timer as we have left the start menu.
					tDisplayFrameStartingScreen.stop();
					
					// if the final boss is dead, tell the player that they have won.
					if(allrooms[12].getMonsterArray()[0].isDead())
					{
						// This calculates the score that the user achieved after completing the game
						int score = monstersKilled*10 + p1.getHealth()*100 - timeMins*100;
						// Printing out to the user that they won the game and asking if they want to play again?
						int r = JOptionPane.showConfirmDialog(null, "You Won! \nWould you like to play again?\nYou killed: " + monstersKilled + " monsters, you had " + p1.getHealth() + " health left, and you completed the game in " + df.format(timeMins) + ":" + df.format(timeSeconds) + " time!\nYour score is: " + score +" (Score is calculated by +100 for each hp, -100 for each minute and +10 for each monster killed), ", "You win!", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, new ImageIcon ("images/gameover.png"));
						// Checking If they say yes, then set loadingcomplete to false, and restart the game.
						if(r == 0)
						{
							p1.setHealth(0); // just kills the player to restart the game.
							restart = true; // setting restart to true to restart all game variables.
						}
						// Checking if they say no
						if(r == 1)
						{
							// If they say no, print out a message thanking them for playing and then exit the game.
							JOptionPane.showMessageDialog(null, "Thanks for playing!", "Sole Blade", JOptionPane.INFORMATION_MESSAGE);
							System.exit(0);
						}
					}
					
					// goes through every monster in the current room and calls their move and hit method.
					for(int i = 0; i < allrooms[currentRoomNumber].getMonsterArray().length; i++)
					{
						allrooms[currentRoomNumber].getMonsterArray()[i].Move(p1, allrooms[currentRoomNumber]);
						allrooms[currentRoomNumber].getMonsterArray()[i].Hit(p1);
					}
					

					// goes through all 4 cardinal directions.
					for(int i = 0; i < 4; i++)
					{
						// if there is a door at the cardinal direction, and the player walks into that door:
						if(allrooms[currentRoomNumber].getDoors()[i] != null && (allrooms[currentRoomNumber].getDoors()[i].intersects(p1.getCollisionRect()) == true))
						{
							// this boolean is useful to see if the player just tried to go into the boss room.
							boolean triedtogotoboss = false;
							
							// if the current room before they go to the next room is one of these key rooms, restart the music and do the normal level music.
							if(currentRoomNumber == 4 || currentRoomNumber == 6 || currentRoomNumber == 11)
							{
								//stopMusic();
								//playMusic(new File ("audio/normal_level.wav"));
								playerisinkeyroom = false; // set player in key room to false as they have left the key room.
							}
							
							// if the door leads to room 12, the boss room:
							if(allrooms[currentRoomNumber].getConnectedRooms()[i] == 12)
							{
								// and the player has all 3 keys, 
								if(keysCollected == 3)
								{
									// tell them they have everything they need and show them this prompt, then put them in the boss room.
									JOptionPane.showMessageDialog(null, "You have successfully entered the evil emporors room. \n Defeat him to escape!", "Boss Room!", JOptionPane.INFORMATION_MESSAGE);
									currentRoomNumber = allrooms[currentRoomNumber].getConnectedRooms()[i];
								}
								else
								{
									triedtogotoboss = true; // set tried to go to boss true, because they have failed to go to the boss room.
									// show them this prompt.
									JOptionPane.showMessageDialog(null, "This is the evil emporors room, it is locked by the three keys you need to find. \n Once you have them, come here to fight the emporor.", "Boss Room!", JOptionPane.INFORMATION_MESSAGE);
								}
								
								// reset these booleans in case they get stuck to true.
								p1.setPlrUp(false);
								p1.setPlrDown(false);
								p1.setPlrLeft(false);
								p1.setPlrRight(false);
							}
							else
							{
								// if its not leading to the boss room, put them into the connected room.
								currentRoomNumber = allrooms[currentRoomNumber].getConnectedRooms()[i];
							}
							
							// if they tried to go to the boss and failed, move them back 20 pixels from the door.
							if(triedtogotoboss)
							{
								p1.setPosX(p1.getPosX() - 20);
							}
							else
							{
								// in any other situation, change their coords to the coords that they should be at once they enter the next rooms.
								p1.setPosX(allrooms[currentRoomNumber].getRoomTeleportCoordsX()[i]);
								p1.setPosY(allrooms[currentRoomNumber].getRoomTeleportCoordsY()[i]);
							}
							
							// This controls the music based on which room you are in currently.
							switch(currentRoomNumber)
							{
							// This is when you enter the first key room.
							case 4:
								//stopMusic();
								//playMusic(new File("audio/key_room_1.wav"));
								playerisinkeyroom = true;
								break;
							// This is when you enter the second key room
							case 6:
								//stopMusic();
								//playMusic(new File("audio/key_room_2.wav"));
								playerisinkeyroom = true;
								break;
							// This is when you enter the third key room
							case 11:
								//stopMusic();
								//playMusic(new File("audio/key_room_3.wav"));
								playerisinkeyroom = true;
								break;
							// This is when you enter the boss room.
							case 12:
								//stopMusic();
								//playMusic(new File("audio/boss_music.wav"));
								break;
							}
						}
							
						
					}
				}
				
				// this just starts the collision timer and the normal level music once the loading is done.
				if(onlyonce3 && startCollisionTimer)
				{
					//stopMusic();
					//playMusic(new File ("audio/normal_level.wav"));
					tCollisionChecker.start();
					onlyonce3 = false;
				}

				// this just runs the constantCode inside the player.
				p1.constantCode();
				
				
				
				
				if(p1.isPlrDown())
				{
					if(allrooms[currentRoomNumber].doesCollisionOccur(new Rectangle(p1.getPosX() + p1.getPlayerSpriteDimension()/4 + 20, p1.getPosY() + 1 + p1.getPlayerSpriteDimension()/4 + 40, p1.getPlayerSpriteDimension()/2 - 40, p1.getPlayerSpriteDimension()/2 - 40)))
					{
						// if they would collide then stop them from moving there.
						p1.moveD = false;
					}
					else
					{
						p1.moveD = true;
					}
				}
				else
				{
					p1.moveD = false;
				}
				
				if(p1.isPlrUp())
				{
					if(allrooms[currentRoomNumber].doesCollisionOccur(new Rectangle(p1.getPosX() + p1.getPlayerSpriteDimension()/4 + 20, p1.getPosY() - 1 + p1.getPlayerSpriteDimension()/4 + 40, p1.getPlayerSpriteDimension()/2 - 40, p1.getPlayerSpriteDimension()/2 - 40)))
					{
						// stop you from moving there.
						p1.moveU = false;
					}
					else
					{
						p1.moveU = true;
					}
				}
				else
				{
					p1.moveU = false;
				}
				if(p1.isPlrLeft())
				{
					// if moving left would put you inside of a wall:
					if(allrooms[currentRoomNumber].doesCollisionOccur(new Rectangle(p1.getPosX() - 1 + p1.getPlayerSpriteDimension()/4 + 20, p1.getPosY() + p1.getPlayerSpriteDimension()/4 + 40, p1.getPlayerSpriteDimension()/2 - 40, p1.getPlayerSpriteDimension()/2 - 40)))
					{
						// stop you from moving left.
						p1.moveL = false;
					}
					else
					{
						p1.moveL = true;
					}
				}
				else
				{
					p1.moveL = false;
				}
				if(p1.isPlrRight()) // otherwise if you are moving right:
				{
					// if moving right would put you in a wall:
					if(allrooms[currentRoomNumber].doesCollisionOccur(new Rectangle(p1.getPosX() + 1 + p1.getPlayerSpriteDimension()/4 + 20, p1.getPosY() + p1.getPlayerSpriteDimension()/4 + 40, p1.getPlayerSpriteDimension()/2 - 40, p1.getPlayerSpriteDimension()/2 - 40)))
					{
						// stop moving right.
						p1.moveR = false;
					}
					else
					{
						p1.moveR = true;
					}
				}
				else 
				{
					p1.moveR = false;
				}
				
				
				// if the player is attacking do this, otherwise check if they want to move. This is so that the player cannot move while attacking. 
				if(p1.isAttacking())
				{
					// inside of the player class, change the image mode to 2 (the attack mode).
					p1.setCurrentimage(2);
				}
				else 
				{
					
					// if the player is going down.
					if(p1.moveD)
					{
						{
							// if they would not collide set the current image mode to 1 (the move mode) and move the player.
							p1.setCurrentimage(1);
							p1.setpPosY(p1.getPosY());
							p1.setPosY(p1.getPosY() + 1);
						}
					}
					else if(p1.moveU) // otherwise if you are moving up
					{
						{
							// otherwise move you up and set the current image mode to 1 (move mode).
							p1.setCurrentimage(1);
							p1.setpPosY(p1.getPosY());
							p1.setPosY(p1.getPosY() - 1);
						}
					}
					
					// if the player is going left:
					if(p1.moveL)
					{
						
						{
							// otherwise move you left and change the image mode to moving (1).
							p1.setCurrentimage(1);
							p1.setFacing(0); // also set facing to 0, which is left.
							p1.setpPosX(p1.getPosX());
							p1.setPosX(p1.getPosX() - 1);
						}
					}
					else if(p1.moveR) // otherwise if you are moving right:
					{
						
						{
							// if it wont put you in a wall, move right and set the image mode to 1 (moving).
							p1.setCurrentimage(1);
							p1.setFacing(1); // also set facing to 1, which is right.
							p1.setpPosX(p1.getPosX());
							p1.setPosX(p1.getPosX() + 1);
						}
					}

					// if the player is not moving at all, set the current player image mode to 0, which is idle.
					if(!p1.isPlrDown() && !p1.isPlrRight() && !p1.isPlrLeft() && !p1.isPlrUp())
					{
						p1.setCurrentimage(0);
					}
				}
				
				// if player is not attacking, stop the timer.
				if(!p1.isAttacking())
				{
					tResetAttack.stop();
				}
				
				// repainting at the end of the timer.
				repaint();
				
				// Checking if loading is complete, which means the player has loaded in, and tutorial is true, the user gets some tips on where to begin on their adventure.
				if(tutorial && loadingComplete)
				{
					JOptionPane.showMessageDialog(null, "To move, use the arrow keys. To attack, press space. \nYou attack in the direction you are facing. You get healed a little when you kill monsters.\nYou must find the three keys scattered throughout this dungeon so that you can fight the evil emporor that trapped you here and escape!", "Tutorial", JOptionPane.INFORMATION_MESSAGE);
					JOptionPane.showMessageDialog(null, "Head through the door on the bottom to find your first key. Attack the key to pick it up.", "Tutorial", JOptionPane.INFORMATION_MESSAGE);
					tutorial = false;
				}
			}
		});
		
		// This is a key listener
		addKeyListener(new KeyListener() 
		{
			// This is a method that checks if keys are pressed.
			public void keyPressed(KeyEvent e)
			{
				// If startmenu equals true, which means you are on the starting page, this is the code that provides you with the functionality of moving up and down on the start menu.
				if(startmenu)
				{
					// If the user wants to go down, increase selection.
					if(e.getKeyCode() == KeyEvent.VK_DOWN)
					{
						if(selection < 1)
						{
							selection++;
						}
						
					}
					
					// If the user wants to go up and they were currently on "CREDITS", then decrease selection.
					if(e.getKeyCode() == KeyEvent.VK_UP)
					{
						
						if (selection > 0) 
						{
							selection--;
						}
					}
					// If the user makes up their mind on whatever they want to click, they can click by either pressing space or enter.
					if(e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_SPACE)
					{
						// This checks if the user clicked on START.
						if(selection == 0)
						{
							// If they did, set startmenu to false.
							startmenu = false;
						}
						// If they clicked on credits, then display Credits.
						if(selection == 1)
						{
							JOptionPane.showMessageDialog(null, " Game design: Sibte Ali \n Game Art: LuizMelo (player character) &  VacaRoxa (tilemap) \n Game code: Mostly Sibte Ali & a little bit of Nipun \n Game Music: Disasterpeace \n \n Thank you for playing!", "Credits", JOptionPane.INFORMATION_MESSAGE);

						}
					}
				}
				else
				{
					if(e.getKeyCode() == KeyEvent.VK_SPACE)
					{
						
						if(onlyonce)
						{
							
							if(p1.getFacing() == 1)
							{
								input = getScaledImage(new ImageIcon("images/playerSprites/Attack1/tile000.png").getImage(), 200, 150);
								//System.out.println("IDLE R");
							}
							else
							{
								input = getScaledImage(new ImageIcon("images/playerSprites/Attack1L/tile000.png").getImage(), 200, 150);
								//System.out.println("IDLE L");
							}
							

							p1.setAttacking(true);
							
							tResetAttack.restart();
							onlyonce = false;
						}
					}
					// If the user presses up, setting playerup to true.
					if(e.getKeyCode() == KeyEvent.VK_UP)
					{
						p1.setPlrUp(true);
					}
					// If the user presses down, setting playerdown to true.
					if(e.getKeyCode() == KeyEvent.VK_DOWN)
					{
						p1.setPlrDown(true);
					}
					// If the user presses left, setting playerleft to true.
					if(e.getKeyCode() == KeyEvent.VK_LEFT)
					{
						p1.setPlrLeft(true);
					}
					// If the user presses right, then setting playerright to true.
					if(e.getKeyCode() == KeyEvent.VK_RIGHT)
					{
						p1.setPlrRight(true);
					}
					
				}
			}
			
			public void keyReleased(KeyEvent e)
			{
				// If the user releases the up arrow key, resetting the playerup to false.
				if(e.getKeyCode() == KeyEvent.VK_UP)
				{
					p1.setPlrUp(false);
				}
				// If the user releases the down arrow key, resetting the playerdown to false.
				if(e.getKeyCode() == KeyEvent.VK_DOWN)
				{
					p1.setPlrDown(false);
				}
				// If the user releases the left arrow key, resetting the playerleft to false.
				if(e.getKeyCode() == KeyEvent.VK_LEFT)
				{
					p1.setPlrLeft(false);
				}
				// If the user releases the right arrow key, resetting the playeright to false.
				if(e.getKeyCode() == KeyEvent.VK_RIGHT)
				{
					p1.setPlrRight(false);
				}
			}
			public void keyTyped(KeyEvent e){}
		});
		// If enableDeveloper is true, then the user is granted with a lot of HP that they can test out the game with. (CREATIVE MODE).
		if(enableDeveloper)
		{
			p1.setMaxHealth(1000000);
			p1.setHealth(1000000);
		}
		
		//playMusic(new File("audio/main_menu.wav"));
		tMaintimer.start();
		tDisplayFrameStartingScreen.start();
	}
	
	DecimalFormat df = new DecimalFormat("00");

	
	public void paintComponent(Graphics g)
	{
		// graphics 2D gets set up here.
		Graphics2D g2 = (Graphics2D) g;
		super.paintComponent(g2);

		// setting the rendering hints here.
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		// set the color to white.
		g2.setColor(Color.white);
		
		
		// if we are in the start menu:
		if(startmenu)
		{
			// set the font to f.
			g2.setFont(f);
			
			// once we are done loading start images, draw the background image.
			if(doneloadingstartimage)
			{
				g2.drawImage(getScaledImage(startingScreen[startingScreenDisplayedFrame].getImage(), 1300,  825), 0, 0, null);
			}
			// set the stroke to 4.
			g2.setStroke(new BasicStroke(4));
			
			// depending on the type of flame that was randomly decided when the game was started, show either a blue or red flame.
			if(flametype == 0)
			{
				g2.drawImage(getScaledImage(new ImageIcon("images/blueFlame.gif").getImage(), 125, 250), windowWidth/2 - 125/2, 50, null);
			}
			else
			{
				g2.drawImage(getScaledImage(new ImageIcon("images/redFlame.gif").getImage(), 125, 250), windowWidth/2 - 125/2, 50, null);
			}
			
			// set the color to black and make a black rectangle.
			g2.setColor(Color.black);
			g2.fillRect(windowWidth/2 - 250/2 + 10, 420 - 130, 250 - 15, 50);
			
			// depending on if they have chosen 0, set the color to either white or red.
			if(selection == 0)
			{
				g2.setColor(Color.red);
			}
			else
			{
				g2.setColor(Color.white);
			}
			
			// draw the outline of the same rectangle from before.
			g2.drawRect(windowWidth/2 - 250/2 + 10, 420 - 130, 250 - 15, 50);

			// set the color to white and write "start game" inside of the rectangle.
			g2.setColor(Color.white);
			g2.drawString("START GAME", windowWidth/2 - 250/2 + 30, 455 - 130);
			
			
			// set the color to black and make a black rectangle.
			g2.setColor(Color.black);
			g2.fillRect(windowWidth/2 - 250/2 + 30, 500 - 130, 250 - 60, 50);
			
			// depending on if the selection is 1 or not, set the color to red or white.
			if(selection == 1)
			{
				g2.setColor(Color.red);
			}
			else
			{
				g2.setColor(Color.white);
			}
			
			// draw the outline of the same rectangle from before.
			g2.drawRect(windowWidth/2 - 250/2 + 30, 500 - 130, 250 - 60, 50);
			
			// set the color to white and write "credits" inside of that rectangle.
			g2.setColor(Color.white);
			g2.drawString("CREDITS", windowWidth/2 - 250/2 + 55, 535 - 130);
		}
		else
		{
			// if loading is not complete:
			if(!loadingComplete)
			{
				// draw the skull gif, the string that says which tile is being loaded, and the loading bar itself.
				g2.drawImage(getScaledImage(new ImageIcon("images/skullLoading.gif").getImage(), 200, 200), windowWidth/2 - 120, windowHeight/2 - 250, null);
				g2.drawString("Loading tile: " + loadingImageCount, windowWidth/2 - 64, windowHeight/2 - 32);
				g2.drawRect(0, windowHeight/2 - 16, windowWidth - 9, 32);
				g2.fillRect(0, windowHeight/2 - 16, (int) (windowWidth * ((double)loadingImageCount/imagestotal) - 9), 32);
				
			}
			else
			{
				// draw the current room.
				allrooms[currentRoomNumber].draw(g2);

				// start the collision timer.
				startCollisionTimer = true;
				
				// draw the players heads up display, the health bar, time, keys, monsters killed, everything on the bottom of the screen.
				p1.drawHUD(g2, 10, windowHeight - 64 + 8);
				
				// iterate through the monsters in the room and draw them.
				for(int i = 0; i < allrooms[currentRoomNumber].getMonsterArray().length; i++)
				{
					allrooms[currentRoomNumber].getMonsterArray()[i].Draw(g2);
				}
				
				// draw the player.
				p1.drawPlayer(g2, input);
				
				// If enableDeveloper mode is on, then you can access the hit boxes of the room, player and the entities.
				if(enableDeveloper)
				{
					// draw basic white rectangle for the entire player sprite.
					g2.drawRect(p1.getPosX(), p1.getPosY(), p1.getPlayerSpriteDimension(), p1.getPlayerSpriteDimension());
					
					// set the color to blue and draw the real collision rectangle for the player.
					g2.setColor(Color.blue);
					g2.draw(p1.getCollisionRect());
					
					// draw each and every room hit box rectangle.
					for(int i = 0; i < allrooms[currentRoomNumber].getNumOfColSquares(); i++)
					{
						g2.draw(roomHitBoxes[currentRoomNumber][i]);
					}
					
					// set the color to red.
					g2.setColor(Color.red);
					
					// if the player is attacking, and both of the attacking rectangles are not null, draw the attacking rectangle.
					if(p1.isAttacking() && p1.getAttackRectL() != null && p1.getAttackRectR() != null)
					{
						if(p1.getFacing() == 0)
						{
							g2.draw(p1.getAttackRectL());
						}
						else
						{
							g2.draw(p1.getAttackRectR());
						}
						
					}
					
					// draw each door on each cardinal direction in the room, if there is none then don't draw it.
					for(int i = 0; i < 4; i++)
					{
						if(allrooms[currentRoomNumber].getDoors()[i] != null)
						{
							g2.draw(allrooms[currentRoomNumber].getDoors()[i]);
							
						}
					}
					
					// draw the hit box and attack for each monster.
					for(int i = 0; i < allrooms[currentRoomNumber].getMonsterArray().length; i++) 
					{
						// if the attack box is not null draw it.
						if(allrooms[currentRoomNumber].getMonsterArray()[i].getAttackbox() != null)
						{
							g2.draw(allrooms[currentRoomNumber].getMonsterArray()[i].getAttackbox());
						}
						
						// if the hit box is not null draw it.
						if(allrooms[currentRoomNumber].getMonsterArray()[i].getHitbox() != null)
						{
							g2.draw(allrooms[currentRoomNumber].getMonsterArray()[i].getHitbox());
						}
						
						// set the color to white.
						g2.setColor(Color.white);
						//g2.drawOval(allrooms[currentRoomNumber].getMonsterArray()[i].getxPos() - allrooms[currentRoomNumber].getMonsterArray()[i].getDetectionRange()/2, allrooms[currentRoomNumber].getMonsterArray()[i].getyPos() - allrooms[currentRoomNumber].getMonsterArray()[i].getDetectionRange()/2, allrooms[currentRoomNumber].getMonsterArray()[i].getDetectionRange(), allrooms[currentRoomNumber].getMonsterArray()[i].getDetectionRange());
						
						// draw the radius of the monster detection.
						g2.drawOval(allrooms[currentRoomNumber].getMonsterArray()[i].getxPos() - allrooms[currentRoomNumber].getMonsterArray()[i].getDetectionRange() + 32, allrooms[currentRoomNumber].getMonsterArray()[i].getyPos() - allrooms[currentRoomNumber].getMonsterArray()[i].getDetectionRange() + 32, allrooms[currentRoomNumber].getMonsterArray()[i].getDetectionRange()*2, allrooms[currentRoomNumber].getMonsterArray()[i].getDetectionRange()*2);
						
						// draw a line connecting everyone to the player.
						g2.drawLine(p1.getPosX() + p1.getPlayerSpriteDimension()/2, p1.getPosY() + p1.getPlayerSpriteDimension()/2, allrooms[currentRoomNumber].getMonsterArray()[i].getxPos()+32, allrooms[currentRoomNumber].getMonsterArray()[i].getyPos()+32);
					}
				}
				
				
				
			}
		}
		
	}
	
	private void LoadingStart()
	{

		//System.out.println("WARNING: Artificial delay on loading screen, remove on launch.");
		Thread worker = new Thread()
		{
			public void run() 
			{
				// Initializing all rooms array.
				allrooms = new Room[numberofrooms];

				// initializing all rooms hitboxes array.
				roomHitBoxes = new Rectangle[numberofrooms][30];
				
				
				// Starting setup for room1: 
				
				// Creating collision mask for room 1.
				Rectangle[] room1c = {
							new Rectangle(0, 0, 32, windowHeight - 64 - 25),
							new Rectangle(0, 0, windowWidth, 32), 
							new Rectangle(windowWidth - 32, 0, windowWidth-32, windowHeight - 64 - 25), 
							new Rectangle(0, windowHeight - 64 - 25 - 32, windowWidth, 32),
							new Rectangle(0, 288, 384, 32),
							new Rectangle(160, 96, 32, 224),
							new Rectangle(0, 288, 384, 32),
							new Rectangle(192, 438, 64, 273),
							new Rectangle(96 , 502, 96, 32),
							new Rectangle(256 , 576, 160, 32),
							new Rectangle(544 , 0, 64, 522),
							new Rectangle(608, 256, 96, 32),
							new Rectangle(672, 128, 104, 32),
							new Rectangle(672, 384, 104, 32),
							new Rectangle(672, 512, 104, 32)
									};
				
				
				// initializing door array and connected room array for r1.
				Rectangle[] r1Doors = new Rectangle[4]; 
				int[] r1ConnectedRooms = new int[4]; 
				int[] r1CoordX = new int[4];
				int[] r1CoordY = new int[4];
				Entity[] r1Monsters = new Entity[2];
				
				r1 = new Room(windowWidth, windowHeight - 64, room1c.length, r1Monsters.length); // initializing room1.
				r1.setCollisionSquares(room1c); // attaching room1c to r1.
				
				// filling up the door array with rectangles. 0 is up, so the rectangle that is assigned to it is the one at the top of the room etc.
				r1Doors[0] = new Rectangle(384 - 32, 0, 64, 38);
				r1Doors[2] = new Rectangle(384 - 32 - 64, windowHeight - 64 - 25 - 32 - 5, 64, 38);
				
				// setting the coords for where they get teleported upon going through a room.
				r1CoordX[2] = 320; r1CoordY[2] = -25;
				r1CoordX[0] = 320 - 64; r1CoordY[0] = 593;
				
				// adding a monster to room 1.
				r1Monsters[0] = new Slime(200, 200);
				r1Monsters[1] = new Slime(500, 510);
				
				// Setting the connected rooms
				r1ConnectedRooms[0] = 2;
				r1ConnectedRooms[2] = 1; 
				
				// assigning those arrays to room1.
				r1.setConnectedRooms(r1ConnectedRooms);
				r1.setDoors(r1Doors);
				r1.setRoomTeleportCoordsX(r1CoordX);
				r1.setRoomTeleportCoordsY(r1CoordY);
				r1.setMonsterArray(r1Monsters);
				
				// done setup for room 1.
			
				
				// Starting setup for room2: 
				
				// Creating collision mask for room 2.
				Rectangle[] room2c = {
							new Rectangle(0, 0, 32, windowHeight - 64 - 25),
							new Rectangle(0, 0, windowWidth, 32), 
							new Rectangle(windowWidth - 32, 0, windowWidth-32, windowHeight - 64 - 25), 
							new Rectangle(0, windowHeight - 64 - 25 - 32, windowWidth, 32),
							new Rectangle(224, 182, 64, 372),
							new Rectangle(512, 182, 64, 372),
							new Rectangle(32, 160, 96, 32),
							new Rectangle(96, 256, 128, 32),
							new Rectangle(32, 384, 128, 32),
							new Rectangle(32, 480, 32, 32),
							new Rectangle(128, 480, 96, 32),
							new Rectangle(288, 224, 160, 32),
							new Rectangle(384, 320, 128, 32),
							
							new Rectangle(288, 416, 90, 32),
							new Rectangle(416 + 4, 480, 92, 32),
							
							new Rectangle(576, 480, 96, 32),
							new Rectangle(736, 480, 32, 32),
							new Rectangle(576, 384, 128, 32),
							new Rectangle(576, 288, 32, 32),
							new Rectangle(704, 288, 64, 32),
							new Rectangle(736, 224, 32, 32),
							new Rectangle(576, 224, 64, 32)
									};
				
				// initializing door array and connected room array for r2.
				Rectangle[] r2Doors = new Rectangle[4]; 
				int[] r2ConnectedRooms = new int[4]; 
				int[] r2CoordX = new int[4];
				int[] r2CoordY = new int[4];
				Entity[] r2Monsters = new Entity[2];
				
				r2 = new Room(windowWidth, windowHeight - 64, room2c.length, r2Monsters.length); // initializing room1.
				r2.setCollisionSquares(room2c);
				
				
				// filling up the door array with rectangles. 0 is up, so the rectangle that is assigned to it is the one at the top of the room etc.
				r2Doors[0] = new Rectangle(384 - 32 - 64, 0, 64, 38);
				r2Doors[2] = new Rectangle(384 - 32 + 64, windowHeight - 64 - 25 - 32 - 5, 64, 38);
				
				// setting the coords for where they get teleported upon going through a room.
				r2CoordX[2] = 320 - 64; r2CoordY[2] = -25;
				r2CoordX[0] = 320 + 64; r2CoordY[0] = 593;
				
				// adding monsters/entities
				r2Monsters[0] = new Slime(100, 80);
				r2Monsters[1] = new Slime(580, 550);
				
				// Setting the rooms connected with this room
				r2ConnectedRooms[0] = 0; 
				r2ConnectedRooms[2] = 4;
				// assigning those arrays to room1.
				r2.setConnectedRooms(r2ConnectedRooms);
				r2.setDoors(r2Doors);
				r2.setRoomTeleportCoordsX(r2CoordX);
				r2.setRoomTeleportCoordsY(r2CoordY);
				r2.setMonsterArray(r2Monsters);
				
				// done setup for room 2.
				
				//starting setup for room 3
				
				//adding collision mask for room 3
				Rectangle[] room3c = {
						new Rectangle(0, 0, 32, windowHeight - 64 - 25),
						new Rectangle(0, 0, windowWidth, 32), 
						new Rectangle(windowWidth - 32, 0, windowWidth-32, windowHeight - 64 - 25), 
						new Rectangle(0, windowHeight - 64 - 25 - 32, windowWidth, 32),
						new Rectangle(128, 64, 96, 32),
						new Rectangle(32, 256, 96, 32),
						new Rectangle(224, 0, 64, 384),
						new Rectangle(288, 256, 256, 32),
						new Rectangle(480, 128, 32, 128),
						new Rectangle(384, 288, 32, 128),
						new Rectangle(192, 568, 62, 176),
						new Rectangle(96, 640, 96, 32),
						new Rectangle(672, 96, 96, 32),
						new Rectangle(672, 320, 96, 32),
						new Rectangle(576, 480, 64, 256),
						new Rectangle(480, 544, 96, 32),
						new Rectangle(642, 640, 96, 32)
								}; 
				
				// initializing door array and connected room array for r3.
				Rectangle[] r3Doors = new Rectangle[4]; 
				int[] r3ConnectedRooms = new int[4]; 
				int[] r3CoordX = new int[4];
				int[] r3CoordY = new int[4];
				Entity[] r3Monsters = new Entity[2];
				
				r3 = new Room(windowWidth, windowHeight - 64, room3c.length, r3Monsters.length); // initializing room1.
				r3.setCollisionSquares(room3c);
				
				// filling up the door array with rectangles. 0 is up, so the rectangle that is assigned to it is the one at the top of the room etc.
				r3Doors[2] = new Rectangle(384 - 32, windowHeight - 64 - 25 - 32 - 5, 64, 38);
				r3Doors[1] = new Rectangle(768, 128, 32, 70);
				r3Doors[3] = new Rectangle(0, 128, 35, 64);
				
				// setting the coords for where they get teleported upon going through a room.
				r3CoordX[0] = 320; r3CoordY[0] = 593;
				r3CoordX[3] = 664; r3CoordY[3] = 100;
				r3CoordX[1] = -4; r3CoordY[1] = 78;
				
				// Setting the rooms connected to room 3.
				r3ConnectedRooms[2] = 0;
				r3ConnectedRooms[1] = 3; 
				r3ConnectedRooms[3] = 5;
				
				// adding monsters / entities
				r3Monsters[0] = new Slime(300,420);
				r3Monsters[1] = new Slime(520,370);
				
				
				// assigning those arrays to room3.
				r3.setConnectedRooms(r3ConnectedRooms);
				r3.setDoors(r3Doors);
				r3.setRoomTeleportCoordsX(r3CoordX);
				r3.setRoomTeleportCoordsY(r3CoordY);
				r3.setMonsterArray(r3Monsters);
				
				// done setup for room 3
				
				// starting setup for room 4
				
				// creating collision mask for room 4
				Rectangle[] room4c = {
						new Rectangle(0, 0, 32, windowHeight - 64 - 25),
						new Rectangle(0, 0, windowWidth, 32), 
						new Rectangle(windowWidth - 32, 0, windowWidth-32, windowHeight - 64 - 25), 
						new Rectangle(0, windowHeight - 64 - 25 - 32, windowWidth, 32),
						new Rectangle(0, 320, 128, 32),
						new Rectangle(192, 160, 96, 32),
						new Rectangle(160, 416, 64, 320),
						new Rectangle(224, 544, 96, 32),
						new Rectangle(288, 0, 64, 320),
						new Rectangle(352, 96, 96, 32),
						new Rectangle(512, 160, 96, 32),
						new Rectangle(512, 416, 64, 320),
						new Rectangle(608, 0, 64, 320),
						new Rectangle(576, 480, 96, 32),
						new Rectangle(672, 576, 96, 32)

						
								}; 
				
				// initializing door array and connected room array for r4.
				Rectangle[] r4Doors = new Rectangle[4]; 
				int[] r4ConnectedRooms = new int[4]; 
				int[] r4CoordX = new int[4];
				int[] r4CoordY = new int[4];
				Entity[] r4Monsters = new Entity[2];
				
				r4 = new Room(windowWidth, windowHeight - 64, room4c.length, r4Monsters.length); // initializing room1.
				r4.setCollisionSquares(room4c);
				
				// filling up the door array with rectangles. 0 is up, so the rectangle that is assigned to it is the one at the top of the room etc.
				r4Doors[3] = new Rectangle(0, 128, 33, 64);
				r4Doors[2] = new Rectangle(672, windowHeight - 120 - 3, 64, 32);
				
				// setting the coords for where they get teleported upon going through a room.
				r4CoordX[1] = 0; r4CoordY[1] = 96;
				r4CoordX[0] = 630; r4CoordY[0] = windowHeight - 250;
				
				// Setting the rooms connected to room 4. 
				r4ConnectedRooms[3] = 2;
				r4ConnectedRooms[2] = 9;
				
				// adding the monsters to the room
				r4Monsters[0] = new Slime(400, 400);
                r4Monsters[1] = new Slime(700, 300);
				// assigning those arrays to room4.
				r4.setConnectedRooms(r4ConnectedRooms);
				r4.setDoors(r4Doors);
				r4.setRoomTeleportCoordsX(r4CoordX);
				r4.setRoomTeleportCoordsY(r4CoordY);
				r4.setMonsterArray(r4Monsters);
				// done room 4.
				
				

				// Starting setup for room5: 
				
				// Creating collision mask for room 5.
				Rectangle[] room5c = {
							new Rectangle(0, 0, 32, windowHeight - 64 - 25),
							new Rectangle(0, 0, windowWidth, 32), 
							new Rectangle(windowWidth - 32, 0, windowWidth-32, windowHeight - 64 - 25), 
							new Rectangle(0, windowHeight - 64 - 25 - 32, windowWidth, 32),
							new Rectangle(416, 0, 64, 298),
							new Rectangle(96 + 3*32, 96 + 4*32, 32*7, 32),
							new Rectangle(96 + 4*32, 96 + 5*32, 64, 32*11 + 10),
							new Rectangle(32, 96 + 8*32, 32*4, 32),
							new Rectangle(96, 96 + 11*32, 32*4, 32),
							new Rectangle(32, 96 + 14*32, 32*4, 32),
							new Rectangle(32*9, 96 + 14*32, 32*6, 32),
							new Rectangle(32*18, 96 + 3*32 - 10, 64, 32*16 + 10),
							new Rectangle(32*20, 96 + 16*32, 96, 32),
							new Rectangle(32*21 + 5, 96 + 13*32, 96, 32),
							new Rectangle(32*20, 96 + 10*32, 96, 32),
							new Rectangle(32*21 + 5, 96 + 7*32, 96, 32),
							new Rectangle(32*20, 96 + 4*32, 96, 32)
									};
				
				
				// initializing door array and connected room array for r5.
				Rectangle[] r5Doors = new Rectangle[4]; 
				int[] r5ConnectedRooms = new int[4]; 
				int[] r5CoordX = new int[4];
				int[] r5CoordY = new int[4];
				Entity[] r5Monsters = new Entity[3];
				
				r5 = new Room(windowWidth, windowHeight - 64, room5c.length, r5Monsters.length); // initializing room1.
				r5.setCollisionSquares(room5c); 
				
				// filling up the door array with rectangles. 0 is up, so the rectangle that is assigned to it is the one at the top of the room etc.
				r5Doors[0] = new Rectangle(384 - 32, 0, 64, 38);
				r5Doors[1] = new Rectangle(800 - 32, 64, 32, 64);
				
				// setting the coords for where they get teleported upon going through a room.

				r5CoordX[2] = 320; r5CoordY[2] = -25;
				r5CoordX[3] = 650; r5CoordY[3] = 0;
				
				// adding a monster to room 1.
				r5Monsters[0] = new Key(640 + 32 + 16, 640 + 16);
				r5Monsters[1] = new Slime(96, 96);
				r5Monsters[2] = new Slime(350, 350);
				
				// Setting the rooms connected to room 5
				r5ConnectedRooms[0] = 1;
				r5ConnectedRooms[1] = 7;
				
				// assigning those arrays to room5.
				r5.setConnectedRooms(r5ConnectedRooms);
				r5.setDoors(r5Doors);
				r5.setRoomTeleportCoordsX(r5CoordX);
				r5.setRoomTeleportCoordsY(r5CoordY);
				r5.setMonsterArray(r5Monsters);
				
				// done setup for room 5.
				
				// starting setup for room 6
				
				// Creating collision mask for room 6.
				Rectangle[] room6c = {
							new Rectangle(0, 0, 32, windowHeight - 64 - 25),
							new Rectangle(0, 0, windowWidth, 32), 
							new Rectangle(windowWidth - 32, 0, windowWidth-32, windowHeight - 64 - 25), 
							new Rectangle(0, windowHeight - 64 - 25 - 32, windowWidth, 32),
							new Rectangle(128, 32, 64, 32*16 + 10),
							new Rectangle(32*19, 32, 64, 32*16 + 10),
							new Rectangle(32*11, 32*8 - 10, 64, 32*15 + 10),
							new Rectangle(32*6, 32*4, 32*11, 32),
							new Rectangle(32*8, 32*9, 32*9, 32),
							new Rectangle(32*6, 32*13, 32*3, 32),
							new Rectangle(32*8, 32*18, 32*3, 32),
							new Rectangle(32*13, 32*15, 32*4, 32),
							new Rectangle(32*15, 32*12, 32*4, 32),
									};
				
				
				// initializing door array and connected room array for r6.
				Rectangle[] r6Doors = new Rectangle[4]; 
				int[] r6ConnectedRooms = new int[4]; 
				int[] r6CoordX = new int[4];
				int[] r6CoordY = new int[4];
				Entity[] r6Monsters = new Entity[2];
				
				r6 = new Room(windowWidth, windowHeight - 64, room6c.length, r6Monsters.length); // initializing room1.
				r6.setCollisionSquares(room6c); 
				
				// filling up the door array with rectangles. 0 is up, so the rectangle that is assigned to it is the one at the top of the room etc.
				r6Doors[3] = new Rectangle(0, 96, 35, 64);
				r6Doors[1] = new Rectangle(windowWidth - 38, 128, 35, 64);
				
				// setting the coords for where they get teleported upon going through a room.
				r6CoordX[1] = -6; r6CoordY[1] = 50;
				r6CoordX[3] = windowWidth - 155; r6CoordY[3] = 83;
				
				
				// adding a monster to room 1.
				r6Monsters[0] = new Slime(225, 180);
				r6Monsters[1] = new Slime(275, 180);
				
				// Setting the connected rooms to r6. 
				r6ConnectedRooms[3] = 6;
				r6ConnectedRooms[1] = 2;
				
				// assigning those arrays to room6.
				r6.setConnectedRooms(r6ConnectedRooms);
				r6.setDoors(r6Doors);
				r6.setRoomTeleportCoordsX(r6CoordX);
				r6.setRoomTeleportCoordsY(r6CoordY);
				r6.setMonsterArray(r6Monsters);
				
				// done setup for room 6.
				
				// Starting setup for room7: 
				
				// Creating collision mask for room 7.
				Rectangle[] room7c = {
							new Rectangle(0, 0, 32, windowHeight - 64 - 25),
							new Rectangle(0, 0, windowWidth, 32), 
							new Rectangle(windowWidth - 32, 0, windowWidth-32, windowHeight - 64 - 25), 
							new Rectangle(0, windowHeight - 64 - 25 - 32, windowWidth, 32),
							new Rectangle(156, 32, 32, 94),
							new Rectangle(156, 156 + 32, 32, 64),
							new Rectangle(128, 156 + 64, 32 * 20, 32),
							new Rectangle(32, 156 + 7 * 32, 32 * 20, 32),
							new Rectangle(128, 156 + 12 * 32, 32 * 20, 32),
							new Rectangle(156, 156 + 12 * 32, 32, 64),
							new Rectangle(156, 156 + 16 * 32, 32, 64),
							new Rectangle(32 * 18, 156 + 8 * 32, 32, 64),
							new Rectangle(32 * 18, 156 + 5 * 32, 32, 64),
									};
				
				
				// initializing door array and connected room array for r7.
				Rectangle[] r7Doors = new Rectangle[4]; 
				int[] r7ConnectedRooms = new int[4]; 
				int[] r7CoordX = new int[4];
				int[] r7CoordY = new int[4];
				Entity[] r7Monsters = new Entity[5];
				
				r7 = new Room(windowWidth, windowHeight - 64, room7c.length, r7Monsters.length); // initializing room1.
				r7.setCollisionSquares(room7c); 
				
				// filling up the door array with rectangles. 0 is up, so the rectangle that is assigned to it is the one at the top of the room etc.
				r7Doors[1] = new Rectangle(774, 604, 32, 64);
				
				
				// setting the coords for where they get teleported upon going through a room.
				r7CoordX[3] = 662; r7CoordY[3] = 550;
				
				// adding a monster to room 1.
				r7Monsters[0] = new Key(696, 64);
				r7Monsters[1] = new Slime(320, 50);
				r7Monsters[2] = new Slime(180, 250);
				r7Monsters[3] = new Slime(400, 382);
				r7Monsters[4] = new Slime(340, 572);
				// Setting the connected rooms to r7. 
				r7ConnectedRooms[1] = 5;
				
				
				// assigning those arrays to room7.
				r7.setConnectedRooms(r7ConnectedRooms);
				r7.setDoors(r7Doors);
				r7.setRoomTeleportCoordsX(r7CoordX);
				r7.setRoomTeleportCoordsY(r7CoordY);
				r7.setMonsterArray(r7Monsters);
				
				// done setup for room 7.
				
				// Starting setup for room8: 
				
				// Creating collision mask for room 8.
				Rectangle[] room8c = {
                        new Rectangle(0, 0, 32, windowHeight - 64 - 25),
                        new Rectangle(0, 0, windowWidth, 32), 
                        new Rectangle(windowWidth - 32, 0, windowWidth-32, windowHeight - 64 - 25), 
                        new Rectangle(0, windowHeight - 64 - 25 - 32, windowWidth, 32),
                        new Rectangle(64, 640, 64, 32),
                        new Rectangle(128, 448, 64, 290),
                        new Rectangle(192, 544, 96, 32),
                        new Rectangle(288, 96, 96, 32),
                        new Rectangle(384, 0, 64, 290),
                        new Rectangle(448, 192, 96, 32),
                        new Rectangle(544, 544, 96, 32),
                        new Rectangle(640, 448 ,64, 290)
                                };
				
				
				// initializing door array and connected room array for r8.
				Rectangle[] r8Doors = new Rectangle[4]; 
				int[] r8ConnectedRooms = new int[4]; 
				int[] r8CoordX = new int[4];
				int[] r8CoordY = new int[4];
				Entity[] r8Monsters = new Entity[2];
				
				r8 = new Room(windowWidth, windowHeight - 64, room8c.length, r8Monsters.length); // initializing room1.
				r8.setCollisionSquares(room8c); 
				
				// filling up the door array with rectangles. 0 is up, so the rectangle that is assigned to it is the one at the top of the room etc.
				r8Doors[0] = new Rectangle(560, 0, 64, 38);
				r8Doors[3] = new Rectangle(1, 64, 32, 64);
				
				// setting the coords for where they get teleported upon going through a room.

				r8CoordX[1] = 0; r8CoordY[1] = 20;
				r8CoordX[2] = 532; r8CoordY[2] = 0;
				
				// adding a monster to room 8.
				r8Monsters[0] = new Slime(128, 300);
                r8Monsters[1] = new Slime(400, 400);
                
				// Setting the connected rooms to r8. 
				r8ConnectedRooms[3] = 4;
				r8ConnectedRooms[0] = 8;
		
				// assigning those arrays to room8.
				r8.setConnectedRooms(r8ConnectedRooms);
				r8.setDoors(r8Doors);
				r8.setRoomTeleportCoordsX(r8CoordX);
				r8.setRoomTeleportCoordsY(r8CoordY);
				r8.setMonsterArray(r8Monsters);
				
				// done setup for room 8.
				
				// Starting setup for room9: 
				
				// Creating collision mask for room 9.
				Rectangle[] room9c = {
                        new Rectangle(0, 0, 32, windowHeight - 64 - 25),
                        new Rectangle(0, 0, windowWidth, 32), 
                        new Rectangle(windowWidth - 32, 0, windowWidth-32, windowHeight - 64 - 25), 
                        new Rectangle(0, windowHeight - 64 - 25 - 32, windowWidth, 32),
                        new Rectangle(129, 96, 96,  32),
                        new Rectangle(225, 0, 62, 224),
                        new Rectangle(577, 0, 62, 224),
                        new Rectangle(641, 96, 96, 32),
                        new Rectangle(673, 384, 96, 32),
                        new Rectangle(384, 448, 62, 295),
                        new Rectangle(288, 512, 96, 32),
                        new Rectangle(448, 544, 96, 32)
                                };
				
				
				// initializing door array and connected room array for r9.
				Rectangle[] r9Doors = new Rectangle[4]; 
				int[] r9ConnectedRooms = new int[4]; 
				int[] r9CoordX = new int[4];
				int[] r9CoordY = new int[4];
				Entity[] r9Monsters = new Entity[2];
				
				r9 = new Room(windowWidth, windowHeight - 64, room9c.length, r9Monsters.length); // initializing room1.
				r9.setCollisionSquares(room9c); 
				
				// filling up the door array with rectangles. 0 is up, so the rectangle that is assigned to it is the one at the top of the room etc.
				r9Doors[0] = new Rectangle(384 - 32, 0, 64, 38);
				r9Doors[2] = new Rectangle(560, 696, 64, 38);
				
				// setting the coords for where they get teleported upon going through a room.

				r9CoordX[2] = 320; r9CoordY[2] = -25;
				r9CoordX[0] = 530; r9CoordY[0] = 573;
				
				// adding a monster to room 9.
				r9Monsters[0] = new Slime(100, 300);
                r9Monsters[1] = new Slime(100, 500);
                
				// Setting the connected rooms to r9. 
				r9ConnectedRooms[0] = 9;
				r9ConnectedRooms[2] = 7;
						
				// assigning those arrays to room9.
				r9.setConnectedRooms(r9ConnectedRooms);
				r9.setDoors(r9Doors);
				r9.setRoomTeleportCoordsX(r9CoordX);
				r9.setRoomTeleportCoordsY(r9CoordY);
				r9.setMonsterArray(r9Monsters);
				
				// done setup for room 9.
				
// 				Starting setup for room 10: 
				
				// Creating collision mask for room 10.
				Rectangle[] room10c = {
                        new Rectangle(0, 0, 32, windowHeight - 64 - 25),
                        new Rectangle(0, 0, windowWidth, 32), 
                        new Rectangle(windowWidth - 32, 0, windowWidth-32, windowHeight - 64 - 25), 
                        new Rectangle(0, windowHeight - 64 - 25 - 32, windowWidth, 32),
                        new Rectangle(96, 96, 96, 32),
                        new Rectangle(192, 0, 64, 256),
                        new Rectangle(256, 192, 96, 32),
                        new Rectangle(0, 416, 128, 32),
                        new Rectangle(512, 192, 288, 32),
                        new Rectangle(640, 64, 32, 128),
                        new Rectangle(576, 224, 32, 128),
                        new Rectangle(544, 480, 64, 256),
                        new Rectangle(448, 544, 96, 32),
                        new Rectangle(608,608, 96, 32 )
                                };
				
				
				// initializing door array and connected room array for r10.
				Rectangle[] r10Doors = new Rectangle[4]; 
				int[] r10ConnectedRooms = new int[4]; 
				int[] r10CoordX = new int[4];
				int[] r10CoordY = new int[4];
				Entity[] r10Monsters = new Entity[2];
				
				r10 = new Room(windowWidth, windowHeight - 64, room10c.length, r10Monsters.length); // initializing room1.
				r10.setCollisionSquares(room10c); 
				
				// filling up the door array with rectangles. 0 is up, so the rectangle that is assigned to it is the one at the top of the room etc.
				r10Doors[0] = new Rectangle(384 - 32, 0, 64, 38);
				r10Doors[1] = new Rectangle(768,256, 35, 64);
				r10Doors[2] = new Rectangle(384 - 32, windowHeight - 126, 64, 38);
				
				// setting the coords for where they get teleported upon going through a room.

				r10CoordX[2] = 320; r10CoordY[2] = -25;
				r10CoordX[0] = 320; r10CoordY[0] = 593;
				r10CoordX[3] = 664; r10CoordY[3] = 214;
				
				// adding a monster to room 10.
				r10Monsters[0] = new Slime(200, 336);
                r10Monsters[1] = new Slime (144, 480);
				
				// Setting the connected rooms to r10. 
				r10ConnectedRooms[0] = 3;
				r10ConnectedRooms[1] = 10;
				r10ConnectedRooms[2] = 8;
				
				// assigning those arrays to room10.
				r10.setConnectedRooms(r10ConnectedRooms);
				r10.setDoors(r10Doors);
				r10.setRoomTeleportCoordsX(r10CoordX);
				r10.setRoomTeleportCoordsY(r10CoordY);
				r10.setMonsterArray(r10Monsters);
				
				// done setup for room 10.
				
				// Starting setup for room10: 
				
				// Creating collision mask for room 11.
				Rectangle[] room11c = {
                        new Rectangle(0, 0, 32, windowHeight - 64 - 25),
                        new Rectangle(0, 0, windowWidth, 32), 
                        new Rectangle(windowWidth - 32, 0, windowWidth-32, windowHeight - 64 - 25), 
                        new Rectangle(0, windowHeight - 64 - 25 - 32, windowWidth, 32),
                        new Rectangle(96, 160, 64, 588),
                        new Rectangle(224, 0, 64, 588),
                        new Rectangle(292, 96, 64, 32),
                        new Rectangle(445, 96, 64, 32),
                        new Rectangle(509, 32, 32, 224),
                        new Rectangle(509, 480, 32, 224)
                                };
				
				
				// initializing door array and connected room array for r11.
				Rectangle[] r11Doors = new Rectangle[4]; 
				int[] r11ConnectedRooms = new int[4]; 
				int[] r11CoordX = new int[4];
				int[] r11CoordY = new int[4];
				Entity[] r11Monsters = new Entity[4];
				
				r11 = new Room(windowWidth, windowHeight - 64, room11c.length, r11Monsters.length); // initializing room1.
				r11.setCollisionSquares(room11c); 
				
				// filling up the door array with rectangles. 0 is up, so the rectangle that is assigned to it is the one at the top of the room etc.
				r11Doors[0] = new Rectangle(384 - 32 - 32, 0, 64, 38);
				r11Doors[3] = new Rectangle(0 ,256, 35, 64);
				r11Doors[1] = new Rectangle(768, 256, 35, 64);
				
				// setting the coords for where they get teleported upon going through a room.

				r11CoordX[2] = 320 - 32; r11CoordY[2] = -25;
				r11CoordX[1] = -5; r11CoordY[1] = 208;
				
				// adding a monster to room 11.
				r11Monsters[0] = new Slime(365, 300);
                r11Monsters[1] = new Slime (600, 250);
                r11Monsters[2] = new Slime(600, 425);
                r11Monsters[3] = new Slime(365, 400);
				
				// Setting the connected rooms to r11. 
				r11ConnectedRooms[0] = 11;
				r11ConnectedRooms[3] = 9;
				r11ConnectedRooms[1] = 12;
				
				
				// assigning those arrays to room11.
				r11.setConnectedRooms(r11ConnectedRooms);
				r11.setDoors(r11Doors);
				r11.setRoomTeleportCoordsX(r11CoordX);
				r11.setRoomTeleportCoordsY(r11CoordY);
				r11.setMonsterArray(r11Monsters);
				
				// done setup for room 11.
				
				// Starting setup for room12: 
				
				// Creating collision mask for room 12.
				Rectangle[] room12c = {
							new Rectangle(0, 0, 32, windowHeight - 64 - 25),
							new Rectangle(0, 0, windowWidth, 32), 
							new Rectangle(windowWidth - 32, 0, windowWidth-32, windowHeight - 64 - 25), 
							new Rectangle(0, windowHeight - 64 - 25 - 32, windowWidth, 32),
							new Rectangle(32 * 3, 32 * 7, 32 * 8, 32),
							new Rectangle(32 * 5, 32 * 6, 32, 32 * 13),
							new Rectangle(32 * 20, 32 * 4, 32, 32 * 14),
							new Rectangle(32 * 13, 32 * 16, 32 * 9, 32),
							new Rectangle(32 * 11, 32 * 1 + 10, 64, 32 * 8),
							new Rectangle(32 * 11, 32 * 15 - 10, 64, 32 * 8),
									};
				
				
				// initializing door array and connected room array for r12.
				Rectangle[] r12Doors = new Rectangle[4]; 
				int[] r12ConnectedRooms = new int[4]; 
				int[] r12CoordX = new int[4];
				int[] r12CoordY = new int[4];
				Entity[] r12Monsters = new Entity[3];
				
				r12 = new Room(windowWidth, windowHeight - 64, room12c.length, r12Monsters.length); // initializing room1.
				r12.setCollisionSquares(room12c); 
				
				// filling up the door array with rectangles. 0 is up, so the rectangle that is assigned to it is the one at the top of the room etc.
				r12Doors[2] = new Rectangle(480, 708, 64, 32);

				
				// setting the coords for where they get teleported upon going through a room.
				
				r12CoordX[0] = 436; r12CoordY[0] = 580;
				
				// adding a monster to room 12.
				r12Monsters[0] = new Key(278, 145);
				r12Monsters[1] = new Slime(450, 300);
				r12Monsters[2] = new Slime(250, 400);
				// Setting the connected rooms to r12.
				r12ConnectedRooms[2] = 10;
				
				
				// assigning those arrays to room12.
				r12.setConnectedRooms(r12ConnectedRooms);
				r12.setDoors(r12Doors);
				r12.setRoomTeleportCoordsX(r12CoordX);
				r12.setRoomTeleportCoordsY(r12CoordY);
				r12.setMonsterArray(r12Monsters);
				
				// done setup for room 12.

				// Creating collision mask for room 13.
				Rectangle[] room13c = {
							new Rectangle(0, 0, 32, windowHeight - 64 - 25),
							new Rectangle(0, 0, windowWidth, 32), 
							new Rectangle(windowWidth - 32, 0, windowWidth-32, windowHeight - 64 - 25), 
							new Rectangle(0, windowHeight - 64 - 25 - 32, windowWidth, 32),
									};
				
				
				// initializing door array and connected room array for r13.
				Rectangle[] r13Doors = new Rectangle[4]; 
				int[] r13ConnectedRooms = new int[4]; 
				int[] r13CoordX = new int[4];
				int[] r13CoordY = new int[4];
				Entity[] r13Monsters = new Entity[1];
				
				r13 = new Room(windowWidth, windowHeight - 64, room13c.length, r13Monsters.length); // initializing room1.
				r13.setCollisionSquares(room13c); 
				
				
				// setting the coords for where they get teleported upon going through a room. ie, if they come into a room from the left, left is represented by 1, so their coords will be set to the ones associated with the first position in the array.
				
				r13CoordX[1] = 0; r13CoordY[1] = 300;
				
				// adding a monster to room 13.
				r13Monsters[0] = new Boss(500, 500);
				
				// Setting the connected rooms to r13. 
			
				
				// assigning those arrays to room13.
				r13.setConnectedRooms(r13ConnectedRooms);
				r13.setDoors(r13Doors);
				r13.setRoomTeleportCoordsX(r13CoordX);
				r13.setRoomTeleportCoordsY(r13CoordY);
				r13.setMonsterArray(r13Monsters);
				
				// done setup for room 13.
				
				
				
				//All of the rooms collision rectangle arrays are being added here.
				roomHitBoxes[0] = room1c;
				roomHitBoxes[1] = room2c;
				roomHitBoxes[2] = room3c;
				roomHitBoxes[3] = room4c;
				roomHitBoxes[4] = room5c;
				roomHitBoxes[5] = room6c;
				roomHitBoxes[6] = room7c;
				roomHitBoxes[7] = room8c;
				roomHitBoxes[8] = room9c;
				roomHitBoxes[9] = room10c;
				roomHitBoxes[10] = room11c;
				roomHitBoxes[11] = room12c;
				roomHitBoxes[12] = room13c;
				
				// Adding all of the rooms.
				allrooms[0] = r1;
				allrooms[1] = r2;
				allrooms[2] = r3;
				allrooms[3] = r4;
				allrooms[4] = r5;
				allrooms[5] = r6;
				allrooms[6] = r7;
				allrooms[7] = r8;
				allrooms[8] = r9;
				allrooms[9] = r10;
				allrooms[10] = r11;
				allrooms[11] = r12;
				allrooms[12] = r13;
				

				

				// Loading all the rooms when the game starts
				
				for(int i = 0; i < allrooms.length; i++)
				{
					loadRoom(i);
					System.out.println(i);
				}

				/*
				loadRoom(0);
				loadRoom(1);
				loadRoom(2);		
				loadRoom(3);
				loadRoom(4);
				loadRoom(5);
				loadRoom(6);
				loadRoom(7);
				loadRoom(8);
				loadRoom(9);
				loadRoom(10);
				loadRoom(11);
				loadRoom(12);
				*/
				
				// This is where the loading screen comes in. Load in all sprites.
				
				System.out.println("Artificial delay on loading screen is enabled.");
				DecimalFormat df = new DecimalFormat("000");
				for(int i = 0; i < imagestotal; i++)
				{
					try
					{
						Thread.sleep(5); // WARNING: Artificial delay to make the loading screen look cool. Maybe remove on launch.
					}
					catch(Exception e) 
					{
						e.printStackTrace();
					}

					spritesFull[i] = getScaledImage(new ImageIcon("tiles/tile" + df.format(i) + ".png").getImage(), 32, 32);
					loadingImageCount = i;
					
					if(i == imagestotal - 1)
					{
						loadingComplete = true;
					}
				}

				
				keysCollected = 0;
				timeMins = 0;
				timeSeconds = 0;
				monstersKilled = 0;
				p1.setAttacking(false);
				onlyonce = true;
				onlyonce3 = true;
				onlyonce4 = true;
				onlyonce6 = true;
			}
		};

		worker.start();
	}

	
	private static void loadRoom(int room)
	{
		// This is where each map file is read, then the room is saved to the allrooms array.
		int[][] arr = new int[windowWidth/32][(windowHeight - 64 - 25)/32];

		Scanner scn;
		
		// Taking input from the room text files. 
		try
		{
			scn = new Scanner(new FileReader("rooms/room" + (room + 1) + ".txt"));
			for (int i = 0; i < (windowHeight - 64 - 25) / 32; i++) 
			{
				for (int c = 0; c < windowWidth / 32; c++) 
				{
					int n = scn.nextInt();
					arr[c][i] = n;
				}
			}
			
		}
		catch(FileNotFoundException e)
		{
			e.printStackTrace();
		}	
		// Actually constructs the room
		allrooms[room].CreateRoom(arr);
	}
	
	/**
	 * <h1>Checks and takes action upon monster collision</h1>
	 * checks to see if two given monsters are colliding, and makes sure that they can't move into each other.
	 * <p>
	 * This is arguably useless, but it helps to prevent monsters getting stacked on top of each other into essentially one monster.
	 */
	private void doesMonsterCollisionOccur(Entity m1, Entity m2)
	{
		if(m1.getHitbox() != null && m2.getHitbox() != null)
		{
			if(m1.getHitbox().intersects(m2.getHitbox()) && m1 != m2)
			{
				int[] centerX = {p1.getPosX() + p1.getPlayerSpriteDimension()/2, p1.getPosY() + p1.getPlayerSpriteDimension()/2};
				int[] centerY = {m1.getxPos() + 64/2, m1.getyPos() + 64/2};
				
				if(centerX[0] - centerY[0] != 0)
				{

					m1.setyPos(m1.getPyPos());
				}
				
				if(centerX[1] - centerY[1] != 0)
				{
					m1.setxPos(m1.getPxPos());
				}

			}
		}
	}
	
	/**
	 * <h1>Scales an image</h1>
	 * Scales the image to the given width and height.
	 * <p>
	 * This is used all over the place to scale images. Extremely helpful in multiple different contexts, sometimes it needs to be modified to fit however.
	 */
	private static Image getScaledImage(Image srcImg, int w, int h)
	{
	    BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
	    Graphics2D g2 = resizedImg.createGraphics();

	    //g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	    g2.drawImage(srcImg, 0, 0, w, h, null);
	    g2.dispose();
	    return resizedImg;
	}
	
	
	/*
	 

	// this is the audio stram the music is played on.
	private static AudioStream audiostream;	
	
	
	/**
	 * <h1>Plays music</h1>
	 * Calling this method plays the background music once.
	 * <p>
	 * This is the main method for music in this application, there are no other methods that start music.
	 
	public static void playMusic(File f) 
	{
		InputStream music;
		try
		{
			music = new FileInputStream(f);
			audiostream = new AudioStream(music);
			AudioPlayer.player.start(audiostream);
			
		}
		catch (Exception e)
		{
			e.printStackTrace(); // tell me what's wrong...
		}
		
	}
	
	/**
	 * <h1>Stops the music</h1>
	 * Calling this method stops the background music permanently.
	 * <p>
	 * This is the way to stop the music if needed, it is usually called to stop then restart the music.
	 
	public static void stopMusic()
	{
		AudioPlayer.player.stop(audiostream);
	}
	
	*/
}
