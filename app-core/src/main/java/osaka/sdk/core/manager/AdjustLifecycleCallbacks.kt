package osaka.sdk.core.manager

import android.app.Activity
import osaka.sdk.core.SimpleLifecycleCallbacks
import com.osaka.sdk.Adjust

class AdjustLifecycleCallbacks : SimpleLifecycleCallbacks() {

    override fun onActivityResumed(activity: Activity) {
        Adjust.onResume()
    }

    override fun onActivityPaused(activity: Activity) {
        Adjust.onPause()
    }
}
