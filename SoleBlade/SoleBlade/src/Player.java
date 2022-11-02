import java.awt.*;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

import javax.swing.ImageIcon;

public class Player
{
	// Variables needed in order to make this a functioning class.
	private int PosX, PosY, pPosX, pPosY, health , maxHealth, playerSpriteDimension = 150, attackdamage, counter = 0; 
	private boolean plrUp, plrDown, plrLeft, plrRight, attacking, onlyonce;
	private boolean attacknum = true; // attacknum is either true or false, for attack one/two.
	Rectangle CollisionRect, attackRectL, attackRectR;

	public boolean moveL = false, moveR = false, moveU = false, moveD = false;
	private int currentimage, facing; // facing is either left (0) or right (1).
	
	// these are the 3 key images shwon at the bottom of the screen.
	Image baseimage1 = new ImageIcon("images/grayedOutKeyForDisplay.png").getImage();
	Image baseimage2 = new ImageIcon("images/grayedOutKeyForDisplay.png").getImage();
	Image baseimage3 = new ImageIcon("images/grayedOutKeyForDisplay.png").getImage();
	
	// Initializing decimal format object
	DecimalFormat df = new DecimalFormat("00");

	/**
	 * <h1> Constructor for the player object </h1>
	 * @param x - x-position of the player
	 * @param y - y- position of the player
	 */
	Player(int x, int y)
	{
		PosX = x;
		PosY = y;
		health = 100;
		maxHealth = 100;
		System.out.println(currentimage);
		System.out.println(currentimage);
		attackdamage = 20;
		currentimage = 0;
		facing = 0;
	}
	/**
	 * <h1> The next few methods below are getters and setters that aren't that important to be explained individually.</h1>
	 * @return
	 */
	public Rectangle getAttackRectL()
	{
		return attackRectL;
	}

	public void setAttackRectL(Rectangle attackRectL)
	{
		this.attackRectL = attackRectL;
	}

	public Rectangle getAttackRectR()
	{
		return attackRectR;
	}

	public void setAttackRectR(Rectangle attackRectR)
	{
		this.attackRectR = attackRectR;
	}

	public int getPlayerSpriteDimension()
	{
		return playerSpriteDimension;
	}

	public Rectangle getCollisionRect()
	{
		return CollisionRect;
	}

	public void setCollisionRect(Rectangle collisionRect)
	{
		CollisionRect = collisionRect;
	}

	public void setPlayerSpriteDimension(int playerSpriteDimension)
	{
		this.playerSpriteDimension = playerSpriteDimension;
	}

	public boolean getAttacknum()
	{
		return attacknum;
	}

	public void setAttacknum(boolean attacknum)
	{
		this.attacknum = attacknum;
	}

	public boolean isAttacking()
	{
		return attacking;
	}

	public void setAttacking(boolean attacking)
	{
		this.attacking = attacking;
	}
	
	public int getAttackdamage()
	{
		return attackdamage;
	}

	public void setAttackdamage(int attackdamage)
	{
		this.attackdamage = attackdamage;
	}

	public int getpPosX()
	{
		return pPosX;
	}

	public void setpPosX(int pPosX)
	{
		this.pPosX = pPosX;
	}

	public int getpPosY()
	{
		return pPosY;
	}

	public void setpPosY(int pPosY)
	{
		this.pPosY = pPosY;
	}

	
	public int getHealth()
	{
		return health;
	}

	public void setHealth(int health)
	{
		this.health = health;
	}

	public int getMaxHealth()
	{
		return maxHealth;
	}

	public void setMaxHealth(int maxHealth)
	{
		this.maxHealth = maxHealth;
	}

	public int getPosX()
	{
		return PosX;
	}

	public void setPosX(int gridPosX)
	{
		this.PosX = gridPosX;
	}

	public int getPosY()
	{
		return PosY;
	}

	public void setPosY(int gridPosY)
	{
		this.PosY = gridPosY;
	}
	
	public boolean isPlrUp()
	{
		return plrUp;
	}

	public void setPlrUp(boolean plrUp)
	{
		this.plrUp = plrUp;
	}

	public boolean isPlrDown()
	{
		return plrDown;
	}

	public void setPlrDown(boolean plrDown)
	{
		this.plrDown = plrDown;
	}

	public boolean isPlrLeft()
	{
		return plrLeft;
	}

	public void setPlrLeft(boolean plrLeft)
	{
		this.plrLeft = plrLeft;
	}

	public boolean isPlrRight()
	{
		return plrRight;
	}

	public void setPlrRight(boolean plrRight)
	{
		this.plrRight = plrRight;
	}
	
