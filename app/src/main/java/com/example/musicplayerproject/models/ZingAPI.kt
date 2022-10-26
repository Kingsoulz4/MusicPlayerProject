package com.example.musicplayerproject.models

import android.content.Context
import android.os.Build
import android.os.Debug
import android.util.Log
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.security.InvalidKeyException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.security.SignatureException
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.*
import java.util.AbstractMap.SimpleEntry
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec


class ZingAPI {

    private lateinit var version: String
    private lateinit var url: String
    private lateinit var secretKey: String
    private lateinit var apiKey: String
    private lateinit var ctime: String
    private lateinit var cookieGot:String

    private constructor(version: String, url: String, secretKey:String, apiKey:String, ctime:String) {
        this.version = version
        this.url = url
        this.secretKey = secretKey
        this.apiKey = apiKey
        this.ctime = ctime
    }

    private lateinit var context: Context

    @RequiresApi(Build.VERSION_CODES.O)
    val nowInUtc = OffsetDateTime.now(ZoneOffset.UTC)


    private object Holder {
        val instance = ZingAPI(
        "1.6.34", // VERSION
        "https://zingmp3.vn", // URL
        "2aa2d1c561e809b267f3638c4a307aab", // SECRET_KEY
        "88265e23d4284f25963e6eedac8fbfa3", // API_KEY
        (Math.floor((Calendar.getInstance(TimeZone.getTimeZone("UTC")).timeInMillis).toDouble() / 1000)).toString() // CTIME
    )}


    companion object
    {
        public fun getInstance(context: Context): ZingAPI
        {
            Holder.instance.context = context

            return Holder.instance
        }


    }

    fun getCookie()
    {
        val webViewClient: WebViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String?) {
                super.onPageFinished(view, url)
                cookieGot = CookieManager.getInstance().getCookie(view.getUrl())
                Log.i("Cookie", cookieGot)
                //  newCookies(cookies);
                request()
            }
        }
        var webv = WebView(context)
        webv.webViewClient = webViewClient
        webv.loadUrl("https://zingmp3.vn")




    }

    private fun hashMac256(input: String): String {
        return MessageDigest
            .getInstance("SHA-256")
            .digest(input.toByteArray())
            .fold("") { str, it -> str + "%02x".format(it) }
    }

    private val HMAC_SHA512 = "HmacSHA512"

    private fun toHexString(bytes: ByteArray): String? {
        val formatter = Formatter()
        for (b in bytes) {
            formatter.format("%02x", b)
        }
        return formatter.toString()
    }

    @Throws(SignatureException::class, NoSuchAlgorithmException::class, InvalidKeyException::class)
    fun hashMac512(data: String, key: String): String? {
        val secretKeySpec = SecretKeySpec(key.toByteArray(), HMAC_SHA512)
        val mac: Mac = Mac.getInstance(HMAC_SHA512)
        mac.init(secretKeySpec)
        return toHexString(mac.doFinal(data.toByteArray()))
    }

    fun hashParams(path: String, id: String):String?
    {
        return hashMac512(
            path + hashMac256("ctime=${this.ctime}id=${id}version=${this.version}"),
            secretKey
        )
    }

    fun request()
    {
        var songid = "ZOACFBBU"
        var client = OkHttpClient()
        var body = hashParams("/api/v2/song/get/streaming", songid)?.let {
            FormBody.Builder()
                .add("id", songid)
                .add("sig", it)
                .add("ctime", ctime)
                .add("version", version)
                .add("apiKey", apiKey)
                .build()
        }

        var requestURL = (this.url + "/api/v2/song/get/streaming").toHttpUrlOrNull()?.newBuilder()
        requestURL?.addQueryParameter("id", songid)
        requestURL?.addQueryParameter("sig", hashParams("/api/v2/song/get/streaming", songid))
        requestURL?.addQueryParameter("ctime", ctime)
        requestURL?.addQueryParameter("version", version)
        requestURL?.addQueryParameter("apiKey", apiKey)
        var fullURL = requestURL?.build().toString()

        Log.i("Url Request: ", fullURL)

        var rq = body?.let {
            Request.Builder()
                .url(fullURL)
                .header("Cookie", cookieGot)
                .get()
                .build()
        }

        client.newCall(rq!!).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Toast.makeText(context, "Request api fail", Toast.LENGTH_LONG).show()
            }
            override fun onResponse(call: Call, response: Response) {
                val myResponse = response.body!!.string()

                Log.i("Response Zing:", myResponse.toString())
                val json = JSONObject(myResponse)
                var data = json

            }
        })

    }
}