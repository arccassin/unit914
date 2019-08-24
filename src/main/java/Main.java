import Core.Line;
import Core.Station;
import com.cedarsoftware.util.io.JsonWriter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by User on 19 Авг., 2019
 */
public class Main {
    static HashMap<String, Line> number2line;

    public static void main(String[] args) {
        String url = "src/main/resources/station.html";
        String jsonStrPath = "src/main/resources/file.json";
        File input = new File(url);
        try {
            JSONObject jsonObject = new JSONObject();
            JSONObject jsonStations = new JSONObject();
            jsonObject.put("stations", jsonStations);


            Document doc = Jsoup.parse(input, "UTF-8");


            Element table = doc.selectFirst("table[class=standard sortable]");
            Elements stations = table.select("tbody>tr");
            stations.remove(0);
            //собрем список всех линий
            ArrayList<Line> resultList = new ArrayList<>();
            number2line = new HashMap<>();

            for (Element tableStation : stations) {
                Element tr1 = tableStation.selectFirst("td:eq(0)");
                Element tr2 = tableStation.selectFirst("td:eq(1)>span>a");
                if (tr2 == null) {
                    tr2 = tableStation.selectFirst("td:eq(1)>a");
                }
                Element tr2final = tr2;
                getLineByTrElement(tr1, resultList);
                resultList.forEach(line -> {
                    if (number2line.containsKey(line.getNumber())) {
                        line = number2line.get(line.getNumber());
                    } else
                        number2line.put(line.getNumber(), line);
                    number2line.put(line.getNumber(), line);
                    Station station = new Station(tr2final.text(), line);
                    line.addStation(station);
                });
            }
            //наберем станции в линии
            Set<String> lineNumberSet = number2line.keySet();
            for (String lineNumber : lineNumberSet) {
                JSONArray arrayStations = new JSONArray();
                Line line = number2line.get(lineNumber);
                line.getStations().stream().forEach(station -> arrayStations.add(station.getName()));
//                arrayStations.addAll(line.getStations());
                jsonStations.put(lineNumber, arrayStations);

            }

            //запишем линии в json
            JSONArray jsonLines = new JSONArray();
            jsonObject.put("Lines", jsonLines);
            for (String lineNumber : lineNumberSet) {
                Line line = number2line.get(lineNumber);
                JSONObject jsonline = new JSONObject();
                jsonline.put("number", lineNumber);
                jsonline.put("name", line.getName());
                jsonLines.add(jsonline);
            }

            //приведем к человекочитаемому виду и запишем в файл
            String niceFormattedJson = JsonWriter.formatJson(jsonObject.toJSONString());
            try (FileWriter writer = new FileWriter(jsonStrPath)) {
                writer.write(niceFormattedJson);
                writer.flush();
                writer.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    static void getLineByTrElement(Element tr, ArrayList<Line> resultArray) {
        resultArray.clear();
        Elements elements = tr.getAllElements();
        for (int i = 0; i < elements.size() - 1; i++) {
            Element span1 = elements.get(i);
            Element span2 = elements.get(i + 1);
            if ((span1.tagName().equals("span") && (span2.tagName().equals("span") &&
                    (span1.attributes().hasKey("style")) && (span2.attributes().hasKey("title")))) {
                resultArray.add(new Line(span1.text(), span2.attr("title")));
            }
        }
    }
}
