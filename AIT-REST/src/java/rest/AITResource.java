/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest;
import azure.Connector;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
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
    
    @GET
    @Path("test")
    @Produces(MediaType.TEXT_PLAIN)
    public String testJSON(){
        Connector con = new Connector();
        con.getConnection();
        System.out.println("Arne");
        return "Arne is bere goed";
    }

    /**
     * Retrieves representation of an instance of rest.AITResource
     * @param content
     * @return an instance of java.lang.String
     */
    @POST
    @Path("authentication")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    public String authenticate(String content) {
        try {
            JSONObject data = (JSONObject) new JSONParser().parse(content);
            String username = data.get("user.name").toString();
            String userdomain = data.get("user.domain").toString();
            
            //  TODO
            //  check database in Azure to see if the values are present.
            //  if so, send back the authentication key
            
        } catch(Exception e){
            e.printStackTrace();
        }
        return "OK";
    }
    
    @POST
    @Path("data")
    @Consumes(MediaType.APPLICATION_JSON)
    public void sendData(String content) {
        System.out.println(content);
        
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
