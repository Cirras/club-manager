package data.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single club
 * @see ClubMember
 */
public class Club implements Serializable {
    private StringProperty name = new SimpleStringProperty();
    private ObservableList<ClubMember> members = FXCollections.observableArrayList();
    private transient boolean dirty = false;

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public ObservableList<ClubMember> getMembers() {
        return members;
    }

    public void setMembers(ObservableList<ClubMember> members) {
        this.members = members;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public boolean isDirty() {
        return dirty;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.writeUTF(nameProperty().getValueSafe());
        s.writeObject(new ArrayList<>(members));
    }

    @SuppressWarnings("unchecked")
    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        name = new SimpleStringProperty(s.readUTF());
        members = FXCollections.observableList((List<ClubMember>)s.readObject());
    }
}
