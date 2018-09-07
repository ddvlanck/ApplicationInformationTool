/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package information;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import org.json.simple.JSONObject;

/**
 *
 * @author Dwight
 */
public class ApplicationData implements IData {

    private final String fileContent;

    public ApplicationData(String file) {
        this.fileContent = this.parseFile(file);
    }

    private String parseFile(String file) {
        StringBuilder content = new StringBuilder();
        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            
            String line;
            while( (line = br.readLine()) != null){
                content.append(line);
            }
        } catch(IOException e){
            System.out.println("ERROR : cannot parse file '" + file + "'.");
        }
        return content.toString();
    }

    @Override
    public JSONObject getData() {
        JSONObject appData = new JSONObject();
        appData.put("applicationdata", this.fileContent);
        return appData;
    }

}
