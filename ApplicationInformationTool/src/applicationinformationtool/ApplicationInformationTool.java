/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applicationinformationtool;

import authentication.Authenticator;
import information.SystemData;
import information.UserData;
import org.json.simple.parser.ParseException;

/**
 *
 * @author dwigh
 */
public class ApplicationInformationTool {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        UserData userdata = new UserData();
        SystemData sys = new SystemData();
        Authenticator auth = new Authenticator(userdata, sys);
        System.out.println(sys.getMACAddress());
        try {
            auth.authenticate();
        } catch(ParseException e){
            System.out.println("Exception main ApplicationInformationTool");
        }
    }
    
}
