package mobile.alihasan.com.newsapp.Activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import mobile.alihasan.com.newsapp.Adapter.NewsArticleAdapter
import mobile.alihasan.com.newsapp.App.NewsModule
import mobile.alihasan.com.newsapp.Model.NewsArticleModel
import mobile.alihasan.com.newsapp.Model.NewsCategoryModel
import mobile.alihasan.com.newsapp.R
import org.json.JSONException
import org.json.JSONObject
import java.lang.Exception
import java.util.ArrayList
import java.util.HashMap

class ArticleActivity : AppCompatActivity(), View.OnClickListener {

    //UI
    private lateinit var iv_back: ImageView
    private lateinit var rv_article: RecyclerView
    private lateinit var tv_cat_article: TextView
    private lateinit var tv_country_article: TextView
    private lateinit var prog_list: ProgressBar
    private lateinit var cb_cat: CheckBox
    private lateinit var cb_country: CheckBox
    private lateinit var cb_name: CheckBox
    private lateinit var et_searchArt: AutoCompleteTextView
    private lateinit var iv_delete: ImageView

    //content
    private lateinit var activity: Activity
    private val mdl: NewsModule = NewsModule()
    var queue: RequestQueue? = null

    //value
    private var nameArt: String? = null
    private var countryArt: String? = null
    private var categoryArt: String? = null
    private lateinit var mAdapter: NewsArticleAdapter
    private lateinit var mList: ArrayList<NewsArticleModel>
    private lateinit var mListFilter: ArrayList<NewsArticleModel>
    private var url: String? = null
    private var filterSub: ArrayList<NewsArticleModel> = ArrayList<NewsArticleModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article)
        activity = this
        queue = Volley.newRequestQueue(this)

        iv_back = findViewById(R.id.iv_back_article)
        rv_article = findViewById(R.id.rv_article)
        tv_cat_article = findViewById(R.id.cat_article)
        tv_country_article = findViewById(R.id.country_article)
        prog_list = findViewById(R.id.prog_list_article_news)
        cb_cat = findViewById(R.id.cb_category)
        cb_country = findViewById(R.id.cb_country)
        cb_name = findViewById(R.id.cb_sources)
        et_searchArt = findViewById(R.id.et_search)
        iv_delete = findViewById(R.id.btn_clear)

        val extras = intent.extras
        if (extras != null){
            nameArt = extras.getString("name", "")
            countryArt = extras.getString("country", "")
            categoryArt = extras.getString("category", "")
        }
        tv_cat_article.setText(categoryArt)
        tv_country_article.setText(countryArt)

        iv_back.setOnClickListener(this)
        iv_delete.setOnClickListener(this)

        try {
            loadArticle()
        } catch (e: Exception){
            e.printStackTrace()
        }

        et_searchArt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // TODO Auto-generated method stub
            }

            override fun onTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                getSearchArticle(et_searchArt.text.toString())
            }

            override fun afterTextChanged(s: Editable) {
                if (s.isNotEmpty()) {
                    iv_delete.visibility = View.VISIBLE
                    cb_cat.isClickable = false
                    cb_country.isClickable = false
                    cb_name.isClickable = false
                } else {
                    iv_delete.visibility = View.GONE
                    cb_cat.isClickable = true
                    cb_country.isClickable = true
                    cb_name.isClickable = true
                }
            }
        })

    }

    override fun onResume() {
        super.onResume()
        setDefaultChecked()
    }

    /*override fun onStart() {
        super.onStart()
        setDefaultChecked()
    }

    override fun onPause() {
        super.onPause()
        setDefaultChecked()
    }

    override fun onStop() {
        super.onStop()
        setDefaultChecked()
    }*/

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_back_article ->
            {
                val back = Intent(activity, MainActivity::class.java)
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(back)
            }

            R.id.btn_clear ->
            {
                et_searchArt.setText("")
                et_searchArt.requestFocus()
            }
            else -> {}
        }
    }

    private fun loadArticle() {
        cb_cat.isChecked = true
        setDefaultChecked()
        //CheckedChangeListener()

        rv_article.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        rv_article.setNestedScrollingEnabled(true)
        rv_article.setFocusable(true)
        linearLayoutManager.isAutoMeasureEnabled = true
        linearLayoutManager.isSmoothScrollbarEnabled = true
        rv_article.layoutManager = linearLayoutManager

        val layoutManager = GridLayoutManager(activity, 3)
        rv_article.layoutManager = layoutManager
        mAdapter = NewsArticleAdapter(activity, mList)
        rv_article.adapter = mAdapter
        mAdapter.notifyDataSetChanged()
    }

    private fun setDefaultChecked() {
        cb_cat.setOnCheckedChangeListener { compoundButton, b ->
            if (b){
                if (cb_country.isChecked()){
                    url = mdl.getURL() + "/top-headlines?apiKey=" + mdl.getKey() + "&category=" + categoryArt + "&country=" + countryArt
                    getArticleFromSources(url)
                }else {
                    onCheckboxCheck()
                }
                cb_name.isClickable = false
            } else {
                onCheckboxCheck()
                cb_name.isClickable = true
            }
        }

        cb_country.setOnCheckedChangeListener { compoundButton, b ->
            if (b){
                if (cb_cat.isChecked()){
                    url = mdl.getURL() + "/top-headlines?apiKey=" + mdl.getKey() + "&category=" + categoryArt + "&country=" + countryArt
                    getArticleFromSources(url)
                }else{
                    onCheckboxCheck()
                }
                cb_name.isClickable = false
            } else {
                onCheckboxCheck()
                cb_name.isClickable = false
            }
        }

        cb_name.setOnCheckedChangeListener { compoundButton, b ->
            if (b){
                if (cb_country.isChecked() or cb_cat.isChecked()){
                    cb_cat.isChecked = false
                    cb_country.isChecked = false
                    cb_cat.isClickable = false
                    cb_country.isClickable = false
                }
                onCheckboxCheck()
            } else {
                cb_cat.isClickable = true
                cb_country.isClickable = true
                prog_list.visibility = View.VISIBLE
                rv_article.visibility = View.GONE
            }
        }
        onCheckboxCheck()
    }

    private fun onCheckboxCheck(){
        if (cb_cat.isChecked){
            url = mdl.getURL() + "/top-headlines?apiKey=" + mdl.getKey() + "&category=" + categoryArt
            getArticleFromSources(url)
        }
        else if (cb_country.isChecked){
            url = mdl.getURL() + "/top-headlines?apiKey=" + mdl.getKey() + "&country=" + countryArt
            getArticleFromSources(url)
        }
        else if(cb_name.isChecked){
            url = mdl.getURL() + "/top-headlines?apiKey=" + mdl.getKey() + "&sources=" + nameArt
            getArticleFromSources(url)
            cb_cat.isChecked = false
            cb_country.isChecked = false
            cb_cat.isClickable = false
            cb_country.isClickable = false
        } else {
            prog_list.visibility = View.VISIBLE
            rv_article.visibility = View.GONE
        }
    }

    private fun getArticleFromSources(url: String?) {
        prog_list.visibility = View.VISIBLE
        rv_article.visibility = View.GONE

        mList = ArrayList()
        mAdapter = NewsArticleAdapter(activity, mList)
        rv_article.setAdapter(mAdapter)

        val oGetArticle : StringRequest = object : StringRequest(
            Request.Method.GET, url,
                Response.Listener
                { response ->
                    try {
                        Log.e("RESPONSE", response.toString())
                        prog_list.visibility = View.GONE
                        rv_article.visibility = View.VISIBLE

                        val jsonObject = JSONObject(response)
                        val status = jsonObject.getString("status")

                        if (status.equals("ok"))
                        {
                            val arrData = jsonObject.getJSONArray("articles")

                            if (arrData.length() > 0)
                            {
                                for (i in 0 until arrData.length())
                                {
                                    val oDetil = arrData.getJSONObject(i)

                                    val arrDataSources = oDetil.getJSONObject("source")
                                    val nameArticle = arrDataSources.getString("name")

                                    val titleArticle = oDetil.getString("title")
                                    val urlArticle = oDetil.getString("url")

                                    mList.add(NewsArticleModel(nameArticle, titleArticle, urlArticle, countryArt.toString(), "", ""))
                                }
                            }

                            rv_article.setAdapter(mAdapter)
                        }
                        else
                        {
                            val message = jsonObject.getString("message")
                            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
                        }

                    }catch (e: JSONException){
                        e.printStackTrace()
                        Toast.makeText(activity, "Error: $e", Toast.LENGTH_SHORT).show()
                    }
                },
                Response.ErrorListener
                {  error ->
                    prog_list.visibility = View.VISIBLE
                    rv_article.visibility = View.GONE
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
        queue?.add(oGetArticle)
    }

    private fun getSearchArticle(search: String) {
        prog_list.visibility = View.VISIBLE
        rv_article.visibility = View.GONE

        mListFilter = ArrayList()
        var urlFilter: String = mdl.getURL() + "/top-headlines?apiKey=" + mdl.getKey() + "&q=" + search

        val oGetFilter : StringRequest = object : StringRequest(
            Request.Method.GET, urlFilter,
            Response.Listener
            { response ->
                try {
                    Log.e("RESPONSE", response.toString())
                    prog_list.visibility = View.GONE
                    rv_article.visibility = View.VISIBLE

                    val jsonObject = JSONObject(response)
                    val status = jsonObject.getString("status")

                    if (status.equals("ok"))
                    {
                        val arrData = jsonObject.getJSONArray("articles")

                        if (arrData.length() > 0)
                        {
                            for (i in 0 until arrData.length())
                            {
                                val oDetil = arrData.getJSONObject(i)

                                val arrDataSources = oDetil.getJSONObject("source")
                                val nameArticle = arrDataSources.getString("name")

                                val titleArticle = oDetil.getString("title")
                                val urlArticle = oDetil.getString("url")
                                val descArticle = oDetil.getString("description")
                                val contArticle = oDetil.getString("description")

                                mListFilter.add(NewsArticleModel(nameArticle, titleArticle, urlArticle, countryArt.toString(), descArticle, contArticle))
                            }
                        }

                        mAdapter = NewsArticleAdapter(activity, mListFilter)

                        rv_article.setHasFixedSize(true)
                        val linearLayoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
                        rv_article.setNestedScrollingEnabled(true)
                        rv_article.setFocusable(true)
                        linearLayoutManager.isAutoMeasureEnabled = true
                        linearLayoutManager.isSmoothScrollbarEnabled = true
                        rv_article.layoutManager = linearLayoutManager

                        val layoutManager = GridLayoutManager(activity, 3)
                        rv_article.layoutManager = layoutManager
                        mAdapter = NewsArticleAdapter(activity, mListFilter)
                        rv_article.adapter = mAdapter
                        mAdapter.notifyDataSetChanged()

                    }
                    else
                    {
                        val message = jsonObject.getString("message")
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
                    }

                }catch (e: JSONException){
                    e.printStackTrace()
                    Toast.makeText(activity, "Error: $e", Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener
            {  error ->
                prog_list.visibility = View.VISIBLE
                rv_article.visibility = View.GONE
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
        queue?.add(oGetFilter)
    }

}