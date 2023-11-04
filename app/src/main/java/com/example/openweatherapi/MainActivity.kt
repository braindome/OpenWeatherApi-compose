package com.example.openweatherapi

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.openweatherapi.ui.theme.OpenWeatherAPITheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.Response
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val httpLoggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()

            val api = retrofit.create(OpenWeatherMapApi::class.java)
            val minTempState = remember { mutableStateOf("") }
            val maxTempState = remember { mutableStateOf("") }

            LaunchedEffect(Unit) {
                try {
                    val response = withContext(Dispatchers.IO) {
                        api.getWeather("Rome", "ef01d6a7af729f664c701ccfe640eb2e")
                    }

                    if (response.isSuccessful) {
                        val weatherResponse = response.body()
                        if (weatherResponse != null) {
                            withContext(Dispatchers.Main) {
                                minTempState.value = weatherResponse.main.temp_min.toString()
                                maxTempState.value = weatherResponse.main.temp_max.toString()
                            }
                        } else {
                            // Handle null response
                            Log.d("xxx", "Null response body")
                        }
                    } else {
                        // Handle non-successful response (e.g., 404, 500, etc.)
                        Log.d("xxx", "Non-successful response: ${response.code()}")
                    }
                } catch (e: Exception) {
                    // Handle potential exceptions
                    Log.d("xxx", "Error: ${e.message}")
                }

            }

            OpenWeatherAPITheme {


                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        modifier = Modifier.fillMaxHeight(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "Il tempo a Roma")
                        Row {
                            Text(text = "Min Temp =${minTempState.value}")
                            Text(text = "Max Temp =${maxTempState.value}")
                        }
                    }

                }
            }
        }
    }
}


@Preview (showBackground = true)
@Composable
fun MainActivityPreview() {
    MainActivity()

}

