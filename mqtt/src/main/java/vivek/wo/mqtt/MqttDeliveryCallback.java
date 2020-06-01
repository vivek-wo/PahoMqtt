package vivek.wo.mqtt;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;

public interface MqttDeliveryCallback {

    /**
     * {@link org.eclipse.paho.client.mqttv3.MqttCallback#deliveryComplete(IMqttDeliveryToken)}
     *
     * @param token the delivery token associated with the message.
     */
    void deliveryComplete(IMqttDeliveryToken token);

}
