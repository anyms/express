package app.spidy.expressexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import app.spidy.express.Express
import kotlinx.android.synthetic.main.activity_main.*
import java.net.Inet4Address
import java.net.NetworkInterface

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var express: Express? = null
        startServer.setOnClickListener {
            val host = getIpv4HostAddress()
            express?.terminate()
            express = Express(host = host)
            express?.get("/") { req, res ->
                res.send("hello, world")
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
