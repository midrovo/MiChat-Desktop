package com.idrovo.michat.connection;

import com.idrovo.michat.model.Message;

public interface MessageArrivedAction {
    public void messageArrived(String topic, Message message);
}
