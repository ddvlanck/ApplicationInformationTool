/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package information;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;

/**
 *
 * @author dwigh
 */
public class UserData implements IData {
    
    private static final String USERNAME = "user.name";
    
    public UserData() {}

    @Override
    public Map<String, String> getData() {
        Map<String, String> data = new HashMap<>();
        
        if(!System.getProperty(USERNAME).equals("")){
            data.put("user.name", System.getProperty(USERNAME));
        }
        
        String hostname;
        try {
            hostname = InetAddress.getLocalHost().getCanonicalHostName();
            if(!hostname.equals("")){
                data.put("user.domain", hostname);
            }
        } catch (UnknownHostException ex) {
            Logger.getLogger(UserData.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return data;
    }
    
}
