package vivek.wo.mqtt.sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import vivek.wo.mqtt.PahoMqtt

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        PahoMqtt.Builder("tcp://10.0.5.88:1883", "e4a02f31b247d475")
//                .setSubscribtionTopics(arrayOf("local/e4a02f31b247d475"))
//                .setAutomaticReconnect(false)
//                .build()
//                .setup(this);
        var pahoMqtt = PahoMqtt()
        Thread(Runnable {
            pahoMqtt.setup(applicationContext)
        }).start()



    }
}
