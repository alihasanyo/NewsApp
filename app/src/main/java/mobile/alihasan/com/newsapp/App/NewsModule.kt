package mobile.alihasan.com.newsapp.App

import android.app.Activity
import android.content.SharedPreferences
import java.lang.Exception
import java.text.DecimalFormat

class NewsModule {

    fun getURL(): String {

        var url = ""
        try {
            url = "https://newsapi.org/v2"
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            return url
        }

    }

    fun getKey(): String {

        var urlkey = ""
        try {
            urlkey = "a53dd62d9838485882ba706754224b34"
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            return urlkey
        }

    }

}