package com.idrovo.michat;

import com.idrovo.michat.controller.LoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Objects;

public class MiChatApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(MiChatApplication.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 700, 500);
        stage.setScene(scene);
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("images/chat.png"))));
        stage.setTitle("MiCHat");
        stage.show();

        LoginController loginController = fxmlLoader.getController();
        loginController.setStage(stage);

    }

    public static void main(String[] args) { launch(); }
}
