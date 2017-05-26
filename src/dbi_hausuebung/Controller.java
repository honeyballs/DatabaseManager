package dbi_hausuebung;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import javafx.util.Pair;

public class Controller implements Initializable {

	public static final String dbPath = "jdbc:mysql://localhost:3306/";

	private DBManager manager = null;
	private String[] userCredentials;

	@FXML
	private Label dbLabel;

	@FXML
	private TextArea queryTextField;

	@FXML
	private TableView resultsTable;

	@FXML
	private Button disconnectButton;

	private ObservableList<ObservableList> rows;

	@FXML
	private void handleDbSelect(ActionEvent event) {

		Dialog<String[]> dialog = new Dialog<>();
		dialog.setTitle("Datenbank wählen");
		dialog.setHeaderText("Wählen Sie die gewünschte Datenbank.");

		final ButtonType okButton = new ButtonType("Ok", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes()
				.addAll(okButton, ButtonType.CANCEL);

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		final TextField dbName = new TextField();
		dbName.setPromptText("Datenbankname");
		final TextField userName = new TextField();
		userName.setPromptText("Nutzername");
		final PasswordField pwField = new PasswordField();
		pwField.setPromptText("Passwort");

		grid.add(new Label("Datenbankname"), 0, 0);
		grid.add(dbName, 1, 0);
		grid.add(new Label("Nutzername"), 0, 1);
		grid.add(userName, 1, 1);
		grid.add(new Label("Passwort"), 0, 2);
		grid.add(pwField, 1, 2);

		dialog.getDialogPane().setContent(grid);

		dialog.setResultConverter(new Callback<ButtonType, String[]>() {

			@Override
			public String[] call(ButtonType b) {

				if (b == okButton) {

					String[] credentials = new String[3];
					credentials[0] = dbName.getText();
					credentials[1] = userName.getText();
					credentials[2] = pwField.getText();

					return credentials;

				}

				return null;
			}
		});

		Optional<String[]> result = dialog.showAndWait();

		if (result.isPresent()) {

			userCredentials = result.get();
			dbLabel.setText(dbPath + userCredentials[0]);
			manager = new DBManager(userCredentials);

			if (manager.startConnection()) {
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Verbindung erfolgreich");
				alert.setHeaderText("Glückwunsch!");
				alert.setContentText("Sie sind mit der angeforderten Datenbank verbunden.");
				alert.showAndWait();
				disconnectButton.setVisible(true);
			} else {
				Alert alert = new Alert(AlertType.WARNING);
				alert.setTitle("Fehler");
				alert.setHeaderText("Ein Fehler ist aufgetreten.");
				alert.setContentText("Bei der Verbindung zur Datenbank ist ein Fehler aufgetreten.");
				alert.showAndWait();
			}

		}

	}

	@FXML
	private void executeQuery(ActionEvent event) {

		String query = queryTextField.getText();
		Result result = manager.executeQuery(query);

		if (result != null) {

			rows = FXCollections.observableArrayList();

			// clear the table before adding new items
			resultsTable.getItems().clear();
			resultsTable.getColumns().clear();

			for (String name : result.getColumnNames()) {

				TableColumn col = new TableColumn(name);
				final int index = result.getColumnNames().indexOf(name);

				col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
					public ObservableValue<String> call(
							TableColumn.CellDataFeatures<ObservableList, String> param) {
						return new SimpleStringProperty(param.getValue()
								.get(index).toString());
					}
				});

				resultsTable.getColumns().add(col);

			}

			for (List<String> row : result.getData()) {
				ObservableList<String> observableRow = FXCollections
						.observableArrayList();
				observableRow.addAll(row);
				rows.add(observableRow);
			}

			resultsTable.setItems(rows);

		}

	}

	@FXML
	private void saveSQL(ActionEvent event) {

		FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter(
				"SQL files (*.sql)", "*.sql");
		fileChooser.getExtensionFilters().add(filter);

		File file = fileChooser.showSaveDialog(queryTextField.getScene()
				.getWindow());

		if (file != null) {

			if (queryTextField.getText().length() > 0) {

				try {

					FileWriter fileWriter = new FileWriter(file);
					fileWriter.write(queryTextField.getText());
					fileWriter.close();

				} catch (IOException e) {
					e.printStackTrace();
				}

			} else {

				Alert alert = new Alert(AlertType.WARNING);
				alert.setTitle("Kein Text");
				alert.setHeaderText("Keine Query in Textfeld");
				alert.setContentText("Geben Sie eine Query ein, bevor Sie versuchen zu speichern.");
				alert.showAndWait();

			}

		}

	}

	@FXML
	private void loadSQL(ActionEvent event) {
		
		FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter(
				"SQL files (*.sql)", "*.sql");
		fileChooser.getExtensionFilters().add(filter);
		
		File f = fileChooser.showOpenDialog(queryTextField.getScene().getWindow());
		
		if (f != null && f.canRead()) {
			
			try {
				
				byte[] encoded = Files.readAllBytes(f.toPath());
				String query = new String(encoded, StandardCharsets.UTF_8);
				queryTextField.setText(query);
				
			} catch (IOException e) {

				e.printStackTrace();
			}
			
					
		}
	}

	@FXML
	private void disconnect() {

		if (manager != null) {
			if (manager.isConnected()) {

				manager.disconnect();
				disconnectButton.setVisible(false);
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Disconnect");
				alert.setHeaderText("Verbindung zur Datenbank getrennt.");
				alert.setContentText("Sie haben die Verbindung zur Datenbank getrennt.");
				alert.showAndWait();
				
				resultsTable.getItems().clear();
				resultsTable.getColumns().clear();
				queryTextField.clear();

			}
		}

	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		String[] creds = { "azamon", "root", "" };
		manager = new DBManager(creds);

		if (manager.startConnection()) {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Verbindung erfolgreich");
			alert.setHeaderText("Glückwunsch!");
			alert.setContentText("Sie sind mit der angeforderten Datenbank verbunden.");
			alert.showAndWait();
			disconnectButton.setVisible(true);

		} else {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Fehler");
			alert.setHeaderText("Ein Fehler ist aufgetreten.");
			alert.setContentText("Bei der Verbindung zur Datenbank ist ein Fehler aufgetreten.");
			alert.showAndWait();
		}

	}

}
