/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package authentication;

import data.DataCreator;
import information.UserData;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
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

    private static final String authenticationURL = "http://localhost:8080/AIT-REST/ait/client/authentication";
    private static final String dataURL = "http://localhost:8080/AIT-REST/ait/client/data";
    private final UserData user;

    private String AUTHENTICATION_KEY;

    public Authenticator(UserData user) {
        this.user = user;
    }

    public void authenticate() {
        Map<String, String> basicData = user.getData();
        try {
            JSONObject body = new JSONObject();
            for (Map.Entry<String, String> entry : basicData.entrySet()) {
                body.put(entry.getKey(), entry.getValue());
            }

            URL url = new URL(authenticationURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");

            OutputStream os = conn.getOutputStream();
            os.write(body.toJSONString().getBytes());
            os.flush();

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

            conn.disconnect();

            //  TODO
            //  if an authentication key was received from the server, send more data with the authentication key
            //  to new URL : 'http://localhost:8080/AIT-REST/ait/client/data
            
            this.AUTHENTICATION_KEY = "";

            if (!this.AUTHENTICATION_KEY.equals("")) {

                //  QUESTION : Send authentication key ?
                DataCreator fc = new DataCreator();
                JSONObject data = fc.createDataObject();

                url = new URL(dataURL);

                conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");

                os = conn.getOutputStream();
                os.write(data.toJSONString().getBytes());
                br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

                conn.disconnect();
            }

        } catch (MalformedURLException me) {
            Logger.getLogger(Authenticator.class.getName()).log(Level.SEVERE, null, me);
        } catch (IOException ex) {
            Logger.getLogger(Authenticator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
