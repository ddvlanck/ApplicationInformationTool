/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package information;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

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
            while ((line = br.readLine()) != null) {
                content.append(line);
            }
        } catch (IOException e) {
            //Logger.getLogger(ApplicationData.class.getName()).log(Level.SEVERE, null, "[APPLICATION_DATA]: could not parse file '" + file + "'.");
            System.out.println("[APPLICATION_DATA]: could not parse file '" + file + "'.");
        }
        return content.toString();
    }

    @Override
    public Map<String, String> getData() {
        Map<String, String> data = new HashMap<>();
        data.put("applicationdata", this.fileContent);
        return data;
    }

}
