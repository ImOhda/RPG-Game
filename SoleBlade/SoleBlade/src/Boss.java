import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

import javax.swing.ImageIcon;

public class Boss extends Entity
{
	private int detectionRange; // the detection range of the boss.
	private int attackRange; // the range in which it will attack you.
	
	// all these are frame counters used to cause small delays:
	private int startTime = 0, iframecounter = 0, fakeFrameDelay = 0, framecounterattack = 0, framecountercombo = 0, playerimmortalitycounter = 0, deathframecounter = 0;
	
	// these are booleans that are used throughout the boss class to make him do multiple things:
	private boolean hit = false, immortal = false, lprevious, left, stop = false, gettingup = false, startattack = false, attackL = false, typeofattack = false, playerimmortal = false, dying = false, onlyonce = true;
	
	// image arrays that hold all the images for each of the events.
	Image[] king_attack;
	Image[] king_combo;
	Image[] king_comboL;
	Image[] king_die;
	
	// decimal format object used throughout the loading part of the Boss() constructor.
	DecimalFormat df = new DecimalFormat("00");
	
	/**
	 * <h1>Boss Creation</h1>
	 * initializes the boss and loads all of its images for each action.
	 * <p>
	 * @param x - x value of the boss.
	 * @param y - y value of the boss.
	 */
	Boss(int x, int y)
	{
		// set up total hp, xpos, ypos, detectionrange, attackrange, and initialize king_attack, king_combo, king_comboL, and king_die.
		totalhp = 500;
		hp = totalhp;
		xPos = x;
		yPos = y;
		detectionRange = 600;
		attackRange = 50;
		king_attack = new Image[30];
		king_combo = new Image[58];
		king_comboL = new Image[58];
		king_die = new Image[37];
		
		// load in the images for king_die.
		for(int i = 0; i < 37; i++)
		{
			king_die[i] = getScaledImage(new ImageIcon("images/king_boss/dying/frame_" + df.format(i) + "_delay-0.1s.png").getImage(), 300, 300);
		}
		
		// load in the images for king_attack.
		for(int i = 0; i < 30; i++)
		{
			king_attack[i] = getScaledImage(new ImageIcon("images/king_boss/attack/frame_" + df.format(i) + "_delay-0.1s.png").getImage(), 300, 300);
		}
		
		// load in the images for king_combo and king_comboL.
		for(int i = 0; i < 58; i++)
		{
			king_combo[i] = getScaledImage(new ImageIcon("images/king_boss/combo/frame_" + df.format(i) + "_delay-0.1s.png").getImage(), 300, 300);
			king_comboL[i] = getScaledImage(new ImageIcon("images/king_boss/comboL/frame_" + df.format(i) + "_delay-0.1s.png").getImage(), 300, 300);
		}
	}
	
