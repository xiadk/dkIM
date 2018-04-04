package bean;

public class Message {
    private String token;
    private Ope ope;
    private int fid;
    private Type type;
    private String body;

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
                '}';
    }
}
