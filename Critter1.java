/* CRITTERS Main.java
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

package assignment5;

public class Critter1 extends Critter {
	
	/*
	 * Returns string identity of Critter1
	 * @param none
	 * @return return "1"
	 */
	@Override
	public String toString() { return "1"; }
	
	private int[] moveGenes = new int[4];
	private int dir;
	
	public Critter1() { // cannibal only moves like a chess equivalent of bishop (diagnoal movements)
		moveGenes[0] = 1;
		moveGenes[1] = 3;
		moveGenes[2] = 5;
		moveGenes[3] = 7;
		dir = moveGenes[Critter.getRandomInt(4)];
	}
	
	/*
	 * Perform time step for critter1
	 * @param none
	 * @return nothings is returned
	 */
	@Override
	public void doTimeStep() {
		int moveDecision = Critter.getRandomInt(3);
		switch(moveDecision){
			case 0:
				run(dir);
				break;
			case 1:
				walk(dir);
				break;
			case 2:
				break;
		}
		
		if (this.getEnergy() >= Params.min_reproduce_energy){
			Critter1 child = new Critter1();
			reproduce(child, Critter.getRandomInt(8));
		}
		
		dir = moveGenes[Critter.getRandomInt(4)];
	}

	/*
	 * Perform fight actions for critter 1
	 * @param opponent's string identity
	 * @return returns true if critter wants to fight
	 */
	@Override
	public boolean fight(String opponent) {
		// fights algae or its own type when it has sufficient energy
		
		String cannibalName = "1";
		if ((opponent.equals("A") || opponent.equals(cannibalName)) && getEnergy() > 20){ 
			return true;
		}
		run(dir); // else, it "tries" to run away
		return false;
	}

	/*
	 * Run statistics of critter1
	 * @param list of critter 1 in the world
	 * @return nothing is returned
	 */
	public static void runStats(java.util.List<Critter> cannibals) {
		int total_gene = 0;

		System.out.print("" + cannibals.size() + " total Critter1/Cannibals");
		System.out.println();
	}

	@Override
	public CritterShape viewShape() {
		return CritterShape.TRIANGLE;
	}
	@Override
	public javafx.scene.paint.Color viewOutlineColor() { return javafx.scene.paint.Color.PURPLE; }
	public javafx.scene.paint.Color viewColor() { return null; }
}
