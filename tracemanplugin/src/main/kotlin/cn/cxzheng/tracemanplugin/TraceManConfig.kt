package cn.cxzheng.tracemanplugin

import org.gradle.api.Project

open class TraceManConfig {
    public var output: String = ""
    public var open: Boolean = true
    public var traceConfigFile: String = ""
    public var logTraceInfo: Boolean = false
    public var project: Project?=null
}