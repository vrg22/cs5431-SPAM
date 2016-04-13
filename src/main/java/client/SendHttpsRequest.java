import java.io.*;
import java.net.*;
import javax.net.ssl.*;
import java.util.*;

public class SendHttpsRequest {
    public static String get(String urlStr) {
        try {
            URL url = new URL(urlStr);
            HttpsURLConnection con = (HttpsURLConnection)url.openConnection();

            con.setRequestMethod("GET");
            InputStream ins = con.getInputStream();
            InputStreamReader isr = new InputStreamReader(ins);
            BufferedReader in = new BufferedReader(isr);

            StringBuffer response = new StringBuffer();
            String inputLine;
            while ((inputLine = in.readLine()) != null)
            {
                response.append(inputLine);
            }

            in.close();

            return response.toString();
        } catch (IOException e) {
			System.err.println("IOException occured");
			e.printStackTrace();
            return null;
        }
    }

    public static String post(String urlStr, Map<String, String> params) {
        try {
            URL url = new URL(urlStr);
            HttpsURLConnection con = (HttpsURLConnection)url.openConnection();

            con.setRequestMethod("POST");
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            String paramsStr = SendHttpsRequest.formatParams(params);
    		wr.writeBytes(paramsStr);
    		wr.flush();
    		wr.close();

            InputStream ins = con.getInputStream();
            InputStreamReader isr = new InputStreamReader(ins);
            BufferedReader in = new BufferedReader(isr);

            StringBuffer response = new StringBuffer();
            String inputLine;
            while ((inputLine = in.readLine()) != null)
            {
                response.append(inputLine);
            }

            in.close();

            return response.toString();
        } catch (IOException e) {
            return null;
        }
    }

    public static String delete(String urlStr) {
        try {
            URL url = new URL(urlStr);
            HttpsURLConnection con = (HttpsURLConnection)url.openConnection();

            con.setRequestMethod("DELETE");

            InputStream ins = con.getInputStream();
            InputStreamReader isr = new InputStreamReader(ins);
            BufferedReader in = new BufferedReader(isr);

            StringBuffer response = new StringBuffer();
            String inputLine;
            while ((inputLine = in.readLine()) != null)
            {
                response.append(inputLine);
            }

            in.close();

            return response.toString();
        } catch (IOException e) {
            return null;
        }
    }

    public static String formatParams(Map<String, String> params) {
        StringBuffer formatted = new StringBuffer();

        for (String key : params.keySet()) {
            String value = params.get(key);

            // TODO: sanitize user input (prevent XSS attacks)

            formatted.append(key + "=" + value + "&");
        }

        // Trim trailing '&'
        return formatted.substring(0, formatted.length() - 1);
    }
}
