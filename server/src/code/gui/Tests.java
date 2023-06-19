package code.gui;

import java.util.ArrayList;
import java.util.List;

import code.model.MailSerializable;
import code.parsing.ParserJSON;

public class Tests {
    public static void main(String[] args) {
        ParserJSON parser = new ParserJSON();
        
        // // ID TEST
        // System.out.println(parser.getNextId());
        // System.out.println(parser.getNextId());

        // SEND MAIL TEST
        List<String> d1 = new ArrayList<>();
        d1.add("gabrielgavriliuc@gmail.com");
        MailSerializable newMail = new MailSerializable("lucabroggy@gmail.com", d1, "SESSO NUOVO", "questo è sesso nuovo: Bomba", "OGGI");
        newMail.setId(parser.getNextId());
        parser.addMailToJSON(newMail);
    }
}
