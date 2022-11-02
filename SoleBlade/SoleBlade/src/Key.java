import java.awt.Graphics2D;
import java.awt.Rectangle;

public class Key extends Entity
{
	//Variables needed in order to make this a functioning class.
	private int counter = 0; // Counter to see how many keys you have.
	private boolean keyhit = false, disappear = false; // keyhit boolean checks if the player hit the key, disappear booleans disappears the key after it has been interacted with.
	
	/**
	 * <h1> Constructor for a key object </h1>
	 * @param xpos - x-position of where you want the key to be located.
	 * @param ypos - y-position of where you want the key to be located.
	 */
	Key(int xpos, int ypos)
	{
		xPos = xpos;
		yPos = ypos;
		hitbox = new Rectangle(xPos, yPos, 32, 32);
	}
	
	/**
	 * <h1> All the constant code for key</h1>
	 * If the key is hit, counts 40 milliseconds and then essentially deletes the key, increasing keys collected by one.
	 */
	public void Move(Player p1, Room rc) 
	{
		
		if(keyhit)
		{
			counter++;
		}
		if(counter == 40)
		{
			System.out.println("You hit key");
			counter = 0;
			keyhit = false;
			hitbox = new Rectangle(0,0,0,0);
			disappear = true;
			Main.keysCollected++;
		}
	}
	
	
	/**
	 * <h1> Draws key </h1>
	 * just draws the key if disappear is false.
	 */
	public void Draw(Graphics2D g2)
	{
		if(!disappear) 
		{
			g2.drawImage(Main.spritesFull[60], xPos, yPos, null);
		}
	}
	/**
	 * <h1> Method that checks if the player interacted with the key </h1>
	 * 
	 * 
	 */
	public void Hit(Player p1)
	{
		// if the player is attacking and both the left and right attack rectangles are not null:
		if(p1.isAttacking() && p1.getAttackRectL() != null && p1.getAttackRectR() != null)
		{
			// Checking if the player hit the key while they were facing left. 
			if(p1.getFacing() == 0 && p1.getAttackRectL().intersects(hitbox))
			{
				//setting keyhit to true
				keyhit = true;
			}
			// Checking if the player hit the key while they were facing right.
			if(p1.getFacing() == 1 && p1.getAttackRectR().intersects(hitbox))
			{
				//setting keyhit to true.
				keyhit = true;
			}
		}
	}
}
