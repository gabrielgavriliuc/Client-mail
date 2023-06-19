package code.model;

import java.io.Serializable;
import java.util.List;

public class MailSerializable implements Serializable {
    private static final long serialVersionUID = 8644534200825957679L;
    
    private String id;
    private String source;
    private List<String> destination;
    private String argument;
    private String text;
    private String date;

    public MailSerializable(String id, String source, List<String> destination, String argument, String text, String date) {
        this.id = id;
        this.source = source;
        this.destination = destination;
        this.argument = argument;
        this.text = text;
        this.date = date;
    }

    public MailSerializable(String source, List<String> destination, String argument, String text, String date) {
        this.source = source;
        this.destination = destination;
        this.argument = argument;
        this.text = text;
        this.date = date;
    }

    public MailSerializable() {}

    public String getId() {
        return id;
    }

    public String getSource() {
        return source;
    }

    public List<String> getDestination() {
        return destination;
    }

    public String getArgument() {
        return argument;
    }

    public String getText() {
        return text;
    }

    public String getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "\nMail " + id + ": \n\tFROM: " + source + "\n\tTO: " + destination + "\n\tARGUMENT: " + argument
                + "\n\tTEXT: " + text + "\n\tDATE: " + date;
    }

    public void setId(String id) {
        this.id = id;
    }
}