package vivek.wo.mqtt;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by VIVEK-WO on 2018/3/14.
 */

public class ConnectOptions implements Parcelable {
    /**
     * The default keep alive interval in seconds if one is not specified
     */
    public static final int KEEP_ALIVE_INTERVAL_DEFAULT = 60;
    /**
     * The default connection timeout in seconds if one is not specified
     */
    public static final int CONNECTION_TIMEOUT_DEFAULT = 30;
    /**
     * The default max inflight if one is not specified
     */
    public static final int MAX_INFLIGHT_DEFAULT = 10;
    /**
     * The default clean session setting if one is not specified
     */
    public static final boolean CLEAN_SESSION_DEFAULT = false;

    private boolean cleanSession = CLEAN_SESSION_DEFAULT;
    private boolean automaticReconnect = true;
    private int keepAliveInterval = KEEP_ALIVE_INTERVAL_DEFAULT;
    private int connectionTimeout = CONNECTION_TIMEOUT_DEFAULT;
    private int maxInflight = MAX_INFLIGHT_DEFAULT;
    private String userName;
    private String password;
    private String assetCrtName;

    public ConnectOptions(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public ConnectOptions() {

    }

    protected ConnectOptions(Parcel in) {
        cleanSession = in.readByte() != 0;
        automaticReconnect = in.readByte() != 0;
        keepAliveInterval = in.readInt();
        connectionTimeout = in.readInt();
        maxInflight = in.readInt();
        userName = in.readString();
        password = in.readString();
        assetCrtName = in.readString();
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
        dest.writeString(assetCrtName);
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

    public String getAssetCrtName() {
        return assetCrtName;
    }

    public void setAssetCrtName(String assetCrtName) {
        this.assetCrtName = assetCrtName;
    }

    public void setCleanSession(boolean cleanSession) {
        this.cleanSession = cleanSession;
    }

    public void setAutomaticReconnect(boolean automaticReconnect) {
        this.automaticReconnect = automaticReconnect;
    }

    public void setKeepAliveInterval(int keepAliveInterval) {
        this.keepAliveInterval = keepAliveInterval;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public void setMaxInflight(int maxInflight) {
        this.maxInflight = maxInflight;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}