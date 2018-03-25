package com.smarthome.xiaomi;

import com.smarthome.utils.ConversionUtils;

import java.io.IOException;
import java.net.*;
import java.nio.charset.Charset;

public class Device {
    private final static int PORT = 8888;
    private final static int TARGET_PORT = 54321;
    private String ip;
    private byte[] id;
    private byte[] token;
    private int serverStamp;
    private int serverStampTime;

    private DatagramSocket socket;

    public Device(String ip, byte[] token) throws SocketException {
        this.ip = ip;
        this.token = token;
        this.socket = new DatagramSocket(PORT);
    }

    private void sendPacket(Packet packet) throws IOException {
        byte[] raw = packet.serialize();
        DatagramPacket udpPacket = new DatagramPacket(raw, 0, raw.length, InetAddress.getByName(ip),TARGET_PORT);
        socket.send(udpPacket);
    }

    private Packet receivePacket() throws IOException {
        byte[] buffer = new byte[128];
        DatagramPacket received = new DatagramPacket(buffer, buffer.length);
        socket.receive(received);
        Packet response = Packet.from(buffer);
        if (response.getStamp() > 0) {
            this.serverStamp = response.getStamp();
            this.serverStampTime = (int)(System.currentTimeMillis() / 1000);
        }
        if (this.id == null) {
            this.id = response.getID();
        }
        return response;
    }

    public void initialize() throws IOException {
        if (this.id == null) {


            Packet packet = Packet.generateHandshakePacket();
            sendPacket(packet);

            receivePacket();
        }
    }

    private Packet pack(String method, String params) {
        String payloadStr = String.format("{\"id\":%d,\"method\":\"miIO.%s\",\"params\":%s}", ConversionUtils.bytesToInt(id, 0 , id.length -1 ),method, params);
        byte[] payload = payloadStr.getBytes(Charset.forName("UTF-8"));

        int stamp = (int)((System.currentTimeMillis() / 1000 - this.serverStampTime) + this.serverStamp);
        return new Packet(this.token, this.id, stamp, payload);
    }

    public void locateMe() throws Exception {
        initialize();

        Packet packet = pack("find_me", "[]");


        sendPacket(packet);

    }
}
