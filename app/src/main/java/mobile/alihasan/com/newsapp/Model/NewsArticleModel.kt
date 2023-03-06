package mobile.alihasan.com.newsapp.Model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NewsArticleModel (
    var name : String?,
    var title : String?,
    var url : String?,
    var country : String?,
    var desc : String?,
    var content : String
) : Parcelable