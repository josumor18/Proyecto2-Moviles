package moviles.apps.proyecto2.friendtec.Business;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import moviles.apps.proyecto2.friendtec.Data.API_Access;
import moviles.apps.proyecto2.friendtec.R;

public class NewPostActivity extends AppCompatActivity {

    EditText edtContenido;
    TextView txtNombre;
    Button btnPublicar;
    Usuario_Singleton user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        user = Usuario_Singleton.getInstance();

        edtContenido = findViewById(R.id.edt_contenido);
        btnPublicar = findViewById(R.id.btn_publicar);
        txtNombre = findViewById(R.id.txt_nombre);

        txtNombre.setText(user.getNombre());

    }

    public void publicarClicked(View view){
        if (!TextUtils.isEmpty(edtContenido.getText()) ) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            Date date = new Date();

            String fecha = dateFormat.format(date);

            ExecutePost executePost = new ExecutePost(user.getId(),
                    edtContenido.getText().toString(),"", fecha);

            executePost.execute();
        }
        else {
            edtContenido.setError("Campo Requerido");
        }
    }


    public class ExecutePost extends AsyncTask<String,Void,String> {
        private String idUser;
        private String contenido;
        private String urlImage;
        private String fecha;
        private boolean isPosted = false;

        public ExecutePost(String idUser, String contenido,String urlImage,String fecha) {
            this.idUser = idUser;
            this.contenido = contenido;
            this.urlImage = urlImage;
            this.fecha = fecha;
        }



        @Override
        protected String doInBackground(String... strings) {


            API_Access api = API_Access.getInstance();

            isPosted = api.new_post(idUser, contenido,urlImage,fecha);

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (isPosted) {

                Toast.makeText(NewPostActivity.this, "Publicacion exitosa", Toast.LENGTH_SHORT).show();
                //finish();
            } else
                Toast.makeText(NewPostActivity.this, "Publicacion fallida", Toast.LENGTH_SHORT).show();

        }
    }
}
