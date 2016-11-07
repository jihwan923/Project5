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

import java.lang.reflect.Constructor;
import java.util.List;

import assignment5.Critter;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;


/* see the PDF for descriptions of the methods and fields in this class
 * you may add fields, methods or inner classes to Critter ONLY if you make your additions private
 * no new public, protected or default-package code or data can be added to Critter
 */


public abstract class Critter {
	static double gridCellSize = 850/Params.world_width;
	
	private static String myPackage;
	private	static List<Critter> population = new java.util.ArrayList<Critter>();
	private static List<Critter> babies = new java.util.ArrayList<Critter>();

	// Gets the package name.  This assumes that Critter and its subclasses are all in the same package.
	static {
		myPackage = Critter.class.getPackage().toString().split(" ")[1];
	}
	
	private static java.util.Random rand = new java.util.Random();
	public static int getRandomInt(int max) {
		return rand.nextInt(max);
	}
	
	public static void setSeed(long new_seed) {
		rand = new java.util.Random(new_seed);
	}
	
	public enum CritterShape {
		CIRCLE,
		SQUARE,
		TRIANGLE,
		DIAMOND,
		STAR
	}
	
	/* the default color is white, which I hope makes critters invisible by default
	 * If you change the background color of your View component, then update the default
	 * color to be the same as you background 
	 * 
	 * critters must override at least one of the following three methods, it is not 
	 * proper for critters to remain invisible in the view
	 * 
	 * If a critter only overrides the outline color, then it will look like a non-filled 
	 * shape, at least, that's the intent. You can edit these default methods however you 
	 * need to, but please preserve that intent as you implement them. 
	 */
	public javafx.scene.paint.Color viewColor() { 
		return javafx.scene.paint.Color.WHITE; 
	}
	
	public javafx.scene.paint.Color viewOutlineColor() { return viewColor(); }
	public javafx.scene.paint.Color viewFillColor() { return viewColor(); }
	
	public abstract CritterShape viewShape(); 
	
	/* a one-character long string that visually depicts your critter in the ASCII interface */
	public String toString() { return ""; }
	
	private int energy = 0;
	protected int getEnergy() { return energy; }
	
	private int x_coord;
	private int y_coord;
	private boolean hasMoved = false;
	private int xPast;
	private int yPast;
	private static boolean fight = false;
	
	/**
	  * Given position, it wraps it around the world_width if it is beyond the edge
	  * @param pos is the position that is to be tested
	  * @return return either a wrapped position or unwrapped position if pos is valid
	  */
	protected int wrapX(int pos){
		if (pos < 0){
			return Params.world_width - 1;
		}
		else if (pos == Params.world_width){
			return 0;
		}
		else{
			return pos;
		}
	}
	
	/**
	  * Given position, it wraps it around the world_height if it is beyond the edge
	  * @param pos is the position that is to be tested
	  * @return return either a wrapped position or unwrapped position if pos is valid
	  */
	protected int wrapY(int pos){
		if (pos < 0){
			return Params.world_height - 1;
		}
		else if (pos == Params.world_height){
			return 0;
		}
		else{
			return pos;
		}
	}
	
	/**
	  * Given direction, it calculates the new x position
	  * @param direction is the direction of the movement
	  * @return return new x coordinate
	  */
	protected int newX(int direction){
		switch(direction){
		case 0: return wrapX(x_coord + 1);
		case 1: return wrapX(x_coord + 1);
		case 7: return wrapX(x_coord + 1);
		case 3: return wrapX(x_coord - 1);
		case 4: return wrapX(x_coord - 1);
		case 5: return wrapX(x_coord - 1);
		}
		return x_coord; // for directions 2 and 6
	}
	
	/**
	  * Given direction, it calculates the new y position
	  * @param direction is the direction of the movement
	  * @return return new y coordinate
	  */
	protected int newY(int direction){
		switch(direction){
		case 5: return wrapY(y_coord + 1);
		case 6: return wrapY(y_coord + 1);
		case 7: return wrapY(y_coord + 1);
		case 1: return wrapY(y_coord - 1);
		case 2: return wrapY(y_coord - 1);
		case 3: return wrapY(y_coord - 1);
		}
		return y_coord; // for direction 0 and 4
	}
	
