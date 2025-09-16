/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.inventoryfx;

import java.util.Map;

/**
 *
 * @author Amani
 */
public final class InMemoryUsers {
    private InMemoryUsers(){}

    public static final class User {
        public final String username;
        public final String passwordHash; // sha256
        public final String role;         // "Admin" or "User"
        public User(String u, String h, String r){ username=u; passwordHash=h; role=r; }
    }

    private static final String H123 = SecurityUtils.sha256("123");

    // عدّل القائمة حسب حاجتك أثناء التطوير
    private static final Map<String, User> USERS = Map.of(
            "admin", new User("admin", H123, "Admin"),
            "emp",   new User("emp",   H123, "User"),
            "user",  new User("user",  H123, "User")
    );

    public static User find(String username) {
        if (username == null) return null;
        return USERS.get(username.trim().toLowerCase());
    }
}
