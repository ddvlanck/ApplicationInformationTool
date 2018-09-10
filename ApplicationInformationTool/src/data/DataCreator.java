/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import information.ApplicationData;
import information.SystemData;
import information.UserData;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
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
        for (Map.Entry<String, String> entry : userData.entrySet()) {
            result.put(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, String> entry : sysData.entrySet()) {
            result.put(entry.getKey(), entry.getValue());
        }
        
        //  TODO : appdata       
        

        return result;
    }
}
