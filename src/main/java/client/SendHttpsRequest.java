import java.io.*;
import java.net.*;
import javax.net.ssl.*;
import java.util.*;
import java.security.cert.Certificate;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.security.KeyManagementException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

class SSLSocketFactoryWrapper
{
    private final SSLSocketFactory wrappedFactory;
    private final String[] enabledProtocols;
    private final String[] enabledSuites;

    public SSLSocketFactoryWrapper(SSLSocketFactory factory, String[] protocols, String[] suites) {
        wrappedFactory = factory;
        enabledProtocols = protocols;
        enabledSuites = suites;
    }

    public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
        SSLSocket socket = (SSLSocket)wrappedFactory.createSocket(host, port);
        setParameters(socket);
        return socket;
    }

    public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException,
        UnknownHostException {
        SSLSocket socket = (SSLSocket)wrappedFactory.createSocket(host, port, localHost, localPort);
        setParameters(socket);
        return socket;
    }

    public Socket createSocket(InetAddress host, int port) throws IOException {
        SSLSocket socket = (SSLSocket)wrappedFactory.createSocket(host, port);
        setParameters(socket);
        return socket;
    }

    public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort)
        throws IOException {
        SSLSocket socket = (SSLSocket)wrappedFactory.createSocket(address, port, localAddress, localPort);
        setParameters(socket);
        return socket;
    }

    public Socket createSocket() throws IOException {
        SSLSocket socket = (SSLSocket)wrappedFactory.createSocket();
        setParameters(socket);
        return socket;
    }

    public String[] getDefaultCipherSuites() {
        return wrappedFactory.getDefaultCipherSuites();
    }

    public String[] getSupportedCipherSuites() {
        return enabledSuites == null ? wrappedFactory.getSupportedCipherSuites() : enabledSuites;
    }

    public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException {
        SSLSocket socket = (SSLSocket)wrappedFactory.createSocket(s, host, port, autoClose);
        setParameters(socket);
        return socket;
    }

    public SSLSocketFactory getFactory() {
        return wrappedFactory;
    }

    private void setParameters(SSLSocket socket) {
        if (enabledProtocols != null) {
            socket.setEnabledProtocols(enabledProtocols);
        }
        if (enabledSuites != null) {
            socket.setEnabledCipherSuites(enabledSuites);
        }
    }
}


public class SendHttpsRequest {
    public static String get(String urlStr) throws IOException, NoSuchAlgorithmException, KeyManagementException {
        InputStream ins = null;
        URL url = new URL(urlStr);
        HttpsURLConnection con = (HttpsURLConnection)url.openConnection();

        // configure SSL connection to use specific TLS version and cipher suite
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, null, null);
        SSLSocketFactory factory = context.getSocketFactory();
        SSLSocketFactoryWrapper wrapper = new SSLSocketFactoryWrapper(factory, new String[] { "TLSv1.2" },
            new String[] { "TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384" });
        ((HttpsURLConnection)con).setSSLSocketFactory(wrapper.getFactory());

        // open connection to the server
        con.setRequestMethod("GET");
        con.connect();

        String cs = con.getCipherSuite();
        System.out.println("CipherSuite get:" +cs);

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

    public static String post(String urlStr, Map<String, String> params) throws IOException {
        DataOutputStream wr = null;
        InputStream ins = null;
        URL url = new URL(urlStr);
        HttpsURLConnection con = (HttpsURLConnection)url.openConnection();

        // configure SSL connection to use specific TLS version and cipher suite
        try {
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, null, null);
            SSLSocketFactory factory = context.getSocketFactory();
            SSLSocketFactoryWrapper wrapper = new SSLSocketFactoryWrapper(factory, new String[] { "TLSv1.2" },
                new String[] { "TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384" });
            ((HttpsURLConnection)con).setSSLSocketFactory(wrapper.getFactory());
        } catch (NoSuchAlgorithmException e) {
            System.err.println("NoSuchAlgorithmException raised");
        } catch (KeyManagementException e) {
            System.err.println("KeyManagementException raised");
        }

        // open connection to the server
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        con.connect();

        String cs = con.getCipherSuite();
        System.out.println("CipherSuite post:" +cs);

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
