package moviles.apps.proyecto2.friendtec.Business;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import moviles.apps.proyecto2.friendtec.R;

public class FirendListActivity extends AppCompatActivity {

    ListView lvAmigos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firend_list);

        lvAmigos = findViewById(R.id.lv_amigos);

        //ExecuteGetFriends getFriends = new ExecuteGetFriends();
        //getFriends.execute();


        lvAmigos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Aqui se envia al perfil con el idUser
            }
        });
    }


}
