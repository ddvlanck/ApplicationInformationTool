/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package softwareupdatetool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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
    private JSONObject appdata;

    public VersionUpdater(String filename) {
        this.appdata = this.parseApplicationData(filename);
        this.version = this.getCurrentSoftwareVersion();
    }

    private JSONObject parseApplicationData(String filename) {
        InputStream input = this.getClass().getClassLoader().getResourceAsStream(filename);
        BufferedReader br = new BufferedReader(new InputStreamReader(input));
        JSONObject result = new JSONObject();
        try {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            result = (JSONObject) new JSONParser().parse(sb.toString());
        } catch (IOException e) {
            Logger.getLogger(VersionUpdater.class.getName()).log(Level.SEVERE, "[VersionUpdate]: problems reading application data file.");
        } catch (ParseException e) {
            Logger.getLogger(VersionUpdater.class.getName()).log(Level.SEVERE, "[VersionUpdate]: problem parsing data to JSON.");
        }
        return result;
    }

    private String getCurrentSoftwareVersion() {
        return this.appdata.get("version").toString();
    }

    public String getLatestSoftwareVersion() {
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

        return result.get("version").toString();
    }

    public void downloadNewVersion() {
        //  Download the new version
        try {
            URL url = new URL("http://localhost:8080/AIT-REST-maven/ait/client/downloadUpdate");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "multipart/form-data");
            conn.connect();

            File file = new File("applicationinformationtool/ApplicationInformationTool.jar");
            FileOutputStream output = new FileOutputStream(file);
            InputStream in = conn.getInputStream();
            int bytesRead = -1;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
            in.close();
            output.close();

        } catch (MalformedURLException e) {
            Logger.getLogger(VersionUpdater.class.getName()).log(Level.SEVERE, "[VersionUpdater]: download url is not correct.");
        } catch (IOException e) {
            Logger.getLogger(VersionUpdater.class.getName()).log(Level.SEVERE, "[VersionUpdater]: problem while downloading the newest software version.");
        }
    }

    public File updateVersionFile() {
        this.appdata.put("version", this.getLatestSoftwareVersion());
        File file = new File("modifiedappdata.txt");
        try {
            FileOutputStream output = new FileOutputStream(file);
            output.write(this.appdata.toJSONString().getBytes());
        } catch (IOException e) {
            Logger.getLogger(VersionUpdater.class.getName()).log(Level.SEVERE, "[VersionUpdater]: problem creating the modified app data file.");
        }
        return file;
    }

    public void deleteOldestVersion() {
        File folder = new File("applicationinformationtool");
        File[] files = folder.listFiles();
        
        int fileCount = 0;
        File oldest = null;
        for(File file : files){
            if(file.isFile()){
                fileCount++;
                if(oldest == null || file.lastModified() < oldest.lastModified()){
                    oldest = file;
                }
            }            
        }
        if(fileCount > 1 && oldest != null){
            oldest.delete();
        }
        
    }

    public String getVersion() {
        return version;
    }
}
