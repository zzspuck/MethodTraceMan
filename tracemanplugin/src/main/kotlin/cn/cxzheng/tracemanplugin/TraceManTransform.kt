package cn.cxzheng.tracemanplugin

import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.ClassContext
import com.android.build.api.instrumentation.ClassData
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes
import java.io.File

abstract class TraceManTransform: AsmClassVisitorFactory<TraceManParameter> {
    override fun createClassVisitor(
        classContext: ClassContext,
        nextClassVisitor: ClassVisitor
    ): ClassVisitor {
        println("MethodTraceMan createClassVisitor")
        val traceManConfig = parameters.get()
        val traceConfig = initConfig(traceManConfig)
        traceConfig.parseTraceConfigFile()
        return TraceClassVisitor(Opcodes.ASM9, nextClassVisitor,traceConfig)
    }

    private fun initConfig(configuration: TraceManParameter): Config {
        val config = Config()
        config.mTraceConfigFile = configuration.traceConfigFile.get()
        config.mIsNeedLogTraceInfo = configuration.logTraceInfo.get()
        return config
    }

    override fun isInstrumentable(classData: ClassData): Boolean {
        println("MethodTraceMan isInstrumentable")
        val traceManConfig = parameters.get()
        //val output = traceManConfig.output.get()
     /*   if (output.isEmpty()) {
            traceManConfig.output.set(
                project.getBuildDir().getAbsolutePath() + File.separator + "traceman_output"
            )
        }*/
        val traceConfig = initConfig(traceManConfig).apply {
            parseTraceConfigFile()
        }

        if (traceConfig.isNeedTraceClass(classData.className)) {
            return true
        } else {
            return false
        }
    }
}