package bean;

public enum  Type {
    TEXT(0),
    PHOTO(1),
    ADD_FRIEND(2);

    private int val;
    private Type(int val){

        this.val = val;
    }

    public int getVal(){
        return this.val;
    }
}
