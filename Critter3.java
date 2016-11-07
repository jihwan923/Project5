/* CRITTERS <MyClass.java>
 * EE422C Project 4 submission by
 * Jihwan Lee
 * jl54387
 * 16445
 * Kevin Liang
 * kgl392
 * 16445
 * Slip days used: <0>
 * Fall 2016
 */
package assignment4;

public class Critter3 extends Critter {

	private int direction;
	private int[] moveGenes = new int[4];
	
	public Critter3(){
		moveGenes[0] = 0;
		moveGenes[1] = 2;
		moveGenes[2] = 4;
		moveGenes[3] = 6;
		direction = moveGenes[Critter.getRandomInt(4)];
	}
	/**
	 * @return string to identify critter
	 */
	@Override
	public String toString(){
		return "3";
	}
	
	/**
	 * time step for critter3: critter walks, then reproduces if energy is greater than 100
	 */
	@Override
	public void doTimeStep() {
		// walks like a rook in chess
		walk(direction);
		direction = moveGenes[Critter.getRandomInt(4)];
		if(getEnergy() > 100){	// reproduces if energy is greater than 100
			Critter3 other = new Critter3();
			reproduce(other, direction);
			direction = moveGenes[Critter.getRandomInt(4)];
		}
	}

	
	/**
	 * fight method for critter3: only fights critters of other types
	 * @param opponent string that determines the opposing critter
	 * @return true if critter wants to fight, false if critter runs
	 */
	@Override
	public boolean fight(String opponent) {
		// this critter only fights critters of other types
		if(opponent.equals("3")){	// run if opponent is same critter type as itself
			run(direction);
			direction = moveGenes[Critter.getRandomInt(4)];
			return false;
		}
		return true;
	}

}
