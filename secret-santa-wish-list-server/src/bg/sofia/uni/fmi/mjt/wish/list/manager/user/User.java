package bg.sofia.uni.fmi.mjt.wish.list.manager.user;

public record User(String username, String password) {

    @Override
    public int hashCode() {
        return username.hashCode();
    }
}