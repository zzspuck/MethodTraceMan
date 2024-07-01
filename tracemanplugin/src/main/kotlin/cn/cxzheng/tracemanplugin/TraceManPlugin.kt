package cn.cxzheng.tracemanplugin

import com.android.build.api.instrumentation.FramesComputationMode
import com.android.build.api.instrumentation.InstrumentationScope
import com.android.build.api.variant.AndroidComponentsExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class TraceManPlugin : Plugin<Project> {
    override fun apply(project: Project) {

        println()
        println("===================================TraceManPlugin===============begin==================")
        println()

        //这里appExtension获取方式与原transform api不同，可自行对比
        val appExtension = project.extensions.getByType(
            AndroidComponentsExtension::class.java
        )
        //读取配置文件
        project.extensions.create("TraceMan", TraceManConfig::class.java)
        val extensionNew = project.extensions.getByType(
            TraceManConfig::class.java
        )
        if (!extensionNew.open) {
            // 没打开直接return
            return
        }
        //这里通过transformClassesWith替换了原registerTransform来注册字节码转换操作
        appExtension.onVariants { variant ->


            //可以通过variant来获取当前编译环境的一些信息，最重要的是可以 variant.name 来区分是debug模式还是release模式编译
            variant.instrumentation.transformClassesWith(TraceManTransform::class.java, InstrumentationScope.ALL) {
                //配置通过指定配置的类，携带到TimeCostTransform中
                it.output.set(extensionNew.output)
                it.open.set(extensionNew.open)
                it.traceConfigFile.set(extensionNew.traceConfigFile)
                it.logTraceInfo.set(extensionNew.logTraceInfo)
                it.project.set(project)
            }
            //InstrumentationScope.ALL 配合 FramesComputationMode.COPY_FRAMES可以指定该字节码转换器在全局生效，包括第三方lib
            variant.instrumentation.setAsmFramesComputationMode(FramesComputationMode.COPY_FRAMES)
        }
    }
}