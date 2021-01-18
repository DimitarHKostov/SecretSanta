package bg.sofia.uni.fmi.mjt.wish.list.handler.subhandlers;

import bg.sofia.uni.fmi.mjt.wish.list.validator.Validator;

public class PostWishOutput {
    private static final String RECIPIENT_NOT_REGISTERED_PREFIX = "[ Student with username ";
    private static final String RECIPIENT_NOT_REGISTERED_SUFFIX = " is not registered ]";
    private static final String WISH_EXIST_PREFIX = "[ The same gift for student";
    private static final String WISH_EXIST_SUFFIX = " was already submitted ]";
    private static final String SUCCESSFUL_POST_WISH_PREFIX = "[ Gift ";
    private static final String SUCCESSFUL_POST_WISH_INFIX = " for student ";
    private static final String SUCCESSFUL_POST_WISH_SUFFIX = " submitted successfully ]";

    public String formatRecipientNotRegistered(String recipient) {
        Validator.validateNotNull(recipient, "recipient");

        return RECIPIENT_NOT_REGISTERED_PREFIX + recipient + RECIPIENT_NOT_REGISTERED_SUFFIX;
    }

    public String formatWishAlreadyExist(String recipient) {
        Validator.validateNotNull(recipient, "recipient");

        return WISH_EXIST_PREFIX + recipient + WISH_EXIST_SUFFIX;
    }

    public String formatSuccessfulPostWish(String recipient, String wish) {
        Validator.validateNotNull(recipient, "recipient");
        Validator.validateNotNull(wish, "wish");

        return SUCCESSFUL_POST_WISH_PREFIX + wish
                + SUCCESSFUL_POST_WISH_INFIX
                + recipient + SUCCESSFUL_POST_WISH_SUFFIX;
    }
}
