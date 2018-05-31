package moviles.apps.proyecto2.friendtec.Business;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import moviles.apps.proyecto2.friendtec.Data.API_Access;
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
        //login.execute();
        login.execute();
    }


    public void iniciarSesion(JSONObject response){
        Usuario_Singleton user = Usuario_Singleton.getInstance();

        try {
            response = response.getJSONObject("data");
            user.setId(response.getString("id"));
            user.setNombre(response.getString("name"));
            user.setCarnet(response.getString("carnet"));
            user.setEmail(response.getString("email"));
            user.setFoto(response.getString("foto"));
            user.setAuth_token(response.getString("authentication_token"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Si son correctas...hacer:
        if(chckSesionActiva.isChecked()){
            guardarUsuarioSesion(user.getEmail(), user.getAuth_token());
        }

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void signinClicked(View view){
        //Aqui llama a la activity de registrar
        //Intent intent = new Intent(getApplicationContext(), RegistrarActivity.class);
        //startActivity(intent);
    }

    //------------------------------------------------------------------------------------------------------//
    //----------------------------- Obtiene/Guarda las preferencias de sesion ------------------------------//
    //------------------------------------------------------------------------------------------------------//
    public void guardarUsuarioSesion(String correo, String auth_token){
        SharedPreferences preferences = getSharedPreferences(USER_PREFERENCES, MODE_PRIVATE);
        //Esto es para probar unicamente...después habría que ver si lo que se guarda son todos los datos del usuario o que...
        preferences.edit().putString(PREFERENCE_CARNET, correo).apply();
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
        user.setFoto("");
        user.setAuth_token("");

        //Streaming.pause();
        //Streaming.cleanStreaming();

        /*LoginManager.getInstance().logOut();
        if(perfil != null){
            perfil.stopTracking();
            perfil = null;
        }
        email = "";*/
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
                //isLogged = api.login_token(carnet, auth_token);
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
}
