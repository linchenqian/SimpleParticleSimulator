public class obj {
	public float positionx;
	public float positiony;
	public float velocityx;
	public float velocityy;
	
	obj(float positionx, float positiony, float velocityx, float velocityy){
		this.positionx = positionx;
		this.positiony = positiony;
		this.velocityx = velocityx;
		this.velocityy = velocityy;
	}
	
	public void Go(float accelerationx,float accelerationy){
		positionx += velocityx;
		positiony += velocityy;
		velocityx += accelerationx;
		velocityy += accelerationy;
	}
}