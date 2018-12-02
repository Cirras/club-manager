package ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.scene.layout.Pane;
import data.model.ClubMember;

import java.io.IOException;
import java.io.UncheckedIOException;

public class ClubMemberCell extends ListCell<ClubMember> {
    private final ClubMemberCellController clubMemberCellController ;

    public ClubMemberCell() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("clubMemberCell.fxml"));
            Pane clubMemberCellView = loader.load();
            clubMemberCellController = loader.getController();
            setGraphic(clubMemberCellView);
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    protected void updateItem(ClubMember member, boolean empty) {
        super.updateItem(member, empty);
        clubMemberCellController.setClubMember(member);
    }
}
