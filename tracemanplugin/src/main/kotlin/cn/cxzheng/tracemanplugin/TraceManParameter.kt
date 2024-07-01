package cn.cxzheng.tracemanplugin

import com.android.build.api.instrumentation.InstrumentationParameters
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input

interface TraceManParameter:InstrumentationParameters {
    @get:Input
    val output: Property<String>
    @get:Input
    val open:Property<Boolean>
    @get:Input
    val traceConfigFile:Property<String>
    @get:Input
    val logTraceInfo:Property<Boolean>
    @get:Input
   val project: Property<Project>
}