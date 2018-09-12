/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package softwareupdatetool;

import static com.sun.org.apache.xerces.internal.xinclude.XIncludeHandler.BUFFER_SIZE;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author dwigh
 */
public final class VersionUpdater {

    private String version;

    public VersionUpdater() {
        this.getSoftwareVersion();
    }

    //Reads the software version from the file
    public void getSoftwareVersion() {
        InputStream input = this.getClass().getClassLoader().getResourceAsStream("version");
        BufferedReader br = new BufferedReader(new InputStreamReader(input));
        try {
            this.version = br.readLine();
        } catch (IOException e) {
            Logger.getLogger(VersionUpdater.class.getName()).log(Level.SEVERE, "[VersionUpdate]: problems with reading file.");
        }
    }

    public JSONObject getLatestVersion() {
        JSONObject result = null;
        try {
            URL url = new URL("http://localhost:8080/AIT-REST-maven/ait/client/update");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "text/plain");
            conn.connect();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            result = (JSONObject) new JSONParser().parse(reader.readLine());
        } catch (MalformedURLException e) {
            Logger.getLogger(VersionUpdater.class.getName()).log(Level.SEVERE, "[VersionUpdate]: bad URL.");
        } catch (ParseException e) {
            Logger.getLogger(VersionUpdater.class.getName()).log(Level.SEVERE, "[VersionUpdate]: could not parse result.");
        } catch (IOException e) {
            Logger.getLogger(VersionUpdater.class.getName()).log(Level.SEVERE, "[VersionUpdate]: error opening connection.");
        }

        return result;
    }

    public void downloadNewVersion(String downloadURL) {
        //  Download the new version

        //  DEVELOPING PHASE
        //  There are 2 jar files, and we copy the newest the the folder of the other one and remove the old version
        //  In this case, the downloadURL is the name of the newest jar file
        File file = new File("ApplicationInformationTool_v2");
        System.out.println(file.getAbsolutePath());
        try {
            Runtime.getRuntime().exec("cp " + file.getAbsolutePath() + " C:\\Users\\dwigh\\Documents\\NetBeansProjects\\AIT\\ApplicationInformationTool\\SoftwareUpdateTool\\src\\currentVersion");
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public String getVersion() {
        return version;
    }
}
