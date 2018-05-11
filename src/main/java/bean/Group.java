package bean;

import java.sql.Timestamp;

public class Group {

    private int gid;
    private String gname;
    private int owner;
    private String intro;//群介绍
    private String icon;//群头像
    private int mute;//全体禁言
    private Timestamp create_time;
    private int is_del;

    public int getGid() {
        return gid;
    }

    public void setGid(int gid) {
        this.gid = gid;
    }

    public String getGname() {
        return gname;
    }

    public void setGname(String gname) {
        this.gname = gname;
    }

    public int getOwner() {
        return owner;
    }

    public void setOwner(int owner) {
        this.owner = owner;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getMute() {
        return mute;
    }

    public void setMute(int mute) {
        this.mute = mute;
    }

    public Timestamp getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Timestamp create_time) {
        this.create_time = create_time;
    }

    public int getIs_del() {
        return is_del;
    }

    public void setIs_del(int is_del) {
        this.is_del = is_del;
    }

    @Override
    public String toString() {
        return "Group{" +
                "gid=" + gid +
                ", gname='" + gname + '\'' +
                ", owner=" + owner +
                ", intro='" + intro + '\'' +
                ", icon='" + icon + '\'' +
                ", mute=" + mute +
                ", create_time='" + create_time + '\'' +
                ", is_del=" + is_del +
                '}';
    }
}
