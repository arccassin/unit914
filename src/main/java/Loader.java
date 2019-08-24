import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

/**
 * Created by User on 24 Авг., 2019
 */
public class Loader {
    public static void main(String[] args) {
        ArrayList<String> lineNameList = new ArrayList<>();
        JSONParser jsonParser = new JSONParser();

        try (Reader reader = new FileReader("src/main/resources/file.json")) {
            JSONObject jsonData = (JSONObject) jsonParser.parse(reader);
            JSONArray jsonArrayLines = (JSONArray) jsonData.get("Lines");
            for ( Object lineObj: jsonArrayLines) {
                JSONObject jsonObjectLine = (JSONObject) lineObj;
                lineNameList.add((String)(jsonObjectLine.get("number")));
            }
            JSONObject jsonObjectStations = (JSONObject) jsonData.get("stations");
            for (String number: lineNameList) {
                JSONArray jsonArrayStations = (JSONArray) jsonObjectStations.get(number);
                System.out.printf("Line number %8s station count = %4d%n", "\"" + number + "\"", jsonArrayStations.size());
            }

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }
}


//  },
//          "Lines":[
//          {
//          "number":"11",
//          "name":"Большая кольцевая линия"
//          },