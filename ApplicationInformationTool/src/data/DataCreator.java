/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import authentication.Authenticator;
import information.ApplicationData;
import information.SystemData;
import information.UserData;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;

/**
 *
 * @author Dwight
 */
public class DataCreator {

    private Properties prop;
    private UserData user;
    private SystemData sys;
    private ApplicationData app;

    public DataCreator(UserData user, SystemData sys, ApplicationData app) {
        prop = new Properties();
        try {
            prop.load(getClass().getClassLoader().getResourceAsStream("config.properties"));
        } catch (IOException e) {
            Logger.getLogger(Authenticator.class.getName()).log(Level.SEVERE, "[DataCreator]: could not load properties file");
        }
        
        this.user = user;
        this.sys = sys;
        this.app = app;
    }

    public void postData(String authKey) {
        try {
            JSONObject data = this.createDataObject();
            URL url = new URL(prop.getProperty("DATA_URL"));
            
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("AuthKey", authKey);
            conn.setRequestProperty("MAC", sys.getMACAddress());
            
            OutputStream os = conn.getOutputStream();
            os.write(data.toJSONString().getBytes());
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            
            conn.disconnect();
            
            System.out.println("[DataCreator] Sent information.");
            
        } catch (MalformedURLException ex) {
            Logger.getLogger(DataCreator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ProtocolException ex) {
            Logger.getLogger(DataCreator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DataCreator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public JSONObject createDataObject() {

        Map<String, String> userData = user.getData();
        Map<String, String> sysData = sys.getData();
        Map<String, String> appData = app.getData();

        JSONObject result = new JSONObject();
        userData.entrySet().forEach((entry) -> {
            result.put(entry.getKey(), entry.getValue());
        });

        sysData.entrySet().forEach((entry) -> {
            result.put(entry.getKey(), entry.getValue());
        });

        appData.entrySet().forEach((entry) -> {
            result.put(entry.getKey(), entry.getValue());
        });
        
        System.out.println("[DataCreator] Data Created.");
        
        return result;
    }
}
