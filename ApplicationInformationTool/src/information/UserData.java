/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package information;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
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
    private static final String USERDOMAIN = "USERDOMAIN";
    
    public UserData() {}

    @Override
    public JSONObject getData() {
        JSONObject userData = new JSONObject();
        
        if(!System.getProperty(USERNAME).equals("")){
            userData.put("username", System.getProperty(USERNAME));
        }
        
        String hostname;
        try {
            hostname = InetAddress.getLocalHost().getCanonicalHostName();
            if(!hostname.equals("")){
                userData.put("userdomain", hostname);
            }
        } catch (UnknownHostException ex) {
            Logger.getLogger(UserData.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return userData;
    }
    
}
