package com.idrovo.michat.controller;

import com.idrovo.michat.MiChatApplication;
import com.idrovo.michat.connection.MessageArrivedAction;
import com.idrovo.michat.connection.MqttChat;
import com.idrovo.michat.model.Chat;
import com.idrovo.michat.model.Message;
import com.idrovo.michat.model.User;
import com.idrovo.michat.repository.ChatRepository;
import com.idrovo.michat.repository.MessageRepository;
import com.idrovo.michat.repository.UserRepository;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

public class ChatController {
    private final ChatRepository chatRepository = new ChatRepository(new UserRepository(), new MessageRepository());
    private final MqttChat mqttChat = new MqttChat();
    private Stage stage;
    private LoginController loginController;
    private User user;

    @FXML
    private Button btncontacts;

    @FXML
    private Button btnsend;

    @FXML
    private Circle contactchat;

    @FXML
    private VBox containerContact;

    @FXML
    private FlowPane flowpanechat;

    @FXML
    private Label lbllogout;

    @FXML
    private Label lblcontacts;

    @FXML
    private Label lblcontactchat;

    @FXML
    private Label lblestado;

    @FXML
    private Label lblusername;

    @FXML
    private ScrollPane scrollbandeja;

    @FXML
    private ScrollPane scrollchat;

    @FXML
    private HBox hboxContacts;

    @FXML
    private HBox sendMessage;

    @FXML
    private HBox topcontact;

    @FXML
    private TextField txtmessage;

    @FXML
    private Circle usersesion;

    @FXML
    private VBox vboxbandejachat;

    public Circle getContactchat() {
        return contactchat;
    }

    public Label getLblcontactchat() {
        return lblcontactchat;
    }

    public Label getLblestado() {
        return lblestado;
    }

    public ScrollPane getScrollchat() {
        return scrollchat;
    }

    public HBox getSendMessage() {
        return sendMessage;
    }

    public HBox getTopcontact() {
        return topcontact;
    }

    public VBox getContainerContact() {
        return containerContact;
    }

    public FlowPane getFlowpanechat() {
        return flowpanechat;
    }

    public HBox getHboxContacts() {
        return hboxContacts;
    }


    @FXML
    void contactsActiveMouse(MouseEvent event) throws IOException {
        Stage stage1 = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(MiChatApplication.class.getResource("contacts-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 300, 450);

        stage1.setScene(scene);
        stage1.getIcons().add(new Image(Objects.requireNonNull(MiChatApplication.class.getResourceAsStream("images/chat.png"))));
        stage1.setTitle("Contactos Activos");
        stage1.show();

        ContactController contactController = fxmlLoader.getController();
        contactController.init(stage1, user, this);
    }

    @FXML
    void logoutAction(MouseEvent event) {
        chatRepository.getUserRepository().deshabilitarEstado(user);
        desconectarSuscripcion(user);
        this.loginController.showStage();
        this.stage.close();
    }

    //METODO DONDE SE ENVIA EL MENSAJE
    @FXML
    void sendAction(ActionEvent event) {
        User contact = chatRepository.getUserRepository().getUserUsername(lblcontactchat.getText());
        Message message = new Message(user, contact, LocalDateTime.now(), txtmessage.getText());

        mensajesDelChat(message, NodeOrientation.RIGHT_TO_LEFT, Pos.CENTER_LEFT);

        chatRepository.getMessageRepository().ingresarMensajes(message);

        conexionMqttChatSendMessage(contact, message);
        bandejaDeContactos(user.getId());

        txtmessage.clear();
    }

    //METODO DONDE SE INICIA LA VENTANA DEL CHAT
    public void init(Stage stage1, User user, LoginController loginController) {
        this.loginController = loginController;
        this.stage = stage1;
        this.user = user;

        ImagePattern imagePattern = new ImagePattern(new Image("file:" + user.getImage()));

        usersesion.setFill(imagePattern);
        lblusername.setText(user.getUsername());

        bandejaDeContactos(user.getId());
    }

    //METODO DONDE MUESTRA LA LISTA DE LOS CONTACTOS Y TAMBIEN ACCEDER A CADA UNO PARA ACTIVAR EL CHAT DE CONVERSACION
    public void bandejaDeContactos(int id) {
        vboxbandejachat.getChildren().clear();
        List<Chat> chats = chatRepository.bandejaDeContactos(id);
        conexionMqttChatRecibirMessage(chats);
        for (Chat chat: chats) {
            HBox contactHBox = contenedorContacto(chat.getUser().getImage(), chat.getUser().getUsername(), chat.getMessage().getMessage(), chat.getMessage().getLocalDateTime());
            contactHBox.setId(String.valueOf(chat.getUser().getId()));
            contactHBox.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {

                    flowpanechat.getChildren().clear();
                    User contact = chatRepository.getUserRepository().getUserId(Integer.parseInt(contactHBox.getId()));
                    iniciarChat(contact);

                    List<Message> messages = chatRepository.getMessageRepository().mostrarMensajesToFrom(user.getId(), contact.getId());

                    for(Message message : messages) {
                        if(message.getUser1().getId() == user.getId())
                            mensajesDelChat(message, NodeOrientation.RIGHT_TO_LEFT, Pos.CENTER_LEFT);
                        else
                            mensajesDelChat(message, NodeOrientation.LEFT_TO_RIGHT, Pos.CENTER_RIGHT);
                    }
                }
            });
            vboxbandejachat.getChildren().add(contactHBox);
        }
    }

