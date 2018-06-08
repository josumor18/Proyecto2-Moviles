package moviles.apps.proyecto2.friendtec.Business;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;

import moviles.apps.proyecto2.friendtec.R;

public class ProfileActivity extends AppCompatActivity {

    private ArrayList<Post> posts = new ArrayList<Post>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
    }
}
