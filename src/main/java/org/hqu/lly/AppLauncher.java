package org.hqu.lly;
 
import javafx.application.Application;
 
public class AppLauncher {
    public static void main(String[] args) {
        System.setProperty("file.encoding", "UTF-8");
        Application.launch(App.class, args);
    }
}