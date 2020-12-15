import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

public class Weather implements Answerable{
    public static String getAnswer(String message) throws IOException {
        URL url = new URL("http://api.openweathermap.org/data/2.5/weather?q=" + message + "&units=metric&appid=f60ab5f39cc9f3416a9115a9751f8fad");
        Scanner in = new Scanner((InputStream) url.getContent());
        StringBuilder result = new StringBuilder();
        while (in.hasNext()) {
            result.append(in.nextLine());
        }

        Model model = new Model();
        JSONObject object = new JSONObject(result.toString());
        model.setName(object.getString("name"));

        JSONObject main = object.getJSONObject("main");
        model.setTemp(main.getDouble("temp"));

        JSONArray getArray = object.getJSONArray("weather");
        for (int i = 0; i < getArray.length(); i++) {
            JSONObject obj = getArray.getJSONObject(i);
            model.setIcon((String) obj.get("icon"));
        }

        return "В замечательном городе " + model.getName() +
                " сейчас " + model.getTemp() + " градусов по Цельсию" + "\n" +
                "http://openweathermap.org/img/w/" + model.getIcon() + ".png";
    }
}
