package bean;

public enum  Ope {
    PERSONAL(0),
    GROUP(1);
    public int val;
    private Ope(int val){
        this.val = val;
    }

}
