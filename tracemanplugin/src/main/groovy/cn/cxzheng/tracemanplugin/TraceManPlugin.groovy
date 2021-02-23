package cn.cxzheng.tracemanplugin

import com.android.build.api.instrumentation.FramesComputationMode
import com.android.build.api.instrumentation.InstrumentationScope
import com.android.build.api.variant.AndroidComponentsExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * MethodTraceMan Plugin - AGP 8.0+ compatible
 * 使用新的 Instrumentation API 替代已废弃的 Transform API
 */
class TraceManPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        println '*****************MethodTraceMan Plugin apply (AGP 8.0+)*********************'

        // 创建配置扩展
        def traceManConfig = project.extensions.create("traceMan", TraceManConfig)

        // 获取 AndroidComponentsExtension - 必须在 apply 时就获取
        def androidComponents = project.extensions.findByType(AndroidComponentsExtension)
        if (androidComponents == null) {
            // 延迟到 Android 插件应用后再获取
            project.plugins.withId('com.android.application') {
                configurePlugin(project, traceManConfig)
            }
            project.plugins.withId('com.android.library') {
                configurePlugin(project, traceManConfig)
            }
        } else {
            configurePlugin(project, traceManConfig)
        }
    }

    private void configurePlugin(Project project, TraceManConfig traceManConfig) {
        def androidComponents = project.extensions.findByType(AndroidComponentsExtension)
        if (androidComponents == null) {
            println '[MethodTraceMan]: AndroidComponentsExtension not found, skipping...'
            return
        }

        // 为所有变体注册字节码转换 - 必须在这里直接调用，不能在 afterEvaluate 中
        androidComponents.onVariants(androidComponents.selector().all()) { variant ->
            // 在 variant callback 中检查配置，因为此时配置已经可用
            project.afterEvaluate {
                if (!traceManConfig.open) {
                    println "[MethodTraceMan]: Plugin is disabled for variant: ${variant.name}"
                    return
                }

                // 设置默认输出路径
                if (traceManConfig.output == null || traceManConfig.output.isEmpty()) {
                    traceManConfig.output = project.getBuildDir().getAbsolutePath() + File.separator + "traceman_output"
                }
            }

            println "[MethodTraceMan]: Registering instrumentation for variant: ${variant.name}"

            variant.instrumentation.transformClassesWith(
                    TraceManClassVisitorFactory.class,
                    InstrumentationScope.ALL
            ) { params ->
                // 使用 provider 来延迟获取配置值
                params.traceConfigFile.set(project.provider { traceManConfig.traceConfigFile ?: "" })
                params.logTraceInfo.set(project.provider { traceManConfig.logTraceInfo })
                params.pluginEnabled.set(project.provider { traceManConfig.open })
            }

            variant.instrumentation.setAsmFramesComputationMode(
                    FramesComputationMode.COMPUTE_FRAMES_FOR_INSTRUMENTED_METHODS
            )
        }
    }
}
