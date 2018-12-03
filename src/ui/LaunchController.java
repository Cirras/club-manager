package ui;

import data.Context;
import data.model.Club;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;

/**
 * Handles the logic behind the launch view - Handles creating a new club or opening an existing one
 */
public class LaunchController {
    @FXML
    public Button newClubButton;

    @FXML
    public Button openButton;

    @FXML
    void handleNewClub() {
        Context.getContext().setFile(null);
        Context.getContext().setCurrentClub(new Club());

        moveToClubDataView();
    }

    @FXML
    void handleOpen() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Club", "*.club"));

        Stage stage = (Stage)openButton.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            try {
                FileInputStream fis = new FileInputStream(file);
                ObjectInputStream reader = new ObjectInputStream(fis);
                Club club = (Club) reader.readObject();
                Context.getContext().setFile(file);

                Context.getContext().setCurrentClub(club);
                moveToClubDataView();
            }
            catch (IOException | ClassNotFoundException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to load club!", ButtonType.OK);
                alert.showAndWait();
            }
        }
    }

    private void moveToClubDataView() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("clubData.fxml"));
            Stage stage = new Stage();
            stage.setTitle("IntegraDev Club Manager");
            stage.setScene(new Scene(root, 775, 600));
            stage.setResizable(false);
            stage.show();

            openButton.getScene().getWindow().hide();
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
