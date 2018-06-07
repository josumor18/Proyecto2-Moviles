package moviles.apps.proyecto2.friendtec.Business;

public class Usuario {
    private int id;
    private String carnet;
    private String nombre;
    private String email;
    private String carrera;
    private String link_foto;
    private String link_rfoto;
    private int estado_amistad;

    public Usuario() {
    }

    public Usuario(int id, String carnet, String nombre, String email, String carrera, String link_foto, String link_rfoto, int estado_amistad) {
        this.id = id;
        this.carnet = carnet;
        this.nombre = nombre;
        this.email = email;
        this.carrera = carrera;
        this.link_foto = link_foto;
        this.link_rfoto = link_rfoto;
        this.estado_amistad = estado_amistad;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCarnet() {
        return carnet;
    }

    public void setCarnet(String carnet) {
        this.carnet = carnet;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCarrera() {
        return carrera;
    }

    public void setCarrera(String carrera) {
        this.carrera = carrera;
    }

    public String getLink_foto() {
        return link_foto;
    }

    public void setLink_foto(String link_foto) {
        this.link_foto = link_foto;
    }

    public String getLink_rfoto() {
        return link_rfoto;
    }

    public void setLink_rfoto(String link_rfoto) {
        this.link_rfoto = link_rfoto;
    }

    public int getEstado_amistad() {
        return estado_amistad;
    }

    public void setEstado_amistad(int estado_amistad) {
        this.estado_amistad = estado_amistad;
    }
}
