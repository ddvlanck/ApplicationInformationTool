/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest;

import azure.Connector;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
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
                pst.setString(1, "SAVACO"); //  CHANGE TO USERDOMAIN
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
                    System.out.println(authKey);

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
    public void sendData(@HeaderParam("AuthKey") String key, String content) {

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
