package bean;

public enum  Type {
    TEXT(0),
    PHOTO(1),
    ADD_FRIEND(2);

    public int val;
    private Type(int val){

        this.val = val;
    }

}
