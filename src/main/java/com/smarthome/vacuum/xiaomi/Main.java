package com.smarthome.vacuum.xiaomi;

import com.smarthome.xiaomi.Device;
import com.smarthome.xiaomi.Manager;

public class Main {
    public static void main(String[] args) {
        try {
            Manager manager = new Manager();
            manager.start();
            manager.discover();

            Thread.sleep(1000000000);
        } catch (Exception e) {
            System.out.println("Cannot run device. Details "+ e.getMessage() );
        }
    }
}
