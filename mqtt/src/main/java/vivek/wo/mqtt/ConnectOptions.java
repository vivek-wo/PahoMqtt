package vivek.wo.mqtt;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by VIVEK-WO on 2018/3/14.
 */

public class ConnectOptions implements Parcelable {
    private final boolean cleanSession;
    private final boolean automaticReconnect;
    private final int keepAliveInterval;
    private final int connectionTimeout;
    private final int maxInflight;
    private final String userName;
    private final String password;

    public ConnectOptions(boolean cleanSession, boolean automaticReconnect, int
            keepAliveInterval, int connectionTimeout, int maxInflight, String userName, String
                                  password) {
        this.cleanSession = cleanSession;
        this.automaticReconnect = automaticReconnect;
        this.keepAliveInterval = keepAliveInterval;
        this.connectionTimeout = connectionTimeout;
        this.maxInflight = maxInflight;
        this.userName = userName;
        this.password = password;
    }

    protected ConnectOptions(Parcel in) {
        cleanSession = in.readByte() != 0;
        automaticReconnect = in.readByte() != 0;
        keepAliveInterval = in.readInt();
        connectionTimeout = in.readInt();
        maxInflight = in.readInt();
        userName = in.readString();
        password = in.readString();
    }

    public static final Creator<ConnectOptions> CREATOR = new Creator<ConnectOptions>() {
        @Override
        public ConnectOptions createFromParcel(Parcel in) {
            return new ConnectOptions(in);
        }

        @Override
        public ConnectOptions[] newArray(int size) {
            return new ConnectOptions[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (cleanSession ? 1 : 0));
        dest.writeByte((byte) (automaticReconnect ? 1 : 0));
        dest.writeInt(keepAliveInterval);
        dest.writeInt(connectionTimeout);
        dest.writeInt(maxInflight);
        dest.writeString(userName);
        dest.writeString(password);
    }

    public boolean isCleanSession() {
        return cleanSession;
    }

    public boolean isAutomaticReconnect() {
        return automaticReconnect;
    }

    public int getKeepAliveInterval() {
        return keepAliveInterval;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public int getMaxInflight() {
        return maxInflight;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }
}
