package bg.sofia.uni.fmi.mjt.wish.list.manager;

import bg.sofia.uni.fmi.mjt.wish.list.validator.Validator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WishManager {
    private final Map<String, List<String>> wishList;

    public WishManager() {
        this.wishList = new HashMap<>();
    }

    public void addWish(String username, String wish) {
        Validator.validateNotNull(username, "username");
        Validator.validateNotNull(wish, "wish");

        if (!this.wishList.containsKey(username)) {
            List<String> wishes = new LinkedList<>();
            wishes.add(wish);
            this.wishList.put(username, wishes);
        } else {
            this.wishList.get(username).add(wish);
        }
    }

    public boolean containsOtherStudents(String contributorUsername) {
        Validator.validateNotNull(contributorUsername, "contributorUsername");

        if (this.wishList.size() == 0) {
            return false;
        }

        return this.wishList.keySet().stream()
                .noneMatch(l -> l.equals(contributorUsername));
    }

    public List<String> getWishes(String recipient) {
        Validator.validateNotNull(recipient, "recipient");

        List<String> wishes = this.wishList.get(recipient);
        this.wishList.remove(recipient);

        return wishes;
    }

    public String getRandomRecipient(String contributorUsername) {
        Validator.validateNotNull(contributorUsername, "contributorUsername");

        Set<String> keySet = this.wishList.keySet();
        List<String> keyList = new ArrayList<>(keySet);
        String recipient;

        do {
            recipient = keyList.get((int) (Math.random() * keyList.size()));
        } while (recipient.equals(contributorUsername));

        return recipient;
    }

    public boolean isWishAlreadySubmitted(String username, String wish) {
        Validator.validateNotNull(username, "username");
        Validator.validateNotNull(wish, "wish");

        if (!this.wishList.containsKey(username)) {
            return false;
        }

        return this.wishList.get(username).stream()
                .anyMatch(w -> w.equals(wish));
    }
}
