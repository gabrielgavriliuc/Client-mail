package code.connection;

import java.io.Serializable;

import code.model.MailSerializable;

public class Operation implements Serializable {
    private static final long serialVersionUID = 8644534200825957321L;

    private String mode;
    private MailSerializable mailToServer;

    public Operation(String mode, MailSerializable mail) {
        this.mode = mode;
        this.mailToServer = mail;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public MailSerializable getMailToServer() {
        return mailToServer;
    }

    public void setMailToServer(MailSerializable mailToServer) {
        this.mailToServer = mailToServer;
    }
}
