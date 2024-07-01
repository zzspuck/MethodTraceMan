package cn.cxzheng.tracemanplugin

import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.ClassContext
import com.android.build.api.instrumentation.ClassData
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.Opcodes
import java.io.File

abstract class TraceManTransform() : AsmClassVisitorFactory<TraceManParameter> {
    private var mTraceConfig: Config? = null
    override fun createClassVisitor(
        classContext: ClassContext,
        nextClassVisitor: ClassVisitor
    ): ClassVisitor {

        return TraceClassVisitor(Opcodes.ASM9, nextClassVisitor, mTraceConfig!!)
    }

    private fun initConfig(configuration: TraceManParameter): Config {
        val config = Config()
        config.mTraceConfigFile = configuration.traceConfigFile.get()
        config.mIsNeedLogTraceInfo = configuration.logTraceInfo.get()
        return config
    }

    override fun isInstrumentable(classData: ClassData): Boolean {
        val traceManConfig = parameters.get()
        val output = traceManConfig.output.get()
        val project = traceManConfig.project.get()
        if (output.isEmpty()) {
            traceManConfig.output.set(
                project.getBuildDir().getAbsolutePath() + File.separator + "traceman_output"
            )
        }
        mTraceConfig = if (traceManConfig.open.get()) {
            val traceConfig = initConfig(traceManConfig)
            traceConfig.parseTraceConfigFile()
            traceConfig
        } else {
            Config()
        }
        return true
    }
}