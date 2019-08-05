package team.boolbee.poc.android.storage.core.services

import android.app.IntentService
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Intent
import android.content.Context
import android.content.IntentFilter
import android.content.res.Configuration
import android.os.*
import android.util.Log

private const val ACTION_FINISH = "services.action.ACTION_FINISH"

abstract class CustomIntentService : Service {

    private val name: String
    private var serviceLooper: Looper? = null
    private var serviceHandler: ServiceHandler? = null
    private val redelivery: Boolean
    private val stoppingAfterIntentIsHandled: Boolean
    private val listeningForStopIntent: Boolean
    private var finishSignalReceiver: FinishSignalReceiver? = null

    /**************************************************************************/
    /****************************** CONSTRUCTORES *****************************/
    /**************************************************************************/

    constructor(
        name: String,
        redelivery: Boolean = false,
        stoppingAfterIntentIsHandled: Boolean = false,
        listeningForStopIntent: Boolean = false) {
        this.name = name
        this.redelivery = redelivery
        this.stoppingAfterIntentIsHandled = stoppingAfterIntentIsHandled
        this.listeningForStopIntent = listeningForStopIntent
    }

    /**************************************************************************/
    /*************************** METODOS ABSTRACTOS ***************************/
    /**************************************************************************/

    abstract fun getTag(): String
    abstract fun onHandleIntent(intent: Intent?)
    protected fun onFinishSignal() {
        Log.d(getTag(), "onFinishSignal()")
    }

    /**************************************************************************/
    /*********************** METODOS DEL CICLO DE VIDA ************************/
    /**************************************************************************/

    override fun onCreate() {
        Log.d(getTag(), ">>> ON CREATE >>>")
        super.onCreate()

        var thread = HandlerThread("IntentService[" + name + "]")
        thread.start()

        serviceLooper = thread.looper
        serviceHandler = ServiceHandler(thread.looper)
    }

    override fun onStart(intent: Intent?, startId: Int) {
        Log.d(getTag(), ">>> ON START >>>")
        Log.i(getTag(), String.format("onStart(%s)", startId))
        val msg = serviceHandler!!.obtainMessage()
        msg.arg1 = startId
        msg.obj = intent
        serviceHandler!!.sendMessage(msg)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(getTag(), ">>> ON START COMMAND >>>")
        Log.d(getTag(), String.format("onStartCommand(%s, %s)", flags, startId))
        val result = super.onStartCommand(intent, flags, startId)
        Log.d(getTag(), "Result from startCommand " + getResultStringFromStartCommand(result))
        return result
    }

    override fun onLowMemory() {
        Log.d(getTag(), "->> ON LOW MEMORY >>>")
        super.onLowMemory()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        Log.d(getTag(), "->> ON TASK REMOVED >>>")
        super.onTaskRemoved(rootIntent)
    }

    override fun onTrimMemory(level: Int) {
        Log.d(getTag(), "->> ON TRIM MEMORY >>>")
        super.onTrimMemory(level)
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        Log.d(getTag(), "->> ON CONFIGURATION CHANGED >>>")
        super.onConfigurationChanged(newConfig)
    }

    override fun onBind(intent: Intent): IBinder? {
        Log.d(getTag(), "->> ON BIND >>>")
        return null
    }

    override fun onRebind(intent: Intent) {
        Log.d(getTag(), "->> ON REBIND >>>")
        super.onRebind(intent)
    }

    override fun onUnbind(intent: Intent): Boolean {
        Log.d(getTag(), "<<< ON UNBIND <<<")
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        Log.d(getTag(), "<<< ON DESTROY <<<")
        super.onDestroy()
        if (listeningForStopIntent) {
            unregisterReceiver(finishSignalReceiver)
        }

        serviceLooper!!.quit()
    }

    /**************************************************************************/
    /**************************** METODOS PRIVADOS ****************************/
    /**************************************************************************/

    /**
     * Método encargado de proporcionar el texto correspondiente al tipo de
     */
    fun getResultStringFromStartCommand(result: Int): String {
        var sResult : String? = null;
        when (result) {
            START_REDELIVER_INTENT -> sResult = "START_REDELIVER_INTENT"
            START_STICKY_COMPATIBILITY -> sResult = "START_STICKY_COMPATIBILITY"
            START_STICKY -> sResult = "START_STICKY"
            START_NOT_STICKY -> sResult = "START_NOT_STICKY"
            else -> sResult = String.format("CODE %s UNKNOWN", result)
        } // when

        return sResult;
    }

    /**************************************************************************/
    /**************************** CLASES INTERNAS *****************************/
    /**************************************************************************/

    private inner class ServiceHandler(looper: Looper) : Handler(looper) {

        override fun handleMessage(msg: Message) {
            Log.d(getTag(), String.format("handleMessage(%s)", msg.arg1))
            onHandleIntent(msg.obj as Intent)
            if (stoppingAfterIntentIsHandled) {
                Log.d(getTag(), String.format("stopSelf(%s)", msg.arg1))
                stopSelf(msg.arg1)
            }
        }
    }

    /**
     * Receiver que trata el intent de FINISH, deteniendo el servicio.
     */
    private inner class FinishSignalReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == ACTION_FINISH) {
                onFinishSignal()
                stopSelf()
            }
        }
    }
}