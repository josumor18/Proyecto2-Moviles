package moviles.apps.proyecto2.friendtec.Business;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;


import org.json.JSONException;


import java.io.File;
import java.io.IOException;

import moviles.apps.proyecto2.friendtec.Data.API_Access;
import moviles.apps.proyecto2.friendtec.R;

public class EditProfileActivity extends AppCompatActivity {

    EditText edt_correo;
    EditText edt_nombre;
    EditText edt_contasenaActual;
    EditText edt_contrasenaNueva;
    EditText edt_contrasenaConf;

    private static int IMG_RESULT = 1;
    String ImageDecode;
    String picturePath;
    Intent intent;
    String[] FILE;

    ImageButton btn_loadImage;

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
        btn_loadImage = findViewById(R.id.img_Perfil);

        btn_guardar = findViewById(R.id.btn_guardar);
        btn_cambiarCon = findViewById(R.id.btn_cambiarCon);

        edt_nombre.setText(user.getNombre());
        edt_correo.setText(user.getEmail());

        btn_loadImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                /*intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(intent, IMG_RESULT);*/
                intent = new Intent();
// Show only images, no videos or anything else
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
// Always show the chooser (if there are multiple options available)
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), IMG_RESULT);


            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {




                if (requestCode == IMG_RESULT && resultCode == RESULT_OK
                    && null != data) {


                Uri URI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), URI);
                    // Log.d(TAG, String.valueOf(bitmap));

                    btn_loadImage.setImageBitmap(bitmap);

                    //GET IMAGE PATH
                    /*String[] projection = { MediaStore.Images.Media.DATA };

                    Cursor cursor = getContentResolver().query(URI, projection, null, null, null);
                    cursor.moveToFirst();


                    int columnIndex = cursor.getColumnIndex(projection[0]);
                    picturePath = cursor.getString(columnIndex); // returns null
                    cursor.close();*/
                    //picturePath = getRealPathFromURI(URI);
                    //picturePath = getRealPathFromURI(URI);
                    //picturePath = getFileNameByUri(this,URI);

                    Toast.makeText(this, picturePath, Toast.LENGTH_LONG).show();


                } catch (IOException e) {
                    e.printStackTrace();
                }
                /*String[] FILE = { MediaStore.Images.Media.DATA };


                Cursor cursor = getContentResolver().query(URI,
                        FILE, null, null, null);

                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(FILE[0]);
                ImageDecode = cursor.getString(columnIndex);
                cursor.close();

                btn_loadImage.setImageBitmap(BitmapFactory
                        .decodeFile(ImageDecode));*/

            }
        } catch (Exception e) {
            Toast.makeText(this, "Please try again", Toast.LENGTH_LONG)
                    .show();
        }
    }
    public static String getFileNameByUri(Context context, Uri uri)
    {
        String fileName="unknown";//default fileName
        Uri filePathUri = uri;
        if (uri.getScheme().toString().compareTo("content")==0)
        {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            if (cursor.moveToFirst())
            {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);//Instead of "MediaStore.Images.Media.DATA" can be used "_data"
                filePathUri = Uri.parse(cursor.getString(column_index));
                fileName = filePathUri.getLastPathSegment().toString();

            }
        }
        else if (uri.getScheme().compareTo("file")==0)
        {
            fileName = filePathUri.getLastPathSegment().toString();
        }
        else
        {
            fileName = fileName+"_"+filePathUri.getLastPathSegment();
        }
        return fileName;
    }

    /*private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }*/

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
