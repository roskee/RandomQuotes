package com.example.randomquotes

import androidx.lifecycle.viewModelScope
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers

data class Quote(
    val quote: String,
    val author: String,
    val category: String
)

interface ApiService {
    @Headers("X-Api-Key: oL1AeWZap+kNe6eQ8F7+tg==P3mYQt1TubiXyOy8")
    @GET("quotes")
    suspend fun getQuote(): Response<List<Quote>>
}

object RetrofitHelper {
    private const val baseUrl = "https://api.api-ninjas.com/v1/"

    fun getInstance(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}

class QuoteViewModel : ViewModel() {
    private val _quote =
        mutableStateOf<Quote?>(null)

    val quote: MutableState<Quote?> = _quote

    fun getQuote() {
        val quoteApi = RetrofitHelper.getInstance().create(ApiService::class.java)
        viewModelScope.launch {
            val result = quoteApi.getQuote()
            println("fetched quote: $result")
            if (result.isSuccessful) {
                val quote = result.body()
                _quote.value = quote?.get(0)
            }
        }
    }
}

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        title = {
                            Text(
                                text = "Random Quotes",
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    )
                },
                bottomBar = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            text = "Made by: github.com/roskee",
                        )
                    }
                }
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .padding(
                            paddingValues
                                .calculateBottomPadding()
                                .plus(16.dp)
                        )
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.Center,
                ) {
                    QuoteView(quoteModel = QuoteViewModel())
                }
            }
        }
    }
}

@Composable
fun QuoteCard(quote: Quote) {
    Divider()
    Text(
        text = "\"${quote.quote}\"",
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Italic,
        fontSize = 20.sp,
        textAlign = TextAlign.Center
    )
    Divider()
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Text(
            quote.author,
        )
    }
}

@Composable
fun QuoteView(quoteModel: QuoteViewModel) {
    LaunchedEffect(quoteModel) {
        quoteModel.getQuote()
    }
    LazyColumn {
        item {
            QuoteCard(quote = quoteModel.quote.value ?: Quote("Loading", "", ""))
        }
    }
}