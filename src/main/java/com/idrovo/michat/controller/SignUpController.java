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
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

public class SignUpController {
    private final UserRepository userRepository = new UserRepository();
    private String img;
    private LoginController loginController;
    private Stage stage;

    @FXML
    private Button btnCancel;

    @FXML
    private Button btnselectimages;

    @FXML
    private Button btnsignup;

    @FXML
    private Label lblimage;

    @FXML
    private Label lbllastname;

    @FXML
    private Label lblname;

    @FXML
    private Label lblpass;

    @FXML
    private Label lbluser;

    @FXML
    private TextField txtlastname;

    @FXML
    private TextField txtname;

    @FXML
    private PasswordField txtpass;

    @FXML
    private TextField txtuser;

    @FXML
    void cancelAction(ActionEvent event) {
        stage.close();
        loginController.showStage();
    }

    @FXML
    void selectImagesAction(ActionEvent event) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar una imagen");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imagenes", "*.png", "*.jpg"));

        Stage stage1 = new Stage();
        stage1.setScene(null);
        File file = fileChooser.showOpenDialog(stage1);

        if(file != null) {
            String pathTarget = "src/main/java/com/idrovo/michat/images/";
            Path source = Path.of(file.getAbsolutePath());
            Path target = Path.of(pathTarget + file.getName());
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
            this.img = target.toString();
        }

    }

    @FXML
    void signupAction(ActionEvent event) throws IOException {
        if(txtname.getText().isEmpty() || txtlastname.getText().isEmpty() || img == null
                || txtuser.getText().isEmpty() || txtpass.getText().isEmpty()) {
            Stage stage1 = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(MiChatApplication.class.getResource("alert-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 350, 200);
            stage1.setScene(scene);
            stage1.getIcons().add(new Image(Objects.requireNonNull(MiChatApplication.class.getResourceAsStream("images/chat.png"))));
            stage1.setTitle("MiCHat");
            stage1.show();

            AlertController alertController = fxmlLoader.getController();
            alertController.initSignUp(stage1, "Error, no se permiten campos vacíos.");
        } else {
            User user = new User(txtname.getText(), txtlastname.getText(), this.img, txtuser.getText(), txtpass.getText());
            userRepository.crearUsuario(user);
            stage.close();
            loginController.showStage();
        }
    }

    public void init(Stage stage, LoginController loginController) {
        this.loginController = loginController;
        this.stage = stage;
    }
}
