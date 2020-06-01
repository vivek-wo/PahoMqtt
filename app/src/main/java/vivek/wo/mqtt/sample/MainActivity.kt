package vivek.wo.mqtt.sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttToken
import vivek.wo.mqtt.MqttAndroidClient
import vivek.wo.mqtt.MqttConnectionCallback

class MainActivity : AppCompatActivity() {
    val TAG = MainActivity::class.simpleName

    lateinit var client: MqttAndroidClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        client = MqttAndroidClient.Builder(
                        "tcp://192.168.105.161:1883",
                        "5ecf77cc0927690fb0ab0865&test51&2524608000000")
                .setUserName("5ecf77cc0927690fb0ab0865&test51")
                .setPassword("c2f45aacf0a841dfb82606f36bc95dd8".toCharArray())
                .build()
        client.setMqttConnectionCallback(object : MqttConnectionCallback {
            override fun connectComplete(reconnect: Boolean, serverURI: String?) {
                Log.w(TAG, "connectComplete reconnect: $reconnect, serverURI: $serverURI")
            }

            override fun connectionLost(cause: Throwable?) {
                Log.w(TAG, "connectionLost: ${cause?.message}")
            }
        })

        client.setMqttMessageCallback { topic, message ->
            Log.w(TAG, "received Message: $topic")
            Log.w(TAG, "$message")
        }

        client.createMqttClient()

        client.connect(null, object : IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken?) {
                Log.w(TAG, "connect: onSuccess")
                client.subscribe("/sys/5ecf77cc0927690fb0ab0865/test51/thing/event/+/post_reply", 0, null, object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken?) {
                        Log.w(TAG, "subscribe: onSuccess")
                    }

                    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                        Log.w(TAG, "subscribe: onFailure")
                    }
                })
            }

            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                exception?.printStackTrace()
                Log.w(TAG, "connect: onFailure")
            }

        })

    }

    fun publish(view: View) {
        val message = "{\"id\":\"2\",\"version\":\"1.0\",\"params\":{\"Counter\": 1},\"method\":\"thing.event.property.post\"}"
        client.publish("/sys/5ecf77cc0927690fb0ab0865/test51/thing/event/property/post",
                message.toByteArray(), 0, false, null,
                object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken?) {
                        Log.w(TAG, "publish: onSuccess")
                    }

                    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                        Log.w(TAG, "publish: onFailure")
                    }

                })
    }
}
