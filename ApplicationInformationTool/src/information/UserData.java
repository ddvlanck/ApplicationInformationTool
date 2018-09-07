/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package information;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
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
        
        if(!System.getenv(USERDOMAIN).equals("")){
            userData.put("userdomain", System.getenv(USERDOMAIN));
        }
        
        return userData;
    }
    
}
