import java.io.*;
import java.net.*;
import javax.net.ssl.*;
import java.util.*;
import java.security.cert.X509Certificate;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;

public class SendHttpsRequest {
    public static String get(String urlStr) {
        try {
            URL url = new URL(urlStr);
			System.out.println(url);
            HttpsURLConnection con = (HttpsURLConnection)url.openConnection();

            con.setRequestMethod("GET");
			System.out.println("GET");
            InputStream ins = con.getInputStream();
			System.out.println("con.getInputStream()");
            InputStreamReader isr = new InputStreamReader(ins);
            BufferedReader in = new BufferedReader(isr);

            StringBuffer response = new StringBuffer();

			System.out.println("Attempting to get server certificates");
			X509Certificate[] certs = (X509Certificate[]) con.getServerCertificates();
			if (certs == null) {
				System.out.println("Failed to recv server certificate");
			} else {
				System.out.println("recv server certificate");
				for (X509Certificate cert: certs) {
					try {
						cert.checkValidity();
						System.out.println("Certificate is valid");
						System.out.println(cert.toString());
					} catch (CertificateExpiredException cee) {
						System.err.println("CertificateExpiredException");
					} catch (CertificateNotYetValidException cnyv) {
						System.err.println("CertificateNotYetValid");
					}
				}
			}

            String inputLine;
            while ((inputLine = in.readLine()) != null)
            {
                response.append(inputLine);
            }

            in.close();

            return response.toString();
        } catch (IOException e) {
			System.err.println("IOException occured");
			System.err.println(e.getMessage());
			System.err.println(e.getCause());
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
