package code.connection;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import code.model.MailSerializable;
import code.model.ServerModel;
import code.parsing.ParserJSON;
import javafx.application.Platform;


public class ServerConnection {
    // Thread 
    private ExecutorService executor;
    private StartAcceptingConnections acceptConnections;

    // Socket
    private ServerSocket serverSocket;

    // Thread synchronization
    private ReadWriteLock rwLock;
    private Lock readLock;
    private Lock writeLock;

    private ServerModel logModel;
    private boolean serverOn;

    public ServerConnection(ServerModel model) {
        this.logModel = model;

        executor = Executors.newFixedThreadPool(1);

        rwLock = new ReentrantReadWriteLock();
        readLock = rwLock.readLock();
        writeLock = rwLock.writeLock();
    }

    /**
     * It turns the server on
     * When the server is on it can accept connections
     * Each connection will have its Thread
     * @throws IOException
     */
    public void turnServerOn() throws IOException {
        serverOn = true;
        logModel.addLog("--- SERVER TURNED ON ---");

        acceptConnections = new StartAcceptingConnections();
        executor.execute(acceptConnections);
    }

    /**
     * It stops the Server from accepting new Connections until it will be returned on
     */
    public void turnServerOff() {
        serverOn = false;

        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.out.println(
                    "IOException in ServerConnection during turnServerOff() method -> " + e.getMessage());
        }
    }

    /**
     * It closes the application when the GUI is closed
     */
    public void applicationClosed() {
        executor.shutdown();
    }







    /* --- INNER CLASS --- */
    private class StartAcceptingConnections implements Runnable {
        private final int N_THREADS = 3;
        private ExecutorService executorForClients;

        public StartAcceptingConnections() {
            executorForClients = Executors.newFixedThreadPool(N_THREADS);
        }

        @Override
        public void run() {
            try {
                if (serverSocket == null || serverSocket.isClosed()) {
                    serverSocket = new ServerSocket(6900);
                    serverSocket.setReuseAddress(true);
                }

                while (serverOn) {
                    Socket socket = serverSocket.accept();

                    executorForClients.execute(new ServerClientConnection(socket));
                }
                
            } catch (IOException e) {
                System.out.println("IOException in ServerConnection.StartAcceptingConnections during run() method -> " + e.getMessage());
            } finally {
                // Server was turned off
                executorForClients.shutdown();
                Platform.runLater(() -> logModel.addLog("--- SERVER TURNED OFF ---"));
            }
        }

        /* --- INNER INNER CLASS --- */
        private class ServerClientConnection implements Runnable {
            private Socket socket;
            private ObjectOutputStream writer;
            private ObjectInputStream reader;

            private ParserJSON parser;
            private String client;

            public ServerClientConnection(Socket socket) {
                this.socket = socket;
                parser = new ParserJSON();
            }

            @Override
            public void run() {
                try {
                    // Opening the streams
                    writer = new ObjectOutputStream(socket.getOutputStream());
                    reader = new ObjectInputStream(socket.getInputStream());

                    // Recognizing the Client
                    client = (String) reader.readObject();
                    parser.setClientEmail(client);

                    // Adding the event to the logList
                    Platform.runLater(() -> logModel.addLog(client + " CONNECTED TO THE SERVER"));

                    // Taking Client's request
                    String mode = (String) reader.readObject();

                    if (mode.equals("OPERATIONS")) {
                        getOperations();
                    }

                    // Getting the mails of the Client
                    writer.writeObject(getMails());
                    writer.flush();
                } catch (ClassNotFoundException e) {
                    System.out.println(
                            "ClassNotFoundException in ServerConnection.ServerClientConnection during run() method");
                    e.printStackTrace();
                } catch (IOException e) {
                    System.out.println(
                            "IOException in ServerConnection.ServerClientConnection during run() method");
                    e.printStackTrace();
                } finally {
                    closeStreams();
                    try {
                        socket.close();
                    } catch (IOException e) {
                        System.out.println(
                            "IOException in ServerConnection.ServerClientConnection during socket.close() method");
                    e.printStackTrace();
                    }
                }

            }

            /**
             * It gets all the operations that the Client wants to be accomplished
             */
            private void getOperations() {
                try {
                    List<Operation> operations = (List<Operation>) reader.readObject();

                    // Here start our editing operations
                    writeLock.lock();

                    for (Operation operation : operations) {
                        MailSerializable mail = operation.getMailToServer();

                        if (operation.getMode().equals("SEND")) {
                            sendMail(mail);
                        } else {
                            deleteMail(mail);
                        }
                    }

                } catch (ClassNotFoundException e) {
                    System.out.println(
                            "ClassNotFoundException in ServerConnection.ServerClientConnection during getOperations() method");
                    e.printStackTrace();
                } catch (IOException e) {
                    System.out.println(
                            "IOException in ServerConnection.ServerClientConnection during getOperations() method");
                    e.printStackTrace();
                } finally {
                    writeLock.unlock();
                }
            }

            private void sendMail(MailSerializable sendMail) {
                sendMail.setId(parser.getNextId());
                parser.addMailToJSON(sendMail);

                Platform.runLater(() -> logModel.addLog(client + " SENDED a mail"));
            }

            private void deleteMail(MailSerializable deleteMail) {
                parser.deleteMailFromJSON(deleteMail);

                Platform.runLater(() -> logModel.addLog(client + " DELETED a mail"));
            }

            private List<MailSerializable> getMails() {
                List<MailSerializable> mails = null;

                try {
                    String lastReceivedMail = (String) reader.readObject();

                    // Here starts our reading operation
                    readLock.lock();

                    mails = parser.getListOfMessages(lastReceivedMail);

                } catch (ClassNotFoundException e) {
                    System.out.println(
                            "ClassNotFoundException in ServerConnection.ServerClientConnection during getMails() method");
                            e.printStackTrace();
                } catch (IOException e) {
                    System.out.println(
                            "IOException in ServerConnection.ServerClientConnection during getMails() method");
                            e.printStackTrace();
                } finally {
                    readLock.unlock();
                }

                return mails;
            }

            private void closeStreams() {
                try {
                    writer.close();
                    reader.close();
                } catch (IOException e) {
                    System.out.println(
                            "IOException in ServerConnection.ServerClientConnection during closeStreams() method");
                    e.printStackTrace();
                }
            }
        }
    }

}
