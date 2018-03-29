package bean;

public enum  Ope {
    PERSONAL(0),
    GROUP(1);
    private int ope;
    private Ope(int ope){
        this.ope = ope;
    }

    @Override
    public String toString() {
        return "Ope{" +
                "ope=" + ope +
                '}';
    }
}
