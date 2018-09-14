/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package softwareupdatetool;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
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

        //check if file exists
        try {
            File file = new File(filename);
            System.out.println("File " + file.getName() + " is correct.");
        } catch (Exception e) {
            System.out.println("File " + filename + " does not exist.");
            Logger.getLogger(VersionUpdater.class.getName()).log(Level.SEVERE, "[SoftwareUpdateTool]: error, argument file does not exist.");
        }

        VersionUpdater vu = new VersionUpdater(filename);

        //check if folder "applicationinformationtool" exists, if not create
        File dir = new File("applicationinformationtool");
        if (!dir.exists()) {
            //first time downloading
            dir.mkdir();
            vu.downloadNewVersion();

            //add json parsing dependency
            File lib = new File("lib");
            File json = new File("lib/json-simple-1.1.1.jar");
            try {
                Files.copy(lib.toPath(), Paths.get(dir.toPath() + "/lib"));
                Files.copy(json.toPath(), Paths.get(dir.toPath() + "/lib/json-simple-1.1.1.jar"));
            } catch (IOException e) {
                Logger.getLogger(VersionUpdater.class.getName()).log(Level.SEVERE, "[SoftwareUpdateTool]: could not copy library.");
            }

        } else {
            //update
            String latestVersion = vu.getLatestSoftwareVersion();
            if (latestVersion != null && !vu.getVersion().equals(latestVersion)) {
                //  Download the newest version
                vu.downloadNewVersion();

                //  Delete oldest version
                vu.deleteOldestVersion();
            }
        }

        //  We create a new file for the application data, if the version has changed, it will be in this new file
        //  If nothing has changed, this new file will be the same as the old file.
        File appData = vu.updateVersionFile();

        //  Start program
        //  There will always be only one file in the folder /applicationinformationtool
        File program = new File("applicationinformationtool").listFiles()[0];

        try {
            Process ps = Runtime.getRuntime().exec(new String[]{"java", "-jar", program.getAbsolutePath(), "file.txt"});

            InputStream is = ps.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }

            InputStream er = ps.getErrorStream();
            BufferedReader berr = new BufferedReader(new InputStreamReader(er));
            String errline;
            while ((errline = berr.readLine()) != null) {
            Logger.getLogger(VersionUpdater.class.getName()).log(Level.SEVERE, "[SoftwareUpdateTool]: " + errline);
            }

        } catch (IOException e) {
            Logger.getLogger(VersionUpdater.class.getName()).log(Level.SEVERE, "[SoftwareUpdateTool]: problem while starting the application information tool.");
        }
    }

}
