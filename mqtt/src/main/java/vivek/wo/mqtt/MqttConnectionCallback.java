package vivek.wo.mqtt;

public interface MqttConnectionCallback {

    /**
     * {@link org.eclipse.paho.client.mqttv3.MqttCallbackExtended#connectComplete(boolean, String)}
     *
     * @param reconnect
     * @param serverURI
     */
    void connectComplete(boolean reconnect, String serverURI);

    /**
     * {@link org.eclipse.paho.client.mqttv3.MqttCallback#connectionLost(Throwable)}
     *
     * @param cause
     */
    void connectionLost(Throwable cause);
}
