package FNL;
import robocode.*;
import robocode.util.*;
import java.lang.Math;
import java.awt.geom.Point2D;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;

//import java.awt.Color;

// API help : http://robocode.sourceforge.net/docs/robocode/robocode/Robot.html

/**
 * RobotFightFinally - a robot by (your name here)
 */
public class RobotFightFinally extends AdvancedRobot
{

	static Double PI = Math.PI;
	Enemy enemy;
	Point2D center;
	Double maxHeightBattle;
	Double maxWidthBattle;
	Double radius;
	Double minDistance;
	Double safeDistance;
	Double dirrection;
	Double lastDistance;
	Double gunOffet;
	int hit;
	long lastHit;
	long wallTime;
	Random r;
	Double[] percentaje = new Double[]{0.30, 0.20, 0.35, 0.40, 0.45, 0.50, 0.55, 0.60, 0.65, 0.75, 0.85, 0.95, 1.0};
	
	public void run() {
		hit = 0;
		r = new Random();
	    maxHeightBattle = getBattleFieldHeight();
	    maxWidthBattle = getBattleFieldWidth();
        minDistance = 150.0;
		lastDistance = 9999.0;
		gunOffet = 0.0;
		center = new Point2D.Double(maxWidthBattle/2, maxHeightBattle/2);
		setDistanceSafe();
		wallTime = 0;
		lastHit = 0;
		enemy = new Enemy();
		// setColors(Color.red,Color.blue,Color.green); // body,gun,radar
		setAdjustGunForRobotTurn(true);
 		setAdjustRadarForGunTurn(true);
		setAdjustRadarForRobotTurn(true);
		dirrection = 1.0;
		while(true) {
			if(enemy.isNone() || (getTime() - enemy.lastLocated) % 7 == 0){
				enemy.lastLocated = 0.0;
				lastDistance = 9999.0;
				turnRadarRightRadians(Double.POSITIVE_INFINITY);
				continue;
			}
			
		}
	}
	
	public void setDistanceSafe(){
		Double minDistance = Math.min(maxHeightBattle, maxWidthBattle);
		safeDistance = minDistance * 0.25;
	}
	
	public Double checkWall(){
		
		Double perc = percentaje[r.nextInt(percentaje.length)];
		Double newDistance =   3 * safeDistance * perc;
		double next_x = getX() + Math.cos(getHeadingRadians()) * newDistance * getVelocity();
		double next_y = getY() + Math.cos(getHeadingRadians()) * newDistance * getVelocity();
		if((getTime() - wallTime) % 50 == 0 || wallTime == 0){
			wallTime = getTime();
			if(next_x < 200 || next_y < 200){
				dirrection *= -1;
			}else if(next_x > maxWidthBattle - 200 || next_y > maxHeightBattle - 200){
				dirrection *= -1;
			}
		}
		return newDistance;
		
	}

	public void move(){
		if(getTime() % 30 == 0){
			if(hit == 0){
				setDistanceSafe();
			}else if(getTime()-lastHit > 40){
				hit = 0;
			}
		}
		if(enemy.distance > safeDistance * 2.5){
			Double distance = enemy.distance - safeDistance;
			setTurnRight(getHeading() - enemy.heading + enemy.bearing + Math.cos(distance * PI/4));
			Double d = checkWall();
			setAhead(distance);
		}else{
			Double distance = checkWall();
		    setTurnRight(enemy.bearing  + 90);
			setAhead(distance * dirrection);
			if(getTime() % 25 == 0){
				dirrection *= -1;
			}
		}
		if(lastDistance > enemy.distance){
			dirrection *= -1;
		}
		lastDistance = enemy.distance;
		execute();
	}
	
	
	public void Gun(){
  // Esto es linear targetting
   	 double bulletPower = Math.min(3.0,getEnergy());
	    double myX = getX();
	    double myY = getY();
	    double absoluteBearing = getHeadingRadians() + enemy.bearingRadians;
	    double enemyX = getX() + enemy.distance * Math.sin(absoluteBearing);
	    double enemyY = getY() + enemy.distance * Math.cos(absoluteBearing);
	    double enemyHeading = enemy.headingRadians;
	    double enemyVelocity = enemy.speed;
	     
	    double minDistanceGun = 25.0;
	    double deltaTime = 0;
	    double predictedX = enemyX, predictedY = enemyY;
	    while((++deltaTime) * (20.0 - 3.0 * bulletPower) < Point2D.Double.distance(myX, myY, predictedX, predictedY)){    
	      predictedX += Math.sin(enemyHeading);  
	      predictedY += Math.cos(enemyHeading);
	      if(  predictedX < minDistanceGun || predictedY < minDistance || predictedX > maxWidthBattle - minDistanceGun || predictedY > maxHeightBattle - minDistanceGun){
	        predictedX = Math.min(Math.min(minDistanceGun, predictedX), maxWidthBattle - minDistanceGun);  
	        predictedY = Math.min(Math.min(minDistanceGun, predictedY), maxHeightBattle - minDistanceGun);
	        break;
	      }
	    }
	    double theta = Utils.normalAbsoluteAngle(Math.atan2(predictedX - getX(), predictedY - getY()));
		double angle = Utils.normalRelativeAngle(theta - getGunHeadingRadians());
	   	setTurnGunRightRadians(angle);
		 if( Math.abs(Math.abs(angle) - Math.abs(enemy.bearingRadians)) < 3){
			setFire(bulletPower);
		}		
	}
		
	/**
	 * onScannedRobot: What to do when you see another robot
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
		enemy.update(e, getX(), getY());
		setTurnRadarLeftRadians(getRadarTurnRemainingRadians());
		move();
		Gun();
	}
	/**
	 * onHitByBullet: What to do when you're hit by a bullet
	 */
	public void onHitByBullet(HitByBulletEvent e) {
		// Replace the next line with any behavior you would like
		if(hit > 3){
			hit = 0;
			safeDistance += 50.0;
			if(safeDistance > minDistance * 3){
				safeDistance = 300.0;
			}
		}
		lastHit = getTime();
		hit ++;
	}
	
	/**
	 * onHitWall: What to do when you hit a wall
	 */
	public void onHitWall(HitWallEvent e) {
		// Replace the next line with any behavior you would like
		if(wallTime == 0 || (getTime() - wallTime) < 100){
			wallTime = getTime();
		}
		setTurnRightRadians(e.getBearing() + 90);
		setBack(100);
		System.out.println(dirrection);
		
}
	
   class Enemy{
		public double bearing;
		public double bearingRadians;
		public double distance;
		public double energyBefore;
		public double energy;
		public double heading;
		public double headingRadians;
		public double speed;
		public double lastLocated;
		
		public Enemy(){
			lastLocated = 0.0;
		}
		
		public Boolean isNone(){
			return (this.lastLocated == 0.0) ? true : false;
		}
		
		public void update(ScannedRobotEvent e, Double X, Double Y){
			bearing = e.getBearing();
			bearingRadians = e.getBearingRadians();
			distance = e.getDistance();
	
            speed = e.getVelocity();
			energy = e.getEnergy();
		    heading = e.getHeading();
			headingRadians = e.getHeadingRadians();
			lastLocated = e.getTime();
		}
				
	}
}
