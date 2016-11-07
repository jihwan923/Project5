package assignment5;

import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import assignment5.Critter;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;
import java.util.Timer;
import java.util.TimerTask;

public class Main extends Application{
	
	static GridPane grid = new GridPane();
	private static String myPackage;
	static TextArea consoleText = new TextArea();
	static boolean animationSentinel = false;
	public MenuButton statsMenu = new MenuButton();
	public ArrayList<String> statsList = new ArrayList<String>();
	private Timer animationTimer = new Timer();
	private AnimationTask animation = new AnimationTask();
	public int stepPerFrame = 1;
	static int steps = 0;
	
	// Gets the package name.  The usage assumes that Critter and its subclasses are all in the same package.
    static {
        myPackage = Critter.class.getPackage().toString().split(" ")[1];
    }
    
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		try{
			TextArea outBox = consoleText;
	        PrivateConsole sysOut = new PrivateConsole(outBox);
	        PrintStream ps = new PrintStream(sysOut, true);
	        System.setOut(ps);
	        System.setErr(ps);
			if (Params.world_height > Params.world_width){
				Critter.gridCellSize = 850/Params.world_height;
			}
			
			primaryStage.setTitle("Controller");
			grid.setGridLinesVisible(true); // grid lines are now visible
			Pane pane = new Pane(); // pane is a controller
			
//************************************************* Label/ComboBoxes ****************************************************
			Label makeCritter = new Label("Make Critter");
			Label stats = new Label("Critter Statistics");
			Label timestep = new Label("TimeStep");
			Label animate = new Label("Animation");
			Label quit = new Label("Quit");
			ComboBox<String> critterDrop = new ComboBox<String>();
			ComboBox<String> statsDrop = new ComboBox<String>();
			
//************************************************** Add Critter Names to ComboBoxes ******************************************************			
			
			ArrayList<String> classNames = new ArrayList<String>();			
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			Enumeration<URL> resources = classLoader.getResources(myPackage.replace('.', '/'));
			ArrayList<File> directoryPath = new ArrayList<File>();
			directoryPath.add(new File(resources.nextElement().getFile()));
			String directory = directoryPath.get(0).toString().replaceAll("%20", " ");
			File folder = new File(directory);
			File[] fileList = folder.listFiles();
			
			for(int f = 0; f < fileList.length; f++){
				if (fileList[f].isFile()){
					String name = (String)fileList[f].getName();
					//String[] nameEdit = name.split("\\.");
					classNames.add(name.split("\\.")[0]);
				}
			}
			for(String className: classNames){
				try{
					String realName = myPackage + "." + className;
					Class<?> critterClass = Class.forName(realName);
					Constructor<?> newConstructor = critterClass.getConstructor();
					Object newCritter = newConstructor.newInstance();
					if (newCritter instanceof Critter){
						critterDrop.getItems().add(className);
						statsDrop.getItems().add(className);
						
						CheckBox checkBox = new CheckBox(className);  
						CustomMenuItem newItem = new CustomMenuItem(checkBox);    
						newItem.setHideOnClick(false);  
						statsMenu.getItems().add(newItem);
						statsList.add(className);
					}
				}
				catch(Exception e){
					continue;
				}
			}

//********************************************** Critter Slider ***************************************************

			Slider critterSlider = new Slider(1, 100, 5);
			critterSlider.setShowTickMarks(true);
			critterSlider.setShowTickLabels(true);
			critterSlider.setMinorTickCount(1);
			critterSlider.setBlockIncrement(1);
			critterSlider.valueProperty().addListener((obs, oldval, newVal) ->
		    critterSlider.setValue(Math.round(newVal.doubleValue())));
			
			Label critterLabel = new Label("Number of Critters to add: " + Double.toString(critterSlider.getValue()));
			
			critterSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
			    critterLabel.setText("Number of Critters to add: " + critterSlider.getValue());
			});
			
