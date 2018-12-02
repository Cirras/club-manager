package ui;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import data.model.ClubMember;

public class ClubMemberCellController {
    private ClubMember member;

    @FXML
    private Label nameLabel ;

    @FXML
    private Label roleLabel ;

    public void setClubMember(ClubMember member) {
        this.member = member;
        nameLabel.textProperty().unbind();
        roleLabel.textProperty().unbind();

        if (member == null) {
            nameLabel.setText(null);
            roleLabel.setText(null);
        } else {
            nameLabel.textProperty().bind(Bindings.concat(member.firstNameProperty(), " ", member.lastNameProperty()));
            roleLabel.textProperty().bind(member.roleProperty());
        }
    }
}
