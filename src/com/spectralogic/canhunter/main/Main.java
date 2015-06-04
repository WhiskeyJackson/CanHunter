package com.spectralogic.canhunter.main;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.regex.Pattern;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import com.spectralogic.canhunter.canLogSearch.CanLine;
import com.spectralogic.canhunter.fileVisitor.ZipFileVisitor;


public class Main extends Application{

	private File file = new File("/home/nicholasf/perforce workspace/CanHunter/CanLogs/20150325_CANlogs_Fishbowl/");
	private Pattern filter;
	private final TextField canSourceField = new TextField("");
	private final TextField canDestinationField = new TextField("30e");
	private final TextField canDataField = new TextField("11 00 41");
	private final TextField canXidField = new TextField("");
	private final CheckBox sortFilesCheckBox = new CheckBox("Sort Files by DSP");
	private final CheckBox gatherMetricsCheckBox = new CheckBox("Gather Metrics");
	private Label statusLabel;
	private Label doneLabel;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		GridPane mainGrid = new GridPane();

		initGuiGrid(mainGrid);

		addLogLocationChooser(mainGrid, primaryStage);

		addFilterFields(mainGrid);

		addParseButton(mainGrid);

		addStatusLabel(mainGrid);

		show(primaryStage, mainGrid);

	}

	private void initGuiGrid(GridPane grid) {
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));

		Text title = new Text("CAN Hunter");
		title.setFont(Font.font("Tahoma", 20));
		grid.add(title, 0, 0);

		Text words = new Text("This utility will hunt your\r\n CAN logs and Destroy Them!");
		grid.add(words, 0, 1);
	}

	private void addLogLocationChooser(GridPane grid, Stage primaryStage) {
		Label fileLocation = new Label("Choose Log Location");
		GridPane chooserGrid = new GridPane();
		chooserGrid.setHgap(10);

		Button fileChooserButton = new Button("Log File");
		FileChooser fileChooser = new FileChooser();
		fileChooser.setInitialDirectory(file);
		fileChooserButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				file = fileChooser.showOpenDialog(primaryStage);
				fileLocation.setText(file.getAbsolutePath());
				statusLabel.setText("");
				doneLabel.setText("");
			}
		});

		Button directoryChooserButton = new Button("Log Directory");
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setInitialDirectory(file);
		directoryChooserButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				file = directoryChooser.showDialog(primaryStage);
				fileLocation.setText(file.getAbsolutePath());
				statusLabel.setText("");
				doneLabel.setText("");
			}
		});

		gatherMetricsCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if(newValue.booleanValue() == true){
					canSourceField.setText("");
					canSourceField.setEditable(false);
					canDestinationField.setText("30e");
					canDestinationField.setEditable(false);
					canDataField.setText("11 00 41");
					canDataField.setEditable(false);
					canXidField.setText("");
					canXidField.setEditable(false);
				} else {
					canSourceField.setEditable(true);
					canDestinationField.setEditable(true);
					canDataField.setEditable(true);
					canXidField.setEditable(true);
				}
			}
		});

		grid.add(fileLocation, 0, 2);
		chooserGrid.add(fileChooserButton, 0, 0);
		chooserGrid.add(directoryChooserButton, 1, 0);
		chooserGrid.add(sortFilesCheckBox, 2, 0);
		chooserGrid.add(gatherMetricsCheckBox, 3, 0);
		grid.add(chooserGrid, 0, 3);
	}

	private void addFilterFields(GridPane grid) {
		GridPane fieldGrid = new GridPane();
		fieldGrid.setHgap(10);

		Label canSource = new Label("Source");
		fieldGrid.add(canSource, 0, 0);
		fieldGrid.add(canSourceField, 0, 1);

		Label canDestination = new Label("Destination");
		fieldGrid.add(canDestination, 1, 0);
		fieldGrid.add(canDestinationField, 1, 1);

		Label canXid = new Label("Xid");
		fieldGrid.add(canXid, 2, 0);
		fieldGrid.add(canXidField, 2, 1);

		Label canData = new Label("Data");
		fieldGrid.add(canData, 3, 0);
		fieldGrid.add(canDataField, 3, 1);

		grid.add(fieldGrid, 0, 4);

	}

	private void addParseButton(GridPane grid) {
		Button parseButton = new Button();
		parseButton.setText("Parse Logs");

		parseButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				Thread hunterThread = new Thread(() -> {
					Platform.runLater(() -> doneLabel.setText(""));

					String canSource = canSourceField.getText().isEmpty() ? ".*" : canSourceField.getText();
					String canDest = canDestinationField.getText().isEmpty() ? ".*" : canDestinationField.getText();
					String canData = canDataField.getText().isEmpty() ? ".*" : canDataField.getText();
					String xid = canXidField.getText().isEmpty() ? "[0-9A-Fa-f]" : canXidField.getText();

					filter = CanLine.regex(canSource, canDest, canData, xid);

					File resultsDir = new File("./CanHuntResults/");

					try {

						if(!resultsDir.exists() && !resultsDir.mkdir()){
							throw new IOException("failed to make results dir");
						};

						ZipFileVisitor visitor = new ZipFileVisitor(filter, resultsDir, statusLabel);
						visitor.wantFilesSorted = sortFilesCheckBox.isSelected();
						visitor.calculateMetrics = gatherMetricsCheckBox.isSelected();
						Files.walkFileTree(file.toPath(), visitor);


					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					Platform.runLater(() -> doneLabel.setText("Done!"));

				});
				hunterThread.start();

			}


		});

		grid.add(parseButton, 0, 6);
	}



	private void addStatusLabel(GridPane grid) {
		statusLabel = new Label("");
		grid.add(statusLabel, 0, 7);
		doneLabel = new Label("");
		grid.add(doneLabel,0,8);
	}

	private void show(Stage primaryStage, GridPane grid) {
		primaryStage.setTitle("Can Hunter!");
		primaryStage.setScene(new Scene(grid, 700, 400));
		primaryStage.show();
	}

}
