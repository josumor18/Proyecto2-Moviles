package moviles.apps.proyecto2.friendtec.Business;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import moviles.apps.proyecto2.friendtec.Data.API_Access;
import moviles.apps.proyecto2.friendtec.Data.HttpGetBitmap;
import moviles.apps.proyecto2.friendtec.R;

public class RequestsActivity extends AppCompatActivity {

    Toolbar toolbar;
    ListView lv_requests;
    ImageView imguser;
    TextView txt_name;
    Button btn_aceptar;
    Button btn_rechazar;
    private ArrayList<Usuario> requests = new ArrayList<Usuario>();
    Usuario_Singleton user;
    int id_user2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Solicitudes de amistad");
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lv_requests = findViewById(R.id.lv_requests);
        user = Usuario_Singleton.getInstance();

        ExecuteGetRequests executeGetRequests = new ExecuteGetRequests();
        executeGetRequests.execute();
    }


    private void cargarRequests(JSONObject jsonResult){

        try {
            requests.clear();

            JSONArray jsonRequests = jsonResult.getJSONArray("solicitudes");
            for(int i = 0; i < jsonRequests.length(); i++) {
                JSONObject request = (JSONObject) jsonRequests.get(i);

                Usuario usuario = new Usuario();
                usuario.setId(request.getInt("id"));
                usuario.setNombre(request.getString("nombre"));
                usuario.setCarnet(request.getString("carnet"));
                usuario.setEmail(request.getString("email"));
                usuario.setCarrera(request.getString("carrera"));
                usuario.setLink_foto(request.getString("foto"));
                usuario.setLink_rfoto(request.getString("rfoto"));

                requests.add(usuario);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        lv_requests.setAdapter(new RequestsAdapter());
    }

    public class RequestsAdapter extends BaseAdapter {

        public RequestsAdapter() {
            super();
        }

        @Override
        public int getCount() {
            return requests.size();
        }

        @Override
        public Object getItem(int i) {
            return requests.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            LayoutInflater inflater = getLayoutInflater();
            if (view == null) {
                view = inflater.inflate(R.layout.requests_list_item, null);
            }

            imguser = view.findViewById(R.id.img_UserRequest);
            txt_name = view.findViewById(R.id.txtUsernameRequest);
            btn_aceptar = view.findViewById(R.id.btn_acceptRequest);
            btn_rechazar = view.findViewById(R.id.btn_rejectRequest);



            HttpGetBitmap request = new HttpGetBitmap();
            Bitmap userImage = null;
            try {
                userImage = request.execute(requests.get(i).getLink_foto()).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            if(userImage == null){
                userImage = BitmapFactory.decodeResource( getApplicationContext().getResources(),
                        R.drawable.user_foto);
            }
            imguser.setImageBitmap(userImage);

            txt_name.setText(requests.get(i).getNombre());

            btn_aceptar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    id_user2 = requests.get(i).getId();
                    ExecuteActionRequest executeActionRequest = new ExecuteActionRequest(Integer.toString(id_user2),0);
                    executeActionRequest.execute();

                }
            });

            btn_rechazar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    id_user2 = requests.get(i).getId();
                    ExecuteActionRequest executeActionRequest = new ExecuteActionRequest(Integer.toString(id_user2),1);
                    executeActionRequest.execute();

                }
            });


            return view;
        }
    }

    public class ExecuteGetRequests extends AsyncTask<String, Void, String> {
        boolean isOk = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected String doInBackground(String... strings) {
            API_Access api = API_Access.getInstance();

            isOk = api.getRequests(user.getId());

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(isOk){
                cargarRequests(API_Access.getInstance().getJsonObjectResponse());
            }else{
                String mensaje = "Error al obtener los requests";

                Toast.makeText(RequestsActivity.this, mensaje, Toast.LENGTH_SHORT).show();
            }
            //rlLoaderEmisoras.setVisibility(View.INVISIBLE);
        }
    }

    public class ExecuteActionRequest extends AsyncTask<String, Void, String> {
        boolean isOk = false;
        int accion; //0: aceptar 1: rechazar
        String id_user2;

        ExecuteActionRequest(String id_user2, int accion){
            this.id_user2 = id_user2;
            this.accion = accion;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected String doInBackground(String... strings) {
            API_Access api = API_Access.getInstance();
            String id = user.getId();
            String auth = user.getAuth_token();

            if(accion == 0)
                isOk = api.accept_request(id,id_user2,auth);

            else
                isOk = api.reject_request(id,id_user2,auth);

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(isOk){
                /*String token = null;
                try {
                    token = (API_Access.getInstance().getJsonObjectResponse()).getString("auth_token");
                    Usuario_Singleton.getInstance().setAuth_token(token);
                    LoginActivity.actualizarAuth_Token(token, RequestsActivity.this);
                } catch (JSONException e) {
                    e.printStackTrace();
                }*/
                ExecuteGetRequests executeGetRequests = new ExecuteGetRequests();
                executeGetRequests.execute();
                //Toast.makeText(RequestsActivity.this, "Cambio exitoso", Toast.LENGTH_SHORT).show();

            }else{
                String mensaje = "Error";

                Toast.makeText(RequestsActivity.this, mensaje, Toast.LENGTH_SHORT).show();
            }
            //rlLoaderEmisoras.setVisibility(View.INVISIBLE);
        }
    }
}
