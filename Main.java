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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.Timer;
import java.util.TimerTask;

public class Main extends Application{

	static GridPane grid = new GridPane();
	private static String myPackage;
	
	static TextArea consoleText = new TextArea();
	
	static boolean animationSentinel = false;
	
	// Gets the package name.  The usage assumes that Critter and its subclasses are all in the same package.
    static {
        myPackage = Critter.class.getPackage().toString().split(" ")[1];
    }
    
	public static void main(String[] args) {
		launch(args);
	}

	public MenuButton statsMenu = new MenuButton();
	public ArrayList<String> statsList = new ArrayList<String>();
	private Timer animationTimer = new Timer();
	private AnimationTask animation = new AnimationTask();
	public int stepPerFrame = 1;
	static int steps = 0;
	
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
		
			VBox pane = new VBox(); // pane is a controller
			
			ComboBox<String> critterDrop = new ComboBox<String>();
			ComboBox<String> statsDrop = new ComboBox<String>();
			ArrayList<String> classNames = getCritterClasses(myPackage);
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
			
			Slider critterSlider = new Slider(1, 100, 5);
			critterSlider.setShowTickMarks(true);
			critterSlider.setShowTickLabels(true);
			critterSlider.setMinorTickCount(1);
			critterSlider.setBlockIncrement(1);
			critterSlider.valueProperty().addListener((obs, oldval, newVal) ->
		    critterSlider.setValue(Math.round(newVal.doubleValue())));
			
			Label critterLabel = new Label(Double.toString(critterSlider.getValue()));
			
			critterSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
			    critterLabel.setText("Number of Critters to add: " + critterSlider.getValue());
			});
			
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
			
			Slider speedSlider = new Slider(1, 100, 5);
			speedSlider.setShowTickMarks(true);
			speedSlider.setShowTickLabels(true);
			speedSlider.setMinorTickCount(1);
			speedSlider.setBlockIncrement(1);
			speedSlider.valueProperty().addListener((obs, oldval, newVal) ->
			speedSlider.setValue(Math.round(newVal.doubleValue())));
			
			Label speedLabel = new Label(Double.toString(speedSlider.getValue()));
			
			speedSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
			    speedLabel.setText("Animation Speed: " + speedSlider.getValue());
			    stepPerFrame = (int)speedSlider.getValue();
			});
			
			Button buttonAnimate = new Button("Run Animation");
			buttonAnimate.setOnAction(new EventHandler<ActionEvent>() {
				 
	            @Override
	            public void handle(ActionEvent event) {
	            	try{
	            		buttonCritter.setDisable(true);
	            		buttonStats.setDisable(true);
	            		critterSlider.setDisable(true);
	            		critterDrop.setDisable(true);
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
			
			Button buttonStop = new Button("Stop Animation");
			buttonStop.setOnAction(new EventHandler<ActionEvent>() {
				 
	            @Override
	            public void handle(ActionEvent event) {
	            	try{
	            		buttonCritter.setDisable(false);
	            		buttonStats.setDisable(false);
	            		critterSlider.setDisable(false);
	            		critterDrop.setDisable(false);
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
			
			Button buttonStep100 = new Button("Step 100");
			buttonStep100.setOnAction(new EventHandler<ActionEvent>() {
				 
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
			
			Button buttonQuit = new Button("Quit");
			buttonQuit.setOnAction(new EventHandler<ActionEvent>() {
				 
	            @Override
	            public void handle(ActionEvent event) {
	            	System.exit(0);            
	            }
	        });
			
			pane.getChildren().add(buttonCritter);
			pane.getChildren().add(critterDrop);
			pane.getChildren().add(critterSlider);
			pane.getChildren().add(critterLabel);
			pane.getChildren().add(buttonStats);
			pane.getChildren().add(statsMenu);
			pane.getChildren().add(consoleText);
			pane.getChildren().add(buttonAnimate);
			pane.getChildren().add(buttonStop);
			pane.getChildren().add(speedSlider);
			pane.getChildren().add(speedLabel);
			pane.getChildren().add(buttonStep100);
			pane.getChildren().add(buttonQuit);
		
			Scene primaryScene = new Scene(pane, 500, 500);
			primaryStage.setScene(primaryScene);
			primaryStage.show();
		
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

	
	public ArrayList<String> getCritterClasses(String packageName)throws Exception{
		ArrayList<String> listCritterClass = new ArrayList<String>();
		
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		//assert classLoader != null;
		String path = packageName.replace('.', '/');
		Enumeration<URL> resrce;
		Enumeration<URL> resources = classLoader.getResources(path);
		ArrayList<File> directoryPath = new ArrayList<File>();
		
		URL resource = resources.nextElement();
		directoryPath.add(new File(resource.getFile()));
		String directory = directoryPath.get(0).toString();
		directory = directory.replaceAll("%20", " ");
		File folder = new File(directory);
		File[] fileList = folder.listFiles();
		
		for(int i = 0; i < fileList.length; i++){
			if (fileList[i].isFile()){
				String name = (String)fileList[i].getName();
				String[] nameEdit = name.split("\\.");
				name = nameEdit[0];
				listCritterClass.add(name);
			}
		}
		
		return listCritterClass;
	}
	
	
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
	
	public class AnimationTask extends TimerTask{

		@Override
		public void run() {
			Platform.runLater(() -> {
				for(int t = 0; t < stepPerFrame; t++){
					Critter.worldTimeStep();
				}
				Critter.displayWorld();
				//steps++;
				//System.out.println(steps);
			
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
			
				animationTimer.schedule(new AnimationTask(), 100);
			});
		}
	}
}
