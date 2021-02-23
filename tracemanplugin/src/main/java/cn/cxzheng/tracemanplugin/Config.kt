package cn.cxzheng.tracemanplugin

import java.io.File
import java.io.FileNotFoundException

/**
 * Create by cxzheng on 2019/6/4
 * Updated for AGP 8.0+
 */
class Config {

    //一些默认无需插桩的类
    val UNNEED_TRACE_CLASS = arrayOf("R.class", "R$", "Manifest", "BuildConfig")

    //插桩配置文件
    var mTraceConfigFile: String? = null

    //需插桩的包
    private val mNeedTracePackageMap: HashSet<String> by lazy {
        HashSet<String>()
    }

    //在需插桩的包范围内的 无需插桩的白名单
    private val mWhiteClassMap: HashSet<String> by lazy {
        HashSet<String>()
    }

    //在需插桩的包范围内的 无需插桩的包名
    private val mWhitePackageMap: HashSet<String> by lazy {
        HashSet<String>()
    }

    //插桩代码所在类
    var mBeatClass: String? = null

    //是否需要打印出所有被插桩的类和方法
    var mIsNeedLogTraceInfo = false


    fun isNeedTraceClass(fileName: String): Boolean {
        var isNeed = true
        if (fileName.endsWith(".class")) {
            for (unTraceCls in UNNEED_TRACE_CLASS) {
                if (fileName.contains(unTraceCls)) {
                    isNeed = false
                    break
                }
            }
        } else {
            isNeed = false
        }
        return isNeed
    }

    //判断是否是traceConfig.txt中配置范围的类
    fun isConfigTraceClass(className: String): Boolean {

        fun isInNeedTracePackage(): Boolean {
            var isIn = false
            mNeedTracePackageMap.forEach {
                if (className.contains(it)) {
                    isIn = true
                    return@forEach
                }

            }
            return isIn
        }

        fun isInWhitePackage(): Boolean {
            var isIn = false
            mWhitePackageMap.forEach {
                if (className.contains(it)) {
                    isIn = true
                    return@forEach
                }

            }
            return isIn
        }

        fun isInWhiteClass(): Boolean {
            var isIn = false
            mWhiteClassMap.forEach {
                if (className == it) {
                    isIn = true
                    return@forEach
                }

            }
            return isIn
        }

        return if (mNeedTracePackageMap.isEmpty()) {
            !(isInWhitePackage() || isInWhiteClass())
        } else {
            if (isInNeedTracePackage()) {
                !(isInWhitePackage() || isInWhiteClass())
            } else {
                false
            }
        }

    }


    /**
     * 解析插桩配置文件
     */
    fun parseTraceConfigFile() {

        println("[MethodTraceMan] parseTraceConfigFile: $mTraceConfigFile")

        if (mTraceConfigFile.isNullOrEmpty()) {
            println("[MethodTraceMan] Warning: traceConfigFile is not set")
            return
        }

        val traceConfigFile = File(mTraceConfigFile!!)
        if (!traceConfigFile.exists()) {
            throw FileNotFoundException(
                """
                    Trace config file not exist, Please read quickstart.
                    找不到 $mTraceConfigFile 配置文件, 尝试阅读一下 QuickStart。
                """.trimIndent()
            )
        }

        val configStr = Utils.readFileAsString(traceConfigFile.absolutePath)

        // 处理跨平台换行符
        val configArray = configStr
            .replace("\r\n", "\n")
            .replace("\r", "\n")
            .split("\n")
            .filter { it.isNotEmpty() }

        for (config in configArray) {
            var line = config.trim()

            if (line.isBlank()) {
                continue
            }
            if (line.startsWith("#")) {
                continue
            }
            if (line.startsWith("[")) {
                continue
            }

            when {
                line.startsWith("-tracepackage ") -> {
                    line = line.replace("-tracepackage ", "").trim()
                    mNeedTracePackageMap.add(line)
                    println("[MethodTraceMan] tracepackage: $line")
                }
                line.startsWith("-keepclass ") -> {
                    line = line.replace("-keepclass ", "").trim()
                    mWhiteClassMap.add(line)
                    println("[MethodTraceMan] keepclass: $line")
                }
                line.startsWith("-keeppackage ") -> {
                    line = line.replace("-keeppackage ", "").trim()
                    mWhitePackageMap.add(line)
                    println("[MethodTraceMan] keeppackage: $line")
                }
                line.startsWith("-beatclass ") -> {
                    line = line.replace("-beatclass ", "").trim()
                    mBeatClass = line
                    println("[MethodTraceMan] beatclass: $line")
                }
            }
        }

        println("[MethodTraceMan] Config parsed - tracePackages: ${mNeedTracePackageMap.size}, whiteClasses: ${mWhiteClassMap.size}, whitePackages: ${mWhitePackageMap.size}")
    }

}
