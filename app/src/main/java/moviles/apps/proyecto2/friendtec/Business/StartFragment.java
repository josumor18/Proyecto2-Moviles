package moviles.apps.proyecto2.friendtec.Business;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import moviles.apps.proyecto2.friendtec.Data.API_Access;
import moviles.apps.proyecto2.friendtec.Data.HttpGetBitmap;
import moviles.apps.proyecto2.friendtec.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class StartFragment extends Fragment {

    private ArrayList<Post> posts = new ArrayList<Post>();
    private HashMap<Integer, String> nombresUsuario = new HashMap<Integer, String>();
    private HashMap<Integer, String> fotosUsuario = new HashMap<Integer, String>();

    RelativeLayout rlLoader, rlStart;
    ListView lvPosts;
    FloatingActionButton ftbtnCratePost;

    public StartFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_start, container, false);
        rlLoader = v.findViewById(R.id.rlLoaderStart);
        rlStart = v.findViewById(R.id.rlStart);
        rlLoader.setVisibility(View.VISIBLE);
        rlStart.setVisibility(View.INVISIBLE);

        nombresUsuario.clear();
        lvPosts = v.findViewById(R.id.lvPosts);
        ftbtnCratePost = v.findViewById(R.id.ftbtnCreatePost);
        ftbtnCratePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentNewPost = new Intent(getActivity().getApplicationContext(), NewPostActivity.class);
                startActivity(intentNewPost);
            }
        });

        ExecuteGetPosts executeGetPosts = new ExecuteGetPosts();
        executeGetPosts.execute();
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        nombresUsuario.clear();
    }

    private void cargarPosts(JSONObject jsonResult) {

        try {
            posts.clear();
            String token = jsonResult.getString("auth_token");
            Usuario_Singleton.getInstance().setAuth_token(token);
            LoginActivity.actualizarAuth_Token(token, getActivity());
            //JSONArray jsonListUserPosts = jsonResult.getJSONArray("posts");
            JSONArray jsonListUserPosts = jsonResult.getJSONArray("posts");
            for (int i = 0; i < jsonListUserPosts.length(); i++) {
                //JSONArray jsonPosts = jsonListUserPosts.getJSONArray(i);
                //for(int j = 0; j < jsonPosts.length(); j++) {
                //JSONObject post = (JSONObject) jsonPosts.get(j);
                JSONObject post = (JSONObject) jsonListUserPosts.get(i);
                String infoUser = "[username]%[foto]";
                String nombre_usuario = nombresUsuario.get(post.getInt("id_user"));
                if (nombre_usuario == null) {
                    ExecuteGetUserInfo executeGetUserInfo = new ExecuteGetUserInfo();
                    try {
                        infoUser = executeGetUserInfo.execute(Integer.toString(post.getInt("id_user"))).get();
                        nombresUsuario.put(post.getInt("id_user"), infoUser.split("%")[0]);
                        fotosUsuario.put(post.getInt("id_user"), infoUser.split("%")[1]);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    infoUser = nombre_usuario + "%" + fotosUsuario.get(post.getInt("id_user"));
                }
                String[] user_info = infoUser.split("%");
                if(user_info.length != 2){
                    String nom = user_info[0];
                    user_info = new String[]{nom, ""};
                }
                //posts.add(new Post(post.getInt("id_user"), user_info[0], user_info[1], post.getString("contenido"), post.getString("foto"), post.getString("fecha_hora")));
                addPostToList(new Post(post.getInt("id_user"), user_info[0], user_info[1], post.getString("contenido"), post.getString("foto"), post.getString("fecha_hora")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        lvPosts.setAdapter(new PostsAdapter());

        rlStart.setVisibility(View.VISIBLE);
        rlLoader.setVisibility(View.INVISIBLE);
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


    /////////////////////////////////////////////////////////////////////////////////////////////////
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
                userImage = request.execute(posts.get(i).getLink_foto_user()).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            if(userImage == null){
                userImage = BitmapFactory.decodeResource( getActivity().getApplicationContext().getResources(),
                        R.drawable.user_rfoto);
            }
            imgUsuario.setImageBitmap(userImage);

            txtUsername.setText(posts.get(i).getUsername());
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

    /////////////////////////////////////////////////////////////////////////////////////////////////
    public class ExecuteGetPosts extends AsyncTask<String, Void, String> {
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
            isOk = api.getPosts(user.getId(), user.getAuth_token());

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(isOk){
                cargarPosts(API_Access.getInstance().getJsonObjectResponse());
            }else{
                String mensaje = "Error al obtener las publicaciones";

                Toast.makeText(getActivity(), mensaje, Toast.LENGTH_SHORT).show();
            }
            //rlLoaderEmisoras.setVisibility(View.INVISIBLE);
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    public class ExecuteGetUserInfo extends AsyncTask<String, Void, String> {
        boolean isOk = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            API_Access api = API_Access.getInstance();
            //Usuario_Singleton user = Usuario_Singleton.getInstance();
            String info = "[nombre_usuario]%[link_foto]";
            isOk = api.getInfoUser(strings[0]);
            if(isOk){
                try {
                    JSONObject jsonObject = API_Access.getInstance().getJsonObjectResponse();
                    info = jsonObject.getString("username") + "%" + jsonObject.getString("foto");
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            return info;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

        }
    }
}