	public int getFacing()
	{
		return facing;
	}

	public void setFacing(int facing)
	{
		this.facing = facing;
	}

	public int getCurrentimage()
	{
		return currentimage;
	}

	public void setCurrentimage(int currentimage)
	{
		this.currentimage = currentimage;
	}
	
	public void constantCode()
	{
		// this is the collision hit box for the player:
		CollisionRect = new Rectangle(getPosX() + getPlayerSpriteDimension()/4 + 20, getPosY() + getPlayerSpriteDimension()/4 + 40, getPlayerSpriteDimension()/2 - 40, getPlayerSpriteDimension()/2 - 40);
		
		// if attacking is true, it creates the hit boxes for player attack L and R.
		if(attacking == true && Main.attackanimation == 3)
		{
			attackRectL = new Rectangle(getPosX() - 40, getPosY(), 140, 110);
			attackRectR = new Rectangle(getPosX() + getPlayerSpriteDimension() - 100, getPosY(), 140, 110);
		}
		else
		{
			attackRectL = null;
			attackRectR = null;
		}
		
	}
	/**
	 * <h1> This method draws the "inventory" of the user on the bottom of the screen. </h1>
	 * This includes the health bar, key's collected, monsters killed etc.
	 * @param g2 - The graphics 2D object needed in order to draw.
	 * @param x - x-position
	 * @param y - y-position
	 */
	public void drawHUD(Graphics2D g2 ,int x, int y)
	{
		g2.setFont(new Font("Times New Roman", 0, 16)); // smaller version of the font to fit the smaller space.
		
		// health bar:
		g2.setColor(Color.red);
		g2.drawRect(x, y - 20, 300, 24);
		g2.fillRect(x, y - 20,  (int)(300 * (double)health/maxHealth), 24);
		g2.setColor(Color.white);
		g2.drawString("Health: " + health + "/" + maxHealth, x + 8, y - 2);
		
		// keys collected text:
		g2.drawString("Keys Collected: " + Main.keysCollected + "/" + 3, x + 340, y - 10);
		
		// whenever a key is collected, one of the images is replaced by a full key in the following switch case statement.
		switch(Main.keysCollected)
		{
		case 1: 
		{
			baseimage1 = Main.spritesFull[60];
			break;
		}
		case 2:
		{
			baseimage2 = Main.spritesFull[60];
			break;
		}
		case 3:
		{
			baseimage3 = Main.spritesFull[60];
			break;
		}
		}
		
		// draw three keys:
		g2.drawImage(baseimage1, x + 340, y - 12, null);
		g2.drawImage(baseimage2, x + 380, y - 12, null);
		g2.drawImage(baseimage3, x + 420, y - 12, null);
		
		// draw the text that displays the rest of the information:
		g2.drawString("Monsters Killed: " + Main.monstersKilled + "     Current Playtime: " + df.format(Main.timeMins) + ":" + df.format(Main.timeSeconds), x + 480, y - 4);
	}
	
	/**
	 * <h1> This method draws the player in whatever position they are in currently </h1>
	 * @param g2 - Graphics2D object.
	 * @param x - The image.
	 */
	public void drawPlayer(Graphics2D g2, Image x)
	{
		
		// The image when the character is not moving (currentimage 0 is idle)
		if(currentimage == 0)
		{
			// Checking if the character is facing right when he is idle.
			if(facing == 1)
			{
				// If yes, then draw the image of the character facing right when he is idle.
				g2.drawImage(getScaledImage(new ImageIcon("images/playerSprites/hero_idle.gif").getImage(), 150, 150), PosX, PosY, null);
			}
			// Otherwise, drawing the image of the character facing left when he is idle.
			else
			{
				g2.drawImage(getScaledImage(new ImageIcon("images/playerSprites/hero_idleL.gif").getImage(), 150, 150), PosX, PosY, null);
			}
		}
		else if (currentimage == 1) // check if current image is 1, which is move.
		{ 
			if(facing == 1)
			{
				g2.drawImage(getScaledImage(new ImageIcon("images/playerSprites/hero_move.gif").getImage(), 150, 150), PosX, PosY, null);
			}
			else
			{
				g2.drawImage(getScaledImage(new ImageIcon("images/playerSprites/hero_moveL.gif").getImage(), 150, 150), PosX, PosY, null);
			}
		}
		else if (currentimage == 2) // check if current image is 2, which is attack.
		{
			if(facing == 1)
			{
				g2.drawImage(x, PosX + 5, PosY, null);
			}
			else
			{
				g2.drawImage(x, PosX - 50 - 5, PosY, null);
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
