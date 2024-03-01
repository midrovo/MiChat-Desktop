package com.idrovo.michat.controller;

import com.idrovo.michat.MiChatApplication;
import com.idrovo.michat.model.User;
import com.idrovo.michat.repository.UserRepository;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class LoginController {
    private final UserRepository userRepository = new UserRepository();
    private Stage stage;

    @FXML
    private Button btnlogin;

    @FXML
    private ImageView imglogo;

    @FXML
    private Label lblpass;

    @FXML
    private Label lblregister;

    @FXML
    private Label lbluser;

    @FXML
    private PasswordField txtpass;

    @FXML
    private TextField txtuser;

    public PasswordField getTxtpass() {
        return txtpass;
    }

    public void setTxtpass(PasswordField txtpass) {
        this.txtpass = txtpass;
    }

    public TextField getTxtuser() {
        return txtuser;
    }

    public void setTxtuser(TextField txtuser) {
        this.txtuser = txtuser;
    }

    @FXML
    void loginAction(ActionEvent event) throws IOException {
        Stage stage1 = new Stage();
        User user = this.userRepository.validarAcceso(txtuser.getText(), txtpass.getText());
        if(user == null) {
            FXMLLoader fxmlLoader = new FXMLLoader(MiChatApplication.class.getResource("alert-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 350, 200);
            stage1.setScene(scene);
            stage1.getIcons().add(new Image(Objects.requireNonNull(MiChatApplication.class.getResourceAsStream("images/chat.png"))));
            stage1.setTitle("MiCHat");
            stage1.show();

            AlertController alertController = fxmlLoader.getController();
            alertController.init(stage1, this);

        }
        else {
            user.setEstado(true);
            this.userRepository.habilitarEstado(user);
            this.stage.close();
            FXMLLoader fxmlLoader = new FXMLLoader(MiChatApplication.class.getResource("chat-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 900, 550);
            scene.getStylesheets().add(String.valueOf(MiChatApplication.class.getResource("css/style-chat.css")));
            stage1.setScene(scene);
            stage1.getIcons().add(new Image(Objects.requireNonNull(MiChatApplication.class.getResourceAsStream("images/chat.png"))));
            stage1.setTitle("MiCHat");
            stage1.show();

            ChatController chatController = fxmlLoader.getController();
            chatController.init(stage1, user, this);
            txtuser.clear();
            txtpass.clear();

        }

    }

    @FXML
    void signupAction(MouseEvent event) throws IOException {
        this.stage.close();
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(MiChatApplication.class.getResource("signup-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 700, 500);
        stage.setScene(scene);
        stage.getIcons().add(new Image(Objects.requireNonNull(MiChatApplication.class.getResourceAsStream("images/chat.png"))));
        stage.setTitle("Registrarse");
        stage.show();

        SignUpController signUpController = fxmlLoader.getController();
        signUpController.init(stage, this);
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void showStage() {
        this.stage.show();
    }

}
