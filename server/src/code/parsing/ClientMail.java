package code.parsing;

import java.util.ArrayList;
import java.util.List;

import code.model.MailSerializable;

public class ClientMail {
    private String email;
    private List<MailSerializable> mails;

    public ClientMail(String email) {
        this.email = email;
        mails = new ArrayList<>();
    }

    public ClientMail() {}

    public String getEmail() {
        return email;
    }

    public List<MailSerializable> getMails() {
        return mails;
    }

    /**
     * Adds a new received Mail from the Server to the Client list of Mails
     * @param newMail is the new Mail received
     */
    public void addNewMail(MailSerializable newMail) {
        mails.add(newMail);
    }

    /**
     * It removes a specific Mail from the Client list of Mails
     * @param deleteMail is the Mail that need to be removed
     */
    public void removeMail(MailSerializable deleteMail) {
        for (MailSerializable mailSerializable : mails) {
            if (mailSerializable.getId().equals(deleteMail.getId())) {
                deleteMail = mailSerializable;
            }
        }

        mails.remove(deleteMail);
    }

}
