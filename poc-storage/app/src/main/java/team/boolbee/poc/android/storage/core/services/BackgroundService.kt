package team.boolbee.poc.android.storage.core.services

import android.app.IntentService
import android.content.Intent
import android.content.Context

// TODO: Rename actions, choose action names that describe tasks that this
private const val ACTION_FOO = "team.boolbee.poc.android.storage.core.services.action.FOO"

// TODO: Rename parameters
private const val EXTRA_PARAM1 = "team.boolbee.poc.android.storage.core.services.extra.PARAM1"
private const val EXTRA_PARAM2 = "team.boolbee.poc.android.storage.core.services.extra.PARAM2"

/**
 * An [IntentService] subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
class BackgroundService : CustomIntentService {

    private val TAG = BackgroundService::class.java.simpleName;

    override fun getTag() = TAG

    constructor(name: String) : super(name, true, false, false) {
    }

    override fun onHandleIntent(intent: Intent?) {
        when (intent?.action) {
            ACTION_FOO -> {
                val param1 = intent.getStringExtra(EXTRA_PARAM1)
                val param2 = intent.getStringExtra(EXTRA_PARAM2)
                handleActionFoo(param1, param2)
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private fun handleActionFoo(param1: String, param2: String) {
    }

    companion object {
        /**
         * Starts this service to perform action Foo with the given parameters. If
         * the service is already performing a task this action will be queued.
         *
         * @see IntentService
         */
        // TODO: Customize helper method
        @JvmStatic
        fun startActionFoo(context: Context, param1: String, param2: String) {
            val intent = Intent(context, BackgroundService::class.java).apply {
                action = ACTION_FOO
                putExtra(EXTRA_PARAM1, param1)
                putExtra(EXTRA_PARAM2, param2)
            }
            context.startService(intent)
        }
    }
}