	/**
	  * Given direction, it moves the critter 1 step to that direction
	  * @param direction is the direction of the movement
	  * @return nothing is returned
	  */
	protected final void walk(int direction) {
		if (!hasMoved){
			x_coord = newX(direction);
			y_coord = newY(direction);
			hasMoved = true;
		}
		energy = energy - Params.walk_energy_cost;
	}
	
	/**
	  * Given direction, it moves the critter 2 step to that direction
	  * @param direction is the direction of the movement
	  * @return nothing is returned
	  */
	protected final void run(int direction) {
		if (!hasMoved){
			walk(direction);
			hasMoved = false;
			walk(direction);
			hasMoved = true;
			energy = energy - Params.run_energy_cost + Params.walk_energy_cost + Params.walk_energy_cost;
		}
		else{ // if critter has already moved, just subtract the run cost
			energy = energy - Params.run_energy_cost;
		}
	}
	
	/**
	  * Initialize the new offspring and add it to the babies list
	  * @param offspring is the newly created child of a critter
	  * @param direction is the direction the baby will be moved to
	  * @return nothing is returned
	  */
	protected final void reproduce(Critter offspring, int direction) {
		if(this.energy < Params.min_reproduce_energy){	// check that energy is greater than min reproduce energy
			return;
		}
		if(this.energy % 2 == 0){	// if energy is even
			this.energy = this.energy/2;
		}
		else{ // if energy is odd, round up the energy
			this.energy = this.energy/2 + 1;	
		}

		offspring.energy = this.energy/2;
		
		offspring.x_coord = this.x_coord;
		offspring.y_coord = this.y_coord;
		offspring.walk(direction); // move the baby 1 step away from parent
		
		babies.add(offspring); // add it to the babies collection temporarily for a time step
	}

	// Implemented on our critter classes
	public abstract void doTimeStep(); 
	public abstract boolean fight(String oponent); 
	
	/**
	 * create and initialize a Critter subclass.
	 * critter_class_name must be the unqualified name of a concrete subclass of Critter, if not,
	 * an InvalidCritterException must be thrown.
	 * (Java weirdness: Exception throwing does not work properly if the parameter has lower-case instead of
	 * upper. For example, if craig is supplied instead of Craig, an error is thrown instead of
	 * an Exception.)
	 * @param critter_class_name
	 * @throws InvalidCritterException
	 */
	public static void makeCritter(String critter_class_name) throws InvalidCritterException {
		try{
			String packageClass = myPackage + "." + critter_class_name;
			Class<?> critterClass = Class.forName(packageClass);
			Constructor<?> newConstructor = critterClass.getConstructor();
			Object newCritter = newConstructor.newInstance();
			
			if (!(newCritter instanceof Critter)){
				throw new InvalidCritterException(critter_class_name);
			}
			Critter newCrit = (Critter)newCritter;
			newCrit.energy = Params.start_energy;
			newCrit.x_coord = getRandomInt(Params.world_width);
			newCrit.y_coord = getRandomInt(Params.world_height);
			newCrit.xPast = newCrit.x_coord;
			newCrit.yPast = newCrit.y_coord;
			population.add((Critter)newCritter); // add to the critter collection
		}
		catch(ClassNotFoundException c){
			throw new InvalidCritterException(critter_class_name);
		}
		catch(InstantiationException i){
			throw new InvalidCritterException(critter_class_name);
		}
		catch(IllegalAccessException a){
			throw new InvalidCritterException(critter_class_name);
		}
		catch(Exception e){
			throw new InvalidCritterException(critter_class_name);
		}
	}
	
