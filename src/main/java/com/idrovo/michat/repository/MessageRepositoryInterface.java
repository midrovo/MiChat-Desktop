package com.idrovo.michat.repository;

import com.idrovo.michat.model.Message;

import java.util.List;

public interface MessageRepositoryInterface {
    void ingresarMensajes(Message message);
    List<Message> mostrarMensajesToFrom(int userId, int contactId);
    Message mostrarUltimoMensajeToFrom(int userId, int contactId);
}
