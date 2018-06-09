package moviles.apps.proyecto2.friendtec.Business;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import com.cloudinary.utils.ObjectUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import moviles.apps.proyecto2.friendtec.Data.API_Access;
import moviles.apps.proyecto2.friendtec.Data.HttpGetBitmap;
import moviles.apps.proyecto2.friendtec.R;

public class LoginActivity extends AppCompatActivity {

    private static final String USER_PREFERENCES = "user.preferences.friendtec";
    private static final String PREFERENCE_CARNET = "string.carnet.sesion";
    private static final String PREFERENCE_AUTH_TOKEN = "string.token.sesion";
    private static final String PREFERENCE_SESION_ACTIVA = "boolean.sesion.isActiva";

    private static String carnet = "";
    private static String nombre = "";
    private static String token = "";
    private static String foto = "";

    public static boolean isFacebook=false;
    RelativeLayout rlLogin, rlLoader;
    ProgressBar progressBarLogin;
    EditText edtCarnetLogin;
    EditText edtPasswordLogin;
    CheckBox chckSesionActiva;
    TextView txtRegistrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(getEstadoSesion()){
            String[] userData = getUsuarioSesion();
            carnet = userData[0];
            token = userData[1];
            nombre = "Usuario App";//Se obtiene de BD por medio del username
            //foto = "Fotoooo";//Se obtiene de BD por medio del username
            ///IR AL ASYNKTASK pero hacer un metodo para iniciar sesion solo con token
            ExecuteLogin login = new ExecuteLogin(carnet, token);
            login.setAuth_Token(carnet, token);
            login.execute();
        }else{
            //LoginManager.getInstance().logOut();

            SharedPreferences preferences = this.getSharedPreferences(USER_PREFERENCES, MODE_PRIVATE);
            preferences.edit().putString(PREFERENCE_CARNET, "").apply();
            preferences.edit().putString(PREFERENCE_AUTH_TOKEN, "").apply();
            preferences.edit().putBoolean(PREFERENCE_SESION_ACTIVA, false).apply();
        }

        txtRegistrar = findViewById(R.id.txtRegistrar);
        txtRegistrar.setText(Html.fromHtml(getResources().getString(R.string.strRegistrateAqui)));

        edtCarnetLogin = findViewById(R.id.txtCarnet);
        edtPasswordLogin = findViewById(R.id.txtPass);

