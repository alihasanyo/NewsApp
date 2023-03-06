package mobile.alihasan.com.newsapp.Adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.method.TextKeyListener.clear
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filter.FilterResults
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import mobile.alihasan.com.newsapp.Model.NewsArticleModel
import mobile.alihasan.com.newsapp.R
import java.lang.Exception
import java.util.Collections.addAll

class NewsArticleAdapter(private var context: Context, data: ArrayList<NewsArticleModel>) : RecyclerView.Adapter<NewsArticleAdapter.ArticleNewsHolder>()
{
    var mData: ArrayList<NewsArticleModel>
    init {
        this.mData = data
    }

    fun getFilter(): Filter {
        return articleFilter
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleNewsHolder {
        val view  : View = LayoutInflater.from(context).inflate(R.layout.item_article_news, parent, false)
        return ArticleNewsHolder(view)
    }

    override fun onBindViewHolder(holder: ArticleNewsHolder, position: Int) {
        val m : NewsArticleModel = mData[position]

        holder.nameArticle.setText(m.name)
        holder.countryArticle.setText(m.country)
        holder.titleArticle.setText(m.title)

        holder.layoutArticle.setOnClickListener {
            try {
                val browserArticle = Intent(Intent.ACTION_VIEW, Uri.parse(m.url))
                context.startActivity(browserArticle)
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    class ArticleNewsHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var nameArticle: TextView
        var countryArticle: TextView
        var titleArticle: TextView
        var layoutArticle: CardView

        init {
            nameArticle = itemView.findViewById(R.id.item_name_article)
            countryArticle = itemView.findViewById(R.id.item_country_article)
            titleArticle = itemView.findViewById(R.id.item_title_article)
            layoutArticle = itemView.findViewById(R.id.cv_article)
        }
    }

    private val articleFilter: Filter = object : Filter() {

        override fun performFiltering(s: CharSequence): FilterResults {
            val results = FilterResults()
            val suggestions: ArrayList<NewsArticleModel> = ArrayList()

            if (s == null || s.isEmpty()) {
                suggestions.addAll(mData)
            } else {
                val filterPattern = s.toString().lowercase().trim()
                for (m: NewsArticleModel in mData) {
                    if (m.title?.lowercase()?.contains(filterPattern) == true ||
                        m.content?.lowercase()?.contains(filterPattern) == true ||
                        m.desc?.lowercase()?.contains(filterPattern) == true) {
                        suggestions.add(m)
                    }
                }
            }
            results.values = suggestions
            results.count = suggestions.size
            return results
        }

        override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
            //clear()
            addAll(filterResults.values as ArrayList<*>)
            notifyDataSetChanged()
        }

        override fun convertResultToString(resultValue: Any): CharSequence {
            return (resultValue as NewsArticleModel).title.toString()
        }
    }

}