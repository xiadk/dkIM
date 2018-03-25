package dao;

public class FriendsDao {
    private static FriendsDao friendsDao = new FriendsDao();

    public static FriendsDao getFriendsDao() {
        return friendsDao;
    }
}
