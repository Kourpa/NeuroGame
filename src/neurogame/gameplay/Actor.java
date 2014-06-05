package neurogame.gameplay;

import java.awt.Image;
import java.awt.geom.Area;

import neurogame.level.World;

public abstract class Actor extends GameObject{

	//movement variables
	protected double velY = 0;
	protected double velX = 0;
	protected double friction = 0.9f;
	protected double xSpeed = 0.01f;
	protected double ySpeed = 0.01f;
	protected double velD = 0.003f;

	//actor health;
	protected int health = 1;

	public Actor(double x, double y, double width, double height, String name, Image image, World world) {
		super(x, y, width, height, name, image, world);
		this.world = world;
	}


	/*
	 * The following methods update the velocity of the object
	 * based on a particular direction.
	 */
	public void moveN(){
		if(velY > -ySpeed){
			velY -= velD;
		}
	}
	public void moveS(){
		if(velY < ySpeed){
			velY += velD;
		}
	}
	public void moveE(){
		if(velX < xSpeed){
			velX += velD;
		}
	}
	public void moveW(){
		if(velX > -xSpeed){
			velX -= velD;
		}
	}
	public void moveNE(){
		if(velY > -ySpeed){
			velY -= velD;
		}
		if(velX < xSpeed){
			velX += velD;
		}
	}
	public void moveNW(){
		if(velY > -ySpeed){
			velY -= velD;
		}
		if(velX > -xSpeed){
			velX -= velD;
		}
	}
	public void moveSE(){
		if(velY < ySpeed){
			velY += velD;
		}
		if(velX < xSpeed){
			velX += velD;
		}
	}
	public void moveSW(){
		if(velY < ySpeed){
			velY += velD;
		}
		if(velX > -xSpeed){
			velX -= velD;
		}
	}
	public void moveTo(double x, double y){
		double dx = this.getX() - x;
		double dy = this.y - y;

		double dir = Math.atan2((double)dy,(double)dx);

		double nextX = Math.cos(dir);
		double nextY = Math.sin(dir);

		if(x > this.x){
			if(velX < xSpeed){
				velX += velD;
			}
		}
		else if(x < this.x){
			if(velX > -xSpeed){
				velX -= velD;
			}
		}
		else{
			moveE();
		}

		if(y > this.y){
			if(velY < ySpeed){
				velY += velD;
			}
		}
		else if(y < this.y){
			if(velY > -ySpeed){
				velY -= velD;
			}
		}
	}
	public void updatePosition(){
		//apply friction
		velY *= friction;
		velX *= friction;

		//update actor position
		y += velY;
		x += velX;
	}
	//end of the movement methods-----------------------------------
	
	protected boolean wallCollision(){
//		Area area = new Area(getHitBox());
//		area.intersect(world.getCollisionArea());
		return false;
	}
	
	protected boolean wallCollision(int steps, int yDir){
		double tempX = x;
		double tempY = y;
		double tempVelY = velY;
		double tempVelX = velX;
		
		for(int i = 0; i < steps; i++){
			if(yDir == 1)moveS();
			else if(yDir == -1) moveN();
			else moveW();
			updatePosition();
			if (wallCollision()){
				x = tempX;
				y = tempY;
				velY = tempVelY;
				velX = tempVelX;
				return true;
			}
		}
		
		if (wallCollision()){
			x = tempX;
			y = tempY;
			velY = tempVelY;
			velX = tempVelX;
			return true;
		}
		else{
			x = tempX;
			y = tempY;
			velY = tempVelY;
			velX = tempVelX;
			return false;
		}
	}
	

	public int getHealth(){
		return health;
	}

	public void setHealth(int health){
		this.health = health;
	}
}
