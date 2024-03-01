package com.idrovo.michat.repository;

import com.idrovo.michat.model.Chat;

import java.util.List;

public interface ChatRepositoryInterface {
    List<Chat> bandejaDeContactos(int id);
}
