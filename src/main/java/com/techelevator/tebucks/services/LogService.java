package com.techelevator.tebucks.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class LogService {
    public static void over1kLogTransfer(String userName, int id, BigDecimal amount){
        String directoryName = "log";
        String filename = "tranfersover1000.txt";
        File logDirectory = new File(directoryName);
        if(!logDirectory.exists()){
            logDirectory.mkdir();
        }
        String temp = directoryName + "\\" + filename;
        try(FileOutputStream outputStream = new FileOutputStream(temp,true);
        PrintWriter writer = new PrintWriter(outputStream)){
                writer.println(LocalDateTime.now() + " Transfer of $" + amount + " was made by " + userName + " Transfer ID: " + id);
        } catch (Exception e){

        }
    }
    public static void overDraftLog(int userId, BigDecimal amount) {
        String directoryName = "log";
        String filename = "overdrafttransfers.txt";
        File logDirectory = new File(directoryName);
        if (!logDirectory.exists()) {
            logDirectory.mkdir();
        }
        String temp = directoryName + "\\" + filename;
        try (FileOutputStream outputStream = new FileOutputStream(temp, true);
             PrintWriter writer = new PrintWriter(outputStream)) {
            writer.println(LocalDateTime.now() + "A overdraft charge was attempted by " + userId + " with account balance of " + amount);
        } catch (Exception e) {
        }
    }
}
