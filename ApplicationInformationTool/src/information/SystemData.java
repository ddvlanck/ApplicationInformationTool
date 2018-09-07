/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package information;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import org.json.simple.JSONObject;

/**
 *
 * @author Dwight
 */
public class SystemData implements IData {

    @Override
    public JSONObject getData() {
        JSONObject data = new JSONObject();
        
        Runtime rt = Runtime.getRuntime();
        long diskSize = new File("/").getTotalSpace();
        String userName = System.getProperty("user.name");
        long maxMemory = Runtime.getRuntime().maxMemory();
        long memorySize = ((com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getTotalPhysicalMemorySize();
        
        com.sun.management.OperatingSystemMXBean os = (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        long physicalMemorySize = os.getTotalPhysicalMemorySize();

        
        data.put("os.name", System.getProperty("os.name"));
        data.put("os.version", System.getProperty("os.version"));
        data.put("java.version", System.getProperty("java.version"));
        data.put("os.arch", System.getProperty("os.arch"));
        data.put("processors.available", rt.availableProcessors());
        data.put("memory.size", memorySize);
        data.put("memory.max", maxMemory);
        data.put("memory.physical", physicalMemorySize);
        data.put("disk.size", diskSize);
        
        return data;
    }
    
}
