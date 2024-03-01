package com.idrovo.michat.repository;

import com.idrovo.michat.model.Message;
import com.idrovo.michat.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageRepository implements MessageRepositoryInterface {
    private final String url = "jdbc:postgresql://localhost:5300/michatdb";
    private final String username = "postgres";
    private final String password = "1234";

    @Override
    public void ingresarMensajes(Message message) {
        try {
            Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
            String queryInsert = "INSERT INTO public.mensajes(usuario_1, usuario_2, fecha_hora, mensaje) VALUES (?, ?, ?, ?);";
            PreparedStatement preparedStatement = connection.prepareStatement(queryInsert);
            preparedStatement.setInt(1, message.getUser1().getId());
            preparedStatement.setInt(2, message.getUser2().getId());
            preparedStatement.setTimestamp(3, Timestamp.valueOf(message.getLocalDateTime()));
            preparedStatement.setString(4, message.getMessage());
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();

        } catch(SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public List<Message> mostrarMensajesToFrom(int userId, int contactId) {
        UserRepository userRepository = new UserRepository();
        List<Message> messages = new ArrayList<>();
        try {
            Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
            String querySelect = """
                    SELECT id_message, usuario_1, usuario_2, fecha_hora, mensaje
                    FROM public.mensajes WHERE usuario_1 = ? AND usuario_2 = ?
                    OR usuario_1 = ? AND usuario_2 = ?;
                    """;
            PreparedStatement preparedStatement = connection.prepareStatement(querySelect);
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, contactId);
            preparedStatement.setInt(3, contactId);
            preparedStatement.setInt(4, userId);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                User user1 = userRepository.getUserId(resultSet.getInt(2));
                User user2 = userRepository.getUserId(resultSet.getInt(3));
                messages.add(new Message(resultSet.getInt(1), user1, user2, resultSet.getTimestamp(4).toLocalDateTime(), resultSet.getString(5)));
            }
            resultSet.close();
            preparedStatement.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    @Override
    public Message mostrarUltimoMensajeToFrom(int userId, int contactId) {
        UserRepository userRepository = new UserRepository();
        try {
            Connection connection = DriverManager.getConnection(this.url, this.username, this.password);
            String querySelect = """
                    SELECT id_message, usuario_1, usuario_2, fecha_hora, mensaje
                    FROM public.mensajes WHERE usuario_1 = ? AND usuario_2 = ?
                    OR usuario_1 = ? AND usuario_2 = ? ORDER BY id_message
                    DESC LIMIT 1;""";
            PreparedStatement preparedStatement = connection.prepareStatement(querySelect);
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, contactId);
            preparedStatement.setInt(3, contactId);
            preparedStatement.setInt(4, userId);

            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()) {
                User user1 = userRepository.getUserId(resultSet.getInt(2));
                User user2 = userRepository.getUserId(resultSet.getInt(3));
                return  new Message(resultSet.getInt(1), user1, user2, resultSet.getTimestamp(4).toLocalDateTime(), resultSet.getString(5));
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
