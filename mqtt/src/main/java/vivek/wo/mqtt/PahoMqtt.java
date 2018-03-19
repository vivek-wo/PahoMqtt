package vivek.wo.mqtt;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

/**
 * Created by VIVEK-WO on 2018/3/12.
 */

public class PahoMqtt {
    private static final String TAG = "PahoMqtt";
    private final String serverURI;
    private final String clientId;
    private final ConnectOptions connectOptions;
    private final String[] subscribtionTopics;

    PahoMqtt(final Builder builder) {
        this.serverURI = builder.serverURI;
        this.clientId = builder.clientId;
        this.connectOptions = new ConnectOptions(builder.cleanSession, builder.automaticReconnect,
                builder.keepAliveInterval, builder.connectionTimeout, builder.maxInflight,
                builder.userName, builder.password);
        this.subscribtionTopics = builder.subscribtionTopics;
    }

    private IClient iClient;

    public void setup(Context context) {
        Intent intent = new Intent(context, MqttService.class);
        intent.setAction("vivek.wo.mqtt.MqttService.action.setup");
        intent.putExtra("serverURI", this.serverURI);
        intent.putExtra("clientId", this.clientId);
        intent.putExtra("connectOptions", this.connectOptions);
        intent.putExtra("subscribtionTopics", this.subscribtionTopics);
        context.startService(intent);
        context.bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.d(TAG, "onServiceConnected: ");
                iClient = IClient.Stub.asInterface(service);
                try {
                    iClient.addIClientListener(iClientListener);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.d(TAG, "onServiceDisconnected: ");
                try {
                    iClient.removeIClientListener(iClientListener);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                iClient = null;
            }
        }, Context.BIND_AUTO_CREATE);
    }

    private IClientListener iClientListener = new IClientListener.Stub() {

        @Override
        public void messageArrived(String topic, int messageId, int qos, String message) throws
                RemoteException {
            Log.d(TAG, "messageArrived: topic:" + topic + " ,messageId:" + messageId
                    + " ,qos:" + qos + " ,message:" + message);
        }
    };

    public static final class Builder {
        private final String serverURI;
        private final String clientId;
        private boolean cleanSession = false;
        private boolean automaticReconnect = true;
        private int keepAliveInterval = 60;
        private int connectionTimeout = 30;
        private int maxInflight = Short.MAX_VALUE - 1;
        private String userName;
        private String password;
        private String[] subscribtionTopics;

        public Builder(String serverURI, String clientId) {
            this.serverURI = serverURI;
            this.clientId = clientId;
        }

        public Builder setCleanSession(boolean cleanSession) {
            this.cleanSession = cleanSession;
            return this;
        }

        public Builder setAutomaticReconnect(boolean automaticReconnect) {
            this.automaticReconnect = automaticReconnect;
            return this;
        }

        public Builder setKeepAliveInterval(int keepAliveInterval) {
            this.keepAliveInterval = keepAliveInterval;
            return this;
        }

        public Builder setConnectionTimeout(int connectionTimeout) {
            this.connectionTimeout = connectionTimeout;
            return this;
        }

        public Builder setMaxInflight(int maxInflight) {
            this.maxInflight = maxInflight;
            return this;
        }

        public Builder setUserName(String userName) {
            this.userName = userName;
            return this;
        }

        public Builder setPassword(String password) {
            this.password = password;
            return this;
        }

        public Builder setSubscribtionTopics(String[] subscribtionTopics) {
            this.subscribtionTopics = subscribtionTopics;
            return this;
        }

        public PahoMqtt build() {
            return new PahoMqtt(this);
        }
    }
}