	/**
	 * Gets a list of critters of a specific type.
	 * @param critter_class_name What kind of Critter is to be listed.  Unqualified class name.
	 * @return List of Critters.
	 * @throws InvalidCritterException
	 */
	public static List<Critter> getInstances(String critter_class_name) throws InvalidCritterException {
		List<Critter> result = new java.util.ArrayList<Critter>();
		
		if (critter_class_name.equals("Critter")){
			throw new InvalidCritterException(critter_class_name);
		}
		try{
			String packageClass = myPackage + "." + critter_class_name;
			Class<?> critterClass = Class.forName(packageClass);
			Object newCritter = critterClass.newInstance();
			if (!(newCritter instanceof Critter)){
				throw new InvalidCritterException(critter_class_name);
			}
			
			for(Critter c: population){
				if (critterClass.isAssignableFrom(c.getClass())){ ///// ?????
					result.add(c);
				}
			}
		}
		catch(Exception e){
			throw new InvalidCritterException(critter_class_name);
		}
		return result;
	}
	
	/**
	 * Prints out how many Critters of each type there are on the board.
	 * @param critters List of Critters.
	 */
	public static void runStats(List<Critter> critters) {
		System.out.print("" + critters.size() + " critters as follows -- ");
		java.util.Map<String, Integer> critter_count = new java.util.HashMap<String, Integer>();
		for (Critter crit : critters) {
			String crit_string = crit.toString();
			Integer old_count = critter_count.get(crit_string);
			if (old_count == null) {
				critter_count.put(crit_string,  1);
			} else {
				critter_count.put(crit_string, old_count.intValue() + 1);
			}
		}
		String prefix = "";
		for (String s : critter_count.keySet()) {
			System.out.print(prefix + s + ":" + critter_count.get(s));
			prefix = ", ";
		}
		System.out.println();		
	}
	
	/* the TestCritter class allows some critters to "cheat". If you want to 
	 * create tests of your Critter model, you can create subclasses of this class
	 * and then use the setter functions contained here. 
	 * 
	 * NOTE: you must make sure that the setter functions work with your implementation
	 * of Critter. That means, if you're recording the positions of your critters
	 * using some sort of external grid or some other data structure in addition
	 * to the x_coord and y_coord functions, then you MUST update these setter functions
	 * so that they correctly update your grid/data structure.
	 */
	static abstract class TestCritter extends Critter {
		protected void setEnergy(int new_energy_value) {
			super.energy = new_energy_value;
		}
		
		protected void setX_coord(int new_x_coord) {
			super.x_coord = new_x_coord;
		}
		
		protected void setY_coord(int new_y_coord) {
			super.y_coord = new_y_coord;
		}
		
		protected int getX_coord() {
			return super.x_coord;
		}
		
		protected int getY_coord() {
			return super.y_coord;
		}
		

		/*
		 * This method getPopulation has to be modified by you if you are not using the population
		 * ArrayList that has been provided in the starter code.  In any case, it has to be
		 * implemented for grading tests to work.
		 */
		protected static List<Critter> getPopulation() {
			return population;
		}
		
		/*
		 * This method getBabies has to be modified by you if you are not using the babies
		 * ArrayList that has been provided in the starter code.  In any case, it has to be
		 * implemented for grading tests to work.  Babies should be added to the general population 
		 * at either the beginning OR the end of every timestep.
		 */
		protected List<Critter> getBabies() {
			return babies;
		}
	}
	
