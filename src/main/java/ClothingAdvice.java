import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

public class ClothingAdvice implements Answerable{
    public static String getAnswer(String message) throws IOException {
        URL url = new URL("http://api.openweathermap.org/data/2.5/weather?q=" + message + "&units=metric&appid=f60ab5f39cc9f3416a9115a9751f8fad");

        Scanner in = new Scanner((InputStream) url.getContent());
        StringBuilder result = new StringBuilder();
        while (in.hasNext()) {
            result.append(in.nextLine());
        }

        JSONObject object = new JSONObject(result.toString());

        JSONObject main = object.getJSONObject("main");

        JSONArray weather = object.getJSONArray("weather");

        Double temp = main.getDouble("temp");
        String pic = "";
        JSONObject data = weather.getJSONObject(0);
        int status = data.getInt("id");
        for (int i = 0; i < weather.length(); i++) {
            JSONObject obj = weather.getJSONObject(i);
            pic = (String) obj.get("icon");
        }
        return chooseFirstAdvice(temp) + chooseSecondAdvice(status) +"http://openweathermap.org/img/w/" + pic + ".png";
    }

    public static String chooseFirstAdvice(Double temperature) {
        String advice = "";
        if (temperature < -40.0) { advice += Advices.EXTREMELY_COLD; }
        else if (temperature <= -15.0) { advice += Advices.VERY_COLD; }
        else if (temperature <= -5.0) { advice += Advices.COLD; }
        else if (temperature <= 0.0) { advice += Advices.BELOW_ZERO; }
        else if (temperature <= 10.0) { advice += Advices.ABOVE_ZERO; }
        else if (temperature <= 18.0) { advice += Advices.NORMAL; }
        else if (temperature <= 25.0) { advice += Advices.HOT; }
        else if (temperature <= 35.0) { advice += Advices.VERY_HOT; }
        else { advice += Advices.EXTREMELY_HOT; }
        return advice + " ";
    }

    public static String chooseSecondAdvice(Integer status) {
        String advice = "";
        if (status <= 232) { advice += Advices.THUNDERSHTORM; }
        else if (status <= 531) { advice += Advices.RAIN; }
        else if (status <= 622) { advice += Advices.SNOW; }
        else  if (status <= 761) { advice += Advices.HAZE; }
        else  if (status <= 781) { advice += Advices.TORNADO; }
        else if (status == 800) { advice += Advices.CLEAR; }
        else if (status <= 804) { advice += Advices.CLOUDS; }
        return advice;
    }
}
