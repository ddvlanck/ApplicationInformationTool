/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import information.SystemData;
import information.UserData;
import java.util.Map;
import org.json.simple.JSONObject;

/**
 *
 * @author Dwight
 */
public class DataCreator {

    public JSONObject createDataObject() {
        UserData user = new UserData();
        SystemData sys = new SystemData();
        //ApplicationData app = new ApplicationData("file.json");

        Map<String, String> userData = user.getData();
        Map<String, String> sysData = sys.getData();

        JSONObject result = new JSONObject();
        userData.entrySet().forEach((entry) -> {
            result.put(entry.getKey(), entry.getValue());
        });

        sysData.entrySet().forEach((entry) -> {
            result.put(entry.getKey(), entry.getValue());
        }); 

        //  TODO : appdata       

        return result;
    }
}
