package app.spidy.expressexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import app.spidy.express.Express
import app.spidy.hiper.Hiper
import app.spidy.kotlinutils.debug
import kotlinx.android.synthetic.main.activity_main.*
import java.net.Inet4Address
import java.net.NetworkInterface

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val hiper = Hiper.getInstance()

        var express: Express? = null
        startServer.setOnClickListener {
            val host = getIpv4HostAddress()
            debug(host)
            express?.terminate()
            express = Express(host = host, port = 3000)
            express?.get("/") { req, res ->
                res.send("hello, world")
            }
            express?.get("/mp3") { req, res ->
                val r = hiper.get("http://www.friendstamilmp3.in/songs2/A-Z%20Movie%20Songs/Antha%20Veetil%20Oru%20Kovil/Aayiram%20Jenmangal.mp3", isStream = true)
                res.stream(r.stream!!, r.headers["content-length"]!!.toLong(), "audio/mp3")
                r.stream?.close()
            }
            express?.runAsync()
            startServer.text = host
        }
    }


    private fun getIpv4HostAddress(): String {
        NetworkInterface.getNetworkInterfaces()?.toList()?.map { networkInterface ->
            networkInterface.inetAddresses?.toList()?.find {
                !it.isLoopbackAddress && it is Inet4Address
            }?.let { return it.hostAddress }
        }
        return "127.0.0.1"
    }
}
