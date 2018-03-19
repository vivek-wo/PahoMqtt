package vivek.wo.mqtt.sample

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import vivek.wo.mqtt.PahoMqtt
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        PahoMqtt.Builder("tcp://10.0.5.88:1883", "e4a02f31b247d475")
                .setSubscribtionTopics(arrayOf("local/e4a02f31b247d475"))
                .setAutomaticReconnect(false)
                .build()
                .setup(this);

    }
}
