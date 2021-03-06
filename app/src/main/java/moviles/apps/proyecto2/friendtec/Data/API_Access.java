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
    private JSONObject jsonObjectResponseNotifs = new JSONObject();
    private JSONObject jsonObjectResponseAmigos = new JSONObject();
    private JSONObject jsonObjectResponseChats = new JSONObject();
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

    public boolean register(String carne, String carrera, String email, String username, String password){
        jsonObjectResponse = new JSONObject();
        HashMap<String, String> Parametros = new HashMap<String, String>();
        Parametros.put("carnet",carne);
        Parametros.put("carrera",carrera);
        Parametros.put("nombre", username);
        Parametros.put("email", email);
        Parametros.put("password", password);
        return makePOSTRequest("users/register", "POST", true, true, Parametros, HttpsURLConnection.HTTP_CREATED);

    }
    //VERIFICAR NOMBRES CORRECTOS EN EL BACKEND
    public boolean change_user(String id ,String name, String email,String authToken){
        jsonObjectResponse = new JSONObject();
        HashMap<String, String> Parametros = new HashMap<String, String>();
        Parametros.put("id", id);
        Parametros.put("name",name);
        Parametros.put("email",email);
        Parametros.put("authentication_token",authToken);
        return makePOSTRequest("users/change_user", "PUT", true, true, Parametros, HttpsURLConnection.HTTP_OK);
    }
    //VERIFICAR NOMBRES CORRECTOS EN EL BACKEND
    public boolean change_pass(String id ,String password, String new_password,String authToken){
        jsonObjectResponse = new JSONObject();
        HashMap<String, String> Parametros = new HashMap<String, String>();
        Parametros.put("id", id);
        Parametros.put("password", password);
        Parametros.put("new_password", new_password);
        Parametros.put("authentication_token",authToken);
        return makePOSTRequest("users/change_pass", "PUT", true, true, Parametros, HttpsURLConnection.HTTP_OK);
    }

    public boolean new_post(String idUser, String contenido, String urlImage, String fecha){
        jsonObjectResponse = new JSONObject();
        HashMap<String, String> Parametros = new HashMap<String, String>();
        Parametros.put("id",idUser);
        Parametros.put("contenido",contenido);
        Parametros.put("foto", urlImage);
        Parametros.put("fecha_hora", fecha);
        return makePOSTRequest("posts/create", "POST", true, true, Parametros, HttpsURLConnection.HTTP_CREATED);

    }

    public boolean change_image(String id ,String photoN,String photoR){
        jsonObjectResponse = new JSONObject();
        HashMap<String, String> Parametros = new HashMap<String, String>();
        Parametros.put("id", id);
        Parametros.put("f1",photoN);
        Parametros.put("f2",photoR);
        return makePOSTRequest("users/update_image", "PUT", true, true, Parametros, HttpsURLConnection.HTTP_OK);
    }
    public boolean accept_request(String id ,String id_user2,String authToken){
        jsonObjectResponse = new JSONObject();
        HashMap<String, String> Parametros = new HashMap<String, String>();
        Parametros.put("id", id);
        Parametros.put("id_user2",id_user2);
        Parametros.put("auth_token",authToken);
        return makePOSTRequest("solicituds/aceptar", "POST", true, true, Parametros, HttpsURLConnection.HTTP_OK);
    }

    public boolean reject_request(String id ,String id_user2,String authToken){
        //jsonObjectResponse = new JSONObject();
        String urlEsp = "solicituds/delete?id=" + id + "&id_user2=" + id_user2 + "&auth_token=" + authToken;
        return makeDELETERequest(urlEsp, "DELETE", HttpsURLConnection.HTTP_OK);
    }

    public boolean getAmigos(String idUser){
        jsonArrayResponse = new JSONArray();
        String urlEsp = "amigos/get_amigos?id=" + idUser;
        return makeGETRequest(urlEsp, "GET", HttpsURLConnection.HTTP_OK, 2);
    }

    public boolean getPosts(String idUser, String auth_token){
        jsonArrayResponse = new JSONArray();
        String urlEsp = "posts/get_friend_posts?id=" + idUser + "&auth_token=" + auth_token;
        return makeGETRequest(urlEsp, "GET", HttpsURLConnection.HTTP_OK, 0);
    }

    public boolean getPostsProfile(String idUser){
        jsonArrayResponse = new JSONArray();
        String urlEsp = "posts/get_posts?id=" + idUser;
        return makeGETRequest(urlEsp, "GET", HttpsURLConnection.HTTP_OK, 0);
    }

    public boolean getRequests(String idUser){
        jsonArrayResponse = new JSONArray();
        String urlEsp = "solicituds/get?id=" + idUser;
        return makeGETRequest(urlEsp, "GET", HttpsURLConnection.HTTP_OK, 0);
    }

    public boolean getInfoUser(String idUser){
        jsonArrayResponse = new JSONArray();
        String urlEsp = "users/get_username_foto?id_user=" + idUser;
        return makeGETRequest(urlEsp, "GET", HttpsURLConnection.HTTP_OK, 0);
    }

    public boolean search(String idUser, String auth_token, String busqueda){
        jsonArrayResponse = new JSONArray();
        String urlEsp = "users/search?id=" + idUser + "&auth_token=" + auth_token + "&busqueda=" + busqueda;
        return makeGETRequest(urlEsp, "GET", HttpsURLConnection.HTTP_OK, 0);
    }

    public boolean search_button_action(String idUser, String id_user2, String accion){
        jsonObjectResponse = new JSONObject();
        HashMap<String, String> Parametros = new HashMap<String, String>();
        Parametros.put("id", idUser);
        Parametros.put("id_user2", id_user2);
        Parametros.put("accion", accion);
        return makePOSTRequest("users/search_button", "POST", true, true, Parametros, HttpsURLConnection.HTTP_OK);
    }

    public boolean getNotifications(String idUser){
        jsonArrayResponse = new JSONArray();
        String urlEsp = "notifications/get?id=" + idUser;
        return makeGETRequest(urlEsp, "GET", HttpsURLConnection.HTTP_OK, 1);
    }

    public boolean setFalseNotifications(String idUser){
        jsonObjectResponse = new JSONObject();
        HashMap<String, String> Parametros = new HashMap<String, String>();
        Parametros.put("id", idUser);
        return makePOSTRequest("notifications/set_false", "PUT", true, true, Parametros, HttpsURLConnection.HTTP_OK);
    }

    public boolean getFriendsLocations(String idUser){
        jsonArrayResponse = new JSONArray();
        String urlEsp = "locations/get_friends_locations?id=" + idUser;
        return makeGETRequest(urlEsp, "GET", HttpsURLConnection.HTTP_OK, 0);
    }

    public boolean postLocation(String idUser, String latitud, String longitud){
        jsonObjectResponse = new JSONObject();
        HashMap<String, String> Parametros = new HashMap<String, String>();
        Parametros.put("id", idUser);
        Parametros.put("latitud", latitud);
        Parametros.put("longitud", longitud);
        return makePOSTRequest("locations/create", "POST", true, true, Parametros, HttpsURLConnection.HTTP_OK);
    }

    public boolean getChats(String idUser){
        jsonArrayResponse = new JSONArray();
        String urlEsp = "chats/get?id=" + idUser;
        return makeGETRequest(urlEsp, "GET", HttpsURLConnection.HTTP_OK, 3);
    }

    public boolean setTrueChat(String idChat){
        jsonObjectResponse = new JSONObject();
        HashMap<String, String> Parametros = new HashMap<String, String>();
        Parametros.put("id_chat", idChat);
        return makePOSTRequest("chats/set_true", "PUT", true, true, Parametros, HttpsURLConnection.HTTP_OK);
    }

    public boolean getChatMessages(String idChat){
        jsonArrayResponse = new JSONArray();
        String urlEsp = "chats/get_chat_messages?id_chat=" + idChat;
        return makeGETRequest(urlEsp, "GET", HttpsURLConnection.HTTP_OK, 0);
    }

    public boolean sendMessage(String id, String id_friend, String id_chat, String message){
        jsonObjectResponse = new JSONObject();
        HashMap<String, String> Parametros = new HashMap<String, String>();
        Parametros.put("id", id);
        Parametros.put("id_friend", id_friend);
        Parametros.put("id_chat", id_chat);
        Parametros.put("message", message);
        return makePOSTRequest("chats/send_message", "POST", true, true, Parametros, HttpsURLConnection.HTTP_OK);
    }

    /////////////////////// GET Respuesta del servidor: JSONObject ////////////////////////////////
    public JSONObject getJsonObjectResponse(){
        Log.d("estado: ", ""+estadoRequest);
        return jsonObjectResponse;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////// GET Respuesta del servidor: JSONObject ////////////////////////////////
    public JSONObject getJsonObjectResponseNotifs(){
        Log.d("estado: ", ""+estadoRequest);
        return jsonObjectResponseNotifs;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////// GET Respuesta del servidor: JSONObject ////////////////////////////////
    public JSONObject getJsonObjectResponseAmigos(){
        Log.d("estado: ", ""+estadoRequest);
        return jsonObjectResponseAmigos;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////// GET Respuesta del servidor: JSONObject ////////////////////////////////
    public JSONObject getJsonObjectResponseChats(){
        Log.d("estado: ", ""+estadoRequest);
        return jsonObjectResponseChats;
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
    //devolver_en= 0:JsonObjectResponse; 1:JsonObjectResponseNotifs; 2:JsonObjectResponseAmigos
    private boolean makeGETRequest(String urlEsp, String metodo, int responseCode, int devolver_en) {
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

            switch(devolver_en){
                case 0:
                    jsonObjectResponse = new JSONObject(result);
                    break;
                case 1:
                    jsonObjectResponseNotifs = new JSONObject(result);
                    break;
                case 2:
                    jsonObjectResponseAmigos = new JSONObject(result);
                    break;
                case 3:
                    jsonObjectResponseChats = new JSONObject(result);
                    break;
            }

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
