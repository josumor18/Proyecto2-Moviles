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

public class ProfileActivity extends AppCompatActivity {

    private ArrayList<Post> posts = new ArrayList<Post>();

    Toolbar toolbar;

    int idUser;
    Usuario usuario_perfil;
    Usuario_Singleton user;
    TextView txtNombre;
    TextView txtCarrera;
    ImageView imageView;
    ListView lvPosts;

    public void openChatClicked(View v){
        int id_chat = 0;
        for(Chat chat: MainActivity.lista_chats){
            if(idUser == chat.getId_friend()){
                id_chat = chat.getId_chat();
                break;
            }
        }
        Intent chatIntent = new Intent(getApplicationContext(), ChatActivity.class);
        chatIntent.putExtra("id_chat", id_chat);
        chatIntent.putExtra("id_friend", idUser);
        startActivity(chatIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Perfil de Usuario");
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        user = Usuario_Singleton.getInstance();
        txtNombre = findViewById(R.id.txt_nombre);
        txtCarrera = findViewById(R.id.txt_carrera);
        imageView = findViewById(R.id.imgView_perfil);
        lvPosts = findViewById(R.id.lv_postsPerfil);

        idUser = getIntent().getIntExtra("idUser",0);

        //Toast.makeText(ProfileActivity.this,idUser,Toast.LENGTH_LONG ).show();

        for(Usuario u : user.getListaAmigos()){
            if(u.getId() == idUser)
                usuario_perfil = u;
        }

        txtNombre.setText(usuario_perfil.getNombre());
        txtCarrera.setText(usuario_perfil.getCarrera());

        HttpGetBitmap request = new HttpGetBitmap();
        Bitmap userImage = null;
        try {
            userImage = request.execute(usuario_perfil.getLink_foto()).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if(userImage == null){
            userImage = BitmapFactory.decodeResource( getApplicationContext().getResources(),
                    R.drawable.user_foto);
        }
        imageView.setImageBitmap(userImage);

        ExecuteGetPosts executeGetPosts = new ExecuteGetPosts(Integer.toString(idUser));
        executeGetPosts.execute();



    }

    private void cargarPosts(JSONObject jsonResult){

        try {
            posts.clear();

            JSONArray jsonPosts = jsonResult.getJSONArray("posts");
            for(int i = 0; i < jsonPosts.length(); i++) {
                JSONObject post = (JSONObject) jsonPosts.get(i);


                addPostToList(new Post(post.getInt("id_user"), usuario_perfil.getNombre(),usuario_perfil.getLink_rfoto(),post.getString("contenido"), post.getString("foto"), post.getString("fecha_hora")));


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        lvPosts.setAdapter(new PostsAdapter());
    }

    private void addPostToList(Post nuevo){
        int cont = 0;
        for(cont = 0; cont < posts.size(); cont++){
            if(posts.get(cont).getFecha_hora().before(nuevo.getFecha_hora())){
                break;
            }
        }
        posts.add(cont, nuevo);
    }

    public class PostsAdapter extends BaseAdapter {

        public PostsAdapter() {
            super();
        }

        @Override
        public int getCount() {
            return posts.size();
        }

        @Override
        public Object getItem(int i) {
            return posts.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            LayoutInflater inflater = getLayoutInflater();
            if (view == null) {
                view = inflater.inflate(R.layout.posts_list_item, null);
            }

            ImageView imgUsuario = view.findViewById(R.id.imgUserPost);
            TextView txtUsername = view.findViewById(R.id.txtUsernamePost);
            TextView txtPost = view.findViewById(R.id.txtPost);
            ImageView imgImagePost = view.findViewById(R.id.imgImagePost);
            TextView txtFechaHora = view.findViewById(R.id.txtFechaHora);


            HttpGetBitmap request = new HttpGetBitmap();
            Bitmap userImage = null;
            try {
                userImage = request.execute(usuario_perfil.getLink_foto()).get();
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

            txtUsername.setText(usuario_perfil.getNombre());
            txtPost.setText(posts.get(i).getContenido());

            HttpGetBitmap request2 = new HttpGetBitmap();
            Bitmap postImage = null;
            try {
                postImage = request2.execute(posts.get(i).getLink_foto()).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            imgImagePost.setImageBitmap(postImage);

            txtFechaHora.setText(posts.get(i).getFechaHoraString());


            return view;
        }
    }

    public class ExecuteGetPosts extends AsyncTask<String, Void, String> {
        boolean isOk = false;
        String idUser;

        ExecuteGetPosts(String idUser){
            this.idUser=idUser;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //rlLoaderEmisoras.setVisibility(View.VISIBLE);
            //rlLogin.setVisibility(View.INVISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {
            API_Access api = API_Access.getInstance();

            isOk = api.getPostsProfile(idUser);

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(isOk){
                cargarPosts(API_Access.getInstance().getJsonObjectResponse());
            }else{
                String mensaje = "Error al obtener las publicaciones";

                Toast.makeText(ProfileActivity.this, mensaje, Toast.LENGTH_SHORT).show();
            }
            //rlLoaderEmisoras.setVisibility(View.INVISIBLE);
        }
    }
}
