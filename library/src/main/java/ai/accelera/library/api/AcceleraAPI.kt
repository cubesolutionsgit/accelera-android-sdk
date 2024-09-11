package ai.accelera.library.api

import android.webkit.URLUtil
import org.json.JSONObject
import ai.accelera.library.AcceleraConfiguration
import ai.accelera.library.utils.LogUtils
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class AcceleraAPI(
    private val acceleraConfig: AcceleraConfiguration,
) {

    companion object {
        const val PATH_ZVUK_TEMPLATE = "/zvuk/template"
        const val PATH_EVENTS_EVENT = "/events/event"
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

        const val HTTPS_TEMPLATE = "https://"

        const val LOG_TAG_ACCELERA_API = "IN_APP_ACCELERA_API"
    }

    private val job = Job()
    private val name = CoroutineName("accelera api scope")
    private val scope: CoroutineScope = CoroutineScope(job + name + Dispatchers.IO)

    fun logEvent(
        data: Map<String, Any>,
        completion: (String) -> Unit,
        onError: (Exception) -> Unit,
        overrideBaseUrl: String? = null,
    ) {
        LogUtils.info(LOG_TAG_ACCELERA_API, "logEvent data - $data")

        scope.launch(Dispatchers.IO) {
            try {
                // Создать строку JSON с параметрами
                val jsonObjectString = getJsonParamsLoadBanner(data)

                val baseUrl: StringBuilder = StringBuilder()
                if (URLUtil.isValidUrl(overrideBaseUrl)) {
                    baseUrl.append(overrideBaseUrl)
                } else {
                    baseUrl.append(acceleraConfig.url)
                }
                baseUrl.append(PATH_EVENTS_EVENT)

                val url = URL(baseUrl.toString())

                val urlConnection = if (acceleraConfig.url.contains(HTTPS_TEMPLATE)) {
                    url.openConnection() as HttpsURLConnection
                } else {
                    url.openConnection() as HttpURLConnection
                }

                urlConnection.requestMethod = REQUEST_METHOD_POST
                urlConnection.setRequestProperty(HEADER_AUTH_KEY, acceleraConfig.token)
                urlConnection.setRequestProperty(HEADER_TYPE_KEY, HEADER_TYPE_VALUE)
                urlConnection.setRequestProperty(HEADER_ACCEPT_KEY, HEADER_ACCEPT_VALUE)
                urlConnection.doInput = true
                urlConnection.doOutput = true

                // Отправляем JSON, который мы создали
                val outputStreamWriter = OutputStreamWriter(urlConnection.outputStream)
                outputStreamWriter.write(jsonObjectString)
                outputStreamWriter.flush()

                // Проверяем успешно ли установлено соединение
                val responseCode = urlConnection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = urlConnection.inputStream.bufferedReader().use {
                        it.readText()
                    }  // по умолчанию UTF-8
                    LogUtils.info(
                        tag = LOG_TAG_ACCELERA_API,
                        msg = "logEvent response - $response"
                    )

                    // Вызываем колбэк с результатом
                    completion.invoke(response)
                } else {
                    LogUtils.error(
                        tag = LOG_TAG_ACCELERA_API,
                        msg = "Response code - $responseCode and not 200"
                    )

                    // Вызываем колбэк с ошибкой
                    onError.invoke(Exception("Response code - $responseCode and not 200"))
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
        overrideBaseUrl: String? = null,
    ) {
        LogUtils.info(
            tag = LOG_TAG_ACCELERA_API,
            msg = "loadBanner"
        )

        scope.launch(Dispatchers.IO) {
            try {
                val baseUrl: StringBuilder = StringBuilder()
                if (URLUtil.isValidUrl(overrideBaseUrl)) {
                    baseUrl.append(overrideBaseUrl)
                } else {
                    baseUrl.append(acceleraConfig.url)
                }
                baseUrl.append(PATH_ZVUK_TEMPLATE)
                baseUrl.append(REQUEST_QUERY_PREFIX)
                baseUrl.append(acceleraConfig.userId)

                val url = URL(baseUrl.toString())

                // Если URL начинается с https, то используем HttpsURLConnection
                val urlConnection = if (acceleraConfig.url.contains(HTTPS_TEMPLATE)) {
                    url.openConnection() as HttpsURLConnection
                } else {
                    url.openConnection() as HttpURLConnection
                }

                urlConnection.requestMethod = REQUEST_METHOD_GET
                urlConnection.setRequestProperty(HEADER_AUTH_KEY, acceleraConfig.token)
                urlConnection.setRequestProperty(HEADER_TYPE_KEY, HEADER_TYPE_VALUE)
                urlConnection.setRequestProperty(HEADER_ACCEPT_KEY, HEADER_ACCEPT_VALUE)
                urlConnection.doInput = true
                urlConnection.doOutput = false

                // Проверяем успешно ли установлено соединение
                val responseCode = urlConnection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = urlConnection.inputStream.bufferedReader().use {
                        it.readText()
                    }  // по умолчанию UTF-8

                    // Формируем JSON файл из ответа сервера
                    val jsonObject = JSONObject(response)
                    LogUtils.info(
                        tag = LOG_TAG_ACCELERA_API,
                        msg = "loadBanner jsonObject - $jsonObject"
                    )

                    // Вызываем колбэк с результатом
                    completion.invoke(jsonObject)
                } else {
                    LogUtils.error(
                        tag = LOG_TAG_ACCELERA_API,
                        msg = "Response code - $responseCode and not 200"
                    )
                    onError.invoke(Exception("Response code - $responseCode and not 200"))
                }
            } catch (exception: Exception) {
                LogUtils.error(
                    tag = LOG_TAG_ACCELERA_API,
                    msg = "loadBanner - " + exception.localizedMessage
                )
                // Вызываем колбэк с ошибкой
                onError.invoke(exception)
            }
        }
    }

    private fun getJsonParamsLoadBanner(data: Map<String, Any>): String {
        LogUtils.info(
            tag = LOG_TAG_ACCELERA_API,
            msg = "getJsonParamsLoadBanner data - $data"
        )

        // Создаем JSON с помощью JSONObject
        val jsonObject = JSONObject()
        jsonObject.put(REQUEST_JSON_ID, acceleraConfig.userId)
        val jsonMap = JSONObject(data)
        jsonObject.put(REQUEST_JSON_DATA, jsonMap)

        LogUtils.info(
            tag = LOG_TAG_ACCELERA_API,
            msg = "getJsonParamsLoadBanner jsonObject - $jsonObject"
        )
        // Преобразовываем JSONObject в строку
        return jsonObject.toString()
    }
}