//*********************************************** Add Critter Button ************************************************			

			Button buttonCritter = new Button("Add Critter");
			buttonCritter.setOnAction(new EventHandler<ActionEvent>() {
				 
	            @Override
	            public void handle(ActionEvent event) {
	            	try{
	            		if (critterDrop.getValue() != null){
	            			int temp = (int)critterSlider.getValue();
	            			for (int i = 0; i < temp; i++){
	            				Critter.makeCritter(critterDrop.getValue().toString());
	            			}
	            			Critter.displayWorld();
	            			for(int i = 0; i < statsMenu.getItems().size(); i++){
		            			CustomMenuItem m = (CustomMenuItem)statsMenu.getItems().get(i);
		            			CheckBox c = (CheckBox)m.getContent();
		            			if(c.isSelected()){
		            				String className = statsList.get(i);
		            				String fullName = myPackage + "." + className;
		            				Class critterClass = Class.forName(fullName);
		            				java.util.List<Critter> critterClassList = Critter.getInstances(className);
		            				Class<?>[] types = {List.class};
		            				Method runStatsMethod = critterClass.getMethod("runStats", types);
		            				runStatsMethod.invoke(null, critterClassList);
		            			}
		            		}
	            			
	            			//System.out.println();
	            		}
	            	}
	            	catch(Exception e){
	            		e.printStackTrace();
	            	}
	            }
	        });

//********************************************* Button to Run Stats *******************************************************

			Button buttonStats = new Button("RunStats");
			buttonStats.setOnAction(new EventHandler<ActionEvent>() {
				 
	            @Override
	            public void handle(ActionEvent event) {
	            	try{
	            		for(int i = 0; i < statsMenu.getItems().size(); i++){
	            			CustomMenuItem m = (CustomMenuItem)statsMenu.getItems().get(i);
	            			CheckBox c = (CheckBox)m.getContent();
	            			if(c.isSelected()){
	            				String className = statsList.get(i);
	            				String fullName = myPackage + "." + className;
	            				Class critterClass = Class.forName(fullName);
	            				java.util.List<Critter> critterClassList = Critter.getInstances(className);
	            				Class<?>[] types = {List.class};
	            				Method runStatsMethod = critterClass.getMethod("runStats", types);
	            				runStatsMethod.invoke(null, critterClassList);
	            			}
	            		}
	            		
	            		//System.out.println();
	            	}
	            	catch(Exception e){
	            		e.printStackTrace();
	            	}
	            }
	        });
			
//********************************************************** Step 1 Button *************************************************************
			
			Button step1 = new Button("TimeStep +1");
			step1.setOnAction(new EventHandler<ActionEvent>() {
				 
	            @Override
	            public void handle(ActionEvent event) {
	            	Critter.worldTimeStep();
	            	Critter.displayWorld();
	            	try{
	            		for(int i = 0; i < statsMenu.getItems().size(); i++){
	            			CustomMenuItem m = (CustomMenuItem)statsMenu.getItems().get(i);
	            			CheckBox c = (CheckBox)m.getContent();
	            			if(c.isSelected()){
	            				String className = statsList.get(i);
	            				String fullName = myPackage + "." + className;
	            				Class critterClass = Class.forName(fullName);
	            				java.util.List<Critter> critterClassList = Critter.getInstances(className);
	            				Class<?>[] types = {List.class};
	            				Method runStatsMethod = critterClass.getMethod("runStats", types);
	            				runStatsMethod.invoke(null, critterClassList);
	            			}
	            		}
	            	}
	            	catch(Exception e){
	            		e.printStackTrace();
	            	}
	            }
	        });

//************************************************************* Step 100 Button ************************************************************			

			Button step100 = new Button("TimeStep +100");
			step100.setOnAction(new EventHandler<ActionEvent>() {
				 
	            @Override
	            public void handle(ActionEvent event) {
	            	for(int i = 0; i < 100; i++){
	            		Critter.worldTimeStep();
	            	}
	            	Critter.displayWorld();
	            	try{
	            		for(int i = 0; i < statsMenu.getItems().size(); i++){
	            			CustomMenuItem m = (CustomMenuItem)statsMenu.getItems().get(i);
	            			CheckBox c = (CheckBox)m.getContent();
	            			if(c.isSelected()){
	            				String className = statsList.get(i);
	            				String fullName = myPackage + "." + className;
	            				Class critterClass = Class.forName(fullName);
	            				java.util.List<Critter> critterClassList = Critter.getInstances(className);
	            				Class<?>[] types = {List.class};
	            				Method runStatsMethod = critterClass.getMethod("runStats", types);
	            				runStatsMethod.invoke(null, critterClassList);
	            			}
	            		}
	            	}
	            	catch(Exception e){
	            		e.printStackTrace();
	            	}
	            }
	        });

