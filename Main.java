 package assignment5;


import java.io.File;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Main extends Application{

	static GridPane grid = new GridPane();
	private Integer timeStepNumber = 0;
	private static String myPackage;
	static {
	    myPackage = Critter.class.getPackage().toString().split(" ")[1];
	}
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		try{
			if (Params.world_height > Params.world_width){
				Critter.gridCellSize = 850/Params.world_height;
			}
			primaryStage.setTitle("Controller");
		
			grid.setGridLinesVisible(true); // grid lines are now visible
		
			Pane pane = new Pane(); // Stackpane is a controller
			// time step buttons
			Button timestep1 = new Button("TimeStep: +1");
			Button timestep100 = new Button("TimeStep: +100");
			Button timestep1000 = new Button("TimeStep: +1000");
			// time step label
			Button timeStepIncrement = new Button("Increment TimeStep");
			Button timeStepDecrement = new Button("Decrement TimeStep");
			Button customTimeStep = new Button("Custom TimeStep");
			Label timeSteps = new Label(timeStepNumber.toString());
			Label makeCritter = new Label("Make Critter");
			Label TimeStep = new Label("Time Step");
			Label stats = new Label("Critter Stats");
			ComboBox critterDrop = new ComboBox();
			ComboBox statsDrop = new ComboBox();
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
			    critterLabel.setText("" + critterSlider.getValue());
			});
			
			Button button1 = new Button("Add Critter");
			button1.setOnAction(new EventHandler<ActionEvent>() {
				 
	            @Override
	            public void handle(ActionEvent event) {
	            	try{
	            		if (critterDrop.getValue() != null){
	            			int temp = (int)critterSlider.getValue();
	            			for (int i = 0; i < temp; i++){
	            				Critter.makeCritter(critterDrop.getValue().toString());
	            			}
	            			Critter.displayWorld();
	            		}
	            	}
	            	catch(Exception e){
	            		e.printStackTrace();
	            	}
	            }
	        });
			
		
			// timeStepIncrement action handler
			timeStepIncrement.setOnAction(new EventHandler<ActionEvent>(){
				@Override
				public void handle(ActionEvent event){
					timeStepNumber++;
					timeSteps.setText(timeStepNumber.toString());
				}
			});
			// timeStepDecrement action handler
			timeStepDecrement.setOnAction(new EventHandler<ActionEvent>(){
				@Override
				public void handle(ActionEvent event){
					if(timeStepNumber != 0){
						timeStepNumber--;
						timeSteps.setText(timeStepNumber.toString());
					}
				}
			});
			// customTimeStep action handler
			customTimeStep.setOnAction(new EventHandler<ActionEvent>(){
				@Override
				public void handle(ActionEvent event){
					for(int i = 0; i < timeStepNumber; i++){
						Critter.worldTimeStep();
					}
					Critter.displayWorld();
					// reset to 0?
				}
			});
			// timestep1 action handler
			timestep1.setOnAction(new EventHandler<ActionEvent>(){
				@Override
				public void handle(ActionEvent event){
					Critter.worldTimeStep();
					Critter.displayWorld();
				}
			});
			//timestep100 action handler
			timestep100.setOnAction(new EventHandler<ActionEvent>(){
				@Override
				public void handle(ActionEvent event){
					for(int i = 0; i < 100; i++){
						Critter.worldTimeStep();
					}
					Critter.displayWorld();
				}
			});
			//timestep1000 action handler
			timestep1000.setOnAction(new EventHandler<ActionEvent>(){
				@Override
				public void handle(ActionEvent event){
					for(int i = 0; i < 1000; i++){
						Critter.worldTimeStep();
					}
					Critter.displayWorld();
				}
			});
			button1.setOnAction(new EventHandler<ActionEvent>() {

	            @Override
	            public void handle(ActionEvent event) {
	            	try{
	            		for (int i = 0; i <= 2; i++){
	            			Critter.makeCritter("Craig");
	            		}
	            		Critter.displayWorld();
	            	}
	            	catch(Exception e){
	            		e.printStackTrace();
	            	}
	            }
	        });
			
			
	        stats.setLayoutX(300);
	        stats.setLayoutY(0);
	        TimeStep.setLayoutX(150);
	        TimeStep.setLayoutY(0);
	        makeCritter.setLayoutX(0);
	        makeCritter.setLayoutY(0);
	        timestep1.setLayoutX(150);
	        timestep1.setLayoutY(50);
	        timestep100.setLayoutX(150);
	        timestep100.setLayoutY(100);
	        timestep1000.setLayoutX(150);
	        timestep1000.setLayoutY(150);
	        button1.setLayoutX(0);
	        button1.setLayoutY(50);
	        critterSlider.setLayoutX(0);
	        critterSlider.setLayoutY(150);
	        critterDrop.setLayoutX(0);
	        critterDrop.setLayoutY(200);
	        critterLabel.setLayoutX(0);
	        critterLabel.setLayoutY(100);
	        statsDrop.setLayoutX(300);
	        statsDrop.setLayoutY(50);
			pane.getChildren().addAll(stats, TimeStep, makeCritter);
			pane.getChildren().addAll(timestep1, timestep100, timestep1000);
			pane.getChildren().add(button1);
			pane.getChildren().add(critterDrop);
			pane.getChildren().add(critterSlider);
			pane.getChildren().add(critterLabel);
			pane.getChildren().add(statsDrop);
		
			Scene primaryScene = new Scene(pane, 500, 500);
			primaryStage.setScene(primaryScene);
			primaryStage.show();
		
			Stage secondStage = new Stage();
			secondStage.setTitle("Critter World");
		
			Scene secondScene = new Scene(grid, Params.world_width*Critter.gridCellSize, Params.world_height*Critter.gridCellSize);
			secondStage.setScene(secondScene);
			secondStage.show();
			Critter.displayWorld();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	
	public ArrayList<String> getCritterClasses(String packageName) throws Exception {
		ArrayList<String> listCritterClass = new ArrayList<String>();
		
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		assert classLoader != null;
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
}
