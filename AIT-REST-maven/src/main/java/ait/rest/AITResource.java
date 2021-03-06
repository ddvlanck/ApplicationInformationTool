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
import static com.sun.org.apache.xerces.internal.xinclude.XIncludeHandler.BUFFER_SIZE;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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
    private ADLStoreClient client;

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of AITResource
     */
    public AITResource() {
        this.connector = new Connector();
        this.queryCreator = new QueryCreator(connector);
        AccessTokenProvider provider = new ClientCredsTokenProvider("https://login.microsoftonline.com/863ddd56-6f86-4d42-a0c4-b52fbd6d288d/oauth2/token", "20fdfcd2-8440-4b5d-9270-1c6cdd7ae068", "PB62AG6w6uEQUPRAGYfvz5pVJ6TzNhc3jO8XDQ2R3Lo=");
        this.client = ADLStoreClient.createClient(accountFQDN, provider);
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
                //  Get information about the company - if the company has an ALLOW='True', we process the computer itself
                ResultSet rs = this.queryCreator.getCompanyInformation(userdomain);

                Boolean allow = false;
                int id = -1;
                while (rs.next()) {
                    allow = rs.getBoolean("ALLOW");
                    id = rs.getInt("ID");
                }

                rs.close();

                //  Company is allowed - process the computer itself
                if (allow) {
                    rs = this.queryCreator.getComputerInformation(MAC, id);
                    //rs = this.queryCreator.getComputerInformation("10-00-00-00-00-00", 1);

                    //  The computer exists - extract the necessary information
                    if (rs.isBeforeFirst()) {
                        String authKey = null;
                        Boolean allowPc = false;
                        while (rs.next()) {
                            authKey = rs.getString("AUTHKEY");
                            allowPc = rs.getBoolean("ALLOW");
                        }

                        if (allowPc && authKey == null) {
                            //  Generate authKey
                            authKey = this.generateAuthenticationKey();
                            this.queryCreator.insertAuthenticationKey(authKey, MAC, id);
                            result.put("AuthKey", authKey);
                        } else if (allowPc && authKey != null) {
                            result.put("AuthKey", authKey);
                        } else {
                            //  Send basic data to unauthorized file
                            System.out.println("SENDING DATA TO FILE");
                            this.sendBasicData(data);
                            result.put("AuthKey", null);
                        }

                    } else {    //  Computer doesn't exist - add it
                        String authKey = this.generateAuthenticationKey();
                        this.queryCreator.addComputer(MAC, id, authKey);
                        result.put("AuthKey", authKey);
                    }
                } else {
                    System.out.println("DATA SEND");
                    this.sendBasicData(data);
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

                    this.client.createDirectory(data.get("user.domain").toString() + "/" + data.get("mac.address") + "/" + data.get("user.name"));

                    String filename = data.get("user.domain").toString() + "/" + data.get("mac.address") + "/" + data.get("user.name") + "/" + new SimpleDateFormat("yyyy-MM-dd-HH_mm_ss").format(new Date());
                    OutputStream stream = this.client.createFile(filename, IfExists.OVERWRITE);
                    stream.write(content.getBytes());
                    stream.flush();
                }
            }

        } catch (IOException | SQLException | ParseException e) {
            Logger.getLogger(AITResource.class.getName()).log(Level.SEVERE, "[REST_API]: error sending data.");
        }
    }

    @GET
    @Path("update")
    @Produces(MediaType.APPLICATION_JSON)
    public String sendJson() {
        //  Here we need to access Azure Data Lake > ApplicationTool > version
        //  In that file, the latest version number can be found
        JSONObject versie = new JSONObject();
        try {
            InputStream in = this.client.getReadStream("ApplicationTool/version");
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String version = reader.readLine();
            versie.put("version", version);
        } catch(IOException e){
            Logger.getLogger(AITResource.class.getName()).log(Level.SEVERE, "[REST_API]: could not retrieve version from Azure Data Lake");
        }
        return versie.toJSONString();
    }
    
    @GET
    @Path("downloadUpdate")
    @Produces(MediaType.MULTIPART_FORM_DATA)
    public InputStream sendVersion(){
        InputStream input = null;
        try {
            input = this.client.getReadStream("ApplicationTool/ApplicationInformationTool.jar");
        } catch(IOException e){
            e.printStackTrace();
        }
        return input;
        
        /*JSONObject version = new JSONObject();
        File file = new File("ApplicationInformationTool.jar");
        try {
            InputStream in = this.client.getReadStream("ApplicationTool/ApplicationInformationTool.jar");
            FileOutputStream output = new FileOutputStream(file);
            int bytesRead = -1;
            byte[] buffer = new byte[Integer.parseInt(BUFFER_SIZE)];
            while( (bytesRead = in.read(buffer)) != -1){
                output.write(buffer, 0, bytesRead);
            }
            in.close();
            output.close();
        } catch(IOException e){
            Logger.getLogger(AITResource.class.getName()).log(Level.SEVERE, "[REST_API]: could not retrieve version from Azure Data Lake");
        }
        return Response.ok(file, MediaType.MULTIPART_FORM_DATA).header("Content-Disposition", "attachment; file=\"" + file.getName() + "\"").build();*/
    }

    private void sendBasicData(JSONObject data) {
        try {
            OutputStream stream = this.client.getAppendStream("unauthorized_client_information.log");
            data.put("Timestamp", new SimpleDateFormat("yyyy-MM-dd-HH_mm_ss").format(new Date()));
            byte[] buffer = data.toJSONString().getBytes();
            stream.write(buffer);
            stream.write(new String("\n").getBytes());
            stream.close();
        } catch (IOException e) {
            //e.printStackTrace();
            Logger.getLogger(AITResource.class.getName()).log(Level.SEVERE, "[REST_API]: error appending data to the 'unauthorized client information' file.");
        }
    }

    private String generateAuthenticationKey() {
        SecureRandom rnd = new SecureRandom();
        byte[] token = new byte[12];
        rnd.nextBytes(token);
        return new BigInteger(1, token).toString(16);
    }

}
