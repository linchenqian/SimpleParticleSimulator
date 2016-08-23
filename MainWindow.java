import java.applet.Applet;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.Date;

public class MainWindow extends Applet implements Runnable{
	
	float objMass;
	float accelerationsx[], accelerationsy[];
	final float g = 1f;
	int objNumber;
	obj objs[];
	float sizeScale;
	float mapSizex, mapSizey;
	Thread thread;
	float fps;
	int delay;
	int mouseX=000, mouseY=000;
	public void init(){
		this.setSize(2000, 1000);
		sizeScale = 1f;//1 m per pixel
		mapSizex = this.getWidth()*sizeScale;
		mapSizey = this.getHeight()*sizeScale;
		objMass = 1000;//1 kg

		int objAmountx = 1;
		int objAmounty = 2000;
		objNumber = objAmountx*objAmounty;//10 objects per 100km
		objs = new obj[objNumber];
		accelerationsx = new float[objNumber];
		accelerationsy = new float[objNumber];
		for(int i = 0; i <= objs.length - 1; i++){
			//objs[i] = new obj((-mapSizex/2)+mapSizex/objAmountx*((float)(i%objAmountx)+0.5f),(-mapSizey/2)+mapSizey/objAmounty*((float)(i/objAmountx)+0.5f),0,0);
			float PosX=(float)((Math.random()-0.5)*mapSizex*0.1);
			float PosY=(float)((Math.random()-0.5)*mapSizey*0.5);
			//float PosX=(-mapSizex/2)/2+mapSizex/objAmountx*((float)(i%objAmountx)+0.5f)/2;
			//float PosY=(-mapSizey/2)+mapSizey/objAmounty*((float)(i/objAmountx)+0.5f);
			float ar=(float) Math.sqrt(0.15/Math.sqrt(Math.pow(PosX, 2)+Math.pow(PosY, 2)));
			float VelX=(float)(ar*PosY);
			float VelY=(float)(-ar*PosX);
			objs[i] = new obj(PosX,PosY,VelX,VelY);
		}
		this.addMouseMotionListener(new MouseMotionListener(){

			@Override
			public void mouseDragged(MouseEvent arg0) {
				// TODO Auto-generated method stub
				mouseX = (int) (arg0.getX()*sizeScale-mapSizex/2);
				mouseY = (int) (arg0.getY()*sizeScale-mapSizey/2);
			}

			@Override
			public void mouseMoved(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
	}
	
	public void start(){
		thread = new Thread(this);
		thread.start();
	}
	
	public void stop(){
		
	}

	public void run() {
		fps = 60f;
		delay = (int) (1000 / fps);
		BufferedImage map = new BufferedImage(this.getWidth(),this.getHeight(),BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = (Graphics2D) map.getGraphics();
		g2d.setBackground(Color.BLACK);
		g2d.setColor(Color.WHITE);
		int x,y;
		//skip frame
		int skip = 1;
		int skipcount = 0;
		while (true){
			try {
				calculateAcceleration2();
				//calculateAcceleration();
				g2d.clearRect(0, 0, this.getWidth(), this.getHeight());
				skipcount++;
				for(int i = 0; i <= objs.length - 1; i++){
					x = (int) (objs[i].positionx/sizeScale)+this.getWidth()/2;
					y = (int) (objs[i].positiony/sizeScale)+this.getHeight()/2;
					if(skipcount == skip){
						g2d.setColor(Color.GREEN);
						g2d.drawLine(x, y, x+(int)(accelerationsx[i]*fps*1), y+(int)(accelerationsy[i]*fps*1));
						g2d.setColor(Color.WHITE);
						g2d.drawLine(x,y,x,y);
					}
					objs[i].Go(accelerationsx[i], accelerationsy[i]);
					accelerationsx[i]=0;
					accelerationsy[i]=0;
					//relocate if out of the map
					/*
					while(objs[i].positionx>mapSizex/2){
						objs[i].positionx=objs[i].positionx-mapSizex;
						//objs[i].positiony=-objs[i].positiony;
					}
					while(objs[i].positionx<-mapSizex/2){
						objs[i].positionx=objs[i].positionx+mapSizex;
						//objs[i].positiony=-objs[i].positiony;
					}
					while(objs[i].positiony>this.mapSizey/2){
						objs[i].positiony=objs[i].positiony-mapSizey;
						//objs[i].positionx=-objs[i].positionx;
					}
					while(objs[i].positiony<-mapSizey/2){
						objs[i].positiony=objs[i].positiony+mapSizey;
						//objs[i].positionx=-objs[i].positionx;
					}
					*/
				}
				if(skipcount==skip){
					refresh(map);
					skipcount=0;
					Thread.sleep(delay);
				}
				//this.getGraphics().setColor(Color.blue);
				//this.getGraphics().drawRect(0, 0, 10, 10);
			} catch (Exception e) {}
		}
	}
	
	public void refresh(Image scene) {
		// Graphics g = this.getGraphics();
		// super.paintComponents(g);
		this.getGraphics().drawImage(scene, 0, 0, null);
	}
	
	private void calculateAcceleration2(){
		float currentAccelerationx;
		float currentAccelerationy;
		float dx, dy, r, a;
		for(int i = 0; i <= objs.length-1; i++){
			dx = (mouseX-objs[i].positionx)*1;
			dy = (mouseY-objs[i].positiony)*1;
			r = (float) Math.sqrt(Math.pow(dx, 2f) + Math.pow(dy, 2f));
			a = (float) (g*(objMass*10000)*0.5*Math.pow((r*1), -2));
			//if(a>10)
			a=10;
			currentAccelerationx = a * (dx / r)/fps;
			currentAccelerationy = a * (dy / r)/fps;
			
			accelerationsx[i] += currentAccelerationx;
			accelerationsy[i] += currentAccelerationy;
		}
	}
	
	private void calculateAcceleration(){
		float currentAccelerationx;
		float currentAccelerationy;
		float dx, dy, r, a;
		for(int i = 0; i < objs.length-1; i++){
			for(int o = i + 1; o <= objs.length - 1; o++){
				dx = (objs[o].positionx-objs[i].positionx)*1;
				dy = (objs[o].positiony-objs[i].positiony)*1;
				r = (float) Math.sqrt(Math.pow(dx, 2f) + Math.pow(dy, 2f));
				a = (float) (g*(objMass*300)*0.5*Math.pow((r*1), -2));
				if(a>10)
					a=10;
				currentAccelerationx = a * (dx / r)/fps;
				currentAccelerationy = a * (dy / r)/fps;
				
				accelerationsx[i] += currentAccelerationx;
				accelerationsy[i] += currentAccelerationy;
				accelerationsx[o] -= currentAccelerationx;
				accelerationsy[o] -= currentAccelerationy; 
			}
		}
	}
}
