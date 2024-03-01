package com.idrovo.michat.connection;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.idrovo.michat.model.Message;
import com.idrovo.michat.model.User;

import com.idrovo.michat.repository.UserRepository;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.time.LocalDateTime;

public class MqttChat {
    private final UserRepository userRepository = new UserRepository();
    String broker = "tcp://localhost:1883";
    MqttClient client;

    public MqttChat() {}

    public void connect(User user) throws MqttException {
        MemoryPersistence persistence = new MemoryPersistence();
        client = new MqttClient(broker, user.getUsername(), persistence);
        MqttConnectOptions connOpts = new MqttConnectOptions();
        client.connect(connOpts);
    }

    public void subscribe(String topic) throws MqttException{
        client.subscribe(topic);
    }

    public void unsubscribe(String topic) throws MqttException {
        client.unsubscribe(topic);
    }

    public void sendMessage(String topic, Message message) throws MqttPersistenceException, MqttException {
        MqttMessage mqttMessage = new MqttMessage(message.toString().getBytes());
        client.publish(topic, mqttMessage);

    }

    public void messageArrived(MessageArrivedAction action) {

        client.setCallback(new MqttCallback() {

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {

                String text = new String(message.getPayload());
                JsonParser parser = new JsonParser();
                JsonObject jsonObjectMsg = parser.parse(text.toString()).getAsJsonObject();

                String content = jsonObjectMsg.get("content").getAsString();

                JsonObject jsonFrom = jsonObjectMsg.get("from").getAsJsonObject();
                int fromId = jsonFrom.get("id").getAsInt();

                JsonObject jsonTo = jsonObjectMsg.get("to").getAsJsonObject();
                int toId = jsonTo.get("id").getAsInt();

                User from = userRepository.getUserId(fromId);
                User to = userRepository.getUserId(toId);

                LocalDateTime date = LocalDateTime.now();
                Message newMessage = new Message(from, to, date, content);

                action.messageArrived(topic, newMessage);

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken arg0) {
            }

            @Override
            public void connectionLost(Throwable arg0) {
            }
        });

    }
}
