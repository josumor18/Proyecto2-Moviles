package moviles.apps.proyecto2.friendtec.Business;

import android.graphics.Bitmap;

import java.util.ArrayList;

public class Usuario_Singleton {
    private static final Usuario_Singleton ourInstance = new Usuario_Singleton();

    private String id;
    private String nombre;
    private String carnet;
    private String email;
    private String carrera;
    private String url_foto;
    private String url_foto_rounded;
    private Bitmap foto;
    private Bitmap foto_rounded;
    private String auth_token;
    private ArrayList<Usuario> listaAmigos = new ArrayList<Usuario>();

    public static Usuario_Singleton getInstance() {
        return ourInstance;
    }

    private Usuario_Singleton() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCarnet() {
        return carnet;
    }

    public void setCarnet(String carnet) {
        this.carnet = carnet;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAuth_token() {
        return auth_token;
    }

    public void setAuth_token(String auth_token) {
        this.auth_token = auth_token;
    }

    public Bitmap getFoto() {
        return foto;
    }

    public void setFoto(Bitmap foto) {
        this.foto = foto;
    }

    public Bitmap getFoto_rounded() {
        return foto_rounded;
    }

    public void setFoto_rounded(Bitmap foto_thumbnail) {
        this.foto_rounded= foto_thumbnail;
    }

    public String getUrl_foto() {
        return url_foto;
    }

    public void setUrl_foto(String url_foto) {
        this.url_foto = url_foto;
    }

    public String getUrl_foto_rounded() {
        return url_foto_rounded;
    }

    public void setUrl_foto_rounded(String url_foto_rounded) {
        this.url_foto_rounded = url_foto_rounded;
    }

    public String getCarrera() {
        return carrera;
    }

    public void setCarrera(String carrera) {
        this.carrera = carrera;
    }

    public ArrayList<Usuario> getListaAmigos() {
        return listaAmigos;
    }

    public void setListaAmigos(ArrayList<Usuario> listaAmigos) {
        this.listaAmigos = listaAmigos;
    }

    public void addAmigo(Usuario amigo){
        this.listaAmigos.add(amigo);
    }

    public boolean esAmigo (int id_user){
        for(Usuario amigo: listaAmigos){
            if(amigo.getId() == id_user){
                return true;
            }
        }
        return false;
    }

    public void eliminarAmigo(int id_user){
        for(int i = 0; i < listaAmigos.size(); i++){
            if(listaAmigos.get(i).getId() == id_user){
                listaAmigos.remove(i);
                break;
            }
        }
    }
}
