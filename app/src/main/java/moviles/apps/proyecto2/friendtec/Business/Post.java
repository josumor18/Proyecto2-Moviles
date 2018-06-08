package moviles.apps.proyecto2.friendtec.Business;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Post extends Object{
    private int id_user;
    private String username;
    private String link_foto_user;
    private String contenido;
    private String link_foto;
    private Date fecha_hora;

    public Post(int id_user, String username, String link_foto_user, String contenido, String link_foto, String fecha_hora){
        this.id_user = id_user;
        this.username = username;
        this.link_foto_user = link_foto_user;
        this.contenido = contenido;
        this.link_foto = link_foto;
        String dateIn = fecha_hora.split("T")[0];
        String timeIn = fecha_hora.split("T")[1];//.split(".")[0];
        timeIn = timeIn.split("\\.")[0];
        String fechaHora = dateIn + " " + timeIn;
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        Date date = new Date();
        try{
            date = format.parse(fechaHora);
        }catch(Exception e){
            e.printStackTrace();
        }
        this.fecha_hora = date;
    }

    public int getId_user() {
        return id_user;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLink_foto_user() {
        return link_foto_user;
    }

    public void setLink_foto_user(String link_foto_user) {
        this.link_foto_user = link_foto_user;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public String getLink_foto() {
        return link_foto;
    }

    public void setLink_foto(String link_foto) {
        this.link_foto = link_foto;
    }

    public Date getFecha_hora() {
        return fecha_hora;
    }

    public void setFecha_hora(Date fecha_hora) {
        this.fecha_hora = fecha_hora;
    }

    public String getFechaHoraString(){
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ENGLISH);
        return format.format(fecha_hora);
    }
}
