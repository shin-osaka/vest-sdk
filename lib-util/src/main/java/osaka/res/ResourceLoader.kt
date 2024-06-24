package osaka.res

import android.content.res.Resources
import osaka.res.strings.Strings
import osaka.util.AppGlobal
import osaka.util.LogUtil
import osaka.res.strings.`Strings-EN`
import osaka.res.strings.`Strings-HI`
import osaka.res.strings.`Strings-IN`
import osaka.res.strings.`Strings-PT`
import java.util.Locale

object ResourceLoader {

    val TAG: String = ResourceLoader::class.java.simpleName

    @JvmStatic
    val strings: Strings by lazy(LazyThreadSafetyMode.NONE) {
        //注意：这里按照app内置的语言选择资源，不是按照系统的语言选择资源
        val resources: Resources = AppGlobal.application!!.resources
        val config = resources.configuration
        val appLocale: Locale = if (!config.locales.isEmpty) {
            config.locales[0]
        } else {
            config.locale
        }
        val appLanguage = appLocale.language //获取app内置语言
        val sysLanguage = Locale.getDefault().language //获取系统语言
        var stringsRes = when (appLanguage) {
            "en" -> `Strings-EN`
            "hi" -> `Strings-HI`
            "in" -> `Strings-IN`
            "pt" -> `Strings-PT`
            else -> `Strings-EN`
        }
        LogUtil.d(
            TAG, "Load string resource for lan[%s]: %s",
            appLanguage, stringsRes::class.java.simpleName
        )
        stringsRes
    }

}