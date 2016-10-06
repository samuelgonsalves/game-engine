package sendreceiveobjects;

import java.io.Serializable;

public class Myobject implements Serializable
{
	int x,y;
	String z;
	static int count=0;
	Myobject()
	{
		count++;
		this.x=10+count;
		this.y=2+count;
		this.z="Hi";
	}
	public void display()
	{
		System.out.println("X:"+ x);
		System.out.println("Y:"+y);
		System.out.println("Z:"+z);
	}
}
