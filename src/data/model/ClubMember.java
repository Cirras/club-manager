package data.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Represents a member of a club and all their associated information
 * @see Club
 */
public class ClubMember implements Serializable {
    private StringProperty firstName = new SimpleStringProperty();
    private StringProperty lastName = new SimpleStringProperty();
    private StringProperty email = new SimpleStringProperty();
    private StringProperty phoneNumber = new SimpleStringProperty();
    private StringProperty role = new SimpleStringProperty();

    public String getFirstName() {
        return firstName.get();
    }

    public StringProperty firstNameProperty() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName.set(firstName);
    }

    public String getLastName() {
        return lastName.get();
    }

    public StringProperty lastNameProperty() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName.set(lastName);
    }

    public String getEmail() {
        return email.get();
    }

    public StringProperty emailProperty() {
        return email;
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    public String getPhoneNumber() {
        return phoneNumber.get();
    }

    public StringProperty phoneNumberProperty() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber.set(phoneNumber);
    }

    public String getRole() {
        return role.get();
    }

    public StringProperty roleProperty() {
        return role;
    }

    public void setRole(String role) {
        this.role.set(role);
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.writeUTF(firstNameProperty().getValueSafe());
        s.writeUTF(lastNameProperty().getValueSafe());
        s.writeUTF(emailProperty().getValueSafe());
        s.writeUTF(phoneNumberProperty().getValueSafe());
        s.writeUTF(roleProperty().getValueSafe());
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        firstName = new SimpleStringProperty(s.readUTF());
        lastName = new SimpleStringProperty(s.readUTF());
        email = new SimpleStringProperty(s.readUTF());
        phoneNumber = new SimpleStringProperty(s.readUTF());
        role = new SimpleStringProperty(s.readUTF());
    }
}
