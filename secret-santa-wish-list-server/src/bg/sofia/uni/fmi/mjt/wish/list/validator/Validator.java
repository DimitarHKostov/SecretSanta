package bg.sofia.uni.fmi.mjt.wish.list.validator;

public class Validator {
    public static void validateNotNull(Object parameter, String parameterName) {
        if (parameter == null) {
            throw new IllegalArgumentException(parameterName + " is null");
        }
    }
}
