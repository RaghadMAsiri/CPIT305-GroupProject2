/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.inventoryfx;

/**
 *
 * @author Amani
 */
public class AccountLockedException extends AuthException {
    private final long secondsLeft;
    public AccountLockedException(long secondsLeft) {
        super("Account locked. Try again in " + secondsLeft + "s.");
        this.secondsLeft = secondsLeft;
    }
    public long getSecondsLeft() { return secondsLeft; }
}
