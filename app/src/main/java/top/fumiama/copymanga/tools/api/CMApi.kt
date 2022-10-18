package top.fumiama.copymanga.tools.api

import android.util.Base64
import com.bumptech.glide.load.model.LazyHeaders
import top.fumiama.dmzj.copymanga.R
import top.fumiama.copymanga.MainActivity
import top.fumiama.copymanga.tools.http.DownloadTools
import top.fumiama.copymanga.ui.settings.SettingsFragment.Companion.settingsPref
import java.io.File
import java.net.URLEncoder

object CMApi {
    var myGlideHeaders: LazyHeaders? = null
        get() {
            if(field === null)
                field = LazyHeaders.Builder()
                    .addHeader("referer", MainActivity.mainWeakReference?.get()?.getString(R.string.referUrl)!!)
                    .addHeader("User-Agent", MainActivity.mainWeakReference?.get()?.getString(R.string.pc_ua)!!)
                    .addHeader("source", "copyApp")
                    .addHeader("webp", "1")
                    .addHeader("region", if(settingsPref?.getBoolean("", false) == false) "1" else "0")
                    .addHeader("platform", "3")
                    .build()
            return field
        }
    fun getZipFile(exDir: File?, manga: String, caption: CharSequence, name: CharSequence) = File(exDir, "$manga/$caption/$name.zip")
    fun getApiUrl(id: Int, arg1: String?, arg2: String?) = MainActivity.mainWeakReference?.get()?.getString(id)?.let { String.format(it, arg1, arg2) }
    fun getApiUrl(id: Int, arg1: String?, arg2: String?, arg3: Int? = 0) = MainActivity.mainWeakReference?.get()?.getString(id)?.let { String.format(it, arg1, arg2, arg3) }
    fun getLoginConnection(username: String, pwd: String, salt: Int) = MainActivity.mainWeakReference?.get()?.getString(R.string.loginApiUrl)?.let {
            DownloadTools.getConnection(it, "POST")?.apply {
                doOutput = true
                setRequestProperty("content-type", "application/x-www-form-urlencoded;charset=utf-8")
                setRequestProperty("platform", "3")
                setRequestProperty("accept", "application/json")
                val r = if(settingsPref?.getBoolean("", false) == false) "1" else "0"
                val pwdb64 = Base64.encode("$pwd-$salt".toByteArray(), Base64.DEFAULT).decodeToString()
                outputStream.write("username=${URLEncoder.encode(username)}&password=$pwdb64&salt=$salt&platform=3&authorization=Token+&version=1.4.4&source=copyApp&region=$r&webp=1".toByteArray())
            }
        }
    }
