import java.awt.Graphics2D;
import java.awt.Rectangle;

public class Room
{
	int InternalWidth, InternalHeight, numOfColSquares;


	private int[][] roomValues;
	private Rectangle[] collisionSquares;
	private Rectangle[] doors = new Rectangle[4]; // 0 - up, 1 - right, 2 - down, 3 - left.
	private int[]roomTeleportCoordsX = new int[4];
	private int[]roomTeleportCoordsY = new int[4];
	private Entity[] monsterArray;
	
	private int[] connectedRooms = new int[4]; // What rooms are connected to what rooms.
	
	/**
	 * <h1> Constructor for the room </h1>
	 * @param w - width of the room
	 * @param h - height of the room
	 * @param colsquares - collision squares in the room
	 * @param numofmonsters - number of monsters inside the room.
	 */
	Room(int w, int h, int colsquares, int numofmonsters)
	{
		InternalWidth = w;
		InternalHeight = h;
		roomValues = new int[InternalWidth/32][InternalHeight/32];
		numOfColSquares = colsquares;
		monsterArray = new Entity[numofmonsters];
	}
	/**
	 * <h1> The next few methods in this class are just getters and setters that don't need to be explained individually. </h1>
	 * @return
	 */
	public int[] getRoomTeleportCoordsX() {
		return roomTeleportCoordsX;
	}

	public void setRoomTeleportCoordsX(int[] roomTeleportCoordsX) {
		this.roomTeleportCoordsX = roomTeleportCoordsX;
	}

	public int[] getRoomTeleportCoordsY() {
		return roomTeleportCoordsY;
	}

	public void setRoomTeleportCoordsY(int[] roomTeleportCoordsY) {
		this.roomTeleportCoordsY = roomTeleportCoordsY;
	}


	public Rectangle[] getDoors()
	{
		return doors;
	}

	public void setDoors(Rectangle[] doors)
	{
		this.doors = doors;
	}

	public int getNumOfColSquares()
	{
		return numOfColSquares;
	}
	
	public void setNumOfColSquares(int numOfColSquares)
	{
		this.numOfColSquares = numOfColSquares;
	}
	
	public Rectangle[] getCollisionSquares()
	{
		return collisionSquares;
	}

	public void setCollisionSquares(Rectangle[] collisionSquares)
	{
		this.collisionSquares = collisionSquares;
	}
	public void CreateRoom(int[][] arr) 
	{
		roomValues = arr;
	}
	
	public int[][] getRoomArray()
	{
		return roomValues;
	}
	
	public Entity[] getMonsterArray()
	{
		return monsterArray;
	}

	public void setMonsterArray(Entity[] monsterArray)
	{
		this.monsterArray = monsterArray;
	}

	public int[] getConnectedRooms()
	{
		return connectedRooms;
	}

	public void setConnectedRooms(int[] connectedRooms)
	{
		this.connectedRooms = connectedRooms;
	}
	
	/**
	 * <h1> Method that draws the room </h1>
	 * @param g2 - takes in a Graphics2D object.
	 */
	public void draw(Graphics2D g2) 
	{
		for(int xi = 0; xi < InternalWidth/32; xi++)
		{
			for(int yi = 0; yi < InternalHeight/32 - 1; yi++)
			{
				g2.drawImage(Main.spritesFull[roomValues[xi][yi]], xi*32, yi*32, null);
			}

		}
		
	}

	/**
	 * <h1> This method checks if a collision occurs within the room </h1>
	 * @param r - rectangle itself.
	 * @return - returning if it collides or not.
	 */
	public boolean doesCollisionOccur(Rectangle r)
	{
		boolean ret = false;
		
		for(int i = 0; i < numOfColSquares; i++)
		{
			if(!ret && r != null)
			{
				ret = collisionSquares[i].intersects(r);
			}
		}
		
		return ret;
	}

}
