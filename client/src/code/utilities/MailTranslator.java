package code.utilities;

import code.model.Mail;
import code.model.MailSerializable;

public class MailTranslator {

    /**
     * It transforms a MailSerializable into a Mail object keeping all its details
     * @param m is the MailSerializable
     * @return a Mail object 
     */
    public static Mail toMail(MailSerializable m) {
        return new Mail(m.getId(), m.getSource(), m.getDestination(), m.getArgument(), m.getText(), m.getDate());
    }

    /**
     * It transforms a Mail into a MailSerializable object keeping all its details
     * @param m is the Mail
     * @return a MailSerializable object 
     */
    public static MailSerializable toMailSerializable(Mail m) {
        return new MailSerializable(m.getId(), m.getSource(), m.getDestination(), m.getArgument(), m.getText(), m.getDate());
    }

}
