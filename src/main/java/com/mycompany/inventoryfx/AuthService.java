/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.inventoryfx;

/**
 *
 * @author Amani
 */
public class AuthService {
    private AuthService(){}

    // يعيد الدور ("Admin" أو "User") أو يرمي استثناء محدد
    public static String authenticate(String username, String plainPassword)
            throws AuthException {

        String u = username == null ? "" : username.trim().toLowerCase();

        // 1) قفل؟
        if (LoginAttemptTracker.isLocked(u)) {
            long sec = LoginAttemptTracker.secondsLeft(u);
            LoginLogger.log(u, false, "LOCKED(" + sec + "s left)");
            throw new AccountLockedException(sec);
        }

        // 2) تحقق مستخدم + كلمة المرور (SHA-256 + slowEquals)
        InMemoryUsers.User user = InMemoryUsers.find(u);
        String pHash = SecurityUtils.sha256(plainPassword);

        if (user != null && SecurityUtils.slowEquals(user.passwordHash, pHash)) {
            LoginAttemptTracker.recordSuccess(u);
            LoginLogger.log(u, true, "Login ok (" + user.role + ")");
            return user.role; // "Admin" or "User"
        }

        // 3) فشل → زِد العداد وقد يصبح Locked
        LoginAttemptTracker.recordFailure(u);
        if (LoginAttemptTracker.isLocked(u)) {
            long sec = LoginAttemptTracker.secondsLeft(u);
            LoginLogger.log(u, false, "LOCKED after failures");
            throw new AccountLockedException(sec);
        } else {
            LoginLogger.log(u, false, "Invalid credentials");
            throw new InvalidCredentialsException();
        }
    }
}
