package moviles.apps.proyecto2.friendtec.Business;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import moviles.apps.proyecto2.friendtec.Data.API_Access;
import moviles.apps.proyecto2.friendtec.R;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FragmentTransaction fragmentTransaction;
    Runnable runnable;
    final Handler handler  = new Handler();

    public static ArrayList<Notificacion> notificaciones = new ArrayList<Notificacion>();

    private String[] toolbarTitle = {"Inicio", "Buscar", "Mensajes", "Notificaciones"};
    private int[] tabUnselectedIcon = {R.drawable.ic_round_home_24px, R.drawable.ic_round_search_24px, R.drawable.ic_outline_email_24px, R.drawable.ic_round_notifications_24px};
    private  int[] tabSelectedIcon = {R.drawable.ic_round_home_24px_sel, R.drawable.ic_round_search_24px_sel, R.drawable.ic_outline_email_24px_sel, R.drawable.ic_round_notifications_24px_sel};
    Toolbar toolbar;
    TabLayout tabLayout;
    ImageView imgUserPhoto;
    Bitmap bitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tabLayout = findViewById(R.id.appbartabs);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        cargarLayout();
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                cambiarFragment(tab.getPosition());
                cambiarIconoSeleccionado(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                cambiarIconoDeseleccionado(tab.getPosition());
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        bitmap = Usuario_Singleton.getInstance().getFoto_rounded();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        Bitmap toolbar_bitmap = bitmap;

        int toolbar_length = 1;
        // Calculate ActionBar height
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
        {
            toolbar_length = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
        }
        Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, toolbar_length - 30, toolbar_length - 30, true));
        toolbar.setNavigationIcon(d);//Usuario_Singleton.getInstance().getFoto());

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View hView =  navigationView.getHeaderView(0);
        ImageView nav_img = (ImageView) hView.findViewById(R.id.imgUserPhoto);
        nav_img.setImageBitmap(bitmap);
        nav_img.getLayoutParams().height = 150;
        nav_img.getLayoutParams().width = 150;
        TextView nav_name = hView.findViewById(R.id.txtNameHeader);
        nav_name.setText(Usuario_Singleton.getInstance().getNombre());
        TextView nav_email = hView.findViewById(R.id.txtEmailHeader);
        nav_email.setText(Usuario_Singleton.getInstance().getCarnet());

        StartFragment startFragment = new StartFragment();
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.contenedor, startFragment);
        fragmentTransaction.commit();

        runnable = new Runnable() {
            public void run() {
                ExecuteGetNotifications not = new ExecuteGetNotifications();
                not.execute();

                handler.postDelayed(this, 10000);
            }
        };

        handler.postDelayed(runnable, 1000);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_perfil) {

        } else if (id == R.id.nav_amigos) {

        } else if (id == R.id.nav_mapa) {
            Intent mapsIntent = new Intent(this, MapsActivity.class);
            startActivity(mapsIntent);
        } else if (id == R.id.nav_requests) {

        } else if (id == R.id.nav_logout) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void cargarLayout(){
        tabLayout.addTab(tabLayout.newTab().setIcon(tabSelectedIcon[0]));
        for(int i = 1; i <= 3; i++){
            tabLayout.addTab(tabLayout.newTab().setIcon(tabUnselectedIcon[i]));
        }
    }

    private void cambiarFragment(int tab){
        switch(tab){
            case 0:
                StartFragment startFragment = new StartFragment();
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.contenedor, startFragment);
                fragmentTransaction.commit();
                break;
            case 1:
                SearchFragment searchFragment = new SearchFragment();
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.contenedor, searchFragment);
                fragmentTransaction.commit();
                break;
            case 3:
                NotificationsFragment notificationsFragment = new NotificationsFragment();
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.contenedor, notificationsFragment);
                fragmentTransaction.commit();
                break;
        }
    }

    private void cambiarIconoSeleccionado(int position){
        tabLayout.getTabAt(position).setIcon(tabSelectedIcon[position]);
        toolbar.setTitle(toolbarTitle[position]);
    }

    private void cambiarIconoDeseleccionado(int position){
        tabLayout.getTabAt(position).setIcon(tabUnselectedIcon[position]);
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////
    public class ExecuteGetNotifications extends AsyncTask<String, Void, String> {
        boolean isOk = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            API_Access api = API_Access.getInstance();
            Usuario_Singleton user = Usuario_Singleton.getInstance();

            isOk = api.getNotifications(user.getId());

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(isOk){
                try {
                    notificaciones.clear();
                    JSONObject jsonObject = API_Access.getInstance().getJsonObjectResponse();
                    JSONArray notifications = jsonObject.getJSONArray("notificaciones");

                    for (int i = 0; i < notifications.length(); i++){
                        JSONObject notificacion = (JSONObject) notifications.get(i);
                        Notificacion nueva = new Notificacion(notificacion.getInt("id_friend"), notificacion.getInt("id_post"), notificacion.getBoolean("visto"));
                        notificaciones.add(nueva);
                        if (!(nueva.isVisto())){
                            if(tabLayout.getSelectedTabPosition() != 3){
                                tabLayout.getTabAt(3).setIcon(R.drawable.ic_round_notifications_active_24px);
                            }
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}
