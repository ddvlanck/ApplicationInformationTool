/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package softwareupdatetool;

import org.json.simple.JSONObject;

/**
 *
 * @author dwigh
 */
public class SoftwareUpdateTool {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        VersionUpdater vu = new VersionUpdater();
        JSONObject result = vu.getLatestVersion();
        if(result != null){
            //String latestVersion = result.get("Version").toString();
            String latestVersion = "2.0";
            if(!vu.getVersion().equals(latestVersion)){
                System.out.println("OKE");
                //  Download the newest version
                vu.downloadNewVersion(result.get("URL").toString());
                
                //  Delete old version
                //TODO
            }
        }
        
        //  START PROGRAM
        //TODO
    }
    
}