	/**
	  * Implements a time step that does the following in order:
	  * 1. Invoke doTimeStep for all critters
	  * 2. Resolves conflicts and fights between critters
	  * 3. Apply rest energy cost to all critters
	  * 4. Remove all dead critters
	  * 5. Refresh algae count in the world
	  * 6. Add newly created babies to the world
	  * @param nothing is passed in
	  * @return nothing is returned
	  */
	public static void worldTimeStep() {
		for(Critter c: population){ // invoke doTimeStep for all critters
			c.doTimeStep();
		}
		
		boolean repeatAgain = true;
		boolean breakOut = false;
		
		fight = true; // set fight to true
		
		while(repeatAgain){ // deal with all conflicts/encounters of the critters
			breakOut = false;
			for(int i = 0; i < population.size(); i++){
				for(int j = i + 1; j < population.size(); j++){
					Critter c1 = population.get(i);
					Critter c2 = population.get(j);
					if ((c1.x_coord == c2.x_coord && c1.y_coord == c2.y_coord) && (c1.energy > 0 && c2.energy > 0)){
						String name1 = c1.toString();
						String name2 = c2.toString();
						int cx1 = c1.x_coord;
						int cx2 = c2.x_coord;
						int cy1 = c1.y_coord;
						int cy2 = c2.y_coord;
						
						boolean decision1 = c1.fight(name2);
						boolean decision2 = c2.fight(name1);
						if (!decision1){
							if (!isRunSafe(c1)){
								c1.x_coord = cx1;
								c1.y_coord = cy1;
							}
						}
						if (!decision2){
							if (!isRunSafe(c2)){
								c2.x_coord = cx2;
								c2.y_coord = cy2;
							}
						}
						
						if (c1.energy > 0 && c2.energy > 0 && (c1.x_coord == c2.x_coord && c1.y_coord == c2.y_coord)){
							encounter(c1,c2,decision1,decision2);
							breakOut = true;
							break;
						}
					}
				}
				if (breakOut){
					break;
				}
			}
			if (!breakOut){ // if there were no conflicts, exit parsing the encounter
				repeatAgain = false;
			}
		}
		
		for(Critter c: population){ // apply rest energy cost to all critters
			c.energy = c.energy - Params.rest_energy_cost;
		}
		
		for(int i = 0; i < population.size(); i++){ // remove all dead critters
			Critter c = population.get(i);
			c.hasMoved = false; 
			c.xPast = c.x_coord; // update critters' x and y past coordinate values
			c.yPast = c.y_coord; 
			
			if (c.energy <= 0){
				population.remove(i);
				i -= 1;
			}
			
			if (population.size() == 0){
				break;
			}
		}
		
		for(int j = 0; j < Params.refresh_algae_count; j++){ // refresh algae count
			try{
				makeCritter("Algae");
			}
			catch(InvalidCritterException c){
				
			}
		}
		
		for(Critter baby: babies){ // add all newly created critters in the world
			baby.hasMoved = false;
			population.add(baby);
		}
		babies.clear();
		fight = false; // set fight back to false
	}
	
	/**
	  * Displays the current status of the world
	  * @param nothing is passed in
	  * @return nothing is returned
	  */
	public static void displayWorld() {
		Main.grid.getChildren().clear();
		for(int i = 0; i < Params.world_width; i++){
			for(int j = 0; j < Params.world_height; j++){
				Shape s = new Rectangle(gridCellSize,gridCellSize);
				s.setFill(null);
				s.setStroke(Color.LIGHTYELLOW);
				Main.grid.add(s, i, j);
			}
		}
		
		for(Critter c: population){ // place all the critters to the world
			switch(c.viewShape()){
				case CIRCLE:
					Shape newCircle = new Circle(gridCellSize/2);
					newCircle.setFill(c.viewFillColor());
					newCircle.setStroke(c.viewOutlineColor());
					Main.grid.add(newCircle, c.x_coord, c.y_coord);
					break;
				case SQUARE:
					Shape newSquare = new Rectangle(gridCellSize, gridCellSize);
					newSquare.setFill(c.viewFillColor());
					newSquare.setStroke(c.viewOutlineColor());
					Main.grid.add(newSquare, c.x_coord, c.y_coord);
					break;
				case TRIANGLE:
					Polygon newTriangle = new Polygon();
					newTriangle.getPoints().setAll(new Double[]{
							gridCellSize/2, 1.0,
							1.0, gridCellSize-1.0,
							gridCellSize-1.0, gridCellSize-1.0});
					newTriangle.setFill(c.viewFillColor());
					newTriangle.setStroke(c.viewOutlineColor());
					Main.grid.add(newTriangle, c.x_coord, c.y_coord);
					break;
				case DIAMOND:
					Polygon newDiamond = new Polygon();
					newDiamond.getPoints().setAll(new Double[]{
							gridCellSize/2, 1.0,
							1.0, gridCellSize/2,
							gridCellSize/2, gridCellSize-1.0,
							gridCellSize-1.0, gridCellSize/2});
					newDiamond.setFill(c.viewFillColor());
					newDiamond.setStroke(c.viewOutlineColor());
					Main.grid.add(newDiamond, c.x_coord, c.y_coord);
					break;
				case STAR: 
					Polygon newStar = new Polygon();
					newStar.getPoints().setAll(new Double[]{
							2.0, gridCellSize-2.0,
							gridCellSize/2, 2.0,
							gridCellSize-10.0, gridCellSize-2.0,
							2.0, gridCellSize/2.5,
							gridCellSize-2.0, gridCellSize/2.5});
					newStar.setFill(c.viewFillColor());
					newStar.setStroke(c.viewOutlineColor());
					Main.grid.add(newStar, c.x_coord, c.y_coord);
					break;
			}
				
		}
	}
	
