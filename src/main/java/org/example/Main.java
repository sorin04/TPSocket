package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;

public class Main {

    public static void main(String[] args) throws IOException {

        final int port = 5000;
        boolean deconnexionClientDemandee = false;
        char[] bufferEntree = new char[65535];
        String messageRecu;
        String reponse;

        String localIPAddress = getLocalIPAddress();

        ServerSocket monServerDeSocket = new ServerSocket(port);
        System.out.println("Serveur en fonctionnement sur IP: " + localIPAddress);

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
                        reponse = "Bienvenue !";

                    } else if (messageRecu.equalsIgnoreCase("TIME")) {
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date date = new Date();
                        reponse = "Voici la date et l'heure : " + formatter.format(date);

                    } else if (messageRecu.startsWith("ECHO")) {
                        reponse = messageRecu.substring(4);

                    } else if (messageRecu.equalsIgnoreCase("YOU")) {
                        reponse = "Serveur @IP " + localIPAddress + ":PORT " + socketDuClient.getLocalPort();

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
            socketDuClient.close();
            System.out.println("Client déconnecté.");

            // Réinitialisation pour le client suivant
            deconnexionClientDemandee = false;
        }
    }


    public static String getLocalIPAddress() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                if (networkInterface.isUp() && !networkInterface.isLoopback()) {
                    Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                    while (inetAddresses.hasMoreElements()) {
                        InetAddress inetAddress = inetAddresses.nextElement();
                        if (inetAddress instanceof java.net.Inet4Address) {
                            return inetAddress.getHostAddress();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "127.0.0.1";
    }
}
