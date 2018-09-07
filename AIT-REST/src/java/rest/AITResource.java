/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.core.MediaType;

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
     * @param content
     * @return an instance of java.lang.String
     */
    @POST
    @Path("authentication")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    public String authenticate(String content) {
        //TODO return proper representation object
        System.out.println(content);
        return "POST";
    }
    
    @POST
    @Path("data")
    @Consumes(MediaType.APPLICATION_JSON)
    public void sendData(String content) {
        //TODO return proper representation object
        System.out.println("GET SUCCEED");
    }
    
}