	/**
	  * Resolves encounter of two critters in same spot
	  * @param c1 is the first critter
	  * @param c2 is the second critter
	  * @param fight1 is first critter's decision to fight
	  * @param fight2 is second critter's decision to fight
	  * @return nothing is returned
	  */
	private static void encounter(Critter c1, Critter c2, boolean fight1, boolean fight2){
		int powerLevel1 = getRandomInt(c1.energy + 1);
		int powerLevel2 = getRandomInt(c2.energy + 1);
		
		if (!fight1){
			powerLevel1 = 0;
		}
		if (!fight2){
			powerLevel2 = 0;
		}
		
		if (powerLevel1 > powerLevel2){ // the critter with lower fight/power level loses
			c1.energy = c1.energy + (c2.energy/2);
			c2.energy = 0;
		}
		else{ // if both have same power/fight level, just choose second critter as the winner
			c2.energy = c2.energy + (c1.energy/2);
			c1.energy = 0;
		}
	}
	
	/**
	  * Checks if a critter during encounter can run away to the spot it wants to run to
	  * @param crit is the critter that is being checked
	  * @return return true if critter can run to the spot safely
	  */
	private static boolean isRunSafe(Critter crit){
		for(Critter c: population){ // dead critters are ignored
			if (c.x_coord == crit.x_coord && c.y_coord == crit.y_coord && crit != c && c.energy > 0){
				return false;
			}
		}
		return true;
	}

	
	protected String look(int direction, boolean steps){
		int currentX = this.x_coord;
		int currentY = this.y_coord;
		int lookX;
		int lookY;
		
		boolean taken = false;
		String takenCritter = "";
		
		if(steps){
			lookX = newX(direction);
			lookY = newY(direction);
		} 
		else{
			lookX = newX(direction);
			lookY = newY(direction);
			this.x_coord = lookX;
			this.y_coord = lookY;
			lookX = newX(direction);
			lookY = newY(direction);
			this.x_coord = currentX;
			this.y_coord = currentY;
		}
		// if called during fight step
		if(fight){
			for(Critter c: population){
				if(c.x_coord == lookX && c.y_coord == lookY && c != this){
					taken = true;
					takenCritter = c.toString();
				}
			}
		
		} // if called during doTimeStep
		else{
			for(Critter c: population){
				if(c.xPast == lookX && c.yPast == lookY && c != this){
					taken = true;
					takenCritter = c.toString();
				}
			}
		}
		// reset coords back to original pre look
		this.x_coord = currentX;
		this.y_coord = currentY;
		this.energy = this.energy - Params.look_energy_cost;
		
		if(taken){
			return takenCritter;
		}
		else{
			return null;
		}
	}
	
	/**
	  * Clear the world
	  * @param nothing is passed in
	  * @return nothing is returned
	  */
	public static void clearWorld(){
		population.clear();
		babies.clear();
	}
}
