package bean;

public enum  Type {
    TEXT(0),
    PHOTO(1),
    ADD_FRIEND(2),
    GROUP_HINT(3);

    public int val;
    private Type(int val){

        this.val = val;
    }

}
