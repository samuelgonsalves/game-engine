package section1;
import processing.core.PApplet;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

public class usingprocessing extends PApplet 
{
	static Shape x;
	rectangle r[]=new rectangle[5];
	
	//key press[] is used to track whether a key is pressed. 0 <-> 'a', 1 <-> 'd', 2 <-> Space
	
	boolean key_press[]=new boolean[3];
	
	static int velocity_x,velocity_y,gravity=1,ground_y=450;
	static boolean moving=false;
	static boolean jumping=false;
	static final int HEIGHT=500;
	static final int WIDTH=500;
	
	
	
	public static void main(String[] args) 
	{			
		PApplet.main("section1.usingprocessing");	
	}

	public void settings()
	{
		size(WIDTH,HEIGHT);
	}
	public void setup()
	{
		r[0]=new rectangle(this,60,350,100,100);
		r[1]=new rectangle(this,40,20,100,140);
		r[2]=new rectangle(this,300,150,100,30);
		r[3]=new rectangle(this,300,100,20,50);
		r[4]=new rectangle(this,400,300,80,100);
		x=new Rectangle2D.Double(r[0].x,r[0].y,r[0].width,r[0].height);
	}
	
	/*
	 * Jumping is based on the logic described in the following tutorial:
	 * Website - http://www.rodedev.com/tutorials/gamephysics/ 
	 * Author - Nate Rode 	 
	 */
	public void draw()
	{	
		background(0);
		stroke(126);
		line(0,450,500,450);
		for(int i=0;i<r.length;i++)
		{
			r[i].set_color(5+i*60);
			r[i].draw_rect();
	
		}
		
		if(jumping==false && key_press[2]==true)
		{
			velocity_y=-15;
			jumping=true;
		}
		if(jumping==true)
			 {
				if((r[0].y + velocity_y+ r[0].height) < ground_y)
			 	{
					
						r[0].y= r[0].y + velocity_y;
						velocity_y = velocity_y + gravity;		
				}
				else
				{
					jumping=false;
					key_press[2]=false;
					r[0].y=ground_y-r[0].height;
				}
			 }
	
		if(moving==false && key_press[0]==true)
		{
			moving=true;
			velocity_x=-8;
		}
		else if(moving==false && key_press[1]==true)
		{
			moving=true;
			velocity_x=8;	
		}
		else
		{
			moving = false;
		}
		if(moving ==true )
		{
			if(!crossedBoundary() && !collision())
				r[0].x+=velocity_x;
		}
		}
	public void collision_y()
	{
		/*
		 * Code to detect vertical collisions goes here
		 */
		
	}
	
	/*
	 * collision()
	 * Returns if a collision with another rectangle has occurred
	 */
	
	public boolean collision()
	{
		int flag_check=0;

		x=new Rectangle2D.Double(r[0].x + velocity_x,r[0].y+velocity_y,r[0].width,r[0].height);
		for(int i=1;i<5;i++)
			if(x.intersects(r[i].x,r[i].y,r[i].width,r[i].height))
			{
				flag_check=1;
				break;
			}
		if(flag_check==1)
		{
			return true;
		}
		else
			return false;
	}

	/*
	 * crossedBoundary()
	 * Returns true if the rectangle is going to cross the left or right boundary of the frame
	 */

	public boolean crossedBoundary()
	{
		if((r[0].x+r[0].width+velocity_x)>WIDTH)
			return true;
		else if((r[0].x+velocity_x)<0)
			return true;
		else
			return false;
	}
	
	 
	 public void keyPressed()
	 {
		 if(key=='a'||key=='A')
			 key_press[0]=true;
		 if(key=='d'||key=='D')
			 key_press[1]=true;
		 if(key==32) // Space is pressed
			 key_press[2]=true;
	 }
	 
	 public void keyReleased()
	 {
		 if(key=='a' || key=='A')
			 key_press[0]=false;
		 if(key=='d'||key=='D')
			 key_press[1]=false;
		 
	}
}

