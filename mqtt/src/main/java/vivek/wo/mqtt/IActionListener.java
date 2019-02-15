package vivek.wo.mqtt;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/**
 * Created by VIVEK-WO on 2018/3/19.
 */

public interface IActionListener extends IInterface {

    public static abstract class Stub extends Binder implements IActionListener {
        public static final String DESCRIPTOR = "vivek.wo.mqtt.IActionListener";

        public Stub() {
            this.attachInterface(this, DESCRIPTOR);
        }

        public static IActionListener asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iInterface = obj.queryLocalInterface(DESCRIPTOR);
            if (iInterface != null && iInterface instanceof IActionListener) {
                return (IActionListener) iInterface;
            }
            return new Stub.Proxy(obj);
        }

        @Override
        public IBinder asBinder() {
            return this;
        }

        @Override
        protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws
                RemoteException {
            switch (code) {
                case INTERFACE_TRANSACTION: {
                    reply.writeString(DESCRIPTOR);
                    return true;
                }
                case TRANSACTION_onFailure: {
                    data.enforceInterface(DESCRIPTOR);
                    String _arg0;
                    _arg0 = data.readString();
                    String _arg1;
                    _arg1 = data.readString();
                    this.onFailure(_arg0, _arg1);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_onSuccess: {
                    data.enforceInterface(DESCRIPTOR);
                    String _arg0;
                    _arg0 = data.readString();
                    this.onSuccess(_arg0);
                    reply.writeNoException();
                    return true;
                }
            }
            return super.onTransact(code, data, reply, flags);
        }

        public static class Proxy implements IActionListener {
            private IBinder mRemote;

            public Proxy(IBinder remote) {
                mRemote = remote;
            }

            public String getInterfaceDescriptor() {
                return DESCRIPTOR;
            }

            @Override
            public IBinder asBinder() {
                return mRemote;
            }

            @Override
            public void onSuccess(String userContext) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeString(userContext);
                    mRemote.transact(TRANSACTION_onSuccess, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public void onFailure(String userContext, String detailMessage) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeString(userContext);
                    _data.writeString(detailMessage);
                    mRemote.transact(TRANSACTION_onFailure, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        static final int TRANSACTION_onSuccess = IBinder.FIRST_CALL_TRANSACTION + 0;
        static final int TRANSACTION_onFailure = IBinder.FIRST_CALL_TRANSACTION + 1;
    }

    void onSuccess(String userContext) throws RemoteException;

    void onFailure(String userContext, String detailMessage) throws RemoteException;
}
