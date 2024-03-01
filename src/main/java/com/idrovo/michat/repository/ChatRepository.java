package com.idrovo.michat.repository;

import com.idrovo.michat.model.Chat;
import com.idrovo.michat.model.Message;
import com.idrovo.michat.model.User;

import java.util.ArrayList;
import java.util.List;

public class ChatRepository implements ChatRepositoryInterface {
    private UserRepository userRepository; //= new UserRepository();
    private MessageRepository messageRepository;// = new MessageRepository();

    public ChatRepository(UserRepository userRepository, MessageRepository messageRepository) {
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
    }

    @Override
    public List<Chat> bandejaDeContactos(int id) {
        List<Chat> chats = new ArrayList<>();
        List<User> contactos = userRepository.listaContactos(id);

        for (User contacto : contactos) {
            Message message = messageRepository.mostrarUltimoMensajeToFrom(id, contacto.getId());

            if(message == null)
                continue;

            chats.add(new Chat(contacto, message));
        }

        return chats;
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public MessageRepository getMessageRepository() {
        return messageRepository;
    }

    public void setMessageRepository(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }
}
