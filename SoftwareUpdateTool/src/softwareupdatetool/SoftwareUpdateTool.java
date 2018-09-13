/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package softwareupdatetool;

import java.io.File;
import java.io.IOException;
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
        String filename = args.length > 0 ? args[0] : "applicationdata";

        // TODO code application logic here
        VersionUpdater vu = new VersionUpdater(filename);
        String latestVersion = vu.getLatestSoftwareVersion();
        if (latestVersion != null && !vu.getVersion().equals(latestVersion)) {
            //  Download the newest version
            vu.downloadNewVersion();

            //  Delete oldest version
            vu.deleteOldestVersion();
        }

        //  We create a new file for the application data, if the version has changed, it will be in this new file
        //  If nothing has changed, this new file will be the same as the old file.
        File appData = vu.updateVersionFile();

        //  Start program
        //  There will always be only one file in the folder /applicationinformationtool
        File program = new File("src/applicationinformationtool").listFiles()[0];
        
        try {
            Runtime.getRuntime().exec("java -jar " + program.getAbsolutePath() + " " + appData.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
