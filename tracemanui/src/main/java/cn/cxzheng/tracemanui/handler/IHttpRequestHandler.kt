package cn.cxzheng.tracemanui.handler

/**
 * Create by cxzheng on 2019/7/7
 */
interface IHttpRequestHandler {

    fun handle(path: String): Map<String, String?>
}
