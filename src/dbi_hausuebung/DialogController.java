package dbi_hausuebung;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class DialogController implements Initializable{

	@FXML
	private TextField dbNameField, userNameField, pwField;
	
	@FXML
	private Button cancelButton, okButton;
	
	@FXML
	private void getUserData() {
		
		System.out.println(dbNameField.getText());
		
		
	}
	
	@FXML
	private void closeDialog() {
		
		Stage stage = (Stage) cancelButton.getScene().getWindow();
		stage.close();
		
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		
	}

}
