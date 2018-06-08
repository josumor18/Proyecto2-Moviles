package moviles.apps.proyecto2.friendtec.Business;


import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import moviles.apps.proyecto2.friendtec.Data.API_Access;
import moviles.apps.proyecto2.friendtec.Data.HttpGetBitmap;
import moviles.apps.proyecto2.friendtec.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {

    private ArrayList<Usuario> resultados_busqueda, resultados_aux;
    TextView edtxtSearch;
    Button btnSearch;
    Spinner spSortOptions;
    ListView lvResultadosBusqueda;

    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        resultados_busqueda = new ArrayList<Usuario>();
        resultados_aux = new ArrayList<Usuario>();

        View v = inflater.inflate(R.layout.fragment_search, container, false);
        edtxtSearch = v.findViewById(R.id.edtxtSearch);
        btnSearch = v.findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String busqueda = edtxtSearch.getText().toString();
                ExecuteBuscar executeBuscar = new ExecuteBuscar();
                executeBuscar.execute(busqueda);
            }
        });

        lvResultadosBusqueda = v.findViewById(R.id.lvResultadosBusqueda);
        spSortOptions = v.findViewById(R.id.spSortOptions);
        spSortOptions.setSelection(0);
        spSortOptions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) view).setTextColor(getResources().getColor(R.color.colorSpinnerOption));

                ordenarResultados(position);

                lvResultadosBusqueda.setAdapter(new ResultsAdapter());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        resultados_busqueda = new ArrayList<Usuario>();
        resultados_aux = new ArrayList<Usuario>();
    }

    public void cargarResultados(JSONObject response){
        resultados_aux.clear();
        resultados_busqueda.clear();
        try{
            String token = response.getString("auth_token");
            Usuario_Singleton.getInstance().setAuth_token(token);
            LoginActivity.actualizarAuth_Token(token, getActivity());

            JSONArray resultados = response.getJSONArray("resultados");
            for(int i = 0; i < resultados.length(); i++){
                JSONObject user_json = (JSONObject) resultados.get(i);

                Usuario usuario = new Usuario();
                usuario.setId(user_json.getInt("id"));
                usuario.setNombre(user_json.getString("nombre"));
                usuario.setCarnet(user_json.getString("carnet"));
                usuario.setEmail(user_json.getString("email"));
                usuario.setCarrera(user_json.getString("carrera"));
                usuario.setLink_foto(user_json.getString("foto"));
                usuario.setLink_rfoto(user_json.getString("rfoto"));

                resultados_aux.add(usuario);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        ordenarResultados(spSortOptions.getSelectedItemPosition());

        lvResultadosBusqueda.setAdapter(new ResultsAdapter());
    }

    private void ordenarResultados(int opcion){
        resultados_busqueda = new ArrayList<Usuario>();
        if(opcion == 1){
            //Orden alfabético
            for(Usuario usuario: resultados_aux){
                int i = 0;
                for(i = 0; i < resultados_busqueda.size(); i++){
                    String nomUs = usuario.getNombre();
                    String nomBus = resultados_busqueda.get(i).getNombre();
                    if(nomUs.compareTo(nomBus) < 0){
                        break;
                    }
                }
                resultados_busqueda.add(i, usuario);
            }
        }else if(opcion == 2){
            //Primero amigos
            Usuario_Singleton user = Usuario_Singleton.getInstance();
            ArrayList<Usuario> NoAmigos = new ArrayList<Usuario>();
            for(Usuario usuario: resultados_aux){
                if(user.esAmigo(usuario.getId())){
                    resultados_busqueda.add(usuario);
                }else{
                    NoAmigos.add(usuario);
                }
            }

            resultados_busqueda.addAll(NoAmigos);
        }else{
            resultados_busqueda = resultados_aux;
        }

    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    public class ResultsAdapter extends BaseAdapter {

        public ResultsAdapter() {
            super();
        }

        @Override
        public int getCount() {
            return resultados_busqueda.size();
        }

        @Override
        public Object getItem(int i) {
            return resultados_busqueda.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            LayoutInflater inflater = getLayoutInflater();
            if (view == null) {
                view = inflater.inflate(R.layout.search_listview_item, null);
            }

            ImageView imgUsuario = view.findViewById(R.id.img_UserRequest);
            TextView txtUsername = view.findViewById(R.id.txtUsernamSearch);
            Button btnAddDelAmigo = view.findViewById(R.id.btnAddDelAmigo);

            HttpGetBitmap request = new HttpGetBitmap();
            Bitmap userImage = null;
            try {
                userImage = request.execute(resultados_busqueda.get(i).getLink_foto()).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            imgUsuario.setImageBitmap(userImage);

            txtUsername.setText(resultados_busqueda.get(i).getNombre());

            if(Usuario_Singleton.getInstance().esAmigo(resultados_busqueda.get(i).getId())){
                btnAddDelAmigo.setText(" Eliminar de mis amigos ");
            }else{
                btnAddDelAmigo.setText("  Agregar a mis amigos  ");
            }
            return view;
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    public class ExecuteBuscar extends AsyncTask<String, Void, String> {
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
            isOk = api.search(user.getId(), user.getAuth_token(), strings[0]);

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(isOk){
                cargarResultados(API_Access.getInstance().getJsonObjectResponse());
            }else{
                String mensaje = "Error al obtener los resultados de búsqueda";

                Toast.makeText(getActivity(), mensaje, Toast.LENGTH_SHORT).show();
            }
            //rlLoaderEmisoras.setVisibility(View.INVISIBLE);
        }
    }
}
