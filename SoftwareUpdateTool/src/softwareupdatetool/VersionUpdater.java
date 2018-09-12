/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package softwareupdatetool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    public void setSoftwareVersion(String version) {
        File fnew = new File(this.getClass().getResource("/version").getPath());
        FileWriter f2;

        try {
            f2 = new FileWriter(fnew, false);
            f2.write(version);
            f2.close();
        } catch (IOException e) {
            e.printStackTrace();
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
        try {
            URL url = this.getClass().getResource("/newVersion/ApplicationInformationTool_v2.jar");
            URL dir = this.getClass().getResource("/currentVersion");
            System.out.println("Url: " + url.getPath() + " \ndir: " + dir.getPath());

            //remove old version
            URL old = this.getClass().getResource("/currentVersion/ApplicationInformationTool.jar");
            if (old.getPath() != null) {
                System.out.println("old: " + old.getPath());
                Files.deleteIfExists(Paths.get(old.getPath()));
            }

            //copy new version to currentVersion directory
            Path temp = Files.copy(Paths.get(url.getPath()), Paths.get(dir.getPath() + "/ApplicationInformationTool.jar"));

            if (temp != null) {
                System.out.println("File moved successfully");
                this.setSoftwareVersion("2.0");
            } else {
                System.out.println("Failed to move the file");
            }

        } catch (IOException ex) {
            Logger.getLogger(VersionUpdater.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getVersion() {
        return version;
    }
}
