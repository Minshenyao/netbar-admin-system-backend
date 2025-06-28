package edu.ahut.context;

public class UserContext {
    private static final ThreadLocal<String> currentToken = new ThreadLocal<>();
    
    public static void setCurrentToken(String token) {
        currentToken.set(token);
    }
    
    public static String getCurrentToken() {
        return currentToken.get();
    }

    public static void clear() {
        currentToken.remove();
    }
} 