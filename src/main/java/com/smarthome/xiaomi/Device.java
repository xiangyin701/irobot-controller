package com.smarthome.xiaomi;

import java.io.IOException;
import java.net.*;

public class Device {
    private final static int PORT = 8888;
    private final static int TARGET_PORT = 54321;
    private String ip;

    private DatagramSocket socket;

    public Device(String ip) throws SocketException {
        this.ip = ip;
        this.socket = new DatagramSocket(PORT);
    }

    private void sendPacket(Packet packet) throws IOException {
        byte[] raw = packet.serialize();
        DatagramPacket udpPacket = new DatagramPacket(raw, 0, raw.length, InetAddress.getByName(ip),TARGET_PORT);
        socket.send(udpPacket);
    }

    public static Device discover() throws Exception {
        Packet packet = Packet.generateHandshakePacket();
        byte[] raw = packet.serialize();

        DatagramSocket socket = new DatagramSocket(PORT);
        DatagramPacket udpPacket = new DatagramPacket(raw, 0, raw.length, InetAddress.getByName("255.255.255.255"),TARGET_PORT);
        socket.send(udpPacket);
        Thread.sleep(1000);
        socket.send(udpPacket);

        byte[] buffer = new byte[128 * 4];
        DatagramPacket received = new DatagramPacket(buffer, buffer.length);
        socket.receive(received);
        return null;
    }

    public void start() throws IOException {
        Packet packet = Packet.generateHandshakePacket();
        sendPacket(packet);
        byte[] buffer = new byte[128];
        DatagramPacket received = new DatagramPacket(buffer, buffer.length);
        socket.receive(received);

        System.out.println(received.getAddress());

        //new packet
        //build connection to robot
        //send
    }
}
