package com.idrovo.michat.controller;

import com.idrovo.michat.model.Message;
import com.idrovo.michat.model.User;
import com.idrovo.michat.repository.MessageRepository;
import com.idrovo.michat.repository.UserRepository;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.util.List;

public class ContactController {
    private final MessageRepository messageRepository = new MessageRepository();
    private final UserRepository userRepository = new UserRepository();
    private User user;
    private Stage stage;
    private ChatController chatController;

    @FXML
    private ScrollPane spcontact;

    @FXML
    private VBox vbcontact;

    public void init(Stage stage1, User user, ChatController chatController) {
        this.user = user;
        this.stage = stage1;
        this.chatController = chatController;
        contactosActivos(user.getId());
    }

    public void contactosActivos(int id) {
        for (User contactoActivo :userRepository.usersActive(id)) {
            Circle contactactive = new Circle(27.5, new ImagePattern(new Image("file:" + contactoActivo.getImage())));
            Label lblcontact = new Label(contactoActivo.getUsername());
            HBox hBoxcontact = new HBox(lblcontact);
            hBoxcontact.setAlignment(Pos.CENTER);
            HBox contacto = new HBox(20, contactactive, hBoxcontact);
            contacto.setAlignment(Pos.CENTER_LEFT);
            contacto.setId(String.valueOf(contactoActivo.getId()));

            lblcontact.getStyleClass().add("lblcontact");
            contacto.getStyleClass().add("bandeja-contacto");

            contacto.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    chatController.getFlowpanechat().getChildren().clear();
                    User contact = userRepository.getUserId(Integer.parseInt(contacto.getId()));
                    iniciarChat(contact);
                    List<Message> messages = messageRepository.mostrarMensajesToFrom(user.getId(), contact.getId());
                    for (Message message : messages) {
                        if(message.getUser1().getId() == user.getId())
                            chatController.mensajesDelChat(message, NodeOrientation.RIGHT_TO_LEFT, Pos.CENTER_LEFT);
                        else
                            chatController.mensajesDelChat(message, NodeOrientation.LEFT_TO_RIGHT, Pos.CENTER_RIGHT);

                    }
                    stage.close();
                }
            });

            vbcontact.getChildren().add(contacto);
        }
    }

    public void iniciarChat(User contacto) {
        ImagePattern imagePattern = new ImagePattern(new Image("file:" + contacto.getImage()));
        this.chatController.getContainerContact().setVisible(false);
        this.chatController.getScrollchat().setVisible(true);
        this.chatController.getSendMessage().setVisible(true);
        this.chatController.getTopcontact().setVisible(true);
        this.chatController.getHboxContacts().setVisible(true);
        this.chatController.getContactchat().setFill(imagePattern);
        this.chatController.getLblcontactchat().setText(contacto.getUsername());
        this.chatController.getLblestado().setText("Activo");
        this.chatController.getLblestado().setStyle("-fx-text-fill: #00ff28;");
    }
}
