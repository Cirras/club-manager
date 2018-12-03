package ui;

import data.Context;
import data.model.ClubMember;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import data.model.Club;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;

/**
 * Handles any logic between the Club model and the ClubData view
 * @see Club
 */
public class ClubDataController {

    private Club club;

    @FXML
    public Button saveChangesButton;

    @FXML
    public Button closeButton;

    @FXML
    public TextField clubNameField;

    @FXML
    public TextField firstNameField;

    @FXML
    public TextField roleField;

    @FXML
    public TextField emailField;

    @FXML
    public TextField lastNameField;

    @FXML
    public TextField phoneNumberField;

    @FXML
    public Button saveMemberButton;

    @FXML
    public Button addClubMemberButton;

    @FXML
    public Button removeMemberButton;

    @FXML
    public ListView<ClubMember> clubMemberList;

    /**
     * Initializes the controller after FMXL fields are injected by JavaFX
     */
    public void initialize() {
        club = Context.getContext().getCurrentClub();

        clubNameField.setText(club.getName());
        clubNameField.textProperty().addListener((observable, oldValue, newValue) -> {
            club.setName(clubNameField.getText());
            club.setDirty(true);
        });

        clubMemberList.setCellFactory(c -> new ClubMemberCell());
        clubMemberList.setItems(club.getMembers());
        clubMemberList.getSelectionModel()
                      .selectedItemProperty()
                      .addListener((observable, oldValue, newValue) -> updateClubMemberFields());

        if (club.getMembers().isEmpty()) {
            disableClubMemberControls();
        }
        else {
            selectClubMember(0);
        }
    }

    /**
     * Resets the data in the Club Member detail pane so the user can enter in information for a new member
     * The name is slightly misleading, since it won't actually add the club member until the new info is saved.
     * @see ClubDataController#handleSaveMember
     */
    @FXML
    void handleAddClubMember() {
        clubMemberList.getSelectionModel().clearSelection();
        enableClubMemberControls();
        updateClubMemberFields();
    }

    /**
     * Adds a club member to the current club
     */
    @FXML
    void handleSaveMember() {
        if (clubMemberFieldsAreEmpty())
        {
            showErrorAlert("All fields must be populated.");
            return;
        }

        ClubMember selectedClubMember = selectedClubMember();

        if (selectedClubMember == null)
        {
            selectedClubMember = new ClubMember();
            populateClubMemberFromFields(selectedClubMember);

            club.getMembers().add(selectedClubMember);
            selectClubMember(club.getMembers().size() - 1);
        }
        else
        {
            populateClubMemberFromFields(selectedClubMember);
        }

        club.setDirty(true);
    }

    /**
     * Removes a club member from the current club
     */
    @FXML
    void handleRemoveClubMember() {
        int index = clubMemberList.getSelectionModel().getSelectedIndex();

        if (index == -1) {
            return;  //No club member currently selected in the list.
        }

        club.getMembers().remove(index);

        if (club.getMembers().isEmpty()) {
            disableClubMemberControls();
            clubMemberList.getSelectionModel().clearSelection();
        } else {
            clubMemberList.getSelectionModel().select(Math.max(0, index - 1));
        }

        updateClubMemberFields();
        club.setDirty(true);
    }

    /**
     * Resets the Club Data view for a new club, and removes the old club data from memory
     */
    @FXML
    private void handleNew() {
        if (!dirtyPromptCheck())
            return;

        Context.getContext().setFile(null);
        Context.getContext().setCurrentClub(new Club());
        this.initialize();
    }

