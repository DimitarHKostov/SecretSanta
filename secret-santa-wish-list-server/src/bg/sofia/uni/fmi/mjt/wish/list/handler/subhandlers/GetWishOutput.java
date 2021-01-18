package bg.sofia.uni.fmi.mjt.wish.list.handler.subhandlers;

import bg.sofia.uni.fmi.mjt.wish.list.validator.Validator;

import java.util.List;

public class GetWishOutput {
    private static final String NO_PRESENT_STUDENTS = "[ There are no students present in the wish list ]";
    private static final String SUCCESSFUL_GET_WISH_PREFIX = "[ ";
    private static final String SUCCESSFUL_GET_WISH_SUFFIX = "]";
    private static final String SUCCESSFUL_GET_WISH_WISHES_PREFIX = ": [";
    private static final String SUCCESSFUL_GET_WISH_WISHES_SUFFIX = "] ";

    public String formatNoPresentStudents() {
        return NO_PRESENT_STUDENTS;
    }

    public String formatSuccessfulGetWish(String randomRecipient, List<String> wishes) {
        Validator.validateNotNull(randomRecipient, "randomRecipient");
        Validator.validateNotNull(wishes, "wishes");

        StringBuilder wishAccumulator = new StringBuilder();

        int i = 0;
        while (i < wishes.size() - 1) {
            wishAccumulator.append(wishes.get(i)).append(", ");
            i++;
        }
        wishAccumulator.append(wishes.get(i));

        return SUCCESSFUL_GET_WISH_PREFIX + randomRecipient
                + SUCCESSFUL_GET_WISH_WISHES_PREFIX + wishAccumulator.toString() + SUCCESSFUL_GET_WISH_WISHES_SUFFIX
                + SUCCESSFUL_GET_WISH_SUFFIX;
    }
}
