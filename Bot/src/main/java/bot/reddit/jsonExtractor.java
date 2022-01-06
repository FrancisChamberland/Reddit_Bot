package bot.reddit;

import bot.reddit.exceptions.UrlException;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class jsonExtractor {

    private static final String ERROR_READER = "Could not get reader from : %s";
    private static final String ERROR_JSON_SYNTAX = "Could not get json from : %s";
    private static final String ERROR_JSON_STATE = "Wrong method used for : %s";
    private static final String ERROR_FORMAT = "URL format is invalid : %s";
    private static final String ERROR_CONNECTION = "Could not connect to : %s";
    private static final String ERROR_RESPONSE = "Response code is note ok : %s";

    public static JsonObject getJsonObjectFromUrl(String urlString) throws UrlException {
        HttpURLConnection connection = getConnectionFromUrl(urlString);
        InputStreamReader inputStreamReader = getReaderFromConnection(connection);
        return getJsonObjectFromReader(inputStreamReader);
    }

    public static JsonArray getJsonArrayFromUrl(String urlString) throws UrlException {
        HttpURLConnection connection = getConnectionFromUrl(urlString);
        InputStreamReader inputStreamReader = getReaderFromConnection(connection);
        return getJsonArrayFromReader(inputStreamReader);
    }

    public static HttpURLConnection getConnectionFromUrl(String urlString) throws UrlException {
        URL url = getUrlFromString(urlString);
        HttpURLConnection connection = getConnectionFromUrl(url);
        int responseCode = getResponseCodeFromConnection(connection);
        return connection;
    }

    public static InputStreamReader getReaderFromConnection(HttpURLConnection connection) throws UrlException {
        try{
            InputStream inputStream = connection.getInputStream();
            return new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        } catch (IOException e){
            throw new UrlException(String.format(ERROR_READER, connection.getURL()));
        }
    }

    public static JsonObject getJsonObjectFromReader(InputStreamReader reader) throws UrlException {
        try{
            return JsonParser.parseReader(reader).getAsJsonObject();
        } catch (JsonSyntaxException e){
            throw new UrlException(String.format(ERROR_JSON_SYNTAX, reader));
        } catch (IllegalStateException e){
            throw new UrlException(String.format(ERROR_JSON_STATE, reader));
        }
    }

    public static JsonArray getJsonArrayFromReader(InputStreamReader reader) throws UrlException {
        try{
            return JsonParser.parseReader(reader).getAsJsonArray();
        } catch (JsonSyntaxException e){
            throw new UrlException(String.format(ERROR_JSON_SYNTAX, reader));
        } catch (IllegalStateException e){
            throw new UrlException(String.format(ERROR_JSON_STATE, reader));
        }
    }

    public static URL getUrlFromString(String urlString) throws UrlException {
        try{
            return new URL(urlString);
        } catch (MalformedURLException e){
            throw new UrlException(String.format(ERROR_FORMAT, urlString));
        }
    }

    public static HttpURLConnection getConnectionFromUrl(URL url) throws UrlException {
        try{
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestProperty("User-agent", "bot");
            return connection;
        } catch (IOException e){
            throw new UrlException(String.format(ERROR_CONNECTION, url));
        }
    }
    public static int getResponseCodeFromConnection(HttpURLConnection connection) throws UrlException {
        try{
            return  connection.getResponseCode();
        } catch (IOException e){
            throw new UrlException(String.format(ERROR_RESPONSE, connection.getURL()));
        }
    }
}
