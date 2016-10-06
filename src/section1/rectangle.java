package section1;
import processing.core.PApplet;
public class rectangle 
{
	PApplet parent;
	int x,y,width,height;
	
	rectangle(PApplet p,int x,int y,int width,int height)
	{
		parent=p;
		this.x=x;
		this.y=y;
		this.width=width;
		this.height=height;
	}
	//For different colors for a rectangle
	public void set_color(int color)
	{
		parent.fill(color%255,(color+40)%255,(color*3)%255);
	}
	//To draw the rectangle on the processing sketch
	public void draw_rect()
	{
		parent.rect(x,y,width,height);
	}
	
}