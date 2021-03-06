/* CRITTERS <MyClass.java>
 * EE422C Project 5 submission by
 * Jihwan Lee
 * jl54387
 * 16445
 * Kevin Liang
 * kgl392
 * 16445
 * Slip days used: <0>
 * Fall 2016
 */
package assignment5;

public class Critter4 extends Critter {
	private int randomDir;
	/**
	 * @return string to identify critter
	 */
	public String toString(){
		return "4";
	}
	/**
	 * time step for Critter4: doesn't move 80% of the time, walks 10% of the time, run's 10% of the time and reproduces if energy is greater than 200
	 */
	@Override
	public void doTimeStep() {
		int randomInt = Critter.getRandomInt(10);
		randomDir = Critter.getRandomInt(8);
		if(randomInt <= 7){
		}
		else if(randomInt == 8){
			run(randomDir);
			randomDir = Critter.getRandomInt(8);
		}
		else if(randomInt == 9){
			walk(randomDir);
			randomDir = Critter.getRandomInt(8);
		}
		if(getEnergy() > 200){	// only reproduces if energy is > 200
			Critter4 lazycritter = new Critter4();
			reproduce(lazycritter, randomDir);
			randomDir = Critter.getRandomInt(8);
		}
	}
	/**
	 * critter4 only fights algae and runs from any other critter
	 * @param string to identify opponent type
	 * @return true if the critter fights algae, false if critter runs
	 */
	@Override
	public boolean fight(String opponent) {
		if(opponent.equals("@")){	// fights algae
			return true;
		}
		walk(randomDir);			// walks away from any other fight
		randomDir = Critter.getRandomInt(8);
		return false;
	}
	@Override
	public CritterShape viewShape() {
		return CritterShape.DIAMOND;
	}
	public javafx.scene.paint.Color viewOutlineColor() { return javafx.scene.paint.Color.PINK; }
	public javafx.scene.paint.Color viewColor() { return javafx.scene.paint.Color.CYAN; }
}