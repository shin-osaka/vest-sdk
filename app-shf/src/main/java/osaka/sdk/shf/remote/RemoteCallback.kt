package osaka.sdk.shf.remote

interface RemoteCallback {
    fun onResult(success: Boolean, remoteConfig: RemoteConfig?)
}