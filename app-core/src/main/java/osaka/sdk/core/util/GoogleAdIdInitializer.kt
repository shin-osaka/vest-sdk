package osaka.sdk.core.util

import android.text.TextUtils
import osaka.util.AppGlobal
import osaka.util.LogUtil
import com.osaka.sdk.Adjust

object GoogleAdIdInitializer {
    private val TAG = GoogleAdIdInitializer::class.java.simpleName
    private var isWaitingGoogleAdId = false
    fun needUpdateGoogleAdId(): Boolean {
        val googleAdId = PreferenceUtil.readGoogleADID()
        return TextUtils.isEmpty(googleAdId)
    }

    fun init() {
        if (needUpdateGoogleAdId() && !isWaitingGoogleAdId) {
            LogUtil.d(TAG, "need update GoogleAdId")
            isWaitingGoogleAdId = true
            startGetAdjustGoogleAdId()
        } else {
            LogUtil.d(TAG, "no need update GoogleAdId")
        }
    }

    private fun startGetAdjustGoogleAdId() {
        Adjust.getGoogleAdId(AppGlobal.application) { s ->
            LogUtil.d(TAG, "onGoogleAdIdRead: %s", s)
            PreferenceUtil.saveGoogleADID(s)
            isWaitingGoogleAdId = false
        }
    }
}
