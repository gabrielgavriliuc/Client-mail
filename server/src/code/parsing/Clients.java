package code.parsing;

import java.util.List;

public class Clients {
    private String nextId;
    private List<ClientMail> clients;

    
    public List<ClientMail> getClients() {
        return clients;
    }

    public String getNextId() {
        return nextId;
    }

    public void setNextId(String nextId) {
        this.nextId = nextId;
    }

}
