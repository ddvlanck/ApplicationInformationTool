/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applicationinformationtool;

import authentication.Authenticator;
import data.DataCreator;
import information.ApplicationData;
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

        System.out.println("[ApplicationInformationTool] Starting tool");

        // Production phase:
        //String filename = args[0];
        
        //Developher phase
        String filename = "file.txt";
        
        UserData user = new UserData();
        SystemData sys = new SystemData();
        ApplicationData app = new ApplicationData(filename);
        Authenticator auth = new Authenticator(user, sys);
        try {
            String authKey = auth.authenticate();

            if (authKey != null) {

                System.out.println("[ApplicationInformationTool] Authentication Succesful.");

                DataCreator dc = new DataCreator(user, sys, app);
                dc.postData(authKey);

                System.out.println("[ApplicationInformationTool] Done.");

            }
            System.exit(0);
        } catch (ParseException e) {
            System.out.println("Exception main ApplicationInformationTool");
        }

    }

}
