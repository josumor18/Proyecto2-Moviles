package moviles.apps.proyecto2.friendtec.Business;

public class Chat {
    private int id_friend;
    private int id_chat;
    private boolean visto;
    private String last_message;

    public Chat() {
    }

    public Chat(int id_friend, int id_chat, boolean visto, String last_message) {
        this.id_friend = id_friend;
        this.id_chat = id_chat;
        this.visto = visto;
        this.last_message = last_message;
    }

    public int getId_friend() {
        return id_friend;
    }

    public void setId_friend(int id_friend) {
        this.id_friend = id_friend;
    }

    public int getId_chat() {
        return id_chat;
    }

    public void setId_chat(int id_chat) {
        this.id_chat = id_chat;
    }

    public boolean isVisto() {
        return visto;
    }

    public void setVisto(boolean visto) {
        this.visto = visto;
    }

    public String getLast_message() {
        return last_message;
    }

    public void setLast_message(String last_message) {
        this.last_message = last_message;
    }
}