        chckSesionActiva = findViewById(R.id.chckSesionActva);
    }

    public void loginClicked(View view){
        carnet = edtCarnetLogin.getText().toString();
        String pass = edtPasswordLogin.getText().toString();
        //Validar si las credenciales son correctas
        ExecuteLogin login = new ExecuteLogin(carnet, pass);
        login.execute();
    }


    public void iniciarSesion(JSONObject response){
        Usuario_Singleton user = Usuario_Singleton.getInstance();

        try {
            response = response.getJSONObject("data");
            user.setId(response.getString("id"));
            user.setNombre(response.getString("nombre"));
            user.setCarnet(response.getString("carnet"));
            user.setEmail(response.getString("email"));
            user.setCarrera(response.getString("carrera"));
            user.setUrl_foto(response.getString("foto"));
            user.setUrl_foto_rounded(response.getString("rfoto"));
            user.setAuth_token(response.getString("auth_token"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        HttpGetBitmap request = new HttpGetBitmap();
        Bitmap coverImage = null;
        try {
            coverImage = request.execute(user.getUrl_foto()).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        user.setFoto(coverImage);

        HttpGetBitmap request_r = new HttpGetBitmap();
        Bitmap coverImage_r = null;
        try {
            coverImage_r = request_r.execute(user.getUrl_foto_rounded()).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        user.setFoto_rounded(coverImage_r);
        //Si son correctas...hacer:
        if(chckSesionActiva.isChecked()){
            guardarUsuarioSesion(user.getEmail(), user.getAuth_token());
        }

        ExecuteGetAmigos executeGetAmigos = new ExecuteGetAmigos();
        executeGetAmigos.execute();

        //En el metodo cargarAmigos es donde Hace el cambio de Activity (Intent)
        /*Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();*/
    }

    public void signinClicked(View view){
        //Aqui llama a la activity de registrar
        //Intent intent = new Intent(getApplicationContext(), RegistrarActivity.class);
        //startActivity(intent);
    }
    public void clickRegistrate(View view){
        Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
        startActivity(intent);
    }

    //------------------------------------------------------------------------------------------------------//
    //----------------------------- Obtiene/Guarda las preferencias de sesion ------------------------------//
    //------------------------------------------------------------------------------------------------------//
    public void guardarUsuarioSesion(String correo, String auth_token){
        SharedPreferences preferences = getSharedPreferences(USER_PREFERENCES, MODE_PRIVATE);
        //Esto es para probar unicamente...después habría que ver si lo que se guarda son todos los datos del usuario o que...
        preferences.edit().putString(PREFERENCE_CARNET, carnet).apply();
        preferences.edit().putString(PREFERENCE_AUTH_TOKEN, auth_token).apply();
        preferences.edit().putBoolean(PREFERENCE_SESION_ACTIVA, chckSesionActiva.isChecked()).apply();
    }

    public static void actualizarAuth_Token(String auth_token, Context c){
        SharedPreferences preferences = c.getSharedPreferences(USER_PREFERENCES, MODE_PRIVATE);

        preferences.edit().putString(PREFERENCE_AUTH_TOKEN, auth_token).apply();
        //preferences.edit().putString("tokenAux", auth_token).apply();
    }

    public static void cerrarSesion(Context c){
        Usuario_Singleton user = Usuario_Singleton.getInstance();
        user.setId("");
        user.setNombre("");
        user.setEmail("");
        user.setUrl_foto("");
        user.setUrl_foto_rounded("");
        user.setAuth_token("");
        user.setFoto(null);
        user.setFoto_rounded(null);
        user.setListaAmigos(new ArrayList<Usuario>());

        SharedPreferences preferences = c.getSharedPreferences(USER_PREFERENCES, MODE_PRIVATE);

        preferences.edit().putString(PREFERENCE_CARNET, "").apply();
        preferences.edit().putString(PREFERENCE_AUTH_TOKEN, "").apply();
        preferences.edit().putBoolean(PREFERENCE_SESION_ACTIVA, false).apply();
    }

    public String[] getUsuarioSesion(){
        String[] userData = new String[2];
        SharedPreferences preferences = getSharedPreferences(USER_PREFERENCES, MODE_PRIVATE);
        //Esto es para probar unicamente...después habría que ver si lo que se obtiene son todos los datos del usuario o que (segun lo que se haya guardado)...
        userData[0] = preferences.getString(PREFERENCE_CARNET, "");
        userData[1] = preferences.getString(PREFERENCE_AUTH_TOKEN, "");
        return userData;
    }

    public boolean getEstadoSesion(){
        SharedPreferences preferences = getSharedPreferences(USER_PREFERENCES, MODE_PRIVATE);
        return preferences.getBoolean(PREFERENCE_SESION_ACTIVA, false);
    }
    //------------------------------------------------------------------------------------------------------//
    //------------------------------------------------------------------------------------------------------//
    //------------------------------------------------------------------------------------------------------//


    //------------------------------------------------------------------------------------------------------//
    public class ExecuteLogin extends AsyncTask<String, Void, String> {
        private String name;
        private String carnet;
        private String password;
        private String auth_token;
        int tipoAutenticacion = 0;// 0-Formulario, 1-Facebook, 2-Authentication Token(sesion abierta)
        private boolean isLogged = false;

        //Login con los campos de carnet y contraseña
        public ExecuteLogin(String carnet, String password){
            this.carnet = carnet;
            this.password = password;
            tipoAutenticacion = 0;
        }

        //Login con facebook
        public ExecuteLogin(String name, String carnet, String auth_token){
            this.name = name;
            this.carnet = carnet;
            this.password = "";
            this.auth_token = auth_token.substring(0, 10);
            tipoAutenticacion = 1;
        }

        //set Authentication Token
        public void setAuth_Token(String carnet, String auth_token){
            this.carnet = carnet;
            this.auth_token = auth_token;
            tipoAutenticacion = 2;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //rlLoader.setVisibility(View.VISIBLE);
            //rlLogin.setVisibility(View.INVISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {

            API_Access api = API_Access.getInstance();
            if(tipoAutenticacion == 2){
                //login con sesion ya abierta de antes
                isLogged = api.login_token(carnet, auth_token);
            }else if(tipoAutenticacion == 1){
                //login con facebook
                //isLogged = api.login_facebook(name, carnet, auth_token);
                isFacebook = true;
            }else if (tipoAutenticacion == 0){
                //login con los campos del formulario (carnet, password)
                isLogged = api.login(carnet, password);
                isFacebook = false;
            }


            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(isLogged){
                iniciarSesion(API_Access.getInstance().getJsonObjectResponse());
            }else{
                String mensaje = "Error al iniciar sesión";
                try {
                    mensaje = (API_Access.getInstance().getJsonObjectResponse()).getString("message");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Toast.makeText(LoginActivity.this, mensaje, Toast.LENGTH_SHORT).show();
                //rlLoader.setVisibility(View.INVISIBLE);
                //rlLogin.setVisibility(View.VISIBLE);
            }
        }
    }

    public void cargarAmigos(JSONObject response) {
        Usuario_Singleton user = Usuario_Singleton.getInstance();
        try {
            JSONArray jsonAmigos = response.getJSONArray("amigos");
            for(int i = 0; i < jsonAmigos.length(); i++){
                JSONObject amigo_i = (JSONObject) jsonAmigos.get(i);

                Usuario amigo = new Usuario();
                amigo.setId(amigo_i.getInt("id"));
                amigo.setNombre(amigo_i.getString("nombre"));
                amigo.setCarnet(amigo_i.getString("carnet"));
                amigo.setEmail(amigo_i.getString("email"));
                amigo.setCarrera(amigo_i.getString("carrera"));
                amigo.setLink_foto(amigo_i.getString("foto"));
                amigo.setLink_rfoto(amigo_i.getString("rfoto"));

                user.addAmigo(amigo);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public class ExecuteGetAmigos extends AsyncTask<String, Void, String> {
        private boolean isOk = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //rlLoader.setVisibility(View.VISIBLE);
            //rlLogin.setVisibility(View.INVISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {

            API_Access api = API_Access.getInstance();
            isOk = api.getAmigos(Usuario_Singleton.getInstance().getId());

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(isOk){
                cargarAmigos(API_Access.getInstance().getJsonObjectResponse());
            }else{
                String mensaje = "Error al obtener amigos";
                try {
                    mensaje = (API_Access.getInstance().getJsonObjectResponse()).getString("message");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Toast.makeText(LoginActivity.this, mensaje, Toast.LENGTH_SHORT).show();
                //rlLoader.setVisibility(View.INVISIBLE);
                //rlLogin.setVisibility(View.VISIBLE);
            }
        }
    }

}