/* CRITTERS Main.java
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

public class Critter2 extends Critter {

	/*
	 * Returns string identity of Critter2
	 * @param none
	 * @return return "2"
	 */
	@Override
	public String toString() { return "2"; }
	
	private static final int GENE_TOTAL = 24;
	private int[] genes = new int[8];
	private int encounterGene;
	private int dir;
	
	public Critter2() {
		for (int k = 0; k < 8; k += 1) {
			genes[k] = GENE_TOTAL / 8;
		}
		encounterGene = 90;
		dir = Critter.getRandomInt(8);
	}
	
	/*
	 * Perform time step for critter2
	 * @param none
	 * @return nothings is returned
	 */
	@Override
	public void doTimeStep() {
		// stupid critter always wastes energy by walking or running twice.
		int moveDecision = Critter.getRandomInt(3);
		switch(moveDecision){
			case 0:
				run(dir);
				run(dir);
				break;
			case 1:
				walk(dir);
				walk(dir);
				break;
			case 2:
				break;
		}
		
		if (getEnergy() >= Params.min_reproduce_energy) {
			Critter2 child = new Critter2();
			child.encounterGene = this.encounterGene;
			int e = Critter.getRandomInt(100);
			if (e < 10){
				child.encounterGene -= 1;
				if (child.encounterGene < 0){
					child.encounterGene = 0;
				}
			}
			
			for (int k = 0; k < 8; k += 1) {
				child.genes[k] = this.genes[k];
			}
			int g = Critter.getRandomInt(8);
			while (child.genes[g] == 0) {
				g = Critter.getRandomInt(8);
			}
			child.genes[g] -= 1;
			g = Critter.getRandomInt(8);
			child.genes[g] += 1;
			reproduce(child, Critter.getRandomInt(8));
		}
		
		/* pick a new direction based on our genes */
		int roll = Critter.getRandomInt(GENE_TOTAL);
		int turn = 0;
		while (genes[turn] <= roll) {
			roll = roll - genes[turn];
			turn = turn + 1;
		}
		assert(turn < 8);
		
		dir = (dir + turn) % 8;
	}

	/*
	 * Perfrom fight actions for critter 2
	 * @param opponent's string identity
	 * @return returns true if critter wants to fight
	 */
	@Override
	public boolean fight(String opponent) {
		int encounterChoice = Critter.getRandomInt(100);
		
		// majority of the time, this critter thinks algae is dangerous
		if (opponent.equals("A") && (encounterChoice < encounterGene)){  
			run(dir); // it wastes more energy by running
			run(dir); // AND wastes another energy by running again
			return false;
		}
		return true; // it fights other critters
	}

	
	public static void runStats(java.util.List<Critter> imbs) {
		int total_gene = 0;

		for (Object obj : imbs) {
			Critter2 c = (Critter2) obj;
			total_gene += c.encounterGene;
		}
		System.out.print("" + imbs.size() + " total Critter 2    ");
		if (imbs.size() > 0){
			System.out.print("" + total_gene / (imbs.size()) + "% avoid Algae ");
		}
		System.out.println();
	}

	@Override
	public CritterShape viewShape() {
		return CritterShape.CIRCLE;
	}
	public javafx.scene.paint.Color viewOutlineColor() { return javafx.scene.paint.Color.ORANGE; }
	public javafx.scene.paint.Color viewColor() { return javafx.scene.paint.Color.RED; }
}