    /**
     * Saves the current club to the disk using the file known in the application context
     * @see Context#getFile
     */
    @FXML
    private void handleSave() {
        if (!Context.getContext().getFileExists()) {
            handleSaveAs();
            return;
        }

        try {
            File file = Context.getContext().getFile();
            FileOutputStream fout = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fout);

            oos.writeObject(club);
            club.setDirty(false);
        }
        catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Failed to save club.");
        }
    }

    /**
     * Prompts the user for a filename and location, then saves the current club to the disk.
     */
    @FXML
    private void handleSaveAs() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Club", "*.club"));

        String name = club.getName();

        if (name != null && !name.isEmpty()) {
            fileChooser.setInitialFileName(club.getName() + ".club");
        }

        Stage stage = (Stage)saveChangesButton.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            Context.getContext().setFile(file);
            handleSave();
        }
    }

    /**
     * Handles opening/un-serializing a new club from a .club file
     */
    @FXML
    void handleOpen() {
        if (!dirtyPromptCheck())
            return;

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Club", "*.club"));

        Stage stage = (Stage)saveChangesButton.getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            try {
                FileInputStream fis = new FileInputStream(file);
                ObjectInputStream reader = new ObjectInputStream(fis);
                Club club = (Club) reader.readObject();
                Context.getContext().setFile(file);

                Context.getContext().setCurrentClub(club);
                this.initialize();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to load club!", ButtonType.OK);
                alert.showAndWait();
            }
        }
    }

    /**
     * Closes the current club and returns to the launch screen
     */
    @FXML
    private void handleClose() {
        if (!dirtyPromptCheck())
            return;

        try {
            Parent root = FXMLLoader.load(getClass().getResource("launch.fxml"));
            Stage stage = new Stage();
            stage.setTitle("IntegraDev Club Manager");
            stage.setScene(new Scene(root, 400, 350));
            stage.setResizable(false);
            stage.show();

            closeButton.getScene().getWindow().hide();
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Exits the application
     */
    @FXML
    private void handleExit() {
        if (!dirtyPromptCheck())
            return;

        System.exit(0);
    }

    /**
     * @return The Club Member which is currently selected in the Club Member list view
     */
    private ClubMember selectedClubMember() {
        return clubMemberList.getSelectionModel().getSelectedItem();
    }

    /**
     * Programmatically selects a club member in the Club Member list view
     * @param index Position in the Club Member list view to select
     */
    private void selectClubMember(int index)
    {
        clubMemberList.getSelectionModel().select(index);
    }

    /**
     * Populates a ClubMember object with current data from the text fields in the Club Member detail pane
     * @param member ClubMember object that will have its data populated from the fields
     */
    private void populateClubMemberFromFields(ClubMember member) {
        member.setFirstName(firstNameField.getText());
        member.setLastName(lastNameField.getText());
        member.setEmail(emailField.getText());
        member.setPhoneNumber(phoneNumberField.getText());
        member.setRole(roleField.getText());
    }

    /**
     * @return boolean indicating whether any of the text fields in the Club Member detail pane are empty
     */
    private boolean clubMemberFieldsAreEmpty() {
        return (firstNameField.getText().isEmpty() ||
                lastNameField.getText().isEmpty() ||
                emailField.getText().isEmpty() ||
                phoneNumberField.getText().isEmpty() ||
                roleField.getText().isEmpty());
    }

    /**
     * Updates all of the text fields in the Club Member detail pane for the currently selected member in the list
     */
    private void updateClubMemberFields() {
        ClubMember selectedClubMember = selectedClubMember();

        if (selectedClubMember == null) {
            firstNameField.setText("");
            lastNameField.setText("");
            emailField.setText("");
            phoneNumberField.setText("");
            roleField.setText("New Member");
        }
        else {
            firstNameField.setText(selectedClubMember.getFirstName());
            lastNameField.setText(selectedClubMember.getLastName());
            emailField.setText(selectedClubMember.getEmail());
            phoneNumberField.setText(selectedClubMember.getPhoneNumber());
            roleField.setText(selectedClubMember.getRole());

            enableClubMemberControls();
        }
    }

    /**
     * Disables the text fields and buttons in the Club Member detail pane
     */
    private void disableClubMemberControls()
    {
        firstNameField.setDisable(true);
        lastNameField.setDisable(true);
        emailField.setDisable(true);
        phoneNumberField.setDisable(true);
        roleField.setDisable(true);
        saveMemberButton.setDisable(true);
        removeMemberButton.setDisable(true);
    }

    /**
     * Enables the text fields and buttons in the Club Member detail pane
     */
    private void enableClubMemberControls()
    {
        firstNameField.setDisable(false);
        lastNameField.setDisable(false);
        emailField.setDisable(false);
        phoneNumberField.setDisable(false);
        roleField.setDisable(false);
        saveMemberButton.setDisable(false);
        removeMemberButton.setDisable(false);
    }

    /**
     * Simply displays an error message to the user
     * @param message The error message to display
     */
    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.showAndWait();
    }

    /**
     * Checks whether the club has been modified before doing a certain action
     * @return boolean indicating whether to complete the action
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean dirtyPromptCheck() {
        if (club.isDirty())
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "You have unsaved changes - Save now?",
                                    ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
            alert.showAndWait();

            ButtonType result = alert.getResult();

            if (result == ButtonType.YES) {
                handleSave();
                return true;
            }
            else {
                return (result == ButtonType.NO);
            }
        }

        return true;
    }
}
