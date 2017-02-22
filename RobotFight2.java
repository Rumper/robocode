package FNL;
import robocode.*;
import java.util.Random;
import java.lang.Math;
import java.awt.Color;

// API help : http://robocode.sourceforge.net/docs/robocode/robocode/Robot.html

/**
 * RobotFight2 - a robot by (your name here)
 */
public class RobotFight2 extends AdvancedRobot
{
 //Variables staticas
        private double maxHeight;
        private double maxWidth;
        private double maxDistance;
        private double max_power = Rules.MAX_BULLET_POWER;
        private double min_power = Rules.MIN_BULLET_POWER;
        private double max_speed = Rules.MAX_VELOCITY;
        //Variables del robot
        private int hitEnemy;
        private int hitMy;
        private long lastHit;
        private int dirrection;
        private double distanceBy;
		private double angleDected;
		Random randomGenerator = new Random();
        RobotEnemy enemy;
	/**
	 * run: RobotFight2's default behavior
	 */
	public void run() {
		// Initialization of the robot should be put here

		// After trying out your robot, try uncommenting the import at the top,
		// and the next line:
		maxHeight = 600.0;
		maxWidth = 600.0;
		maxDistance = Math.sqrt(maxHeight * maxHeight + maxWidth * maxWidth);
		enemy = new RobotEnemy();
		hitEnemy = 0;
        hitMy = 0;
        lastHit = (long)0.0;
		setColors(Color.yellow,Color.black,Color.white); // body,gun,radar
    
		// Robot main loop	
			while(true) {
							move();
							execute();
	            scan();
							execute();
			    shoot();
							execute();
				execute();
			}
		}
	
    public void move(){
		double distanceByWall = 0.0;
		if(getX() < maxWidth * 0.25){
		 	if(220 <= getTurnRemaining() && getTurnRemaining() >= 320){
					setTurnLeft(getHeadingRadians() - 180);
				}
			distanceByWall = 10;
			setAhead(distanceByWall);	
        }else 		if(getX() > maxWidth * 0.75){
		 	if(20 <= getTurnRemaining() && getTurnRemaining() >= 120){
					setTurnLeft(	getHeadingRadians() + 180);
				}
			distanceByWall = 10;	
						setAhead(distanceByWall);
			execute();
        }else
		if(getY() < maxHeight * 0.25){
		 	if( 60 >= getTurnRemaining() && getTurnRemaining() >= 300){
					setTurnLeft(getHeadingRadians() + 180);
				}
			distanceByWall = 10;	
						setAhead(distanceByWall);
			execute();
        }else
		if(getY() > maxWidth * 0.75){
		 	if(240 <= getTurnRemaining() && getTurnRemaining() >= 100){
					setTurnLeft(getHeadingRadians() - 180);
				}
			distanceByWall = 10;
						setAhead(distanceByWall);
			execute();	
        }else{
		  distanceByWall = randomGenerator.nextInt(100);
		if(randomGenerator.nextInt(50) % 2 == 0){
			setTurnLeft(	getHeadingRadians() - randomGenerator.nextInt(50));
			setAhead(distanceByWall);
			execute();
		}else{
			setTurnRight(	getHeadingRadians() - randomGenerator.nextInt(50));
			setAhead(distanceByWall);
			execute();
		}
        }

    }
	
    public Boolean isLocated(){
		long time = Math.abs(enemy.TIME - getTime());
 		if(time !=0 && time % 20 == 0){
	          enemy.isLocated = true;
			  enemy.TIME = 0; 
        }
	   return enemy.isLocated;  
    }

    public void scan(){
	    if(!isLocated()){
	   			setTurnRadarLeft(getRadarTurnRemaining() + 20);
		}else{
			setTurnGunLeft(enemy.BEARING);
			setTurnRadarLeft(enemy.BEARING);
		}  
    }
	
    public void shoot(){
	  if(isLocated()){
		double power = max_power * (1 - enemy.DISTANCE / maxDistance);
		long TimebyHit = (long)(enemy.DISTANCE / (20 - 3 * power));
		double offset = 0;
		setTurnGunLeft(getGunTurnRemaining()  - enemy.BEARING + offset);
        setFire(power);
	  }else{
	  	setFire(min_power);
	  }
    }
	
     public Double AngelBy(double x1, double y1, double x2, double y2)
     {
        return Math.atan2((x1 - x2), (y1 - y2));
     }
	/**
	 * onScannedRobot: What to do when you see another robot
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
		enemy.isLocated = true;
		enemy.TIME = getTime();
		enemy.SPEED = e.getVelocity();
		enemy.DISTANCE = e.getDistance();
		enemy.BEARING = e.getBearing();
		enemy.HEADING = e.getHeading();
		enemy.ENERGY = e.getEnergy();
		enemy.X = getX() + Math.cos(enemy.BEARING) * enemy.DISTANCE;
		enemy.Y = getY() + Math.sin(enemy.BEARING) * enemy.DISTANCE;
		scan();
	}
	
	/**
	 * onHitRobot: What to do when you're hit by a Robot enemy
	 */	
	 public void onHitRobot(HitRobotEvent e)
        {
		   setTurnGunLeft(enemy.BEARING);
            if (getEnergy() > max_power)
            {
                setFire(max_power);
            }
			lastHit = getTime();
			enemy.TIME = getTime();
        }

	/**
	 * onHitByBullet: What to do when you're hit by a bullet
	 */
	public void onHitByBullet(HitByBulletEvent e) {
			hitEnemy += 1;
			lastHit = getTime();
	}
	
	/**
	 * onHitWall: What to do when you hit a wall
	 */
	public void onHitWall(HitWallEvent e) {
		setAhead(-100);
	}
	
	class RobotEnemy
    {
        //Ha sido localizado
        public Boolean isLocated;
        //Energia actual del enemigo
        public double ENERGY;
        //Posicion x, y ultima vez visto
        public double X;
        public double Y;
        //velocidad que se mueve
        public double SPEED;
        //Apuntamiento
        public double HEADING;
        //Orientacion
        public double BEARING;
        //Ultima vez localizado
        public long TIME;
        //Distancia
        public double DISTANCE;
        //acceleración;

        public RobotEnemy()
        {
            isLocated = false;
        }

        public double nextAngle()
        {
            double diff = Math.abs(HEADING - BEARING);
            double Rotation = 10 - 0.75 * Math.abs(SPEED);
            if(diff < 90)
            {
                return BEARING - Rotation;
            }else
            {
                return HEADING - Rotation;
            }
        }

        public double nextX()
        {
            return X + Math.sin(nextAngle()) * SPEED;
        }

        public double nextY()
        {
            return Y + Math.sin(nextAngle()) * SPEED;
        }
    }
}
