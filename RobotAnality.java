package FNL;
import robocode.*;
import robocode.util.*;
import java.lang.Math;
//import java.awt.Color;

// API help : http://robocode.sourceforge.net/docs/robocode/robocode/Robot.html

/**
 * Robot2 - a robot by (your name here)
 */
public class RobotAnality extends AdvancedRobot
{
	//Reglas
	double max_power;
	double min_power;
	double max_height;
	double max_width;
	Boolean left_right;
	Boolean shock_wall;
	//Variable miRobot
	int dirrection;
	double max_distance;
	long hit_time;
	double angle;
	double power;
	double angle_radar;
	int wall_time;
	//Variables Robotenemigo
	double e_distance;
	double e_bearing;
	double e_heading;
	long e_show;
	double e_x;
	double e_y;
	double e_velocity;
	double hit_by;
	int count;
	
	
	public void run() {
		// setColors(Color.red,Color.blue,Color.green); // body,gun,radar
		//Inicialiacion Reglas
		max_power = Rules.MAX_BULLET_POWER;
        min_power = Rules.MIN_BULLET_POWER;
	    max_height = getBattleFieldHeight() ;
	    max_width = getBattleFieldWidth()  ;
		shock_wall = false;
		left_right = false;
		count = 0;
		//Inicialiacion Robot
		dirrection = 1;
		max_distance = 150;
		angle = 0;
		hit_time = 0;
		angle_radar = 360;
		//Inicializacion Enemigo
		e_distance = 9999999.0;
		e_bearing = 0;
		e_heading = 0;
		e_show = 0;
		e_x = 0;
		e_y = 0;
		e_velocity = 0;
		hit_by = 0;
		wall_time = 0;
		
		setAdjustGunForRobotTurn(true);
 		setAdjustRadarForGunTurn(true);
		setAdjustRadarForRobotTurn(true);;
 		turnRadarRightRadians(2* Math.PI);
		while(true) {
		
			//Situación Radart
			double next_x = getX() - Math.cos(getHeadingRadians()) * getDistanceRemaining();
			double next_y = getY() - Math.cos(getHeadingRadians()) * getDistanceRemaining();
			if(next_x < 0 && next_y < 0){
				setMaxVelocity(3);
				if(next_x < 0 && next_y < 0){
					angle = AngleBy(getX(), getY(), 0, 0) + Math.PI /2;
				}else if(next_x < 0){
					angle = AngleBy(getX(), getY(), 0, next_y) - Math.PI /2;
				}else if (next_y < 0){
					angle = AngleBy(getX(), getY(), next_x, 0) + Math.PI /2;
				}
				shock_wall = true;
			}else if(next_x > max_width && next_y > max_height){
				setMaxVelocity(3);
				if(next_x > max_width && next_y > max_height){
					angle = AngleBy(getX(), getY(), max_width, max_height) - Math.PI /2;
				}else if(next_x > max_width){
					angle = AngleBy(getX(), getY(), max_width, next_y) + Math.PI /2;
				}else if (next_y > max_height){
					angle = AngleBy(getX(), getY(), next_x, max_height) - Math.PI /2;
				}
				shock_wall = true;
			}else{
				angle = e_bearing + Math.PI / 4;
				setMaxVelocity(Rules.MAX_VELOCITY);
				shock_wall = false;
			}
			System.out.println(wall_time);
			if( wall_time  != 0){
				if(wall_time == 3){
					wall_time =0;
				}
				wall_time +=1;
				setAhead(max_distance + (int)Math.random() * max_distance);	
			}else{
				if(left_right){
					if(shock_wall){
						setTurnLeftRadians(NormalAngleBearing(angle));
					}else{
						setTurnLeftRadians(NormalAngleBearing(e_bearing + Math.PI / 4));
					}
				}else{
					if(shock_wall){
						setTurnRightRadians(NormalAngleBearing(angle));
					}else{
						setTurnLeftRadians(NormalAngleBearing(e_bearing + Math.PI / 4));
					}
				}
				if(e_distance > max_distance/2){
					if(count > 7 ){
						dirrection *= -1;
						count = 0;
					}
					setAhead(dirrection * max_distance);
				}else{
					setAhead(max_distance + (int)Math.random() * max_distance);
				}
				count += 1;
			}
			
			double  temp = getTime() - e_show;
			if(temp > 7){
				angle_radar = 360;
				e_distance = 999999;
			}else{
				angle_radar = getRadarHeadingRadians() -  AngleBy(getX(), getY(), e_x, e_y);
			}
			if (angle_radar < 0){
			 	angle_radar -= Math.PI/7;
			}else{
			 	angle_radar += Math.PI/7; 
			}
			setTurnRadarLeftRadians(NormalAngleBearing(angle_radar));
			//disparamos
	       //Situacion Gun y Disparo
			power = max_power * (600/e_distance);
			temp = (20 - 3 * power);
			angle = NormalAngleBearing(getHeadingRadians() + e_bearing - getGunHeadingRadians());
			setTurnGunRightRadians(angle + angle * temp / 100 * Math.sin(dirrection));
			fire(power);
			
			//activamos todo
			execute();
		}
		}

		public Double NormalAngleBearing(double angle) {
			if (angle > Math.PI){
			 	angle -= 2*Math.PI;
			}
			if (angle < -Math.PI){
			 	angle += 2*Math.PI;
			}
			return angle;
		 } 
        public Double AngleBy(double x1, double y1, double x2, double y2)
        {
        	 double x = x2-x1;
			 double y = y2-y1;
			 double h =  Math.sqrt((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1));
			 
			 if( x > 0 && y > 0 )
			 {
			 	return Math.asin( x / h );
			 }
			 if( x > 0 && y < 0 )
			 {
			 	return Math.PI - Math.asin( x / h );
			 }
			 if( x < 0 && y < 0 )
			 {
			 	return Math.PI + Math.asin( -x / h );
			 }
			 if( x < 0 && y > 0 )
			 {
			 	return 2.0*Math.PI - Math.asin( -x / h );
			 }
			 return 0.0; 
        }


	public void onScannedRobot(ScannedRobotEvent e) {
		e_distance = e.getDistance();
		e_bearing = e.getBearingRadians();
		e_heading = e.getHeadingRadians();
		e_show = getTime();
		e_velocity = e.getVelocity();
		double angle = (getHeadingRadians()+e_bearing) %( 2*Math.PI);
		e_x = getX() + Math.sin(angle) * e_distance;
		e_y = getY() + Math.cos(angle) * e_distance;
	}


	public void onHitByBullet(HitByBulletEvent e) {
		dirrection *= -1;
	}
	
	public void onBulletHit(BulletHitEvent e) {
		hit_time = getTime();
	}
	
	public void onHitRobot(HitRobotEvent e){
		setTurnGunLeftRadians(e.getBearingRadians());
		fire(max_power/2);
	}

	public void onHitWall(HitWallEvent e) {
		if(wall_time == 0){
			dirrection *= -1;
			setBack(max_distance + (int)Math.random() * max_distance);
			execute();
		}
		 wall_time += 1;
	}
}
