package moviles.apps.proyecto2.friendtec.Business;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;

import moviles.apps.proyecto2.friendtec.Data.API_Access;
import moviles.apps.proyecto2.friendtec.R;

public class EditProfileActivity extends AppCompatActivity {

    EditText edt_correo;
    EditText edt_nombre;
    EditText edt_contasenaActual;
    EditText edt_contrasenaNueva;
    EditText edt_contrasenaConf;

    Button btn_guardar;
    Button btn_cambiarCon;

    Usuario_Singleton user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        user = Usuario_Singleton.getInstance();

        edt_correo = findViewById(R.id.edt_correo);
        edt_nombre = findViewById(R.id.edt_nombre);
        edt_contrasenaNueva = findViewById(R.id.edt_nuevaCon);
        edt_contasenaActual = findViewById(R.id.edt_contrasena);
        edt_contrasenaConf = findViewById(R.id.edt_nuevaConfCon);

        btn_guardar = findViewById(R.id.btn_guardar);
        btn_cambiarCon = findViewById(R.id.btn_cambiarCon);

        edt_nombre.setText(user.getNombre());
        edt_correo.setText(user.getEmail());
    }

    public void guardarCambiosClicked(View view){

        if (!TextUtils.isEmpty(edt_correo.getText()) && !TextUtils.isEmpty(edt_nombre.getText())) {

            ExecuteChange executeChange = new ExecuteChange(user.getId(),
                    edt_nombre.getText().toString(), edt_correo.getText().toString(),user.getAuth_token());

            executeChange.execute();
        }
        else {
            edt_nombre.setError("Campo Requerido");
            edt_correo.setError("Campo Requerido");
        }




    }
    public void cambiarContrasenaClicked(View view){
        String actualCont = edt_contasenaActual.getText().toString();

        String nuevaCont = edt_contrasenaNueva.getText().toString();
        boolean val1 = true;
        boolean val2 = true;

        if (nuevaCont.length() < 6 || TextUtils.isEmpty(nuevaCont)) {
            val1 = false;
            edt_contrasenaNueva.setError("Min. 6 caracteres");
            edt_contrasenaNueva.setText("");
            edt_contrasenaConf.setText("");
        }

        if (!nuevaCont.equals(edt_contrasenaConf.getText().toString())) {
            val2 = false;
            edt_contrasenaConf.setError("Contraseñas no coinciden");

        }

        if (val1 && val2) {
            ExecuteChange exChange = new ExecuteChange(user.getId(),edt_contasenaActual.getText().toString(),
                    edt_contrasenaNueva.getText().toString(),edt_contrasenaConf.getText().toString(),
                    user.getAuth_token());

            exChange.execute();
        }
    }

    public class ExecuteChange extends AsyncTask<String,Void,String> {
        private String id;
        private String email;
        private String name;
        private String password;
        private String new_password;
        private int tipoAut = 0;
        private String authToken;
        private boolean isChanged = false;

        public ExecuteChange(String id, String name, String email, String authToken) {
            this.name = name;
            this.email = email;
            this.id = id;
            this.authToken = authToken;
            tipoAut = 0;
        }

        public ExecuteChange(String id, String password, String new_password, String conf_password, String authToken) {
            this.id = id;
            this.password = password;
            this.new_password = new_password;
            this.authToken = authToken;
            tipoAut = 1;
        }


        @Override
        protected String doInBackground(String... strings) {


            API_Access api = API_Access.getInstance();
            if (tipoAut == 0)
                isChanged = api.change_user(id, name, email, authToken);
            else if (tipoAut == 1)
                isChanged = api.change_pass(id, password, new_password, authToken);


            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (isChanged) {
                String token = null;
                try {
                    token = (API_Access.getInstance().getJsonObjectResponse()).getString("authentication_token");
                    Usuario_Singleton.getInstance().setAuth_token(token);
                    //LoginActivity.actualizarAuth_Token(token, getApplicationContext());
                    //Si es Activity
                    LoginActivity.actualizarAuth_Token(token, EditProfileActivity.this);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                user.setEmail(edt_correo.getText().toString());
                user.setNombre(edt_nombre.getText().toString());
                Toast.makeText(EditProfileActivity.this, "Cambio exitoso", Toast.LENGTH_SHORT).show();
                //finish();
            } else
                Toast.makeText(EditProfileActivity.this, "Cambio no exitoso", Toast.LENGTH_SHORT).show();

        }
    }
}
