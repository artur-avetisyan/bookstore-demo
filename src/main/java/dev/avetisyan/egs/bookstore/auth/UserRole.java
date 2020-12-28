package dev.avetisyan.egs.bookstore.auth;

public enum UserRole {
    ADMIN((short) 1, "Admin"),
    USER((short) 2, "User");

    private final short id;
    private final String name;

    public short getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    UserRole(short roleId, String name) {
        this.id = roleId;
        this.name = name;
    }
}
