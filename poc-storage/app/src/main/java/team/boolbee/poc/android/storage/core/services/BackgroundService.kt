package team.boolbee.poc.android.storage.core.services

import android.app.IntentService
import android.content.Intent
import android.content.Context
import android.util.Log

public const val ACTION_SERVICE_STARTED = "services.action.SERVICE_STARTED"
public const val ACTION_SERVICE_STOPPED = "services.action.SERVICE_STOPPED"

/**
 * An [IntentService] subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
class BackgroundService : CustomIntentService {

    private val TAG = BackgroundService::class.java.simpleName;

    override fun getTag() = TAG

    constructor() : super("fdfs", true, false, false) {

    }

    constructor(name: String) : super(name, true, false, false) {
    }

    override fun onHandleIntent(intent: Intent?) {
        val intent = Intent(ACTION_SERVICE_STARTED)
        sendBroadcast(intent)
        Log.d(getTag(), "onHandleIntent")
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private fun handleActionFoo(param1: String, param2: String) {
    }

    override fun onDestroy() {
        super.onDestroy()
        val intent = Intent(ACTION_SERVICE_STOPPED)
        sendBroadcast(intent)
    }

    companion object {
        @JvmStatic
        fun startActionFoo(context: Context) {
            val intent = Intent(context, BackgroundService::class.java)
            context.startService(intent)
        }

        fun stop(context: Context) {
            val intent = Intent(context, BackgroundService::class.java)
            context.stopService(intent)
        }
    }
}
