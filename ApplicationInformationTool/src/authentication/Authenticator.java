/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package authentication;

import information.UserData;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;

/**
 *
 * @author dwigh
 */
public class Authenticator {
    
    private static final String URL = "http://example.org";
    private final UserData user;
    
    public Authenticator(UserData user){
        this.user = user;
    }
    
    public void authenticate(){
        Map<String, String> basicData = user.getData();
        try {
            StringBuilder params = new StringBuilder("?");
            for(Object key: basicData.keySet()){
                params.append(URLEncoder.encode(key.toString(), "UTF-8"));
                params.append("=");
                params.append(URLEncoder.encode(basicData.get(key).toString(), "UTF-8"));
                params.append("&");
            }
            params.substring(0, params.length() - 1);
            
            URL url = new URL(URL + params.toString());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Length", "application/json");
            connection.setDoOutput(true);
            //connection.setReadTimeout(15000);
            connection.connect();
            
            //TODO read result from server
            //https://alvinalexander.com/blog/post/java/how-open-url-read-contents-httpurl-connection-java
            
        } catch(MalformedURLException me){
            Logger.getLogger(Authenticator.class.getName()).log(Level.SEVERE, null, me);
        } catch (IOException ex) {
            Logger.getLogger(Authenticator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
