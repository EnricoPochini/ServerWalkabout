import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Test {

    private static final int porta = 149;
    private static final String ip = "127.0.0.1";

    public static void main(String[] args){

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("inserisci il messaggio da mandare");
        String mess = null;
        try{
            mess = br.readLine();
        }catch (IOException ignored) {
        }

        //mess = "Stefano"+"\t"+"Arrivo: 13:50"+"\n"+"Intolleranze: vegano 1 ";



        try(Socket socket = new Socket(ip,porta);
            PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            ){

            out.println(mess);

            String risp = in.readLine();

            System.out.println(risp);


            socket.close();


        }catch (IOException ignored){



        }


    }





}