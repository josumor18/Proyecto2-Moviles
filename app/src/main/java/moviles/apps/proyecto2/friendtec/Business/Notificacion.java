package moviles.apps.proyecto2.friendtec.Business;

public class Notificacion {
    private int id_user;
    private int id_post;
    private boolean visto;

    public Notificacion() {
    }

    public Notificacion(int id_user, int id_post, boolean visto) {
        this.id_user = id_user;
        this.id_post = id_post;
        this.visto = visto;
    }

    public int getId_user() {
        return id_user;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

    public int getId_post() {
        return id_post;
    }

    public void setId_post(int id_post) {
        this.id_post = id_post;
    }

    public boolean isVisto() {
        return visto;
    }

    public void setVisto(boolean visto) {
        this.visto = visto;
    }
}
