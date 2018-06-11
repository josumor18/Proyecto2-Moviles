package moviles.apps.proyecto2.friendtec.Business;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import moviles.apps.proyecto2.friendtec.Data.API_Access;
import moviles.apps.proyecto2.friendtec.Data.HttpGetBitmap;
import moviles.apps.proyecto2.friendtec.R;

public class ChatActivity extends AppCompatActivity {

    Toolbar toolbar;
    private int id_chat;
    private int id_friend;
    Bitmap userImage;
    ListView lvMensajesChat;
    TextView txtMessage;
    Button btnSendMessage;
    private boolean shutdown = false;
    private boolean nuevos = false;

    Runnable runnable;
    final Handler handler  = new Handler();

    private ArrayList<Mensaje> lista_mensajes = new ArrayList<Mensaje>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        id_chat = getIntent().getIntExtra("id_chat", 0);
        // Ir a pedir los chats
        id_friend = getIntent().getIntExtra("id_friend", 0);
        Usuario amigo = Usuario_Singleton.getInstance().getAmigo(id_friend);

        HttpGetBitmap request = new HttpGetBitmap();
        userImage = null;
        try {
            userImage = request.execute(amigo.getLink_rfoto()).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if(userImage == null){
            userImage = BitmapFactory.decodeResource( getApplicationContext().getResources(),
                    R.drawable.user_rfoto);
        }

        int toolbar_length = 1;
        // Calculate ActionBar height
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
        {
            toolbar_length = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
        }
        Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(userImage, toolbar_length - 30, toolbar_length - 30, true));
        toolbar.setLogo(d);
        toolbar.setTitle("  " + amigo.getNombre());
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lvMensajesChat = findViewById(R.id.lvMensajesChat);

        ExecuteGetMessages executeGetMessages = new ExecuteGetMessages();
        executeGetMessages.execute();

        txtMessage = findViewById(R.id.txtMessage);

        runnable = new Runnable() {
            public void run() {
                if(shutdown){
                    finish();
                }else{
                    ExecuteGetMessages executeGetMessages1 = new ExecuteGetMessages();
                    executeGetMessages1.execute();
                }


                handler.postDelayed(this, 5000);
            }
        };

        handler.postDelayed(runnable, 1000);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        shutdown = true;
        return true;
    }

    public void send_message(View v){
        String mensaje = txtMessage.getText().toString();
        if(!(mensaje.isEmpty())){
            ExecuteSendMessage executeSendMessage = new ExecuteSendMessage();
            executeSendMessage.execute(mensaje);
        }
    }

    public void cargarMensajes(JSONObject response) {
        Usuario_Singleton user = Usuario_Singleton.getInstance();
        try {
            JSONArray jsonMessages = response.getJSONArray("mensajes");
            if(jsonMessages.length() > lista_mensajes.size()){
                nuevos = true;
                lista_mensajes = new ArrayList<Mensaje>();
                for(int i = 0; i < jsonMessages.length(); i++){
                    JSONObject jsonMensaje = (JSONObject) jsonMessages.get(i);

                    String texto = jsonMensaje.getString("message");
                    boolean enviado = jsonMensaje.getBoolean("enviado");

                    lista_mensajes.add(new Mensaje(texto, enviado));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(nuevos){
            lvMensajesChat.setAdapter(new MessagesAdapter());
            nuevos = false;
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    public class MessagesAdapter extends BaseAdapter {

        public MessagesAdapter() {
            super();
        }

        @Override
        public int getCount() {
            return lista_mensajes.size();
        }

        @Override
        public Object getItem(int i) {
            return lista_mensajes.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            LayoutInflater inflater = getLayoutInflater();
            if (view == null) {
                view = inflater.inflate(R.layout.message_item, null);
            }


            ImageView imgUserMessage = view.findViewById(R.id.imgUserMessage);
            ImageView imgFriendMessage = view.findViewById(R.id.imgFriendMessage);
            TextView txtFriendMessage = view.findViewById(R.id.txtFriendMessage);
            //txtFriendMessage.setBackgroundResource(R.drawable.rounded_message_text);
            TextView txtUserMessage = view.findViewById(R.id.txtUserMessage);
            //txtUserMessage.setBackgroundResource(R.drawable.rounded_my_message_text);

            if(lista_mensajes.get(i).enviado){
                imgFriendMessage.setVisibility(View.INVISIBLE);
                imgUserMessage.setImageBitmap(Usuario_Singleton.getInstance().getFoto_rounded());
                txtFriendMessage.setVisibility(View.INVISIBLE);
                txtUserMessage.setText(lista_mensajes.get(i).mensaje);
            }else {
                imgUserMessage.setVisibility(View.INVISIBLE);
                imgFriendMessage.setImageBitmap(userImage);
                txtUserMessage.setVisibility(View.INVISIBLE);
                txtFriendMessage.setText(lista_mensajes.get(i).mensaje);
            }



            return view;
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    public class ExecuteGetMessages extends AsyncTask<String, Void, String> {
        boolean isOk = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //rlLoaderEmisoras.setVisibility(View.VISIBLE);
            //rlLogin.setVisibility(View.INVISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {
            API_Access api = API_Access.getInstance();
            Usuario_Singleton user = Usuario_Singleton.getInstance();
            isOk = api.getChatMessages(Integer.toString(id_chat));

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(isOk){
                cargarMensajes(API_Access.getInstance().getJsonObjectResponse());
            }else{
                String mensaje = "Error al obtener los mensajes";

                Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_SHORT).show();
            }
            //rlLoaderEmisoras.setVisibility(View.INVISIBLE);
        }
    }

    public class Mensaje{
        public String mensaje;
        public boolean enviado;

        public Mensaje(String mensaje, boolean enviado) {
            this.mensaje = mensaje;
            this.enviado = enviado;
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    public class ExecuteSendMessage extends AsyncTask<String, Void, String> {
        boolean isOk = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //rlLoaderEmisoras.setVisibility(View.VISIBLE);
            //rlLogin.setVisibility(View.INVISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {
            API_Access api = API_Access.getInstance();
            Usuario_Singleton user = Usuario_Singleton.getInstance();
            isOk = api.sendMessage(user.getId(), Integer.toString(id_friend), Integer.toString(id_chat), strings[0]);

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(isOk){
                ExecuteGetMessages executeGetMessages = new ExecuteGetMessages();
                executeGetMessages.execute();

                txtMessage.setText("");
            }else{
                String mensaje = "Error al enviar mensaje";

                Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_SHORT).show();
            }
            //rlLoaderEmisoras.setVisibility(View.INVISIBLE);
        }
    }
}
