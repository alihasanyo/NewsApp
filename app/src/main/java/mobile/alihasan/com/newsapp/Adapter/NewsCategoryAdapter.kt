package mobile.alihasan.com.newsapp.Adapter

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import mobile.alihasan.com.newsapp.Activity.ArticleActivity
import mobile.alihasan.com.newsapp.Model.NewsCategoryModel
import mobile.alihasan.com.newsapp.R
import java.lang.Exception
import java.util.ArrayList

class NewsCategoryAdapter(private val context: Activity, data: ArrayList<NewsCategoryModel>) :
        ArrayAdapter<NewsCategoryModel?>(context, R.layout.item_cat_news, data as List<NewsCategoryModel?>)
{

    private val mData: ArrayList<NewsCategoryModel>
    init {
        mData = data
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val rowView: View = inflater.inflate(R.layout.item_cat_news, null, true)
        rowView.id = position

        val category_news = rowView.findViewById<TextView>(R.id.cat_news)
        val country_news = rowView.findViewById<TextView>(R.id.country_news)
        //val content_news = rowView.findViewById<TextView>(R.id.content_news)
        val layouy_cat_news = rowView.findViewById<LinearLayout>(R.id.lay_cat_news)

        val m: NewsCategoryModel = mData[position]

        category_news.setText(m.category)
        country_news.setText(m.name + " - " + m.country)
        //content_news.setText(m.desc)

        layouy_cat_news.setOnClickListener {

            try {
                val browserIntent = Intent(context, ArticleActivity::class.java)
                browserIntent.putExtra("category", m.category)
                browserIntent.putExtra("country", m.country)
                browserIntent.putExtra("name", m.name)
                context.startActivity(browserIntent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return rowView
    }
}