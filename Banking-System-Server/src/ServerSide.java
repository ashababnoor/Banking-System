import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Bank Account - Server Side Code
 *
 * @author Ahmed Shabab Noor
 * @version 1.0
 */

class BankAccount{
    String name;
    String id;
    double balance;
    double interestRate;
    final String separator = " ### ";

    public BankAccount(String name, String id, double balance, double interestRate) {
        this.name = name;
        this.id = id;
        this.balance = balance;
        this.interestRate = interestRate;
    }

    void printInfo(File file) {
        FileWriter fr = null;
        BufferedWriter br = null;
        PrintWriter pr = null;

        try {
            fr = new FileWriter(file, true);
            br = new BufferedWriter(fr);
            pr = new PrintWriter(br);

            String content = name + separator + id + separator + balance + separator + interestRate;
            System.out.println("Printing to file: "+content);
            pr.println(content);

        } catch (IOException e){
            e.printStackTrace();
        } finally {
            try{
                if (pr != null) pr.close();
                if (br != null) br.close();
                if (fr != null) fr.close();
            }catch (IOException | NullPointerException e){
                e.printStackTrace();
            }
        }

    }
    String getInfo(){
        return "Name: "+name+ "; ID: "+id +"; Balance: "+balance+ "; Interest Rate: "+interestRate;
    }
}

public class ServerSide {

    private BufferedWriter writer;
    private BufferedReader reader;
    private final ArrayList<BankAccount> accounts = new ArrayList<>();
    File file = new File("info.txt");

    void initialize(){

        try{
            Scanner scanner = new Scanner(file);

            while (scanner.hasNext()){
                String line = scanner.nextLine();
                if (line.equals("")) continue;

                String separator = " ### ";

                String[] components = line.split(separator);
                String name = components[0];
                String id = components[1];
                String balanceString = components[2];
                double balance = Double.parseDouble(balanceString);
                String interestString = components[3];
                double interestRate = Double.parseDouble(interestString);

                BankAccount newAccount = new BankAccount(name, id, balance, interestRate);
                accounts.add(newAccount);
            }

        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
    }

    void deleteAllFileContent(){
        String FILE_PATH = "info.txt";
        try{
            new FileWriter(FILE_PATH, false).close();
        }catch (IOException e){
            e.printStackTrace();
        }
        System.out.println("Deleted all info from file.");
    }
    void writeAllInfoToFile(){
        for (BankAccount account:accounts) {
            account.printInfo(file);
        }
        System.out.println("Wrote all info to file.");
    }

    void addNewAccount(){
        try{
            String name = reader.readLine();
            String id = reader.readLine();
            double balance = 0; double interestRate = 0;

            boolean state = true;
            while (state){
                try {
                    String balanceString = reader.readLine();

                    balance = Double.parseDouble(balanceString);
                    String interestString = reader.readLine();
                    interestRate = Double.parseDouble(interestString);
                    state = false;
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            BankAccount account = new BankAccount(name, id, balance, interestRate);
            accounts.add(account);
            account.printInfo(file);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    String searchAccount(){
        BankAccount foundAccount = null;
        String idOfAccountHolder = null;

        try{
            System.out.println("waiting for id: ");
            idOfAccountHolder = reader.readLine();
            System.out.println("id received: "+idOfAccountHolder);

        }catch (IOException e){
            e.printStackTrace();
        }

        for (BankAccount account:accounts){
            if (account.id.equals(idOfAccountHolder))
                foundAccount = account;
        }

        if (foundAccount == null)
            return "Account not found.";
        else
            return foundAccount.getInfo();
    }

    void removeAccount() throws Exception {
        BankAccount foundAccount = null;
        String givenID = null;
        try{
            givenID = reader.readLine();
        }catch (IOException e){
            e.printStackTrace();
        }
        System.out.println("received id: "+givenID);

        for (BankAccount account:accounts){
            if (account.id.equals(givenID))
                foundAccount = account;
        }
        if (foundAccount != null) {
            accounts.remove(foundAccount);
            deleteAllFileContent();
            writeAllInfoToFile();
        }
        else {
            throw new Exception("Account with ID: "+givenID+" not found.");
        }
    }

    void updateInfo(){
        try{
            removeAccount(); //first removes the account from the file
            System.out.println("Old info removed.");
            writeToStream("Old info removed.");
            addNewAccount(); //then adds the new info to that account.
        }catch (Exception e){
            System.out.println(e.getMessage());
            writeToStream(e.getMessage());
        }
    }

    int checkRequest(Socket socket) {
        int value = -1;
        try{
            OutputStreamWriter out = new OutputStreamWriter(socket.getOutputStream());
            writer = new BufferedWriter(out);

            InputStreamReader in = new InputStreamReader(socket.getInputStream());
            reader = new BufferedReader(in);

            System.out.println("\n"+"waiting for request...");
            String request = reader.readLine();
            System.out.println("Received request: "+request);
            try{
                value = Integer.parseInt(request);
            }catch (Exception e){
                System.out.println("Bad request. Try again.");
                e.printStackTrace();
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return value;
    }

    void writeToStream(String txt){
        try {
            writer.write(txt+"\n");
            writer.flush();

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ServerSide serverSide = new ServerSide();
        serverSide.initialize();

        try{
            ServerSocket server = new ServerSocket(6000);

            System.out.println("Waiting to connect...");
            Socket socket = server.accept();
            System.out.println("Connected with client: "+socket.getLocalAddress());

            while (true){

                int request = serverSide.checkRequest(socket);
                if (request == 1){
                    String info = serverSide.searchAccount();
                    serverSide.writeToStream(info);
                }
                else if (request == 2){
                    serverSide.addNewAccount();
                }
                else if (request == 3){
                    try{
                        serverSide.removeAccount();
                        serverSide.writeToStream("Successfully removed");
                    }catch (Exception e){
                        System.out.println(e.getMessage());
                        serverSide.writeToStream(e.getMessage());
                    }
                }
                else if (request == 4){
                    serverSide.updateInfo();
                }
                else if (request == 5){
                    break;
                }
            }
            socket.close();

        }catch (IOException e){
            e.printStackTrace();
        }finally {
            try{
                serverSide.reader.close();
                serverSide.writer.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
