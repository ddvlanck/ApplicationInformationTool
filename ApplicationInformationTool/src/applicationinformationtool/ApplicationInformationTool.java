/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applicationinformationtool;

import information.SystemData;
import information.UserData;
import org.json.simple.JSONObject;

/**
 *
 * @author dwigh
 */
public class ApplicationInformationTool {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SystemData sysdata = new SystemData();
        UserData userdata = new UserData();
        //ApplicationData appdata = new ApplicationData("file.json");
        
        JSONObject sys = sysdata.getData();
        JSONObject user = userdata.getData();
        //JSONObject app = appdata.getData();
        
        System.out.println("SystemData: " + sys);
        System.out.println("UserData: " + user);
        //System.out.println("AppData: " + app);
    }
    
}
