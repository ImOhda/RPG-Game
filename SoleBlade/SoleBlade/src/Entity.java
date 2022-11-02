import java.awt.Graphics2D;
import java.awt.Rectangle;

public class Entity
{
	// Some variables that will help build this class.
	int detectionRange;
	Rectangle hitbox, attackbox;
	boolean dead; // Boolean to see if the entity is dead or not
	int xPos, yPos; // X and Y position of the entity.

	int pxPos, pyPos;
	int totalhp, hp; // Total hp that the entity has, the current hp that the entity has.
	
	/**
	 * <h1> All the methods below are just getters and setters, nothing too special going on in them, so commenting them all in one </h1>
	 * 
	 * @return - xPos, yPos, boolean dead, totalhp, hp, detectionRange, hitbox, attackbox
	 *
	 */
	
	public boolean isDead()
	{
		return dead;
	}


	public void setDead(boolean dead)
	{
		this.dead = dead;
	}

	
	public Rectangle getAttackbox()
	{
		return attackbox;
	}


	public void setAttackbox(Rectangle attackbox)
	{
		this.attackbox = attackbox;
	}

	public int getTotalhp()
	{
		return totalhp;
	}


	public void setTotalhp(int totalhp)
	{
		this.totalhp = totalhp;
	}


	public int getHp()
	{
		return hp;
	}


	public void setHp(int hp)
	{
		this.hp = hp;
	}


	public int getDetectionRange()
	{
		return detectionRange;
	}


	public void setDetectionRange(int detectionRange)
	{
		this.detectionRange = detectionRange;
	}

	public int getPxPos()
	{
		return pxPos;
	}


	public void setPxPos(int pxPos)
	{
		this.pxPos = pxPos;
	}


	public int getPyPos()
	{
		return pyPos;
	}


	public void setPyPos(int pyPos)
	{
		this.pyPos = pyPos;
	}

	
	
	
	public int getxPos()
	{
		return xPos;
	}
	public void setxPos(int xPos)
	{
		this.xPos = xPos;
	}
	public int getyPos()
	{
		return yPos;
	}
	public void setyPos(int yPos)
	{
		this.yPos = yPos;
	}
	public Rectangle getHitbox()
	{
		return hitbox;
	}
	public void setHitbox(Rectangle hitbox)
	{
		this.hitbox = hitbox;
	}
	
	public void Move(Player p1, Room rc) 
	{
		System.out.println("ERROR DO NOT MOVE A ENTITY PARENT CLASS OBJECT!");
	}
	
	public void Draw(Graphics2D g2)
	{
		System.out.println("ERROR DO NOT DRAW A ENTITY PARENT CLASS OBJECT!");
	}
	
	public void Hit(Player p1)
	{
		System.out.println("ERROR DO NOT HIT A ENTITY PARENT CLASS OBJECT!");
	}
}
