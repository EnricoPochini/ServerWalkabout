import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalTime;

public class ServerWalkabout {

    //porte server
    private static final int PORTA_LOGIN = 149;
    private static final int PORTA_INFO_GUIDE = 150;
    private static final int PORTA_CUCINA = 151;

    //payload
    private static StringBuilder info;

    //tempi di riavvio
    private static final int oraSpegnimentoPomeriggio = 14;
    private static final int oraSpegnimentoNotte = 0;
    private static boolean rebooted;


    public static void main(String[] args) {

        info = new StringBuilder();


        //creazione gestione thread per singola porta
        GestoreLogin gestlog = new GestoreLogin();
        gestlog.start();

        RiceviDatiGuide ricDati = new RiceviDatiGuide();
        ricDati.start();

        InviaDatiCucina cucinaThread = new InviaDatiCucina();
        cucinaThread.start();



        //reset dati
        int oraCorrente;
        while(true){

            oraCorrente = LocalTime.now().getHour();


            //reset
            if(oraCorrente == oraSpegnimentoPomeriggio || oraCorrente == oraSpegnimentoNotte){

                info = new StringBuilder();
                rebooted = true;

            }//if
            else{
                rebooted = false;
            }//else


            //sleep
            try{

                Thread.sleep(1000);

            }catch(InterruptedException ignored){


            }

        }//while




    }

    //thread per la gestione dei login
    protected static class GestoreLogin extends Thread{

        private static final String COD_FILE_PATH = "codiciGuide/codiciGuide.txt";
        public GestoreLogin() {
        }//costruttore


        @Override
        public void run(){

            while(true){

                try(
                        ServerSocket server = new ServerSocket(PORTA_LOGIN);
                        Socket clientSocket = server.accept();
                ){

                    System.out.println("login client connesso");

                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(),true);

                    //lettura codice inserito dall'utente
                    String userCod = in.readLine();

                    //invio nome guida corrispondente se login andato a buon fine
                    String loginResult = login(userCod);
                    out.println(loginResult);

                    clientSocket.close();


                }catch(IOException ignored){

                    ignored.printStackTrace();


                }


            }//while

        }


        //dato il codice inserito dall'utente restituisce il nome della guida se il login è stato effettuato
        public String login(String ins){

            String guida = "Errore";

            try{

                File fileCodici = new File(COD_FILE_PATH);
                BufferedReader br = new BufferedReader(new FileReader(fileCodici));

                String line = br.readLine();
                while(line != null){

                    String[] codiceEGuida = line.split(",");
                    if(ins.equals(codiceEGuida[0])){

                        guida = codiceEGuida[1];
                        return guida;

                    }//if

                    line = br.readLine();

                }//while


            }catch (IOException ignored){


            }

            return guida;

        }


    }




    //thread info in arrivo dalle guide
    protected static class RiceviDatiGuide extends Thread{

        public RiceviDatiGuide(){
        }//costruttore


        @Override
        public void run(){

            while(true){

                try(ServerSocket server = new ServerSocket(PORTA_INFO_GUIDE);
                    Socket clientSocket = server.accept();
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                ){


                    //salvare le info ricevute
                    String infoGruppo = in.readLine();
                    System.out.println(infoGruppo);
                    info.append("").append(infoGruppo);




                }catch(IOException ignored){


                }


            }//while


        }




    }





    //thread server cucina
    protected static class InviaDatiCucina extends Thread {

        public InviaDatiCucina() {
        }//costruttore

        @Override
        public void run() {

            while(true){


                try (
                        ServerSocket server = new ServerSocket(PORTA_CUCINA);
                        Socket clientSocket = server.accept();
                        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                ) {

                    //messaggio da inviare
                    out.println(info.toString());

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }//while

        }



    }
}