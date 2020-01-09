package pl.krzysztofwojciechowski.langcourses

import android.content.Context
import android.net.ConnectivityManager

fun isOnline(context: Context): Boolean {
    val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
    val networkInfo = manager!!.activeNetworkInfo
    return networkInfo != null && networkInfo.isConnected
}
