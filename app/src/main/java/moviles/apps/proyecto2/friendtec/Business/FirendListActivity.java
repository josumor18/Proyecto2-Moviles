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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.ExecutionException;

import moviles.apps.proyecto2.friendtec.Data.API_Access;
import moviles.apps.proyecto2.friendtec.Data.HttpGetBitmap;
import moviles.apps.proyecto2.friendtec.R;

public class FirendListActivity extends AppCompatActivity {

    Toolbar toolbar;
    ListView lvAmigos;
    Usuario_Singleton user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firend_list);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Lista de Amigos");
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lvAmigos = findViewById(R.id.lv_amigos);

        user = Usuario_Singleton.getInstance();


        lvAmigos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(),ProfileActivity.class);
                int idUser_enviar = user.getListaAmigos().get(position).getId();
                //Toast.makeText(FirendListActivity.this,user.getListaAmigos().get(position).getNombre(),Toast.LENGTH_LONG ).show();
                intent.putExtra("idUser",idUser_enviar);
                startActivity(intent);
            }
        });
        List<Usuario> x = user.getListaAmigos();

        lvAmigos.setAdapter(new AmigosAdapter());
    }


    public class AmigosAdapter extends BaseAdapter {

        public AmigosAdapter() {
            super();
        }

        @Override
        public int getCount() {
            return user.getListaAmigos().size();
        }

        @Override
        public Object getItem(int i) {
            return user.getListaAmigos().get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            LayoutInflater inflater = getLayoutInflater();
            if (view == null) {
                view = inflater.inflate(R.layout.friends_list_item, null);
            }

            ImageView imgUsuario = view.findViewById(R.id.img_UserList);
            TextView txtUsername = view.findViewById(R.id.txtUserList);


            HttpGetBitmap request = new HttpGetBitmap();
            Bitmap userImage = null;
            try {
                userImage = request.execute(user.getListaAmigos().get(i).getLink_foto()).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            if(userImage == null){
                userImage = BitmapFactory.decodeResource( getApplicationContext().getResources(),
                        R.drawable.user_foto);
            }
            imgUsuario.setImageBitmap(userImage);

            txtUsername.setText(user.getListaAmigos().get(i).getNombre());

            return view;
        }
    }

    /*public class ExecuteGetFriends extends AsyncTask<String, Void, String> {
        boolean isOk = false;
        String idUser;

        public ExecuteGetFriends(String idUser) {
            this.idUser = idUser;

        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            API_Access api = API_Access.getInstance();


            isOk = api.getAmigos(idUser);

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(isOk){
                String token = null;

                    JSONObject result = API_Access.getInstance().getJsonObjectResponse();
                    cargarAmigos(result);

                    //Toast.makeText(getActivity(), "Lista obtenida", Toast.LENGTH_SHORT).show();


            }
            else
                Toast.makeText(FirendListActivity.this, "Lista amigos no obtenidos", Toast.LENGTH_SHORT).show();


        }
    }*/


}
