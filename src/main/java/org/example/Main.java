package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Main {

    public static void main(String[] args) throws IOException {

        final int port = 4000;
        boolean deconnexionClientDemandee = false;
        char[] bufferEntree = new char[65535];
        String messageRecu = null;
        String reponse = null;


        ServerSocket monServerDeSocket = new ServerSocket(port);
        System.out.println("Serveur en fonctionnement.");

        while (true) {

            Socket socketDuClient = monServerDeSocket.accept();
            System.out.println("Connexion avec : " + socketDuClient.getInetAddress());
            BufferedReader fluxEntree = new BufferedReader(new InputStreamReader(socketDuClient.getInputStream()));
            PrintStream fluxSortie = new PrintStream(socketDuClient.getOutputStream());

            while (!deconnexionClientDemandee && socketDuClient.isConnected()) {
                System.out.println("Attente d'une requête...");
                fluxSortie.println("Veuillez entrer une requête (HELLO, TIME, ECHO, YOU, ME, FIN) :");


                int NBLus = fluxEntree.read(bufferEntree);
                messageRecu = new String(bufferEntree, 0, NBLus).trim();

                if (messageRecu.length() != 0) {
                    System.out.println("\t\tMessage reçu : " + messageRecu);


                    if (messageRecu.equalsIgnoreCase("HELLO")) {
                        reponse = "Vous êtes connectés au serveur SUPERSERVEUR...Ca va ";
                    } else if (messageRecu.equalsIgnoreCase("TIME")) {

                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date date = new Date();
                        reponse = "Voici la date et l'heure : " + formatter.format(date);
                    } else if (messageRecu.startsWith("ECHO ")) {

                    } else if (messageRecu.equalsIgnoreCase("YOU") || messageRecu.equalsIgnoreCase("WHOAREYOU?")) {

                        reponse = "Serveur @ " + socketDuClient.getLocalAddress() + ":" + socketDuClient.getLocalPort();
                    } else if (messageRecu.equalsIgnoreCase("ME") || messageRecu.equalsIgnoreCase("WHOAM?")) {

                        reponse = "Client @ " + socketDuClient.getInetAddress() + ":" + socketDuClient.getPort();
                    } else if (messageRecu.equalsIgnoreCase("FIN") || messageRecu.equalsIgnoreCase("EXIT")) {
                        reponse = "JE VOUS DECONNECTE !!!";
                        deconnexionClientDemandee = true;
                    } else {
                        reponse = "Requête non reconnue.";
                    }

                    // Envoi de la réponse au client
                    fluxSortie.println(reponse);
                    System.out.println("\t\tMessage émis : " + reponse);
                }
            }

            // Fermeture de la connexion avec le client
            socketDuClient.close();
            System.out.println("Client déconnecté.");

            // Réinitialisation pour le client suivant
            deconnexionClientDemandee = false;
        }
    }
}
