package dev.avetisyan.egs.bookstore.auth;

public enum UserRole {
    ADMIN((short) 1),
    USER((short) 2);

    private short id;

    public short getId() {
        return id;
    }

    UserRole(short roleId) {
        this.id = roleId;
    }
}