	/**
	 * <h1>Moves the boss</h1>
	 * moves the boss and does all of the actions he takes.
	 * <p>
	 * @param p1 - the player he is moving towards.
	 * @param rc - the room he is in.
	 */
	public void Move(Player p1, Room rc) 
	{	
		// if hp is less then or equal to 0, start the dying process.
		if(hp <= 0)
		{
			dying = true; 
		}
		
		// if dying is true and we are on the last frame of death animation, dead is set to true.
		if(dying && deathframecounter == 36)
		{
			dead = true;
		}

		// if nothing is null and not dying, and p1 hitbox intersects the bosses attackbox while player is not immortal:
		if(p1.getCollisionRect() != null && attackbox != null && !dying) {
			if(p1.getCollisionRect().intersects(attackbox) && !playerimmortal)
			{
				// player is now immortal for a second and he looses 20 hp.
				playerimmortal = true;
				p1.setHealth(p1.getHealth() - 20);
			}
		}

		// if player ones collision box and bosses hitbox is not null and boss is not dying, and players hitbox intersects the bosses hitbox while player is not immortal:
		if(p1.getCollisionRect() != null && hitbox != null && !dying) {
			if(p1.getCollisionRect().intersects(hitbox) && !playerimmortal)
			{
				// set player to immortal and health to itself -10.
				playerimmortal = true;
				p1.setHealth(p1.getHealth() - 10);
			}
		}
		
		// if the player is immortal count up until 100 then he is not immortal anymore.
		if(playerimmortal)
		{
			playerimmortalitycounter++;
			if(playerimmortalitycounter == 100)
			{
				playerimmortalitycounter = 0;
				playerimmortal = false;
			}
		}
		
		// if you boss is hit and not immortal, he looses 20 hp and becomes immortal for a second/
		if(hit && !immortal)
		{
			hp -= 20;
			immortal = true;
			hit = false;
		}
		else
		{
			hit = false; // if he is hit while immortal he is no longer hit.
		}
		
		// if he is immortal count up untill 120 then he is not immortal anymore.
		if(immortal)
		{
			iframecounter++;
			
			if(iframecounter == 120)
			{
				immortal = false;
				iframecounter = 0;
			}
		}
		
		// if he is not getting up count till 400 then he gets up.
		if(!gettingup)
		{
			startTime++;
			if(startTime == 400)
			{
				gettingup = true;
			}
		}
		else
		{
			// if he has got up, then create his hitbox.
			hitbox = new Rectangle(xPos + 30, yPos + 20, 24, 64);
	
			// find the center of the player and the center of himself.
			int[] centerX = {p1.getPosX() + p1.getPlayerSpriteDimension()/2, p1.getPosY() + p1.getPlayerSpriteDimension()/2};
			int[] centerY = {xPos + 64/2, yPos + 64/2};
	
			// find the absolute distance between the x and y of the two.
			int pyA = Math.abs(centerX[0] - centerY[0]);
			int pyB = Math.abs(centerX[1] - centerY[1]);
			
			// find the hypotenuse of the x and the y that is the distance between the x position of the player and boss and the y position of the player and boss, if that hypotenuse is less then the detection range then do the following:
			if(Math.sqrt(pyA*pyA + pyB*pyB) < detectionRange && !stop && !dying)
			{		
				// if the boss is right of the player, move left.
				if(centerX[0] - centerY[0] < 0)
				{
					pxPos = xPos;
					xPos--;
					lprevious = left; // set lprevious to the current left and change left to true.
					left = true;
	
				}
				else // if the boss is left of the player, move right.
				{
					pxPos = xPos;
					xPos++;
					lprevious = left; // set lprevious to the current left and change left to false.
					left = false;
	
				}

				// if the boss is below the player, move up.
				if(centerX[1] - centerY[1] < 0)
				{
					pyPos = yPos;
					yPos--;
				}
				else
				{
					// if the boss is up the player, move down.
					pyPos = yPos;
					yPos++;
				}
			}
			
			// find the hypotenuse of the x and the y that is the distance between the x position of the player and boss and the y position of the player and boss, if that hypotenuse is less then the attack range then do the following:
			if(Math.sqrt(pyA*pyA + pyB*pyB) < attackRange && !dying)
			{
				// set stop to true and startattack to true.
				stop = true;
				startattack = true;
				
				// if the player is to the left of the boss attackL is true:
				if(centerX[0] - centerY[0] < 0)
				{
					attackL = true;
				}
				else // if the player is to the right of the boss attackR is true:
				{
					attackL = false;
				}

			}
			
			// this is the dash that happens if the frame is 18 on the downwards attack.
			if(Math.sqrt(pyA*pyA + pyB*pyB) < detectionRange && !dying && typeofattack && framecounterattack == 18 && onlyonce)
			{
				onlyonce = false;
				// move left or right based on where boss is relative to the player
				if(centerX[0] - centerY[0] < 0)
				{
					xPos -= 20;
				}
				else
				{
					xPos += 20;
				}

				// move up or down based on where boss is relative to the player
				if(centerX[1] - centerY[1] < 0)
				{
					yPos -= 20;
				}
				else
				{
					yPos += 20;
				}
			}
			else
			{
				// otherwise keep only once to true.
				onlyonce = true;
			}
			
		}
	}
	
	/**
	 * <h1>When its hit by the player</h1>
	 * moves the boss and does all of the actions he takes.
	 * <p>
	 * @param p - the player he is getting hit by.
	 */
	public void Hit(Player p)
	{
		// if nothing is null and the player is attacking && he is not hit.
		if(p.getAttackRectL() != null && p.getAttackRectR() != null && hitbox != null && p.isAttacking() && !hit)
		{
			if(p.getFacing() == 0 && p.getAttackRectL().intersects(hitbox)) // check if hes facing left and if he is check if his left attack rectangle hits
			{
				// if it does set hit to true.
				hit = true;
			}
			if(p.getFacing() == 1 && p.getAttackRectR().intersects(hitbox)) // check if hes facing right and if he is check if his right attack rectangle hits
			{
				// if it does set hit to true.
				hit = true;
			}
			
		}
	}
	
