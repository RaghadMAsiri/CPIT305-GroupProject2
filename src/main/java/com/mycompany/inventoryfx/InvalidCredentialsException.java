/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.inventoryfx;

/**
 *
 * @author Amani
 */
public class InvalidCredentialsException extends AuthException  {
    public InvalidCredentialsException() {
        super("Invalid username or password.");
    }
}
