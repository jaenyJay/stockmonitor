package watch;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class WatchService {
    private static final String HOST_URL = "https://www.apple.com/kr/shop/retail/pickup-message?pl=true&searchNearby=true&store=R692&parts.0=MKQA3KH/A&option.0=ML2P3FE%2FA&mt=regular&parts.1=MKTG3KH/A&option.1=ML303FE%2FA&mt=regular&_=1633740088271";

    public void get() {
        HttpURLConnection conn = null;
        JSONObject responseJson = null;

        try {
            URL url = new URL(HOST_URL);

            conn = (HttpURLConnection)url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setRequestMethod("GET");
            //conn.setDoOutput(true);

            JSONObject commands = new JSONObject();

            int responseCode = conn.getResponseCode();
            if (responseCode == 400 || responseCode == 401 || responseCode == 500 ) {
                System.out.println(responseCode + " Error!");
            } else {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = "";
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                responseJson = new JSONObject(sb.toString());
                System.out.println(responseJson);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            System.out.println("not JSON Format response");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
