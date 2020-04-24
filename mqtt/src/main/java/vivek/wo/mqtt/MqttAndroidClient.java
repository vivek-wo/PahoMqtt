package vivek.wo.mqtt;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import javax.net.SocketFactory;

public class MqttAndroidClient implements MqttCallbackExtended {
    private String serverURI;
    private String clientId;
    private MqttAsyncClient mqttAndroidClient;
    private MqttConnectOptions options;
    private MqttConnectionCallback mqttConnectionCallback;
    private MqttMessageCallback mqttMessageCallback;

    private MqttAndroidClient(String serverURI, String clientId, MqttConnectOptions options) {
        this.serverURI = serverURI;
        this.clientId = clientId;
        this.options = options;
    }

    public void setMqttConnectionCallback(MqttConnectionCallback mqttConnectionCallback) {
        this.mqttConnectionCallback = mqttConnectionCallback;
    }

    public void setMqttMessageCallback(MqttMessageCallback mqttMessageCallback) {
        this.mqttMessageCallback = mqttMessageCallback;
    }

    public void init() throws MqttException {
        if (mqttAndroidClient != null) {
            throw new MqttException(new IllegalStateException("Client inited"));
        }
        mqttAndroidClient = new MqttAsyncClient(serverURI, clientId, null);
        mqttAndroidClient.setCallback(this);
    }

    public void close() throws MqttException {
        if (mqttAndroidClient != null) {
            mqttAndroidClient.close();
        }
        mqttAndroidClient = null;
    }

    boolean isConnected() {
        return mqttAndroidClient.isConnected();
    }

    IMqttToken disconnect(Object userContext, IMqttActionListener callback) throws MqttException {
        return mqttAndroidClient.disconnect(userContext, callback);
    }

    void disconnectForcibly(int quiesceTimeout) throws MqttException {
        mqttAndroidClient.disconnectForcibly(quiesceTimeout);
    }

    IMqttToken connect(Object userContext, IMqttActionListener callback) throws MqttException {
        return mqttAndroidClient.connect(options, userContext, callback);
    }

    void reconnect() throws MqttException {
        mqttAndroidClient.reconnect();
    }

    IMqttToken subscribe(String topicFilter, int qos, Object userContext,
                         IMqttActionListener callback) throws MqttException {
        return mqttAndroidClient.subscribe(topicFilter, qos, userContext, callback);
    }

    IMqttToken subscribe(String[] topicFilters, int[] qos, Object userContext,
                         IMqttActionListener callback) throws MqttException {
        return mqttAndroidClient.subscribe(topicFilters, qos, userContext, callback);
    }

    IMqttDeliveryToken publish(String topic, byte[] payload, int qos,
                               boolean retained, Object userContext,
                               IMqttActionListener callback) throws MqttException {
        IMqttDeliveryToken token = mqttAndroidClient.publish(topic, payload, qos, retained,
                userContext, callback);
        return token;
    }

    IMqttDeliveryToken publish(String topic, MqttMessage message, Object userContext,
                               IMqttActionListener callback) throws MqttException {
        IMqttDeliveryToken token = mqttAndroidClient.publish(topic, message,
                userContext, callback);
        return token;
    }

    boolean isAutomaticReconnect() {
        return options.isAutomaticReconnect();
    }

    @Override
    public void connectComplete(boolean reconnect, String serverURI) {
        if (mqttConnectionCallback != null) {
            mqttConnectionCallback.connectComplete(reconnect, serverURI);
        }
    }

    @Override
    public void connectionLost(Throwable cause) {
        if (mqttConnectionCallback != null) {
            mqttConnectionCallback.connectionLost(cause);
        }
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        if (mqttMessageCallback != null) {
            mqttMessageCallback.messageArrived(topic, message);
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
    }

    public static class Builder {
        private String serverURI;
        private String clientId;

        private MqttConnectOptions options;

        public Builder(String serverURI, String clientId) {
            this.serverURI = serverURI;
            this.clientId = clientId;
            options = new MqttConnectOptions();
        }

        /**
         * {@link MqttConnectOptions#setKeepAliveInterval(int)}
         *
         * @param keepAliveInterval
         */
        public void setKeepAliveInterval(int keepAliveInterval) {
            this.options.setKeepAliveInterval(keepAliveInterval);
        }

        /**
         * {@link MqttConnectOptions#setMaxInflight(int)}
         *
         * @param maxInflight
         */
        public void setMaxInflight(int maxInflight) {
            this.options.setMaxInflight(maxInflight);
        }

        /**
         * {@link MqttConnectOptions#setUserName(String)}
         *
         * @param userName
         */
        public void setUserName(String userName) {
            this.options.setUserName(userName);
        }

        /**
         * {@link MqttConnectOptions#setPassword(char[])}
         *
         * @param password
         */
        public void setPassword(char[] password) {
            this.options.setPassword(password);
        }

        /**
         * {@link MqttConnectOptions#setSocketFactory(SocketFactory)}
         *
         * @param socketFactory
         */
        public void setSocketFactory(SocketFactory socketFactory) {
            this.options.setSocketFactory(socketFactory);
        }

        /**
         * {@link MqttConnectOptions#setCleanSession(boolean)}
         *
         * @param cleanSession
         */
        public void setCleanSession(boolean cleanSession) {
            this.options.setCleanSession(cleanSession);
        }

        /**
         * {@link MqttConnectOptions#setConnectionTimeout(int)}
         *
         * @param connectionTimeout
         */
        public void setConnectionTimeout(int connectionTimeout) {
            this.options.setConnectionTimeout(connectionTimeout);
        }

        /**
         * {@link MqttConnectOptions#setMqttVersion(int)}
         *
         * @param mqttVersion
         */
        public void setMqttVersion(int mqttVersion) {
            this.options.setMqttVersion(mqttVersion);
        }

        /**
         * {@link MqttConnectOptions#setAutomaticReconnect(boolean)}
         *
         * @param automaticReconnect
         */
        public void setAutomaticReconnect(boolean automaticReconnect) {
            this.options.setAutomaticReconnect(automaticReconnect);
        }

        /**
         * {@link MqttConnectOptions#setMaxReconnectDelay(int)}
         *
         * @param maxReconnectDelay
         */
        public void setMaxReconnectDelay(int maxReconnectDelay) {
            this.options.setMaxReconnectDelay(maxReconnectDelay);
        }

        public MqttAndroidClient build() {
            return new MqttAndroidClient(this.serverURI, this.clientId, options);
        }
    }
}