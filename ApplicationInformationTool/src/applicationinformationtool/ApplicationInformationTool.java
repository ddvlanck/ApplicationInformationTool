/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applicationinformationtool;

<<<<<<< HEAD
import information.ApplicationData;
import information.SystemData;
import information.UserData;
import java.util.Map;
=======
import information.UserData;
>>>>>>> ca9fba1a43777b735173f999af2099f8f493f2f4
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
        //JSONObject user = userdata.getData();
        //JSONObject app = appdata.getData();
        
        System.out.println("SystemData: " + sys);
        //System.out.println("UserData: " + user);
        //System.out.println("SystemData: " + sys);
    }
    
}
