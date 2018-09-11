/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ait.rest;

import ait.azure.Connector;
import com.microsoft.azure.datalake.store.ADLStoreClient;
import com.microsoft.azure.datalake.store.IfExists;
import com.microsoft.azure.datalake.store.oauth2.AccessTokenProvider;
import com.microsoft.azure.datalake.store.oauth2.AzureADToken;
import com.microsoft.azure.datalake.store.oauth2.ClientCredsTokenProvider;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * REST Web Service
 *
 * @author Dwight
 */
@Path("client")
public class AITResource {

    private static String accountFQDN = "datalakeinterns.azuredatalakestore.net";


    @Context
    private UriInfo context;

    /**
     * Creates a new instance of AITResource
     */
    public AITResource() {
    }

    /**
     * Retrieves representation of an instance of rest.AITResource
     *
     * @param content
     * @return an instance of java.lang.String
     */
    @POST
    @Path("authentication")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String authenticate(String content) {
        JSONObject result = new JSONObject();

        try {
            JSONObject data = (JSONObject) new JSONParser().parse(content);
            String userdomain = data.get("user.domain").toString();
            String MAC = data.get("mac.address").toString();

            try {
                Connector connector = new Connector();
                Connection con = connector.getConnection();
                PreparedStatement pst = con.prepareStatement("SELECT * FROM COMPANIES WHERE DOMAIN like ?");
                pst.setString(1, "AUTOMOBILIA"); //  CHANGE TO USERDOMAIN
                ResultSet rs = pst.executeQuery();

                Boolean allow = false;
                int id = -1;
                while (rs.next()) {
                    allow = rs.getBoolean("ALLOW");
                    id = rs.getInt("ID");
                }
                pst.close();

                if (allow) {
                    pst = con.prepareStatement("SELECT AUTHKEY FROM COMPUTERS WHERE MAC like ? AND COMPANYID like ? AND ALLOW='True'");
                    pst.setString(1, MAC); //  CHANGE TO MAC
                    pst.setInt(2, id);
                    rs = pst.executeQuery();

                    String authKey = "";
                    while (rs.next()) {
                        authKey = rs.getString("AUTHKEY");
                    }

                    pst.close();

                    if (authKey == null) {
                        SecureRandom rnd = new SecureRandom();
                        byte[] token = new byte[12];
                        rnd.nextBytes(token);
                        authKey = new BigInteger(1, token).toString(16);

                        pst = con.prepareStatement("UPDATE computers SET authkey = ? WHERE MAC like ? and companyid like ?");
                        pst.setString(1, authKey);
                        pst.setString(2, MAC); //  CHANGE TO MAC
                        pst.setInt(3, id);
                        pst.executeUpdate();
                        pst.close();
                    }

                    result.put("AuthKey", authKey);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result.toJSONString();
    }

    @POST
    @Path("data")
    @Consumes(MediaType.APPLICATION_JSON)
    public void sendData(@HeaderParam("AuthKey") String key, @HeaderParam("MAC") String mac, String content) {
        System.out.println("Iets");
        Connector connector = new Connector();
        Connection conn = connector.getConnection();
        try {
            PreparedStatement pst = conn.prepareStatement("SELECT ALLOW FROM COMPUTERS WHERE MAC = ? AND AUTHKEY = ?");
            pst.setString(1, mac);
            pst.setString(2, key);
            ResultSet rs = pst.executeQuery();
            rs.next();
            Boolean allow = rs.getBoolean("ALLOW");
            if (allow) {
                JSONObject data = (JSONObject) new JSONParser().parse(content);
                
                AccessTokenProvider provider = new ClientCredsTokenProvider("https://login.microsoftonline.com/863ddd56-6f86-4d42-a0c4-b52fbd6d288d/oauth2/token", "20fdfcd2-8440-4b5d-9270-1c6cdd7ae068", "PB62AG6w6uEQUPRAGYfvz5pVJ6TzNhc3jO8XDQ2R3Lo=");
                
                ADLStoreClient client = ADLStoreClient.createClient(accountFQDN, provider);
                
                //System.out.println(data.get("user.domain").toString() + "/" + data.get("mac.address") + "/" + data.get("user.name"));
                client.createDirectory(data.get("user.domain").toString() + "/" + data.get("mac.address") + "/" + data.get("user.name"));
                
                String filename = data.get("user.domain").toString() + "/" + data.get("mac.address") + "/" + data.get("user.name") + "/" + new SimpleDateFormat("yyyy-MM-dd-HH_mm_ss").format(new Date());
                OutputStream stream = client.createFile(filename, IfExists.OVERWRITE);
                stream.write(content.getBytes());
                stream.flush();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        //  TODO :
        //  Create file from content and send it to Azure Data Lake
        //Example to create JSON file"
        /*File file = new File(domain + "-" + mac + "-" + username + "-" + ts);
        try {
            FileWriter fw = new FileWriter(file);
            fw.write(result.toJSONString());
            fw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

}