//****************************************** Step 1000 *********************************************************

			Button step1000 = new Button("TimeStep +1000");
			step1000.setOnAction(new EventHandler<ActionEvent>() {
				 
	            @Override
	            public void handle(ActionEvent event) {
	            	for(int i = 0; i < 1000; i++){
	            		Critter.worldTimeStep();
	            	}
	            	Critter.displayWorld();
	            	try{
	            		for(int i = 0; i < statsMenu.getItems().size(); i++){
	            			CustomMenuItem m = (CustomMenuItem)statsMenu.getItems().get(i);
	            			CheckBox c = (CheckBox)m.getContent();
	            			if(c.isSelected()){
	            				String className = statsList.get(i);
	            				String fullName = myPackage + "." + className;
	            				Class critterClass = Class.forName(fullName);
	            				java.util.List<Critter> critterClassList = Critter.getInstances(className);
	            				Class<?>[] types = {List.class};
	            				Method runStatsMethod = critterClass.getMethod("runStats", types);
	            				runStatsMethod.invoke(null, critterClassList);
	            			}
	            		}
	            	}
	            	catch(Exception e){
	            		e.printStackTrace();
	            	}
	            }
	        });

//**************************************************** Slider to set Animation speed *********************************************
			
			Slider speedSlider = new Slider(1, 100, 5);
			speedSlider.setShowTickMarks(true);
			speedSlider.setShowTickLabels(true);
			speedSlider.setMinorTickCount(1);
			speedSlider.setBlockIncrement(1);
			speedSlider.valueProperty().addListener((obs, oldval, newVal) ->
			speedSlider.setValue(Math.round(newVal.doubleValue())));
			
			Label speedLabel = new Label("Animation Speed:" + Double.toString(speedSlider.getValue()));
			
			speedSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
			    speedLabel.setText("Animation Speed: " + speedSlider.getValue());
			    stepPerFrame = (int)speedSlider.getValue();
			});
			
//****************************************************** Animate Button *************************************************************
			
			Button buttonAnimate = new Button("Run Animation");
			buttonAnimate.setOnAction(new EventHandler<ActionEvent>() {
				 
	            @Override
	            public void handle(ActionEvent event) {
	            	try{
	            		buttonCritter.setDisable(true);
	            		buttonStats.setDisable(true);
	            		critterSlider.setDisable(true);
	            		critterDrop.setDisable(true);
	            		step1.setDisable(true);
	            		step100.setDisable(true);
	            		step1000.setDisable(true);
	            		statsMenu.setDisable(true);
	            		animationSentinel = true;
	            		stepPerFrame = (int)speedSlider.getValue();
	            		animationTimer.schedule(new AnimationTask(), 0);
	            	}
	            	catch(Exception e){
	            		e.printStackTrace();
	            	}
	            }
	        });
			
//********************************************************* Animate Stop Button ********************************************************
			
			Button buttonStop = new Button("Stop Animation");
			buttonStop.setOnAction(new EventHandler<ActionEvent>() {
				 
	            @Override
	            public void handle(ActionEvent event) {
	            	try{
	            		buttonCritter.setDisable(false);
	            		buttonStats.setDisable(false);
	            		critterSlider.setDisable(false);
	            		critterDrop.setDisable(false);
	            		step1.setDisable(false);
	            		step100.setDisable(false);
	            		step1000.setDisable(false);
	            		statsMenu.setDisable(false);
	            		animationSentinel = false;
	            		animationTimer.cancel();
	            		animationTimer = new Timer();
	            	}
	            	catch(Exception e){
	            		e.printStackTrace();
	            	}
	            }
	        });
			

//********************************************** Quit Button *******************************************************
			
			Button buttonQuit = new Button("Quit");
			buttonQuit.setOnAction(new EventHandler<ActionEvent>() {
				 
	            @Override
	            public void handle(ActionEvent event) {
	            	System.exit(0);            
	            }
	        });
			
