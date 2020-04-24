package vivek.wo.mqtt;

import org.eclipse.paho.client.mqttv3.MqttMessage;

public interface MqttMessageCallback {

    /**
     * {@link org.eclipse.paho.client.mqttv3.MqttCallback#messageArrived(String, MqttMessage)}
     *
     * @param topic
     * @param message
     * @throws Exception
     */
    void messageArrived(String topic, MqttMessage message) throws Exception;
}
