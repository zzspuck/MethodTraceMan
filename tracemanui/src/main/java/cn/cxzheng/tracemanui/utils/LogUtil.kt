package cn.cxzheng.tracemanui.utils

import android.util.Log
import cn.cxzheng.tracemanui.MethodTraceServerManager
import cn.cxzheng.tracemanui.MethodTraceServerManager.DEBUG_SERVER_TAG
import cn.cxzheng.tracemanui.MethodTraceServerManager.MTM_LOG_DETAIL

/**
 * Create by cxzheng on 2019-11-09
 */
class LogUtil {

    companion object {


        @JvmStatic
        fun detail(message: String?) {
            if (MethodTraceServerManager.logLevel == MTM_LOG_DETAIL) {
                message?.let { Log.i(DEBUG_SERVER_TAG, it) }
            }
        }

        @JvmStatic
        fun i(message: String?) {
            message?.let { Log.i(DEBUG_SERVER_TAG, it) }
        }


        @JvmStatic
        fun e(message: String?) {
            message?.let { Log.e(DEBUG_SERVER_TAG, it) }
        }
    }
}