//********************************************* Configure Element Placement *******************************************
			//Labels
			makeCritter.setLayoutX(10);
			makeCritter.setLayoutY(0);
			makeCritter.setFont(Font.font("Verdana", 20));
			makeCritter.setTextFill(Color.INDIGO);
			timestep.setLayoutX(250);
			timestep.setLayoutY(0);
			timestep.setFont(Font.font("Verdana", 20));
			timestep.setTextFill(Color.INDIGO);
			stats.setLayoutX(10);
			stats.setLayoutY(225);
			stats.setFont(Font.font("Verdana", 20));
			stats.setTextFill(Color.INDIGO);
			animate.setLayoutX(10);
			animate.setLayoutY(500);
			animate.setFont(Font.font("Verdana", 20));
			animate.setTextFill(Color.INDIGO);
			quit.setLayoutX(250);
			quit.setLayoutY(500);
			quit.setFont(Font.font("Verdana", 20));
			quit.setTextFill(Color.INDIGO);
			//step
			step1.setLayoutX(250);
			step1.setLayoutY(50);
			step100.setLayoutX(250);
			step100.setLayoutY(100);
			step1000.setLayoutX(250);
			step1000.setLayoutY(150);
			//make critter
			critterDrop.setLayoutX(10);
			critterDrop.setLayoutY(50);
			critterLabel.setLayoutX(10);
			critterLabel.setLayoutY(100);
			critterSlider.setLayoutX(10);
			critterSlider.setLayoutY(125);
			buttonCritter.setLayoutX(10);
			buttonCritter.setLayoutY(175);
			//stats
			statsMenu.setLayoutX(10);
			statsMenu.setLayoutY(262);
			buttonStats.setLayoutX(50);
			buttonStats.setLayoutY(262);
			consoleText.setLayoutX(10);
			consoleText.setLayoutY(300);
			//animation
			speedLabel.setLayoutX(10);
			speedLabel.setLayoutY(550);
			speedSlider.setLayoutX(10);
			speedSlider.setLayoutY(600);
			buttonAnimate.setLayoutX(10);
			buttonAnimate.setLayoutY(650);
			buttonStop.setLayoutX(125);
			buttonStop.setLayoutY(650);
			buttonQuit.setLayoutX(250);
			buttonQuit.setLayoutY(550);
			
			// add elements to controller
			pane.getChildren().addAll(makeCritter, timestep, stats, animate);
			pane.getChildren().addAll(buttonCritter, critterDrop, critterSlider, critterLabel);
			pane.getChildren().addAll(buttonStats, statsMenu, consoleText);
			pane.getChildren().addAll(buttonAnimate,buttonStop);
			pane.getChildren().addAll(speedSlider, speedLabel);
			pane.getChildren().addAll(step1, step100, step1000);
			pane.getChildren().addAll(buttonQuit, quit);
			
			// create controller stage
			Scene primaryScene = new Scene(pane, 500, 700);
			primaryStage.setScene(primaryScene);
			primaryStage.show();
			
			// create graph stage
			Stage secondStage = new Stage();
			secondStage.setTitle("Critter World");
			Scene secondScene = new Scene(grid, (Params.world_width+1)*Critter.gridCellSize, (Params.world_height+2)*Critter.gridCellSize);
			secondStage.setScene(secondScene);
			secondStage.show();
			
			
			Critter.displayWorld();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

//************************************************ Get Critter Classes ************************************************************
	
	
//*************************************************** Output Console to Controller TextArea ************************************************
	
	static class PrivateConsole extends OutputStream {
        public TextArea streamOutput;

        public PrivateConsole(TextArea ta) {
            this.streamOutput = ta;
        }

		@Override
		public void write(int arg0) throws IOException {
			streamOutput.appendText(String.valueOf((char) arg0));
		}
    }
	
//************************************************** Animation Run *************************************************************************
	
	public class AnimationTask extends TimerTask{

		@Override
		public void run() {
			Platform.runLater(() -> {
				for(int t = 0; t < stepPerFrame; t++){
					Critter.worldTimeStep();
				}
				Critter.displayWorld();
			
				for(int i = 0; i < statsMenu.getItems().size(); i++){ // run stats
					CustomMenuItem m = (CustomMenuItem)statsMenu.getItems().get(i);
					CheckBox c = (CheckBox)m.getContent();
					try{
						if(c.isSelected()){
							String className = statsList.get(i);
							String fullName = myPackage + "." + className;
							Class critterClass = Class.forName(fullName);
							java.util.List<Critter> critterClassList = Critter.getInstances(className);
							Class<?>[] types = {List.class};
							Method runStatsMethod = critterClass.getMethod("runStats", types);
							runStatsMethod.invoke(null, critterClassList);
						}
					}
					catch(Exception e){
						e.printStackTrace();
					}
				}
				
				//System.out.println();
			
				animationTimer.schedule(new AnimationTask(), 1000);
			});
		}
	}
}
