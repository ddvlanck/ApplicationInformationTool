/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package authentication;

import information.SystemData;
import information.UserData;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author dwigh
 */
public class Authenticator {

    private final UserData user;
    private final SystemData sys;
    private Properties prop;

    private String AUTHENTICATION_KEY;

    public Authenticator(UserData user, SystemData sys) {
        prop = new Properties();
        try {
            prop.load(getClass().getClassLoader().getResourceAsStream("config.properties"));
        } catch (IOException e) {
            Logger.getLogger(Authenticator.class.getName()).log(Level.SEVERE, "[AUTHENTICATOR]: could not load properties file");
        }

        this.user = user;
        this.sys = sys;
    }

    public String authenticate() throws ParseException {
        try {
            System.out.println("[Authenticator] Start authentication...");
            
            JSONObject body = new JSONObject();
            body.put("user.domain", user.getUserDomain());
            body.put("mac.address", sys.getMACAddress());

            URL url = new URL(prop.getProperty("AUTHENTICATION_URL"));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");

            OutputStream os = conn.getOutputStream();
            os.write(body.toJSONString().getBytes());
            os.flush();

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            try {
                JSONObject result = (JSONObject) new JSONParser().parse(br.readLine());
                this.AUTHENTICATION_KEY = (String) result.get("AuthKey");
            } catch (IOException | ParseException e) {
                Logger.getLogger(Authenticator.class.getName()).log(Level.SEVERE, "[AUTHENTICATOR]: could not parse JSONObject from inputstream");
            }

            conn.disconnect();
            System.out.println("[Authenticator] Authenticated with authKey: " + this.AUTHENTICATION_KEY);
            return this.AUTHENTICATION_KEY;
            
        } catch (MalformedURLException me) {
            Logger.getLogger(Authenticator.class.getName()).log(Level.SEVERE, "[AUTHENTICATOR]: malformed URL");
        } catch (IOException ex) {
            Logger.getLogger(Authenticator.class.getName()).log(Level.SEVERE, "[AUTHENTICATOR]: problem with input/output");
        }
        System.out.println("[Authenticator] Permission denied.");
        return null;
    }
}