	/**
	 * <h1>draws the boss</h1>
	 * draws the boss depending on booleans defined at the top.
	 */
	public void Draw(Graphics2D g2) 
	{
		// draw his hp:
		g2.drawRect(xPos - 40, yPos - 20, 150, 8);
		g2.fillRect(xPos - 40, yPos - 20,  (int)(150 * (double)hp/totalhp), 8);
		
		
		// if he's dying iterate through the dying animation with a 15 frame delay between each one.
		if(dying)
		{
			fakeFrameDelay++;
			if(fakeFrameDelay%15 == 0)
			{
				deathframecounter++;
			}
			
			g2.drawImage(king_die[deathframecounter], xPos - 110, yPos - 80, null);
		}
		else if(startattack) // else if start attack is true:
		{
			if(typeofattack) // check which type of attack it is, it typeofattack is true then it is the smash down.
			{
				// draw the smash attack
				g2.drawImage(king_attack[framecounterattack], xPos - 110, yPos - 80, null);
				

				fakeFrameDelay++;
	
				// create the attack box on the frames where it shows up, otherwise attack box is null
				if(framecounterattack >= 19 && framecounterattack <= 22)
				{
					attackbox = new Rectangle(xPos - 20, yPos, 110, 100);
				}
				else
				{
					attackbox = null;
				}
				
				// iterate through each frame every 15 of fakeFrameDelay
				if(fakeFrameDelay%15 == 0)
				{
					fakeFrameDelay = 0;
					framecounterattack++;
					
					// once it is done, swap the type of attack, set framecounterattack back to 0, set stop and startattack back to false.
					if(framecounterattack == 30)
					{
						framecounterattack = 0;
						startattack = false;
						stop = false;
						typeofattack = !typeofattack;
					}
				}
			}
			else
			{
				// for the other attack:
				fakeFrameDelay++;
				
				// for every 15 of fakeFrameDelay, increase the framecountercombo by 1.
				if(fakeFrameDelay% 15 == 0)
				{
					fakeFrameDelay = 0;
					framecountercombo++;
					// when it is done set framecountercombo back to 0 and swap the typeofattack. set startattack and stop back to false.
					if(framecountercombo == 58)
					{
						framecountercombo = 0;
						startattack = false;
						stop = false;
						typeofattack = !typeofattack;
					}
				}
				
				// if attackL is true
				if(attackL)
				{
					// set the attack box to these rectangles on these frames
					if(framecountercombo >= 20 && framecountercombo <= 24)
					{
						attackbox = new Rectangle(xPos - 10, yPos - 50, 105, 150);
					}
					else if(framecountercombo >= 36 && framecountercombo <= 40)
					{
						attackbox = new Rectangle(xPos - 40, yPos, 40, 100);
					}
					else
					{
						// otherwise set it to null
						attackbox = null;
					}
					
					// draw the king
					g2.drawImage(king_comboL[framecountercombo], xPos - 110, yPos - 80, null);
				}
				else // if attackR is true
				{
					// set the attack box to these rectangles on these frames
					if(framecountercombo >= 20 && framecountercombo <= 24)
					{
						attackbox = new Rectangle(xPos - 10, yPos - 50, 105, 150);
					}
					else if(framecountercombo >= 36 && framecountercombo <= 40)
					{
						attackbox = new Rectangle(xPos + 80, yPos, 40, 100);
					}
					else
					{
						// otherwise set attackbox to null.
						attackbox = null;
					}
					
					// draw the king
					g2.drawImage(king_combo[framecountercombo], xPos - 110, yPos - 80, null);
				}
			}
		}
		else if(!gettingup) // if not getting up then just draw the idle king.
		{
			g2.drawImage(getScaledImage(new ImageIcon ("images/king_boss/gifs/king_idle.gif").getImage(), 300, 300), xPos - 110, yPos - 80, null);
		}
		else
		{
			// otherwise draw the normal king walking.
			if(lprevious == !left)
			{
				g2.drawImage(getScaledImage(new ImageIcon ("images/king_boss/gifs/king_walk.gif").getImage(), 300, 300), xPos - 110, yPos - 80, null);
			}
			else if(left)
			{
				g2.drawImage(getScaledImage(new ImageIcon ("images/king_boss/gifs/king_walkL.gif").getImage(), 300, 300), xPos - 110, yPos - 80, null);
			}
			else
			{
		
				g2.drawImage(getScaledImage(new ImageIcon ("images/king_boss/gifs/king_walk.gif").getImage(), 300, 300), xPos - 110, yPos - 80, null);
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
	    Graphics2D g = resizedImg.createGraphics();

	    //g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	    g.drawImage(srcImg, 0, 0, w, h, null);
	    g.dispose();
	    return resizedImg;
	}
}
