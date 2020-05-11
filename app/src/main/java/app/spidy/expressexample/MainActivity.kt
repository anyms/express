package app.spidy.expressexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import app.spidy.express.Express
import kotlinx.android.synthetic.main.activity_main.*
import java.net.Inet4Address
import java.net.NetworkInterface

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val express = Express()
        val host = getIpv4HostAddress()
        ipView.text = host

        express.get("/") { req, res ->
            res.html("""
                {"name": "jeeva", "age": 23}
            """.trimIndent())
        }

        express.post("/about") { req, res ->
            res.send("hello, ${req.form["name"]}")
        }

        express.runAsync(host = host, port = 3000)
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
