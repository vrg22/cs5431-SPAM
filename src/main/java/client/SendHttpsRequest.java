import java.io.*;
import java.net.*;
import javax.net.ssl.*;
import java.util.*;
import java.security.cert.Certificate;

public class SendHttpsRequest {
    public static String get(String urlStr) throws IOException {
        InputStream ins = null;
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
    }

    public static String post(String urlStr, Map<String, String> params)
             throws IOException {
        DataOutputStream wr = null;
        InputStream ins = null;
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
    }

    public static String delete(String urlStr) throws IOException {
        InputStream ins = null;
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
    }

    public static Certificate[] getServerCertificates(String urlStr)
            throws IOException {
        URL url = new URL(urlStr);
        HttpsURLConnection con = (HttpsURLConnection)url.openConnection();
        con.setRequestMethod("GET");
        con.getInputStream();
        return con.getServerCertificates();
    }

    public static String formatParams(Map<String, String> params) {
        if (params == null) return "";

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
        if (formatted.length() == 0) return "";
        return formatted.substring(0, formatted.length() - 1);
    }
}
