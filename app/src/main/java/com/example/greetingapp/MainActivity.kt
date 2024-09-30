package com.example.greetingapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.connectrpc.ProtocolClientConfig
import com.connectrpc.extensions.GoogleJavaProtobufStrategy
import com.connectrpc.impl.ProtocolClient
import com.connectrpc.okhttp.ConnectOkHttpClient
import com.connectrpc.protocols.NetworkProtocol
import com.example.greetingapp.databinding.ActivityMainBinding
import greet.v1.Greet.GreetRequest
import greet.v1.GreetServiceClient
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    companion object {

        private const val HOST: String = "http://10.0.2.2:8080"

    }

    private var binding: ActivityMainBinding? = null
    private var greetServiceClient: GreetServiceClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        enableEdgeToEdge()
        setContentView(binding?.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        greetServiceClient = GreetServiceClient(
            ProtocolClient(
                httpClient = ConnectOkHttpClient(),
                ProtocolClientConfig(
                    host = HOST,
                    serializationStrategy = GoogleJavaProtobufStrategy(),
                    networkProtocol = NetworkProtocol.CONNECT,
                )
            )
        )

        binding?.apply {
            submitButtonView.setOnClickListener {
                lifecycleScope.launch {
                    greetServiceClient?.greet(
                        request = GreetRequest.newBuilder()
                            .setName(yourNameView.text.toString())
                            .build()
                    )?.let { response ->
                        response.success { success ->
                            resultTextView.text = success.message.greeting
                        }
                    }
                }
            }
        }
    }

}