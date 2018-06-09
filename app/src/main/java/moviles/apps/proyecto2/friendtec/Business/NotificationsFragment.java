package moviles.apps.proyecto2.friendtec.Business;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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
public class NotificationsFragment extends Fragment {

    ListView lvNotificaciones;
    private ArrayList<Notificacion> notificaciones = new ArrayList<Notificacion>();

    public NotificationsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_notifications, container, false);
        lvNotificaciones = v.findViewById(R.id.lvNotificaciones);

        for(int i = MainActivity.notificaciones.size() -1 ; i >= 0; i--){
            notificaciones.add(MainActivity.notificaciones.get(i));
        }

        lvNotificaciones.setAdapter(new NotificationsAdapter());

        ExecuteSetFalseNotifications executeSetFalseNotifications = new ExecuteSetFalseNotifications();
        executeSetFalseNotifications.execute();

        return v;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    public class NotificationsAdapter extends BaseAdapter {

        public NotificationsAdapter() {
            super();
        }

        @Override
        public int getCount() {
            return notificaciones.size();
        }

        @Override
        public Object getItem(int i) {
            return notificaciones.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            LayoutInflater inflater = getLayoutInflater();
            if (view == null) {
                view = inflater.inflate(R.layout.notification_list_item, null);
            }
            if(!notificaciones.get(i).isVisto()){
                view.setBackgroundColor(getResources().getColor(R.color.colorBackgroundNewNotification));
            }

            ImageView imgUsuario = view.findViewById(R.id.imgNotificationUser);
            TextView txtNotification = view.findViewById(R.id.txtNotification);

            String nombre = "[nombre_usuario]";
            String foto = "[link_foto]";
            for(Usuario amigo: Usuario_Singleton.getInstance().getListaAmigos()){
                if(amigo.getId() == notificaciones.get(i).getId_user()){
                    nombre = amigo.getNombre();
                    foto = amigo.getLink_rfoto();
                }
            }

            HttpGetBitmap request = new HttpGetBitmap();
            Bitmap userImage = null;
            try {
                userImage = request.execute(foto).get();
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

            txtNotification.setText(Html.fromHtml("Tu amigo <b>" + nombre + "</b> ha realizado una nueva publicación."));

            return view;
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    public class ExecuteSetFalseNotifications extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            API_Access api = API_Access.getInstance();
            Usuario_Singleton user = Usuario_Singleton.getInstance();

            api.setFalseNotifications(user.getId());

            return null;
        }
    }

}
