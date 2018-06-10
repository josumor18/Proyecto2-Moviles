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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import moviles.apps.proyecto2.friendtec.Data.API_Access;
import moviles.apps.proyecto2.friendtec.Data.HttpGetBitmap;
import moviles.apps.proyecto2.friendtec.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatListFragment extends Fragment {

    ListView lvChatList;

    private ArrayList<Chat> chats = new ArrayList<Chat>();

    public ChatListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_chat_list, container, false);
        lvChatList = v.findViewById(R.id.lvChatList);

        for(int i = MainActivity.lista_chats.size() -1 ; i >= 0; i--){
            chats.add(MainActivity.lista_chats.get(i));
        }

        lvChatList.setAdapter(new ChatsAdapter());

        lvChatList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent chatIntent = new Intent(getActivity(), ChatActivity.class);
                chatIntent.putExtra("id_chat", chats.get(position).getId_chat());
                chatIntent.putExtra("id_friend", chats.get(position).getId_friend());
                startActivity(chatIntent);

                view.setBackgroundColor(getResources().getColor(R.color.colorTransparent));
            }
        });

        return v;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    public class ChatsAdapter extends BaseAdapter {

        public ChatsAdapter() {
            super();
        }

        @Override
        public int getCount() {
            return chats.size();
        }

        @Override
        public Object getItem(int i) {
            return chats.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            LayoutInflater inflater = getLayoutInflater();
            if (view == null) {
                view = inflater.inflate(R.layout.chat_item, null);
            }
            if(!chats.get(i).isVisto()){
                view.setBackgroundColor(getResources().getColor(R.color.colorBackgroundNewNotification));
            }

            ImageView imgChatUser = view.findViewById(R.id.imgChatUser);
            TextView txtUsernameChat = view.findViewById(R.id.txtUsernameChat);
            TextView txtLastMessage = view.findViewById(R.id.txtLastMessage);

            String nombre = "[nombre_usuario]";
            String foto = "[link_foto]";
            for(Usuario amigo: Usuario_Singleton.getInstance().getListaAmigos()){
                if(amigo.getId() == chats.get(i).getId_friend()){
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
            imgChatUser.setImageBitmap(userImage);

            txtUsernameChat.setText(nombre);
            txtLastMessage.setText(chats.get(i).getLast_message());
            //txtNotification.setText(Html.fromHtml("Tu amigo <b>" + nombre + "</b> ha realizado una nueva publicación."));

            return view;
        }
    }

}
