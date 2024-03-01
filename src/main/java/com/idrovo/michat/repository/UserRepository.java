package com.idrovo.michat.repository;

import com.idrovo.michat.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserRepository implements UserRepositoryInterface {
    private final String url = "jdbc:postgresql://localhost:5300/michatdb";
    private final String username = "postgres";
    private final String password = "1234";

    @Override
    public void crearUsuario(User user) {
        try {
            Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
            String queryInsert = "INSERT INTO public.users(nombres, apellidos, imagen, usuario, clave) VALUES (?, ?, ?, ?, ?);";
            PreparedStatement preparedStatement = connection.prepareStatement(queryInsert);
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getLastaname());
            preparedStatement.setString(3, user.getImage());
            preparedStatement.setString(4, user.getUsername());
            preparedStatement.setString(5, user.getPassword());
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public User validarAcceso(String username, String password) {
        try {
            Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
            String querySelect = "SELECT id_user, imagen, usuario FROM public.users WHERE usuario = ? AND clave = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(querySelect);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next())
                return new User(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3));

            resultSet.close();
            preparedStatement.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public User getUserId(int id) {
        try {
            Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
            String querySelect = "SELECT id_user, imagen, usuario, estado FROM public.users WHERE id_user = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(querySelect);
            preparedStatement.setInt(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()) {
                return new User(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3), resultSet.getBoolean(4));
            }
            resultSet.close();
            preparedStatement.close();
            connection.close();

        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public User getUserUsername(String username) {
        try {
            Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
            String querySelect = "SELECT id_user, imagen, usuario, estado FROM public.users WHERE usuario = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(querySelect);
            preparedStatement.setString(1, username);

            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()) {
                return new User(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3), resultSet.getBoolean(4));
            }
            resultSet.close();
            preparedStatement.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void habilitarEstado(User user) {
        try {
            Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
            String queryUpdate = "UPDATE public.users SET estado = ? WHERE usuario = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(queryUpdate);
            preparedStatement.setBoolean(1, user.isEstado());
            preparedStatement.setString(2, user.getUsername());
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deshabilitarEstado(User user) {
        try {
            Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
            String queryUpdate = "UPDATE public.users SET estado = ? WHERE usuario = ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(queryUpdate);
            user.setEstado(false);
            preparedStatement.setBoolean(1, user.isEstado());
            preparedStatement.setString(2, user.getUsername());
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<User> usersActive(int id) {
        List<User> usersActives = new ArrayList<>();
        try {
            Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
            String querySelect = "SELECT id_user, imagen, usuario FROM public.users WHERE estado = ? AND id_user != ?;";
            PreparedStatement preparedStatement = connection.prepareStatement(querySelect);
            preparedStatement.setBoolean(1, true);
            preparedStatement.setInt(2, id);

            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()) {
                usersActives.add(new User(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3)));
            }
            resultSet.close();
            preparedStatement.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usersActives;
    }

    @Override
    public List<User> listaContactos(int id) {
        List<User> contactos = new ArrayList<>();
        try {
            Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
            String querySelect = """
                    SELECT U.id_user, U.imagen, U.usuario, U.estado FROM public.users U
                    LEFT JOIN (SELECT CASE WHEN usuario_1 = ? THEN usuario_2 ELSE usuario_1
                    END AS otro_usuario, MAX(fecha_hora) AS ultima_fecha
                    FROM public.mensajes WHERE usuario_1 = ? OR usuario_2 = ?
                    GROUP BY otro_usuario)
                    UltimasMensajes ON U.id_user = UltimasMensajes.otro_usuario
                    LEFT JOIN public.mensajes M ON ((M.usuario_1 = ? AND M.usuario_2 = U.id_user) OR
                    (M.usuario_2 = ? AND M.usuario_1 = U.id_user))
                    AND M.fecha_hora = UltimasMensajes.ultima_fecha WHERE U.id_user != ?
                    ORDER BY M.fecha_hora DESC;
                    """;
            PreparedStatement preparedStatement = connection.prepareStatement(querySelect);
            preparedStatement.setInt(1, id);
            preparedStatement.setInt(2, id);
            preparedStatement.setInt(3, id);
            preparedStatement.setInt(4, id);
            preparedStatement.setInt(5, id);
            preparedStatement.setInt(6, id);

            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()) {
                contactos.add(new User(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3), resultSet.getBoolean(4)));
            }
            resultSet.close();
            preparedStatement.close();
            connection.close();

        } catch(SQLException e) {
            e.printStackTrace();
        }
        return contactos;
    }
}
