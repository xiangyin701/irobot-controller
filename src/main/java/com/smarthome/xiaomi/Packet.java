package com.smarthome.xiaomi;

import com.smarthome.utils.ConversionUtils;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;

public class Packet {
    // Fields in Packet
    private byte[] magic = new byte[] {0x21, 0x31};
    private byte[] unknown;
    private byte[] serial;
    private int stamp;
    private byte[] rawData;

    // Properties to generate packet
    private byte[] token;

    public Packet(byte[] token, byte[] serial, int stamp, byte[] payload) {
        this.token = token;
        this.serial = serial;
        this.stamp = stamp;
        this.rawData = payload;
        this.unknown = new byte[4];
    }

    private Packet() {

    }

    public static Packet generateHandshakePacket() {
        Packet packet = new Packet();
        packet.unknown = new byte[] {(byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff};
        packet.serial = new byte[] {(byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff};
        packet.stamp = 999999;
        return packet;
    }

    public static Packet from(byte[] received) {
        Packet packet = new Packet();
        packet.magic = Arrays.copyOfRange(received, 0,2);
        int length = ConversionUtils.bytesToInt(received, 2,3);
        packet.unknown = Arrays.copyOfRange(received, 4,8);
        packet.serial = Arrays.copyOfRange(received, 8, 12);
        packet.stamp = ConversionUtils.bytesToInt(received, 12, 15);
        packet.token = Arrays.copyOfRange(received, 16, 32);
        return packet;
    }

    public byte[] getID () {
        return this.serial;
    }

    public int getStamp() {
        return this.stamp;
    }

    public byte[] serialize() throws IOException {
        if (this.rawData == null) {
            byte[] handshake = new byte[32];
            Arrays.fill(handshake, (byte)0xff);
            handshake[0] = 0x21;
            handshake[1] = 0x31;
            System.arraycopy(ConversionUtils.intToBytes(32), 2, handshake, 2, 2);
            return handshake;
        }

        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            return null;
        }

        // Encrypte data
        byte[] data = null;
        try {
            byte[] key, iv;
            key = md5.digest(this.token);
            byte[] temp = md5.digest(key);
            byte[] input = new byte[temp.length + token.length];
            System.arraycopy(temp, 0, input, 0, temp.length);
            System.arraycopy(token, 0, input, temp.length, token.length);
            iv = md5.digest(input);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
            AlgorithmParameterSpec paramSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, paramSpec);
            data = cipher.doFinal(this.rawData);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException
                | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
            return null;
        }


        //calculate length
        int length = magic.length + 2 + unknown.length + serial.length + 4 + 16 + data.length;

        // Calculate checksum
        ByteArrayOutputStream checksumOutput = new ByteArrayOutputStream();
        checksumOutput.write(magic);
        checksumOutput.write(ConversionUtils.intToBytes(length),2,2);
        checksumOutput.write(unknown);
        checksumOutput.write(serial);
        checksumOutput.write(ConversionUtils.intToBytes(stamp));
        checksumOutput.write(this.token);
        checksumOutput.write(data);
        md5.reset();
        byte[] checksum = md5.digest(checksumOutput.toByteArray());

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(this.magic);
        out.write(ConversionUtils.intToBytes(length),2,2);
        out.write(unknown);
        out.write(serial);
        out.write(ConversionUtils.intToBytes(stamp));
        out.write(checksum);
        out.write(data);
        return out.toByteArray();
    }





}

