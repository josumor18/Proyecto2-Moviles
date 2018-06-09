package moviles.apps.proyecto2.friendtec.Business;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import moviles.apps.proyecto2.friendtec.Data.API_Access;
import moviles.apps.proyecto2.friendtec.R;

public class SignUpActivity extends AppCompatActivity {

    EditText edt_carne;
    EditText edt_correo;
    EditText edt_nombre;
    EditText edt_contrasena;
    EditText edt_confcontrasena;
    Button btn_registrar;
    Spinner sp_carrera;

    ArrayList<EditText> campos_obligatorios;

    private static String[] listCarreras = {"Ing.Computacion","Ing.Electrica","Administracion de Empresas", "Ing.Ambiental",
        "Ing.Civil", "Ing.Computadores"};

    String carreraSelected = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        edt_carne = findViewById(R.id.edt_carne);
        edt_correo = findViewById(R.id.edt_email);
        edt_nombre = findViewById(R.id.edt_nombre);
        edt_contrasena = findViewById(R.id.edt_password);
        edt_confcontrasena = findViewById(R.id.edt_confirm_password);
        btn_registrar = findViewById(R.id.btn_register);
        sp_carrera = findViewById(R.id.sp_carrera);

        campos_obligatorios = new ArrayList<>();
        campos_obligatorios.add(edt_carne);
        campos_obligatorios.add(edt_correo);
        campos_obligatorios.add(edt_contrasena);

        ArrayAdapter adapterCarrera = new ArrayAdapter(this.getApplicationContext(),android.R.layout.simple_spinner_item,listCarreras);

        adapterCarrera.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        sp_carrera.setAdapter(adapterCarrera);

        sp_carrera.setSelection(0, true);
        View vi = sp_carrera.getSelectedView();
        ((TextView)vi).setTextColor(getResources().getColor(R.color.colorPrimary));

        sp_carrera.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                //Change the selected item's text color
                ((TextView) view).setTextColor(getResources().getColor(R.color.colorPrimary));
                carreraSelected = listCarreras[sp_carrera.getSelectedItemPosition()];
                Toast.makeText(SignUpActivity.this, carreraSelected, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
            }
        });
    }

    public void registerClicked (View view){

        //Validaciones de campos
        String pass1 = edt_contrasena.getText().toString();
        String pass2 = edt_confcontrasena.getText().toString();

        for(EditText edit: campos_obligatorios){
            if(TextUtils.isEmpty(edit.getText())){
                edit.setError("Campo obligatorio");
            }
        }

        if(pass1.length()<6){
            //Toast.makeText(RegistrarActivity.this, "La contraseña debe contener minimo 6 caracteres", Toast.LENGTH_LONG).show();
            edt_contrasena.setError("Min. 6 caracteres");
            edt_contrasena.setText("");
            edt_confcontrasena.setText("");
        }
        else if(!pass1.equals(pass2)){

            //txtInvalidRegis.setVisibility(1);
            Toast.makeText(this, "Contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            edt_contrasena.setText("");
            edt_confcontrasena.setText("");

        }
        else{


            ExecuteRegister register = new ExecuteRegister(edt_carne.getText().toString(),carreraSelected
                    ,edt_correo.getText().toString(), edt_nombre.getText().toString()
                    ,edt_contrasena.getText().toString());
            register.execute();

        }

    }

    public class ExecuteRegister extends AsyncTask<String,Void,String> {
        private String name;
        private String carrera;
        private String carne;
        private String email;
        private String password;
        private boolean isRegistered;

        public ExecuteRegister(String carne, String carrera, String email,String name, String password){
            this.carne = carne;
            this.carrera = carrera;
            this.name = name;
            this.email = email;
            this.password = password;
        }

        @Override
        protected String doInBackground(String... strings) {


            API_Access api = API_Access.getInstance();
            isRegistered = api.register(carne,carrera,email,name,password);



            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(isRegistered) {
                Toast.makeText(SignUpActivity.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                finish();
            }
            else {
                /*String mensaje = "Fail";
                try {
                    mensaje = (API_Access.getInstance().getJsonObjectResponse()).getString("message");
                } catch (JSONException e) {
                    e.printStackTrace();
                }*/

                Toast.makeText(SignUpActivity.this, "Registro fallido" , Toast.LENGTH_SHORT).show();
            }

        }




    }
}
