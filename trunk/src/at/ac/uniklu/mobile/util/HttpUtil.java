package at.ac.uniklu.mobile.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

/**
 * HTTP handler to asynchronously send HTTP messages that have no intended responses and to synchronously retrieve
 * responses to HTTP requests that should carry a payload.
 * 
 * @author Liddle
 */
public class HttpUtil extends Thread {
    /**
     * Default agent string for Android HTTP client.
     */
    public static final String AGENT_STRING = "Android";

    /**
     * Default URL encoding schema.
     */
    public static String DEFAULT_ENCODING = "UTF-8";

    /**
     * Tag for logging.
     */
    private static final String TAG = "HttpUtil";

    /**
     * The URL of an HTTP message to send.
     */
    private String url;

    /**
     * Asynchronous worker constructor.
     * 
     * @param url
     *            The URL of an HTTP message to send.
     */
    public HttpUtil(String url) {
        this.url = url;
    }

    private static String getContent(HttpResponse response) {
        String content = "";
        try {
            content = getContent(response.getEntity().getContent());
        } catch (Exception e) {
            Log.w(TAG, "getContent(HttpResponse)", e);
        }
        return content;
    }

    private static String getContent(InputStream in) {
        String content = "";
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            sb = sb.replace(sb.lastIndexOf("\n"), sb.length(), "");
            content = sb.toString();
        } catch (Exception e) {
            // ignore
            Log.w(TAG, "getContent(InputStream)", e);
        } finally {
            try {
                in.close();
            } catch (Exception e) {
                Log.w(TAG, "getContent(InputStream)", e);
            }
        }
        return content;
    }

    /**
     * Synchronously retrieve the contents of an HTTP request.
     * 
     * @param url
     *            The URL of an HTTP request to process.
     * @return The text returned by the web server for this URL.
     */
    public static String getContent(String url) {
    	HttpClient httpClient = new DefaultHttpClient();  
        HttpGet request = new HttpGet(url);
        try {
            HttpResponse response = httpClient.execute(request);
            String responseString = getContent(response);
            request.abort();
            return responseString;
        } catch (Exception e) {
            Log.w(TAG, "getContent(String)", e);
            return "Exception: " + e;
        }
    }

    /**
     * Synchronously retrieve the contents of an HTTP request.
     * 
     * @param url
     *            The URL of an HTTP request to process.
     * @return The text returned by the web server for this URL.
     * @throws IOException 
     * @throws ClientProtocolException 
     */
    public static String doGetRequest(String url) throws ClientProtocolException, IOException {
    	HttpClient httpClient = new DefaultHttpClient();  
        HttpGet request = null;
        String responseString = null;

        try {            
            request = new HttpGet(url);
            HttpResponse response = httpClient.execute(request);
            responseString = getContent(response);
        } catch (ClientProtocolException cpe) {
            throw cpe;
        } catch (IOException ioe) {
            throw ioe;
        } finally {
            if (request != null) {
                request.abort();
            }
        }
        return responseString;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {
    	HttpClient httpClient = new DefaultHttpClient();
        HttpGet request = new HttpGet(url);
        try {
        	httpClient.execute(request);
        } catch (Exception e) {
            Log.w(TAG, "sendHttpMessage", e);
        } finally {
        	request.abort();
        }
    }

    /**
     * Spawn an independent thread to send this HTTP message asynchronously. Just fire off the request and forget it.
     * 
     * @param url
     *            HTTP message to be sent.
     */
    public static void sendAsyncHttpMessage(String url) {
        HttpUtil worker = new HttpUtil(url);
        worker.start();
    }
}