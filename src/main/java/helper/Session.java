package helper;

import model.Account;

public class Session {
    private static Session instance;
    private Account currentUser;

    private Session() {
        // Private constructor for singleton
    }

    public static Session getInstance() {
        if (instance == null) {
            instance = new Session();
        }
        return instance;
    }

    public void setCurrentUser(Account user) {
        this.currentUser = user;
    }

    public Account getCurrentUser() {
        return currentUser;
    }

    public void clearCurrentUser() {
        this.currentUser = null;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }
}