    /* METODO DONDE SE INICIA LA VENTANA DE CHAT OCULTANDO Y MOSTRANDO CIERTOS CONTENEDORES PARA UNA MEJOR EXPERIENCIA
    DE VISUALIZACION */
    public void iniciarChat(User user) {
        ImagePattern imagePattern = new ImagePattern(new Image("file:" + user.getImage()));
        this.containerContact.setVisible(false);
        this.scrollchat.setVisible(true);
        this.sendMessage.setVisible(true);
        this.topcontact.setVisible(true);
        this.hboxContacts.setVisible(true);
        this.contactchat.setFill(imagePattern);
        this.lblcontactchat.setText(user.getUsername());
        if(user.isEstado()) {
            this.lblestado.setText("Activo");
            this.lblestado.setStyle("-fx-text-fill: #00ff28;");
        }
        else {
            this.lblestado.setText("Ausente");
            this.lblestado.setStyle("-fx-text-fill: #ff0303;");
        }
    }

    //METODO PARA GUARDAR LOS CONTACTOS QUE SERAN MOSTRADOS EN LA BANDEJA DE CHAT
    public HBox contenedorContacto(String imagen, String contacto, String mensaje, LocalDateTime fechaHora) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM, yyyy  HH:mm");
        ImagePattern imagePattern = new ImagePattern(new Image("file:" + imagen));
        Circle imageCircle = new Circle(25, imagePattern);
        Label contact = new Label(contacto);
        Label message = new Label(mensaje);
        Label dateTime = new Label(formatter.format(fechaHora));
        VBox vBox = new VBox(3, contact, message, dateTime);
        HBox contactHBox = new HBox(20, imageCircle, vBox);

        imageCircle.getStyleClass().add("circle");
        contact.getStyleClass().add("lblcontact");
        message.getStyleClass().add("lblmessage");
        dateTime.getStyleClass().add("lbltime");
        vBox.getStyleClass().add("vb-contacto");
        contactHBox.getStyleClass().add("bandeja-contacto");

        return contactHBox;
    }

    //METODO DONDE SE INSERTAN LAS CONVESACIONES DEL USUARIO Y EL CONTACTO QUE SERAN MOSTRADAS EN EL CHAT
    public void mensajesDelChat(Message message, NodeOrientation nodeOrientation, Pos pos) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM, yyyy HH:mm");
        ImagePattern imagePattern = new ImagePattern(new Image("file:" + message.getUser1().getImage()));
        Circle circle = new Circle(25, imagePattern);
        Label mensaje = new Label(message.getMessage());
        Label fechaHora = new Label(formatter.format(message.getLocalDateTime()));
        VBox vBox = new VBox(5, mensaje, fechaHora);
        HBox contenidoMessage = new HBox(15, circle, vBox);
        contenidoMessage.setNodeOrientation(nodeOrientation);

        circle.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);

        fechaHora.setAlignment(pos);

        contenidoMessage.getStyleClass().add("hb-mensaje");
        vBox.getStyleClass().add("conversacion");
        mensaje.getStyleClass().add("lbl-conversacion");
        fechaHora.getStyleClass().add("lbl-fecha-hora");
        flowpanechat.getChildren().add(contenidoMessage);
        ajusteDesplazamientoScroll();
    }

    //METODO PARA DESPLAZAR AUTOMATICAMENTE EL SCROLL EN EL MOMENTO QUE SE RECIBE O SE ENVIA EL MENSAJE
    public void ajusteDesplazamientoScroll() {
        flowpanechat.heightProperty().addListener(new ChangeListener<Object>() {
            @Override
            public void changed(ObservableValue<?> observableValue, Object oldValue, Object newValue) {
                scrollchat.setVvalue((double) newValue);
            }
        });
    }

    /* METODO DONDE SE REALIZA LA CONEXION A MQTT Y LA SUSCRIPCION DEL USUARIO PARA QUE PUEDA RECIBIR EL MENSAJE
       DEL DESTINATARIO */
    public void conexionMqttChatRecibirMessage(List<Chat> contactos) {
        try {
            mqttChat.connect(user);
            mqttChat.subscribe(user.getUsername());
            mqttChat.messageArrived(new MessageArrivedAction() {

                @Override
                public void messageArrived(String topic, Message message) {

                    Platform.runLater(new Runnable() {

                        @Override
                        public void run() {
                            bandejaDeContactos(user.getId());
                            for(Chat chat: contactos) {
                                if(message.getUser1().getUsername().equals(chat.getUser().getUsername()))
                                    mensajesDelChat(message, NodeOrientation.LEFT_TO_RIGHT, Pos.CENTER_RIGHT);
                            }
                        }
                    });
                }
            });

        } catch(MqttException e) {
            System.err.println("Error MQTT al conectar o suscribirse: " + e.getMessage());
        }
    }

    //METODO DONDE EL PRODUCTOR ENVIA EL MENSAJE AL TOPICO QUE LO VA A CONSUMIR
    public void conexionMqttChatSendMessage(User user, Message message) {
        try {
            mqttChat.sendMessage(user.getUsername(), message);
        } catch (MqttPersistenceException e) {
            System.err.println("Error de persistencia MQTT al enviar el mensaje: " + e.getMessage());
        } catch (MqttException e) {
            System.err.println("Error MQTT al enviar el mensaje: " + e.getMessage());
        }
    }

    //METODO DONDE SE DESCONECTA LA SUSCRIPCION DEL USUARIO CUANDO SE DESEA CERRAR LA SESION
    public void desconectarSuscripcion(User user) {
        try {
            mqttChat.unsubscribe(user.getUsername());
        } catch (MqttException e) {
            System.err.println("Error MQTT al desuscribirse: " + e.getMessage());
        }
    }
}
