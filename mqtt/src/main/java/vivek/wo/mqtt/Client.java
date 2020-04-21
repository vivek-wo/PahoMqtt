package vivek.wo.mqtt;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import javax.net.ssl.SSLSocketFactory;

/**
 * Created by VIVEK-WO on 2018/5/21.
 */

public class Client implements MqttCallbackExtended {

    private static final int DISCONNECT_QUIESCE_TIMEOUT = 0;//强制断开连接时间

    private String mClientHandler; // 当前Client的唯一标识
    private String mServerURI;
    private String mClientId;
    private MqttAsyncClient mMqttClient;
    private MqttConnectOptions mOpts;
    private String[] mSubscribtionTopics;
    private SSLSocketFactory mSSLSocketFactory;

    Client(String clientHandler) {
        mClientHandler = clientHandler;
    }

    Client(String clientHandler, String serverURI, String clientId) {
        mClientHandler = clientHandler;
        mServerURI = serverURI;
        mClientId = clientId;
    }

    void resetClient(String serverURI, String clientId) {
        mServerURI = serverURI;
        mClientId = clientId;
    }

    String getClientHandler() {
        return mClientHandler;
    }

    void setSSLSocketFactory(SSLSocketFactory sslSocketFactory) {
        mSSLSocketFactory = sslSocketFactory;
    }

    private void subscribeTopic(String[] topicFilter) {
        int topicsLength = mSubscribtionTopics == null ? 0 : mSubscribtionTopics.length;
        String[] topics = new String[topicsLength + topicFilter.length];
        System.arraycopy(topicFilter, 0, topics, topicsLength, topicFilter.length);
        mSubscribtionTopics = topics;
    }

    boolean isConnected() {
        return mMqttClient == null ? false : mMqttClient.isConnected();
    }

    void disconnect() {
        if (mMqttClient != null) {
            try {
                mMqttClient.disconnectForcibly(DISCONNECT_QUIESCE_TIMEOUT);
                mMqttClient.close();
            } catch (MqttException e) {
                e.printStackTrace();
            }
            mMqttClient = null;
        }
    }

    void connect() {
        disconnect();
        mSubscribtionTopics = null;
        try {
            mMqttClient = new MqttAsyncClient(mServerURI, mClientId, null);
            mMqttClient.setCallback(this);
            if (mOpts != null) {
                IMqttToken token = mMqttClient.connect(mOpts, null, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        MqttException mqttException = null;
                        int reasonCode = -1000;
                        if (exception instanceof MqttException) {
                            mqttException = (MqttException) exception;
                        }
                        if (mqttException != null) {
                            mqttException.printStackTrace();
                            reasonCode = mqttException.getReasonCode();
                        } else {
                            exception.printStackTrace();
                        }
                    }
                });
            }
        } catch (MqttException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
    }

    void reconnect() {
        try {
            if (mMqttClient != null) {
                mMqttClient.reconnect();
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    void subscribe(String subscribtionTopic, int qos) {
        subscribeTopic(new String[]{subscribtionTopic});
        if (isConnected()) {
            try {
                IMqttToken token = mMqttClient.subscribe(subscribtionTopic, qos, null,
                        new IMqttActionListener() {
                            @Override
                            public void onSuccess(IMqttToken asyncActionToken) {
                            }

                            @Override
                            public void onFailure(IMqttToken asyncActionToken, Throwable
                                    exception) {
                            }
                        });
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    void subscribe(String[] subscribtionTopics) {
        subscribeTopic(subscribtionTopics);
        subscribe();
    }

    private void subscribe() {
        if (isConnected()) {
            int[] qos = new int[mSubscribtionTopics.length];
            for (int i = 0; i < mSubscribtionTopics.length; i++) {
                qos[i] = 2;
            }
            try {
                IMqttToken token = mMqttClient.subscribe(mSubscribtionTopics, qos, null,
                        new IMqttActionListener() {
                            @Override
                            public void onSuccess(IMqttToken asyncActionToken) {
                            }

                            @Override
                            public void onFailure(IMqttToken asyncActionToken, Throwable
                                    exception) {
                            }
                        });
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    void publish(String topic, byte[] payload, int qos, boolean retained) {
        try {
            mMqttClient.publish(topic, payload, qos, retained, null,
                    new IMqttActionListener() {
                        @Override
                        public void onSuccess(IMqttToken asyncActionToken) {
                        }

                        @Override
                        public void onFailure(IMqttToken asyncActionToken, Throwable
                                exception) {
                        }
                    });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    void publish(String topic, MqttMessage mqttMessage) {
        try {
            mMqttClient.publish(topic, mqttMessage, null,
                    new IMqttActionListener() {
                        @Override
                        public void onSuccess(IMqttToken asyncActionToken) {
                        }

                        @Override
                        public void onFailure(IMqttToken asyncActionToken, Throwable
                                exception) {
                        }
                    });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    boolean isAutomaticReconnect() {
        return mOpts.isAutomaticReconnect();
    }

    @Override
    public void connectComplete(boolean reconnect, String serverURI) {
        if (reconnect && mSubscribtionTopics != null) {
            subscribe(null);
        }
    }

    @Override
    public void connectionLost(Throwable cause) {
        cause.printStackTrace();
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {

    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }
}