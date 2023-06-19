package code.connection;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import code.model.ClientMail;
import code.model.Mail;
import code.model.MailSerializable;
import code.utilities.MailTranslator;
import javafx.application.Platform;

public class ClientConnection {
    private ScheduledThreadPoolExecutor pool;
    private ClientMail model;
    private List<Operation> operations;

    public ClientConnection(ClientMail model) {
        this.model = model;
        pool = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(1);
        operations = new ArrayList<>();

        // Connecting to the Server
        ClientServerConnection connection = new ClientServerConnection();
        pool.scheduleAtFixedRate(connection, 0, 5, TimeUnit.SECONDS);
    }

    public void shutdown() {
        pool.shutdown();
    }

    public void sendMail(MailSerializable sendMail) {
        operations.add(new Operation("SEND", sendMail));
    }

    public void deleteMail(Mail delete) {
        operations.add(new Operation("DELETE", MailTranslator.toMailSerializable(delete)));
    }
    





    /* --- INNER CLASS --- */
    private class ClientServerConnection implements Runnable {
        private Socket socket;
        private ObjectInputStream reader;
        private ObjectOutputStream writer;

        @Override
        public void run() {
            try {
                socket = new Socket("localhost", 6900);
                Platform.runLater(() -> model.setConnected(true));
                writer = new ObjectOutputStream(socket.getOutputStream());
                reader = new ObjectInputStream(socket.getInputStream());

                // Authentication
                logIn(model.getEmailProperty().getValue());

                // Expressing the type of request
                if (operations.size() > 0) {
                    writer.writeObject("OPERATIONS");
                    writer.flush();
                    sendOperations();
                } else {
                    writer.writeObject("DEFAULT");
                    writer.flush();
                }
                
                // Specify the mails that Client already have
                writer.writeObject(lastReceivedMail());
                writer.flush();

                List<MailSerializable> newMails = (List<MailSerializable>) reader.readObject();

                for (MailSerializable newMail : newMails) {
                    model.addNewMail(newMail);
                }

                model.viewMessages();
            } catch (UnknownHostException e) {
                System.out.println("UnknownHostException in ClientConnnection.ClientServerConnection constructor\n" + e.getMessage());
            } catch (IOException e) {
                System.out.println(
                        "IOException in ClientConnnection.ClientServerConnection constructor\n" + e.getMessage());
                Platform.runLater(() -> model.setConnected(false));
            } catch (ClassNotFoundException e) {
                System.out.println("ClassNotFoundException in ClientConnnection.ClientServerConnection constructor\n" + e.getMessage());
            }
        }

        /**
         * It sends the email of the Client to the Server so it can send him back his
         * list of mails
         * 
         * @param clientMail is Client's email
         */
        private void logIn(String clientMail) {
            try {
                writer.writeObject(clientMail);
                writer.flush();
            } catch (IOException e) {
                System.out.println("IOException in ClientConnnection.ClientServerConnection during logIn() method\n" + e.getMessage());
            }
        }

        /**
         * It sends all the operations of this session to the Server
         */
        private void sendOperations() {
            try {
                writer.writeObject(operations);
                writer.flush();

                operations.clear();
            } catch (IOException e) {
                System.out.println("IOException in ClientConnection.ClientServerConnection during sendMail() method\n"
                        + e.getMessage());
                e.printStackTrace();
            }
        }

        /**
         * It gets the id from the last received mail
         * @return the id of the last received mail if any mail exists
         *         "no mails" otherwise
         */
        private String lastReceivedMail() {
            int maxId = -1;

            List<Mail> receivedMails = new ArrayList<>(model.getInMessages());
            receivedMails.addAll(model.getOutMessages());
            
            for (Mail mail : receivedMails) {
                if (maxId < Integer.valueOf(mail.getId())) {
                    maxId = Integer.valueOf(mail.getId());
                }
            }

            if (maxId == -1) {
                return "No mails";
            } else {
                return String.valueOf(maxId);
            }
        }
    }

}
