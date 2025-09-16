/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.inventoryfx;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

/**
 *
 * @author Amani
 */
public final class LoginLogger {
    private LoginLogger(){}

    private static final String FILE = "login_attempts.log";

    public static synchronized void log(String username, boolean success, String message) {
        try (FileWriter fw = new FileWriter(FILE, true)) {
            fw.write(String.format("[%s] user=%s result=%s msg=%s%n",
                    LocalDateTime.now(),
                    username == null ? "" : username,
                    success ? "SUCCESS" : "FAIL",
                    message == null ? "" : message));
        } catch (IOException e) {
            // لا توقف البرنامج بسبب اللوق
            e.printStackTrace();
        }
    }
}
