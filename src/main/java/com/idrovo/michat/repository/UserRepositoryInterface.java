package com.idrovo.michat.repository;

import com.idrovo.michat.model.User;

import java.util.List;

public interface UserRepositoryInterface {
    void crearUsuario(User user);
    User validarAcceso(String username, String password);
    User getUserId(int id);
    User getUserUsername(String username);
    void habilitarEstado(User user);
    void deshabilitarEstado(User user);
    List<User> usersActive(int id);
    List<User> listaContactos(int id);
}