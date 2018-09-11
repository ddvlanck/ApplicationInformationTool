/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ait.rest;

import ait.azure.Connector;
import ait.azure.QueryCreator;
import com.microsoft.azure.datalake.store.ADLStoreClient;
import com.microsoft.azure.datalake.store.IfExists;
import com.microsoft.azure.datalake.store.oauth2.AccessTokenProvider;
import com.microsoft.azure.datalake.store.oauth2.ClientCredsTokenProvider;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import org.json.simple.parser.ParseException;

/**
 * REST Web Service
 *
 * @author Dwight
 */
@Path("client")
public class AITResource {

    private static String accountFQDN = "datalakeinterns.azuredatalakestore.net";

    private QueryCreator queryCreator;
    private Connector connector;

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of AITResource
     */
    public AITResource() {
        this.connector = new Connector();
        this.queryCreator = new QueryCreator(connector);
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
                ResultSet rs = this.queryCreator.getCompanyInformation(userdomain);

                Boolean allow = false;
                int id = -1;
                while (rs.next()) {
                    allow = rs.getBoolean("ALLOW");
                    id = rs.getInt("ID");
                }

                rs.close();

                if (allow) {
                    rs = this.queryCreator.getAuthenticationKey(MAC, id);

                    String authKey = null;
                    while (rs.next()) {
                        authKey = rs.getString("AUTHKEY");
                    }

                    rs.close();

                    if (authKey == null) {
                        SecureRandom rnd = new SecureRandom();
                        byte[] token = new byte[12];
                        rnd.nextBytes(token);
                        authKey = new BigInteger(1, token).toString(16);

                        this.queryCreator.insertAuthenticationKey(authKey, MAC, id);
                    }

                    result.put("AuthKey", authKey);
                }

            } catch (SQLException e) {
                Logger.getLogger(AITResource.class.getName()).log(Level.SEVERE, "[REST_API]: error when authenticating.");

            }

        } catch (ParseException e) {
            Logger.getLogger(AITResource.class.getName()).log(Level.SEVERE, "[REST_API]: error when parsing the received content.");

        }

        return result.toJSONString();
    }

    @POST
    @Path("data")
    @Consumes(MediaType.APPLICATION_JSON)
    public void sendData(@HeaderParam("AuthKey") String key, @HeaderParam("MAC") String mac, String content) {
        try {
            ResultSet rs = this.queryCreator.getComputerPermissions(mac, key);
            if (rs != null) {
                rs.next();
                Boolean allow = rs.getBoolean("ALLOW");
                rs.close();
                if (allow) {
                    JSONObject data = (JSONObject) new JSONParser().parse(content);

                    AccessTokenProvider provider = new ClientCredsTokenProvider("https://login.microsoftonline.com/863ddd56-6f86-4d42-a0c4-b52fbd6d288d/oauth2/token", "20fdfcd2-8440-4b5d-9270-1c6cdd7ae068", "PB62AG6w6uEQUPRAGYfvz5pVJ6TzNhc3jO8XDQ2R3Lo=");
                    ADLStoreClient client = ADLStoreClient.createClient(accountFQDN, provider);
                    client.createDirectory(data.get("user.domain").toString() + "/" + data.get("mac.address") + "/" + data.get("user.name"));

                    String filename = data.get("user.domain").toString() + "/" + data.get("mac.address") + "/" + data.get("user.name") + "/" + new SimpleDateFormat("yyyy-MM-dd-HH_mm_ss").format(new Date());
                    OutputStream stream = client.createFile(filename, IfExists.OVERWRITE);
                    stream.write(content.getBytes());
                    stream.flush();
                }
            }

        } catch (IOException | SQLException | ParseException e) {
            Logger.getLogger(AITResource.class.getName()).log(Level.SEVERE, "[REST_API]: error sending data.");
        }
    }

}
