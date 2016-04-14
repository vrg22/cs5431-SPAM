import java.io.*;
import java.net.*;
import javax.net.ssl.*;
import java.util.*;

public class SendHttpsRequest {
    public static String get(String urlStr) {
        InputStream ins = null;
        try {
            try {
                URL url = new URL(urlStr);
                HttpsURLConnection con = (HttpsURLConnection)url.openConnection();

                con.setRequestMethod("GET");
                ins = con.getInputStream();
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
            } finally {
                if (ins != null) ins.close();
            }
        } catch (IOException e) {
            return null;
        }
    }

    public static String post(String urlStr, Map<String, String> params) {
        try {
            DataOutputStream wr = null;
            InputStream ins = null;
            try {
                URL url = new URL(urlStr);
                HttpsURLConnection con = (HttpsURLConnection)url.openConnection();

                con.setRequestMethod("POST");
                con.setDoOutput(true);
                wr = new DataOutputStream(con.getOutputStream());
                String paramsStr = SendHttpsRequest.formatParams(params);
        		wr.writeBytes(paramsStr);
        		wr.flush();
        		wr.close();

                ins = con.getInputStream();
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
                e.printStackTrace();
                return null;
            } finally {
                if (wr != null) wr.close();
                if (ins != null) ins.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String delete(String urlStr) {
        try {
            InputStream ins = null;
            try {
                URL url = new URL(urlStr);
                HttpsURLConnection con = (HttpsURLConnection)url.openConnection();

                con.setRequestMethod("DELETE");

                ins = con.getInputStream();
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
            } finally {
                if (ins != null) ins.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String formatParams(Map<String, String> params) {
        StringBuffer formatted = new StringBuffer();

        for (Map.Entry<String, String> entry : params.entrySet()) {
            try {
                String key = URLEncoder.encode(entry.getKey(), "UTF-8");
                String value = URLEncoder.encode(entry.getValue(), "UTF-8");

                // TODO: sanitize user input (prevent XSS attacks)

                formatted.append(key + "=" + value + "&");
            } catch (UnsupportedEncodingException e) {
                return null;
            }
        }

        // Trim trailing '&'
        return formatted.substring(0, formatted.length() - 1);
    }
}
