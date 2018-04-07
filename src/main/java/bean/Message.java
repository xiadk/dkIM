package bean;

public class Message {
    private String token;
    private Ope ope;
    private int fid;
    private Type type;
    private String body;
    private int is_read;
    private int is_del;

    public int getIs_read() {
        return is_read;
    }

    public void setIs_read(int is_read) {
        this.is_read = is_read;
    }

    public int getIs_del() {
        return is_del;
    }

    public void setIs_del(int is_del) {
        this.is_del = is_del;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Ope getOpe() {
        return ope;
    }

    public void setOpe(Ope ope) {
        this.ope = ope;
    }

    public int getFid() {
        return fid;
    }

    public void setFid(int fid) {
        this.fid = fid;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "Message{" +
                "token='" + token + '\'' +
                ", ope=" + ope +
                ", fid=" + fid +
                ", type=" + type +
                ", body='" + body + '\'' +
                ", is_read='" + is_read + '\'' +
                ", is_del='" + is_del + '\'' +
                '}';
    }
}
