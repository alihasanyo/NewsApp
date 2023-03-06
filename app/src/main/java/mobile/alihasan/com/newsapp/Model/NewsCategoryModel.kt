package mobile.alihasan.com.newsapp.Model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NewsCategoryModel (
    var name: String?,
    var country : String?,
    var desc : String?,
    var category : String?,
    var url : String
) : Parcelable