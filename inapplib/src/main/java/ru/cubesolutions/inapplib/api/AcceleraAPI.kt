package ru.cubesolutions.inapplib.api

import android.util.Log
import kotlinx.coroutines.*
import org.json.JSONObject
import ru.cubesolutions.inapplib.AcceleraConfig
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class AcceleraAPI(
    private val acceleraConfig: AcceleraConfig,
) {

    companion object {
        const val PATH_ZVUK_TEMPLATE = "/zvuk/template"
        const val REQUEST_JSON_ID = "id"
        const val REQUEST_JSON_DATA = "data"
        const val REQUEST_METHOD_GET = "GET"
        const val REQUEST_METHOD_POST = "POST"
        const val HEADER_TYPE_KEY = "Content-Type"
        const val HEADER_TYPE_VALUE = "application/json"
        const val HEADER_ACCEPT_KEY = "Accept"
        const val HEADER_ACCEPT_VALUE = "application/json"
        const val HEADER_AUTH_KEY = "Authorization"

        const val REQUEST_QUERY_PREFIX = "?id="

        const val LOG_TAG_ACCELERA_API = "TAG_ACCELERA_API"
    }

    private val job = Job()
    private val name = CoroutineName("accelera api scope")
    private val scope: CoroutineScope = CoroutineScope(job + name + Dispatchers.IO)

    fun logEvent(
        data: Map<String, Any>,
        completion: (JSONObject) -> Unit,
        onError: (Exception) -> Unit,
    ) {


        scope.launch(Dispatchers.IO) {
            try {
                // Создать строку JSON с параметрами
                val jsonObjectString = getJsonParamsLoadBanner(data)

                val url = URL(acceleraConfig.url + PATH_ZVUK_TEMPLATE)
                val httpURLConnection = url.openConnection() as HttpURLConnection
                httpURLConnection.requestMethod = REQUEST_METHOD_POST
                httpURLConnection.setRequestProperty(HEADER_AUTH_KEY, acceleraConfig.token)
                httpURLConnection.setRequestProperty(HEADER_TYPE_KEY, HEADER_TYPE_VALUE)
                httpURLConnection.setRequestProperty(HEADER_ACCEPT_KEY, HEADER_ACCEPT_VALUE)
                httpURLConnection.doInput = true
                httpURLConnection.doOutput = true

                // Отправляем JSON, который мы создали
                val outputStreamWriter = OutputStreamWriter(httpURLConnection.outputStream)
                outputStreamWriter.write(jsonObjectString)
                outputStreamWriter.flush()

                // Проверяем успешно ли установлено соединение
                val responseCode = httpURLConnection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = httpURLConnection.inputStream.bufferedReader().use {
                        it.readText()
                    }  // по умолчанию UTF-8
                    val jsonObject = JSONObject(response)
                    // Вызываем колбэк с результатом
                    completion.invoke(jsonObject)
                    Log.d(LOG_TAG_ACCELERA_API, response)
                } else {
                    onError.invoke(Exception("Response code - $responseCode and not 200"))
                    Log.e(LOG_TAG_ACCELERA_API, responseCode.toString())
                }
            } catch (exception: Exception) {
                exception.printStackTrace()
                // Вызываем колбэк с ошибкой
                onError.invoke(exception)
            }
        }
    }

    fun loadBanner(
        completion: (JSONObject) -> Unit,
        onError: (Exception) -> Unit,
    ) {
        scope.launch(Dispatchers.IO) {
            try {
                val baseUrl: StringBuilder = StringBuilder()
                baseUrl.append(acceleraConfig.url)
                baseUrl.append(PATH_ZVUK_TEMPLATE)
                baseUrl.append(REQUEST_QUERY_PREFIX)
                baseUrl.append(acceleraConfig.userId)

                val url = URL(baseUrl.toString())
                val httpURLConnection = url.openConnection() as HttpURLConnection
                httpURLConnection.requestMethod = REQUEST_METHOD_GET
                httpURLConnection.setRequestProperty(HEADER_AUTH_KEY, acceleraConfig.token)
                httpURLConnection.setRequestProperty(HEADER_TYPE_KEY, HEADER_TYPE_VALUE)
                httpURLConnection.setRequestProperty(HEADER_ACCEPT_KEY, HEADER_ACCEPT_VALUE)
                httpURLConnection.doInput = true
                httpURLConnection.doOutput = false

                // Проверяем успешно ли установлено соединение
                val responseCode = httpURLConnection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = httpURLConnection.inputStream.bufferedReader().use {
                        it.readText()
                    }  // по умолчанию UTF-8
                    val jsonObject = JSONObject(response)
                    // Вызываем колбэк с результатом
                    completion.invoke(jsonObject)
                    Log.d(LOG_TAG_ACCELERA_API, response)
                } else {
                    onError.invoke(Exception("Response code - $responseCode and not 200"))
                    Log.e(LOG_TAG_ACCELERA_API, responseCode.toString())
                }
            } catch (exception: Exception) {
                exception.printStackTrace()
                // Вызываем колбэк с ошибкой
                onError.invoke(exception)
            }
        }
    }

    private fun getJsonParamsLoadBanner(data: Map<String, Any>): String {
        // Создаем JSON с помощью JSONObject
        val jsonObject = JSONObject()
        jsonObject.put(REQUEST_JSON_ID, acceleraConfig.userId)
        jsonObject.put(REQUEST_JSON_DATA, data)
        // Преобразовываем JSONObject в строку
        return jsonObject.toString()
    }
}