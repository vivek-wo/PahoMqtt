package vivek.wo.mqtt;

import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Created by VIVEK-WO on 2018/5/21.
 */

public class Client implements MqttCallbackExtended {
    private static final String TAG = Client.class.getSimpleName();
    private static final int DISCONNECT_QUIESCE_TIMEOUT = 0;//强制断开连接时间

    private RemoteCallbackList<IClientListener> mIClientListenerList = new RemoteCallbackList<>();

    private String mClientHandler; // 当前Client的唯一标识
    private String mServerURI;
    private String mClientId;
    private MqttAsyncClient mMqttClient;
    private MqttConnectOptions mOpts;
    private String[] mSubscribtionTopics;

    private boolean mIsExcuteInited = false;// 是否执行过connect初始化

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

    boolean isExcuteInited() {
        return mIsExcuteInited;
    }

    void addIClientListener(IClientListener iClientListener) throws RemoteException {
        mIClientListenerList.register(iClientListener);
    }

    void removeIClientListener(IClientListener iClientListener) throws RemoteException {
        mIClientListenerList.unregister(iClientListener);
    }

    private void setMqttConnectOptions(ConnectOptions options) {
        mOpts = new MqttConnectOptions();
        mOpts.setCleanSession(options.isCleanSession());
        mOpts.setAutomaticReconnect(options.isAutomaticReconnect());
        mOpts.setKeepAliveInterval(options.getKeepAliveInterval());
        mOpts.setConnectionTimeout(options.getConnectionTimeout());
        mOpts.setMaxInflight(options.getMaxInflight());
        if (options.getUserName() != null && !options.getUserName().trim().equals("")) {
            mOpts.setUserName(options.getUserName());
            mOpts.setPassword(options.getPassword().toCharArray());
        }
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
            mIsExcuteInited = false;
        }
    }

    void connect(ConnectOptions options) {
        disconnect();
        mSubscribtionTopics = null;
        try {
            mMqttClient = new MqttAsyncClient(mServerURI, mClientId, null);
            mMqttClient.setCallback(this);
            setMqttConnectOptions(options);
            if (mOpts != null) {
                IMqttToken token = mMqttClient.connect(mOpts);
                token.waitForCompletion();
                mIsExcuteInited = true;
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
                                Log.d(TAG, "onSuccess: subscribe");
                            }

                            @Override
                            public void onFailure(IMqttToken asyncActionToken, Throwable
                                    exception) {

                                Log.d(TAG, "onFailure: subscribe");
                            }
                        });
                token.waitForCompletion();
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    void subscribe(String[] subscribtionTopics) {
        subscribeTopic(subscribtionTopics);
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
                                Log.d(TAG, "onSuccess: subscribe");
                            }

                            @Override
                            public void onFailure(IMqttToken asyncActionToken, Throwable
                                    exception) {

                                Log.d(TAG, "onFailure: subscribe");
                            }
                        });
                token.waitForCompletion();
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    void pusblish(String topic, byte[] payload, int qos, boolean retained) {
        try {
            mMqttClient.publish(topic, payload, qos, retained);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    void pusblish(String topic, MqttMessage mqttMessage) {
        try {
            mMqttClient.publish(topic, mqttMessage);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    boolean isAutomaticReconnect() {
        return mOpts.isAutomaticReconnect();
    }

    @Override
    public void connectComplete(boolean reconnect, String serverURI) {
        Log.d(TAG, "connectComplete: " + reconnect);
        if (mSubscribtionTopics != null) {
            subscribe(mSubscribtionTopics);
        }
    }

    @Override
    public void connectionLost(Throwable cause) {
        Log.d(TAG, "connectionLost: " + cause.getMessage());
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        Log.d(TAG, "messageArrived: " + topic + " , " + message.getId());
        if (mIClientListenerList != null) {
            int count = mIClientListenerList.beginBroadcast();
            for (int i = 0; i < count; i++) {
                try {
                    mIClientListenerList.getBroadcastItem(i)
                            .messageArrived(topic, message.getId(), message.getQos(), message
                                    .toString());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            mIClientListenerList.finishBroadcast();
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }
}
