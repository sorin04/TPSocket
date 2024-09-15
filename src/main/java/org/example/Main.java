package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

public class Main {

    public static void main(String[] args) throws IOException {
        new Thread(() -> {
            try {
                monServeur();
            } catch (IOException e) {
                System.err.println("Erreur lors de l'exécution du serveur : " + e.getMessage());
            }
        }).start();
    }
    public static void monServeur()throws IOException {
        final int port = 5000;
        boolean deconnexionClientDemandee = false;
        char[] bufferEntree = new char[65535];
        Outil outil = new Outil();
        String messageRecu;
        String reponse;
        ArrayList<IPV4> mesInterface =Outil.getSystemIP();


        String ipAddress = "127.0.0.1";



        ServerSocket monServerDeSocket = new ServerSocket(port);
        System.out.println("Serveur en fonctionnement sur IP: " + port);

        while (true) {
            Socket socketDuClient = monServerDeSocket.accept();
            System.out.println("Connexion avec : " + socketDuClient.getInetAddress());
            BufferedReader fluxEntree = new BufferedReader(new InputStreamReader(socketDuClient.getInputStream()));
            PrintStream fluxSortie = new PrintStream(socketDuClient.getOutputStream());

            while (!deconnexionClientDemandee && socketDuClient.isConnected()) {
                System.out.println("Attente d'une requête...");
                fluxSortie.println("Veuillez entrer une requête (HELLO, TIME, ECHO, YOU, ME, FIN) :");

                int NBLus = fluxEntree.read(bufferEntree);
                if (NBLus == -1){
                    break;
                }
                messageRecu = new String(bufferEntree, 0, NBLus).trim();

                if (messageRecu.length() != 0) {
                    System.out.println("\t\tMessage reçu : " + messageRecu);

                    if (messageRecu.equalsIgnoreCase("HELLO")) {
                        reponse = "Bienvenue Monsieur Moser !";

                    } else if (messageRecu.equalsIgnoreCase("TIME")) {
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date date = new Date();
                        reponse = "Voici la date et l'heure : " + formatter.format(date);

                    } else if (messageRecu.startsWith("ECHO")) {
                        reponse = messageRecu.substring(4);

                    } else if (messageRecu.equalsIgnoreCase("YOU")) {
                        reponse = "Serveur @IP " + ipAddress + ":PORT " + socketDuClient.getLocalPort();

                    } else if (messageRecu.equalsIgnoreCase("ME")) {
                        reponse = "Client @IP " + socketDuClient.getInetAddress() + ":PORT " + socketDuClient.getPort();

                    } else if (messageRecu.equalsIgnoreCase("FIN")) {
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
            fluxEntree.close();
            fluxSortie.close();
            socketDuClient.close();
            System.out.println("Client déconnecté.");
            monServerDeSocket.close();
            System.out.println("Serveur arrêté.");

            // Réinitialisation pour le client suivant
            //deconnexionClientDemandee = false;
        }

        }
    }

