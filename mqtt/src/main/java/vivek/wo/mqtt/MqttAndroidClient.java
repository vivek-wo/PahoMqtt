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

public class MqttAndroidClient {
    private String serverURI;
    private String clientId;
    private MqttAsyncClient mqttAndroidClient;
    private MqttConnectOptions options;
    private MqttConnectionCallback mqttConnectionCallback;
    private MqttMessageCallback mqttMessageCallback;
    private MqttDeliveryCallback mqttDeliveryCallback;

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

    public void setMqttDeliveryCallback(MqttDeliveryCallback mqttDeliveryCallback) {
        this.mqttDeliveryCallback = mqttDeliveryCallback;
    }

    public void createMqttClient() throws MqttException {
        if (mqttAndroidClient != null) {
            throw new MqttException(new IllegalStateException("Client created"));
        }
        mqttAndroidClient = new MqttAsyncClient(serverURI, clientId, null);
        mqttAndroidClient.setCallback(mMqttCallbackExtended);
    }

    public void destroyMqttClient() throws MqttException {
        if (mqttAndroidClient != null) {
            if (isConnected()) {
                disconnectForcibly(0);
            }
            mqttAndroidClient.close();
        }
        mqttAndroidClient = null;
        mqttConnectionCallback = null;
        mqttMessageCallback = null;
    }

    public boolean isConnected() {
        return mqttAndroidClient.isConnected();
    }

    public IMqttToken disconnect(Object userContext, IMqttActionListener callback) throws MqttException {
        return mqttAndroidClient.disconnect(userContext, callback);
    }

    public void disconnectForcibly(int quiesceTimeout) throws MqttException {
        mqttAndroidClient.disconnectForcibly(quiesceTimeout);
    }

    public IMqttToken connect(Object userContext, IMqttActionListener callback) throws MqttException {
        return mqttAndroidClient.connect(options, userContext, callback);
    }

    public void reconnect() throws MqttException {
        mqttAndroidClient.reconnect();
    }

    public IMqttToken subscribe(String topicFilter, int qos, Object userContext,
                                IMqttActionListener callback) throws MqttException {
        return mqttAndroidClient.subscribe(topicFilter, qos, userContext, callback);
    }

    public IMqttToken subscribe(String[] topicFilters, int[] qos, Object userContext,
                                IMqttActionListener callback) throws MqttException {
        return mqttAndroidClient.subscribe(topicFilters, qos, userContext, callback);
    }

    public IMqttDeliveryToken publish(String topic, byte[] payload, int qos,
                                      boolean retained, Object userContext,
                                      IMqttActionListener callback) throws MqttException {
        return mqttAndroidClient.publish(topic, payload, qos, retained,
                userContext, callback);
    }

    public IMqttDeliveryToken publish(String topic, MqttMessage message, Object userContext,
                                      IMqttActionListener callback) throws MqttException {
        return mqttAndroidClient.publish(topic, message,
                userContext, callback);
    }

    public boolean isAutomaticReconnect() {
        return options.isAutomaticReconnect();
    }

    private MqttCallbackExtended mMqttCallbackExtended = new MqttCallbackExtended() {
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
            if (mqttDeliveryCallback != null) {
                mqttDeliveryCallback.deliveryComplete(token);
            }
        }
    };

    public static class Builder {
        private String serverURI;
        private String clientId;

        private MqttConnectOptions options;

        /**
         * Set a serverURI the client may connect to.
         * <p>
         * Each <code>serverURI</code> specifies the address of a server that the client
         * may connect to. Two types of connection are supported <code>tcp://</code> for
         * a TCP connection and <code>ssl://</code> for a TCP connection secured by
         * SSL/TLS. For example:
         * <ul>
         * <li><code>tcp://localhost:1883</code></li>
         * <li><code>ssl://localhost:8883</code></li>
         * </ul>
         * </P>
         *
         * @param serverURI the address of the server to connect to, specified as a URI.
         * @param clientId  a client identifier that is unique on the server being
         *                  connected to
         */
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
        public Builder setKeepAliveInterval(int keepAliveInterval) {
            this.options.setKeepAliveInterval(keepAliveInterval);
            return this;
        }

        /**
         * {@link MqttConnectOptions#setMaxInflight(int)}
         *
         * @param maxInflight
         */
        public Builder setMaxInflight(int maxInflight) {
            this.options.setMaxInflight(maxInflight);
            return this;
        }

        /**
         * {@link MqttConnectOptions#setUserName(String)}
         *
         * @param userName
         */
        public Builder setUserName(String userName) {
            this.options.setUserName(userName);
            return this;
        }

        /**
         * {@link MqttConnectOptions#setPassword(char[])}
         *
         * @param password
         */
        public Builder setPassword(char[] password) {
            this.options.setPassword(password);
            return this;
        }

        /**
         * {@link MqttConnectOptions#setSocketFactory(SocketFactory)}
         *
         * @param socketFactory
         */
        public Builder setSocketFactory(SocketFactory socketFactory) {
            this.options.setSocketFactory(socketFactory);
            return this;
        }

        /**
         * {@link MqttConnectOptions#setCleanSession(boolean)}
         *
         * @param cleanSession
         */
        public Builder setCleanSession(boolean cleanSession) {
            this.options.setCleanSession(cleanSession);
            return this;
        }

        /**
         * {@link MqttConnectOptions#setConnectionTimeout(int)}
         *
         * @param connectionTimeout
         */
        public Builder setConnectionTimeout(int connectionTimeout) {
            this.options.setConnectionTimeout(connectionTimeout);
            return this;
        }

        /**
         * {@link MqttConnectOptions#setMqttVersion(int)}
         *
         * @param mqttVersion
         */
        public Builder setMqttVersion(int mqttVersion) {
            this.options.setMqttVersion(mqttVersion);
            return this;
        }

        /**
         * {@link MqttConnectOptions#setAutomaticReconnect(boolean)}
         *
         * @param automaticReconnect
         */
        public Builder setAutomaticReconnect(boolean automaticReconnect) {
            this.options.setAutomaticReconnect(automaticReconnect);
            return this;
        }

        /**
         * {@link MqttConnectOptions#setMaxReconnectDelay(int)}
         *
         * @param maxReconnectDelay
         */
        public Builder setMaxReconnectDelay(int maxReconnectDelay) {
            this.options.setMaxReconnectDelay(maxReconnectDelay);
            return this;
        }

        public MqttAndroidClient build() {
            return new MqttAndroidClient(this.serverURI, this.clientId, options);
        }
    }
}