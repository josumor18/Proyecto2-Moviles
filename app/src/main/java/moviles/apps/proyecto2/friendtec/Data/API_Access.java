package moviles.apps.proyecto2.friendtec.Data;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

public class API_Access {
    private final String url_base = "https://friendtec.herokuapp.com/api/";
    int estadoRequest = -1;
    private JSONObject jsonObjectResponse = new JSONObject();
    private JSONArray jsonArrayResponse = new JSONArray();

    private static final API_Access ourInstance = new API_Access();

    public static API_Access getInstance() {
        return ourInstance;
    }

    private API_Access() {
    }

    //------------------------------------------------------------------------------------------//
    public boolean login(String carnet, String password){
        jsonObjectResponse = new JSONObject();
        HashMap<String, String> Parametros = new HashMap<String, String>();
        Parametros.put("carnet", carnet);
        Parametros.put("password", password);
        return makePOSTRequest("users/login", "POST", true, true, Parametros, HttpsURLConnection.HTTP_OK);
    }

    public boolean login_token(String carnet, String token){
        jsonObjectResponse = new JSONObject();
        HashMap<String, String> Parametros = new HashMap<String, String>();
        Parametros.put("carnet", carnet);
        Parametros.put("auth_token", token);
        return makePOSTRequest("users/login_token", "POST", true, true, Parametros, HttpsURLConnection.HTTP_OK);
    }

    public boolean getAmigos(String idUser){
        jsonArrayResponse = new JSONArray();
        String urlEsp = "amigos/get_amigos?id=" + idUser;
        return makeGETRequest(urlEsp, "GET", HttpsURLConnection.HTTP_OK);
    }

    public boolean getPosts(String idUser, String auth_token){
        jsonArrayResponse = new JSONArray();
        String urlEsp = "posts/get_friend_posts?id=" + idUser + "&auth_token=" + auth_token;
        return makeGETRequest(urlEsp, "GET", HttpsURLConnection.HTTP_OK);
    }

    public boolean getInfoUser(String idUser){
        jsonArrayResponse = new JSONArray();
        String urlEsp = "users/get_username_foto?id_user=" + idUser;
        return makeGETRequest(urlEsp, "GET", HttpsURLConnection.HTTP_OK);
    }

    public boolean search(String idUser, String auth_token, String busqueda){
        jsonArrayResponse = new JSONArray();
        String urlEsp = "users/search?id=" + idUser + "&auth_token=" + auth_token + "&busqueda=" + busqueda;
        return makeGETRequest(urlEsp, "GET", HttpsURLConnection.HTTP_OK);
    }

    /////////////////////// GET Respuesta del servidor: JSONObject ////////////////////////////////
    public JSONObject getJsonObjectResponse(){
        Log.d("estado: ", ""+estadoRequest);
        return jsonObjectResponse;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////



    /////////////////////////////////////////////// Métodos que ejecutan las solicitudes ////////////////////////////////////////////
    // Solicitud para POSTs
    private boolean makePOSTRequest(String urlEsp, String metodo, boolean doInput, boolean doOutput, HashMap<String, String> Parametros, int responseCode){
        String result = "";
        URL url;
        HttpsURLConnection httpsURLConnection;

        try {
            url = new URL(url_base + urlEsp);
            httpsURLConnection = (HttpsURLConnection) url.openConnection();

            //crea el objeto JSON para enviar los parámetros
            JSONObject parametros = new JSONObject();
            for(String s: Parametros.keySet()){
                parametros.put(s, Parametros.get(s));
            }

            //DEFINE PARAMETROS DE CONEXION
            httpsURLConnection.setReadTimeout(15000);
            httpsURLConnection.setConnectTimeout(15000);
            httpsURLConnection.setRequestMethod(metodo);
            httpsURLConnection.setDoInput(doInput);
            httpsURLConnection.setDoOutput(doOutput);

            //Obtiene el resultado de la solicitud
            OutputStream outputStream = httpsURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String pars = jsonToString(parametros);
            //String g = httpsURLConnection.g
            bufferedWriter.write(pars);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();

            int rCode = httpsURLConnection.getResponseCode();
            if(responseCode == rCode){
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpsURLConnection.getInputStream()));
                StringBuffer stringBuffer = new StringBuffer("");
                String linea = "";
                while((linea = bufferedReader.readLine()) != null){
                    stringBuffer.append(linea);
                    break;
                }
                bufferedReader.close();
                result = stringBuffer.toString();
            }else{
                result = "Error " + responseCode;
            }

            jsonObjectResponse = new JSONObject(result);
            return true;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private  String jsonToString(JSONObject params) throws JSONException, UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        Iterator<String> iterator = params.keys();
        while ((iterator.hasNext())){
            String key = iterator.next();
            Object value = params.get(key);

            if(first){
                //result.append("?");
                first = false;
            }else {
                result.append("&");
            }

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));
        }
        return result.toString();
    }

    // Solicitud para GETs
    private boolean makeGETRequest(String urlEsp, String metodo, int responseCode) {
        String result = "";
        URL url;
        HttpsURLConnection httpsURLConnection;

        try {
            url = new URL(url_base + urlEsp);
            httpsURLConnection = (HttpsURLConnection) url.openConnection();

            //DEFINE PARAMETROS DE CONEXION
            httpsURLConnection.setReadTimeout(15000);
            httpsURLConnection.setConnectTimeout(15000);
            httpsURLConnection.setRequestMethod(metodo);

            //Connect to our url
            httpsURLConnection.connect();

            //Create a new InputStreamReader
            InputStreamReader streamReader = new
                    InputStreamReader(httpsURLConnection.getInputStream());

            //Create a new buffered reader and String Builder
            BufferedReader reader = new BufferedReader(streamReader);
            StringBuilder stringBuilder = new StringBuilder();

            //Check if the line we are reading is not null
            int rCode = httpsURLConnection.getResponseCode();
            if (responseCode == rCode) {
                String inputLine = "";
                while ((inputLine = reader.readLine()) != null) {
                    stringBuilder.append(inputLine);
                }
                result = stringBuilder.toString();
            } else {
                result = "Error " + responseCode;
            }


            //Close our InputStream and Buffered reader
            reader.close();
            streamReader.close();

            jsonObjectResponse = new JSONObject(result);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Solicitud para DELETEs
    private boolean makeDELETERequest(String urlEsp, String metodo, int responseCode){
        URL url;
        HttpsURLConnection httpsURLConnection;

        try {
            url = new URL(url_base + urlEsp);
            httpsURLConnection = (HttpsURLConnection) url.openConnection();
            httpsURLConnection.setRequestMethod("DELETE");
            httpsURLConnection.setDoOutput(false);
            httpsURLConnection.setDoInput(true);
            httpsURLConnection.connect();
            int r = httpsURLConnection.getResponseCode();
            String rC = Integer.toString(httpsURLConnection.getResponseCode());
            if(r==200){
                return true;
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return false;
    }
}
