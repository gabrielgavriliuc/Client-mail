package code.model;

import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Mail {
    private StringProperty id;
    private StringProperty source;
    private ObservableList<String> destination;
    private StringProperty argument;
    private StringProperty text;
    private StringProperty date;

    public Mail(String id, String source, List<String> destination, String argument, String text, String date) {
        this.id = new SimpleStringProperty(id);
        this.source = new SimpleStringProperty(source);
        this.destination = FXCollections.observableArrayList(destination);
        this.argument = new SimpleStringProperty(argument);
        this.text = new SimpleStringProperty(text);
        this.date = new SimpleStringProperty(date);
    }

    public Mail(String source, List<String> destination, String argument, String text, String date) {
        this.id = new SimpleStringProperty();
        this.source = new SimpleStringProperty(source);
        this.destination = FXCollections.observableArrayList(destination);
        this.argument = new SimpleStringProperty(argument);
        this.text = new SimpleStringProperty(text);
        this.date = new SimpleStringProperty(date);
    }

    public String getId() {
        return id.getValue();
    }

    public void setId(String id) {
        this.id.setValue(id);
    }

    public String getSource() {
        return source.getValue();
    }

    public void setSource(String source) {
        this.source.setValue(source);
    }

    public List<String> getDestination() {
        return destination;
    }
    
    public void setDestination(List<String> destination) {
        this.destination.setAll(destination);
    }

    public String getArgument() {
        return argument.getValue();
    }

    public void setArgument(String argument) {
        this.argument.setValue(argument);
    }

    public String getText() {
        return text.getValue();
    }

    public void setText(String text) {
        this.text.setValue(text);
    }

    public String getDate() {
        return date.getValue();
    }

    public void setDate(String date) {
        this.date.setValue(date);
    }

    public StringProperty getIdProperty() {
        return id;
    }

    public StringProperty getSourceProperty() {
        return source;
    }

    public ObservableList<String> getDestinationProperty() {
        return destination;
    }

    public StringProperty getArgumentProperty() {
        return argument;
    }

    public StringProperty getTextProperty() {
        return text;
    }

    public StringProperty getDateProperty() {
        return date;
    }
}