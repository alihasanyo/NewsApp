package mobile.alihasan.com.newsapp.Activity

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import mobile.alihasan.com.newsapp.Adapter.NewsCategoryAdapter
import mobile.alihasan.com.newsapp.App.NewsModule
import mobile.alihasan.com.newsapp.Model.NewsCategoryModel
import mobile.alihasan.com.newsapp.R
import org.json.JSONException
import java.util.ArrayList
import com.android.volley.RequestQueue
import org.json.JSONObject
import java.util.HashMap


class MainActivity : AppCompatActivity() {

    //activity
    private lateinit var activity: Activity
    var queue: RequestQueue? = null

    //content
    private lateinit var list_news: ListView
    private lateinit var load_news: ProgressBar
    private lateinit var mList: ArrayList<NewsCategoryModel>
    private lateinit var mAdapter: NewsCategoryAdapter
    private val mdl: NewsModule = NewsModule()

    private lateinit var actv_cat_news: AutoCompleteTextView
    private var listCategory: ArrayList<String>? = null
    private var adapterHandler: ArrayAdapter<*>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        activity = this
        queue = Volley.newRequestQueue(this);

        list_news = findViewById(R.id.list_category_news)
        load_news = findViewById(R.id.prog_list_category_news)
        actv_cat_news = findViewById(R.id.tv_cat);

        ChooseCategory()
    }

    private fun ChooseCategory(){
        listCategory = ArrayList()
        listCategory?.clear()
        listCategory?.add("All")
        listCategory?.add("business")
        listCategory?.add("entertainment")
        listCategory?.add("general")
        listCategory?.add("health")
        listCategory?.add("science")
        listCategory?.add("sports")
        listCategory?.add("technology")

        adapterHandler = ArrayAdapter<Any?>(activity, R.layout.support_simple_spinner_dropdown_item, listCategory!! as List<Any?>)
        actv_cat_news.setAdapter(adapterHandler)

        actv_cat_news.setOnItemClickListener { adapterView, view, position, id ->
            //Toast.makeText(activity, listCategory!![position], Toast.LENGTH_SHORT).show()
            if (listCategory!![position].equals("All"))
            {
                listCategory!![position] = ""
            }
            getCatNews(listCategory!![position])
        }
    }

    private fun getCatNews(category: String) {
        list_news.visibility = View.GONE
        load_news.visibility = View.VISIBLE

        mList = ArrayList()
        mAdapter = NewsCategoryAdapter(activity, mList)
        list_news.setAdapter(mAdapter)

        val url = mdl.getURL() + "/top-headlines/sources?apiKey=" + mdl.getKey() + "&category=" + category

        val oGetNews: StringRequest = object : StringRequest(
            Request.Method.GET, url,
                Response.Listener
                { response ->
                    try {
                        Log.e("RESPONSE", response.toString())
                        list_news.visibility = View.VISIBLE
                        load_news.visibility = View.GONE

                        val jsonObject = JSONObject(response)
                        val status = jsonObject.getString("status")

                        if (status.equals("ok"))
                        {
                            val arrData = jsonObject.getJSONArray("sources")

                            if (arrData.length() > 0)
                            {
                                for (i in 0 until arrData.length()) {
                                    val oDetil = arrData.getJSONObject(i)
                                    val name_news = oDetil.getString("name")
                                    val country_news = oDetil.getString("country")
                                    val desc_news = oDetil.getString("description")
                                    val cat_news = oDetil.getString("category")
                                    val url_news = oDetil.getString("url")

                                    mList.add(NewsCategoryModel(name_news, country_news, desc_news, cat_news, url_news))
                                }
                            }

                            list_news.setAdapter(mAdapter)
                            mAdapter.notifyDataSetChanged()
                        }
                        else {
                            val message = jsonObject.getString("message")
                            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
                        }

                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Toast.makeText(activity, "Error: $e", Toast.LENGTH_SHORT).show()
                    }
                },
                Response.ErrorListener
                { error ->
                    list_news.visibility = View.GONE
                    load_news.visibility = View.VISIBLE
                })
                {
                //    override fun getParams(): Map<String, String> {
                //        val params: MutableMap<String, String> = HashMap()
                //        //params["user"] = "YOUR USERNAME"
                //        //params["pass"] = "YOUR PASSWORD"
                //        return params
                //    }

                    @Throws(AuthFailureError::class)
                    override fun getHeaders(): Map<String, String> {
                        val headers: MutableMap<String, String> = HashMap()
                        //params["Content-Type"] = "application/x-www-form-urlencoded"
                        headers["User-Agent"] = "Mozilla/5.0"
                        return headers
                    }
                }

        queue?.add(oGetNews)

    }
}