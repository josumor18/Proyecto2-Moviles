package moviles.apps.proyecto2.friendtec.Business;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;


import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;

import org.json.JSONException;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.concurrent.ExecutionException;

import moviles.apps.proyecto2.friendtec.Data.API_Access;
import moviles.apps.proyecto2.friendtec.Data.HttpGetBitmap;
import moviles.apps.proyecto2.friendtec.R;

public class EditProfileActivity extends AppCompatActivity {

    Toolbar toolbar;
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

    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    static SecureRandom rnd = new SecureRandom();

    ImageButton btn_loadImage;

    Button btn_guardar;
    Button btn_cambiarCon;

    Bitmap bitmap;
    Usuario_Singleton user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Editar Perfil");
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

        btn_loadImage.setImageBitmap(user.getFoto());

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
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), URI);
                    // Log.d(TAG, String.valueOf(bitmap));

                    btn_loadImage.setImageBitmap(bitmap);


                    //picturePath = getFileName(URI);
                    picturePath = getPath(EditProfileActivity.this,URI);
                    Toast.makeText(this, picturePath, Toast.LENGTH_LONG).show();

                    ExecuteUploaded executeUploaded = new ExecuteUploaded();
                    executeUploaded.execute();

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        } catch (Exception e) {
            Toast.makeText(this, "Error", Toast.LENGTH_LONG)
                    .show();
        }
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

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @author paulburke
     */
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private String getRealPathFromURI(Uri contentURI) {
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

    public class ExecuteUploaded extends AsyncTask<String, Void, String> {
        boolean isOk;
        @Override
        protected String doInBackground(String... strings) {


            Cloudinary cloudinary = new Cloudinary("cloudinary://523642389612375:w_BVcUQ7VFZ8IQj-iE1-zbqv5iU@ddgkzz2gk");
            try {
                String path = picturePath; //Aquí sería la ruta donde se haya seleccionado la imagen
                File file = new File(picturePath);
                ///////////////////////////////////////////////////////////
                // Generar el random para identificador de la foto
                StringBuilder sb = new StringBuilder(10);
                for( int i = 0; i < 10; i++ )
                    sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
                String rand = sb.toString();
                ///////////////////////////////////////////////////////////

                cloudinary.uploader().upload(file.getAbsoluteFile(), ObjectUtils.asMap("public_id", rand));//emptyMap());

                String photoR = (cloudinary.url().transformation(new Transformation()
                        .radius(360).crop("scale").chain()
                        .angle(0)).imageTag(rand + ".png")).split("\'")[1]; //Random
                // Normal:
                String photoN = (cloudinary.url().transformation(new Transformation()
                        .radius(0).crop("scale").chain()
                        .angle(0)).imageTag(rand + ".png")).split("\'")[1];


                API_Access api = API_Access.getInstance();

                if(api.change_image(user.getId(),photoN,photoR)){

                    HttpGetBitmap request = new HttpGetBitmap();
                    Bitmap userImage = null;
                    try {
                        userImage = request.execute(photoR).get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    if(userImage == null){
                        userImage = BitmapFactory.decodeResource( EditProfileActivity.this.getResources(),
                                R.drawable.user_rfoto);
                    }

                    user.setFoto(bitmap);
                    user.setFoto_rounded(userImage);
                }


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }
}
