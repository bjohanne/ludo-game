/**
 * @author Johanne
 */
package main;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
	@Override
	public void start(Stage stage) throws IOException {
		Parent root = new FXMLLoader(getClass().getResource("../gui/Ludo.fxml")).load();
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.setTitle("Ludo");
		stage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}