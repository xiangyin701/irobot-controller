package com.smarthome.vacuum.xiaomi;

import com.smarthome.utils.ConversionUtils;
import com.smarthome.xiaomi.Device;
import com.smarthome.xiaomi.Manager;

public class Main {


    public static void main(String[] args) {
        try {
            byte[] token = ConversionUtils.hexStringToByteArray("77614667465362784859315474703736");
            Device device = new Device("192.168.0.18", token);
            device.locateMe();
        } catch (Exception e) {
            System.out.println("Cannot run device. Details "+ e.getMessage() );
        }
    }
}
