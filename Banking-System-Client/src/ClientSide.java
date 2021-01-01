import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * Bank Account - Client Side Code
 *
 * @author Ahmed Shabab Noor
 * @version 1.0
 */

public class ClientSide {

    private BufferedWriter writer;
    private BufferedReader reader;
    private final Scanner scanner = new Scanner(System.in);
    private Socket socket;

    ClientSide(){
        try {
            socket = new Socket("127.0.0.1", 6000);

            OutputStreamWriter out = new OutputStreamWriter(socket.getOutputStream());
            writer = new BufferedWriter(out);

            InputStreamReader in = new InputStreamReader(socket.getInputStream());
            reader = new BufferedReader(in);

        }catch (IOException e){
            e.printStackTrace();
        }
    }
    void closeEverything() {
        try {
            socket.close();
            reader.close();
            writer.close();
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    void sendText(String txt){
        try {
            writer.write(txt+"\n");
            writer.flush();

        }catch (IOException e){
            e.printStackTrace();
        }
    }
    String getText(){
        try {
            return reader.readLine();

        }catch (IOException e){
            e.printStackTrace();
        }
        return "Nothing found.";
    }

    void sendAccount(){
        String info;

        System.out.println("Enter name: ");
        info = scanner.nextLine();
        //scanner.nextLine();
        sendText(info);

        System.out.println("Enter ID: ");
        info = scanner.nextLine();
        //scanner.nextLine();
        sendText(info);

        System.out.println("Enter Balance: ");
        info = scanner.nextLine();
        //scanner.nextLine();
        sendText(info);

        System.out.println("Enter Interest Rate: ");
        info = scanner.nextLine();
        //scanner.nextLine();
        sendText(info);
    }
    void sendID(){
        String id = scanner.nextLine();
        sendText(id);
    }

    public static void main(String[] args) {
        ClientSide client = new ClientSide();
        System.out.println("Enter 1 for searching an account");
        System.out.println("Enter 2 for creating new account");
        System.out.println("Enter 3 for removing an account");
        System.out.println("Enter 4 for updating an account");

        Scanner scanner = new Scanner(System.in);

        while (true){
            System.out.println();
            System.out.println("Enter request: ");
            boolean state = true;
            int request = -1;

            while (state){
                request = scanner.nextInt();
                if (request >= 1 && request <= 5)
                    state = false;
            }
            client.sendText(request+"");

            if (request == 1){
                System.out.println("Search for a specific person with their ID.");
                System.out.println("Please Enter ID: ");

                client.sendID();
                String txt = client.getText();
                System.out.println(txt);

            }
            else if (request == 2){
                System.out.println("Create a new Account.");
                client.sendAccount();
            }
            else if (request == 3){
                System.out.println("Remove Account.");
                System.out.println("Enter the ID of the account you want to remove: ");

                client.sendID();
                String txt = client.getText();
                System.out.println(txt);
            }
            else if (request == 4){
                System.out.println("Update info of Account.");
                System.out.println("Enter the ID of the account you want to update: ");
                client.sendID();
                String txt = client.getText();
                System.out.println(txt);

                if (txt.equals("Old info removed.")){
                    System.out.println("Enter Updated Info: ");
                    client.sendAccount();
                }
            }
            else {
                break;
            }
        }

        client.closeEverything();
    }
}
