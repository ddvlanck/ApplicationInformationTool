/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package information;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.HashMap;
import java.util.Map;
import org.json.simple.JSONObject;

/**
 *
 * @author Dwight
 */
public class SystemData implements IData {

    @Override
    public Map<String, String> getData() {
        Map<String, String> data = new HashMap<>();

        Runtime rt = Runtime.getRuntime();
        long diskSize = new File("/").getTotalSpace();
        String userName = System.getProperty("user.name");
        long maxMemory = Runtime.getRuntime().maxMemory();
        long memorySize = ((com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getTotalPhysicalMemorySize();

        com.sun.management.OperatingSystemMXBean os = (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        long physicalMemorySize = os.getTotalPhysicalMemorySize();
        
        StringBuilder address = new StringBuilder();
        try {
            InetAddress ip = InetAddress.getLocalHost();
            NetworkInterface network = NetworkInterface.getByInetAddress(ip);
            byte[] mac = network.getHardwareAddress();
            for (int i = 0; i < mac.length; i++) {
                address.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        data.put("os.name", System.getProperty("os.name"));
        data.put("os.version", System.getProperty("os.version"));
        data.put("java.version", System.getProperty("java.version"));
        data.put("os.arch", System.getProperty("os.arch"));
        data.put("processors.available", Integer.toString(rt.availableProcessors()));
        data.put("memory.size", Long.toString(memorySize));
        data.put("memory.max", Long.toString(maxMemory));
        data.put("memory.physical", Long.toString(physicalMemorySize));
        data.put("disk.size", Long.toString(diskSize));
        data.put("mac.address", address.toString());

        return data;
    }

}
