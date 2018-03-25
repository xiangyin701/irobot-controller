package com.smarthome.xiaomi;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class Manager {
    private final static int TARGET_PORT = 54321;

    private DatagramSocket socket;
    private Thread serverThread;
    private boolean isRunning;

    public Manager() throws SocketException{
        this.socket = new DatagramSocket();
    }

    public void start() {
        if (this.serverThread != null && this.serverThread.isAlive()) {
            return;
        }
        this.serverThread = new Thread(new Receiver());
        this.serverThread.setDaemon(true);
        this.isRunning = true;
        this.serverThread.start();
    }

    public void discover() throws IOException {
        Packet packet = Packet.generateHandshakePacket();
        byte[] raw = packet.serialize();

        DatagramPacket udpPacket = new DatagramPacket(raw, 0, raw.length, InetAddress.getByName("255.255.255.255"),TARGET_PORT);
        socket.send(udpPacket);
    }

    protected class Receiver implements Runnable {

        @Override
        public void run() {
            while (Manager.this.isRunning) {
                try {
                    byte[] buffer = new byte[1028];
                    DatagramPacket response = new DatagramPacket(buffer, buffer.length);
                    socket.receive(response);
                    System.out.println("Packet received!");

                } catch (Exception e) {

                }
            }
        }
    }
}
