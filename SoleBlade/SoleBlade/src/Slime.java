import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

public class Slime extends Entity
{
	// the detection range is the range in which the slime will start to follow you.
	private int detectionRange = 200;
	private int counter = 0, scounter = 0, hcounter = 0, dcounter = 0, dieing = 0; // these count values, once these counters reach certain values they are reset and something happens.
	private boolean left = false, lprevious, stop = false, hitplayer = false, hit = false, dead = false;
	// these booleans are for whether the slime is facing left or not, what the last direction the slime was facing is, whether the slime hit the player, whether the player hit the slime, and finally whether it is dead or not.
	
	
	/**
	 * <h1> The constructor for the slime object. </h1>
	 * @param xpos - x-position of the slime
	 * @param ypos - y-position of the slime.
	 */
	Slime(int xpos, int ypos)
	{
		xPos = xpos;
		yPos = ypos;
		pxPos = xPos;
		pyPos = yPos;
		hp = 100;
		totalhp = 100;
		detectionRange = 200;
	}

	/**
	 * <h1> Method that checks if the player has interacted with the slime. (attacked) </h1>
	 */
	public void Hit(Player p)
	{
		if(p.getAttackRectL() != null && p.getAttackRectR() != null && hitbox != null && p.isAttacking() && !hit)
		{
			if(p.getFacing() == 0 && p.getAttackRectL().intersects(attackbox))
			{
				hit = true;
			}
			if(p.getFacing() == 1 && p.getAttackRectR().intersects(attackbox))
			{

				hit = true;
			}
			
		}
	}
	
	/**
	 * <h1>Moves the slime</h1>
	 * This method should be called every second, it is responsible for most of the slimes actions.
	 * <p>
	 * this method deals with the slime hitting the player, player hitting the slime, dealing damage, and dying.
	 */
	public void Move(Player p1, Room rc) 
	{
		// Increment counter by one.
		counter++;
		
		// if hit:
		if(hit)
		{
			// Increment dcounter by one.
			dcounter++;
			
			// if dcounter is 70:
			if(dcounter == 70)
			{
				// hit is false
				hit = false;
				// set dcounter to 0
				dcounter = 0;
			}
			
			// if dcounter is 20:
			if(dcounter == 20)
			{
				// stop the slime
				stop = true;
				
				// subtract from hp.
				hp -= p1.getAttackdamage();
			}
		}
		
		// if stop is true:
		if(stop)
		{
			// Increment scounter by one.
			scounter++;
			
			// if scounter is 30
			if(scounter == 30)
			{
				//stop is false and scounter is 0
				stop = false;
				scounter = 0;
			}
		}

		// if hit player is true:
		if(hitplayer)
		{
			// Increment hcounter by one.
			hcounter++;
			
			// if hcounter is 80:
			if(hcounter == 80)
			{
				// hitplayer is false and hcounter is 0
				hcounter = 0;
				hitplayer = false;
			}
		}

		
		// if the player has not yet been hit, and both attackbox and player ones collision rectangle are not null:
		if(p1.getCollisionRect() != null && attackbox != null && !hitplayer)
		{
			// if player ones collision rectangle intersects the attackbox:
			if(p1.getCollisionRect().intersects(attackbox))
			{
				// move to the previous x and y position:
				xPos = pxPos;
				yPos = pyPos;
				
				// stop is true, hitplayer is true, and set the players health to itself -10.
				stop = true;
				p1.setHealth(p1.getHealth() - 10);
				hitplayer = true;
			}
		}
		
		
		// if slime is not dead:
		if(!dead)
		{
			// the hitbox and attackbox are the same, both being the rectangle around the slime.
			hitbox = new Rectangle(xPos + 10, yPos + 35, 64 - 20, 64 - 45);
			attackbox = hitbox;
		}
		else
		{
			// if slime is dead, rectangle is nothing and attackbox is also nothing.
			hitbox = new Rectangle(0,0,0,0);
			attackbox = hitbox;
		}

		
		// this stuff is complex, find the center of player, the center of this slime:
		int[] centerX = {p1.getPosX() + p1.getPlayerSpriteDimension()/2, p1.getPosY() + p1.getPlayerSpriteDimension()/2};
		int[] centerY = {xPos + 64/2, yPos + 64/2};

		// find the distance between the two centers:
		int pyA = Math.abs(centerX[0] - centerY[0]);
		int pyB = Math.abs(centerX[1] - centerY[1]);
		
		// if the hypotenuse between the x and y distance between the player and the slime is less then the detection range and counter % 4 is 0 and stop is not true:
		if(Math.sqrt(pyA*pyA + pyB*pyB) < detectionRange && counter%4 == 0 && !stop)
		{
			// set counter back to 0.
			counter = 0;
			
			// if the players x - the slimes x is less then 0, then the slime is left of the player:
			if(centerX[0] - centerY[0] < 0)
			{
				// set the previous xpos to the current one then decrease the current xpos by 1.
				pxPos = xPos;
				xPos--;
				// set lprevious to the current left and set left to true afterwards.
				lprevious = left; 
				left = true;

			}
			else
			{
				// set the previous xpos to the current one then increase the current xpos by 1.
				pxPos = xPos;
				xPos++;
				// set lprevious to the current left and set left to true afterwards.
				lprevious = left;
				left = false;

			}
			
			if(centerX[1] - centerY[1] < 0)
			{
				// set the previous ypos to the current one then decrease the current ypos by 1.
				pyPos = yPos;
				yPos--;
			}
			else
			{
				// set the previous ypos to the current one then increase the current ypos by 1.
				pyPos = yPos;
				yPos++;
			}
		}
		
		// if hp is 0:
		if(hp == 0)
		{
			// start increasing dieing variable:
			dieing++;
			
			// once its 10, increase the number of monsters killed, set dead to true, add 5 to the players hp if he has less then 100, and set the x and y pos to something crazy like -200 to get it off the screen.
			if(dieing == 10)
			{
				Main.monstersKilled++;
				dead = true;
				if(p1.getHealth() + 5 <= 100)
				{
					p1.setHealth(p1.getHealth() + 5);
				}
				xPos = -200;
				yPos = -200;
			}
		}
	}

	/**
	 * <h1>Draws the slime</h1>
	 * This method should be called every frame, it is responsible for drawing the slimes actions.
	 * <p>
	 * this method deals with everything that is displayed from the slime entity.
	 */
	public void Draw(Graphics2D g2) 
	{
		// if not dead:
		if(!dead)
		{
			// draw the health bar of the slime entity.
			g2.drawRect(xPos, yPos - 20, 80, 8);
			g2.fillRect(xPos, yPos - 20,  (int)(80 * (double)hp/totalhp), 8);
			
			// draw the image based on whether it is facing left or not left. On the edge case that it is heading up, instead of alternating between the two, just defult to basic slime facing right.
			if(lprevious == !left)
			{
				g2.drawImage(new ImageIcon ("images/monsterSprites/editedSlime.gif").getImage(), xPos, yPos, null);
			}
			else if(left)
			{
				g2.drawImage(new ImageIcon ("images/monsterSprites/editedSlimeL.gif").getImage(), xPos, yPos, null);
			}
			else
			{
				g2.drawImage(new ImageIcon ("images/monsterSprites/editedSlime.gif").getImage(), xPos, yPos, null);
			}
		}
	}
	
	
	/**
	 * <h1>Returns detection range</h1>
	 * used for debugging reasons while enable developer in main is true.
	 */
	public int getDetectionRange()
	{
		return detectionRange;
	}
}
