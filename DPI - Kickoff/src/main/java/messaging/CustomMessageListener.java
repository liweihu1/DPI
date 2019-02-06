package messaging;

import javax.jms.Message;
import javax.jms.MessageListener;

public class CustomMessageListener implements MessageListener {
    @Override
    public void onMessage(Message message) {
        System.out.println(message);
    }
}
