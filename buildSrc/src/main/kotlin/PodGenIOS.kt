import org.gradle.api.Project
import org.gradle.api.logging.Logger
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.*
import java.io.File
import java.io.IOException
import org.jetbrains.kotlin.konan.target.*
import org.jetbrains.kotlin.gradle.targets.native.tasks.PodGenTask
import org.jetbrains.kotlin.gradle.plugin.cocoapods.CocoapodsExtension.SpecRepos
import org.jetbrains.kotlin.gradle.plugin.cocoapods.CocoapodsExtension.CocoapodsDependency.PodLocation.*
import org.jetbrains.kotlin.gradle.plugin.cocoapods.CocoapodsExtension.CocoapodsDependency.PodLocation
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType
import kotlin.concurrent.thread
import kotlin.reflect.KClass

/**
 * Adapted version from here:
 * https://youtrack.jetbrains.com/issue/KT-54161/Support-adding-extra-code-to-generated-Podfile-from-the-Kotlin-gradle-plugin#focus=Comments-27-6551141.0-0
 */

private val Family.platformLiteral: String
    get() = when (this) {
        Family.OSX -> "macos"
        Family.IOS -> "ios"
        Family.TVOS -> "tvos"
        Family.WATCHOS -> "watchos"
        else -> throw IllegalArgumentException("Bad family ${this.name}")
    }

internal val Project.cocoapodsBuildDirs: CocoapodsBuildDirs
    get() = CocoapodsBuildDirs(this)

internal class CocoapodsBuildDirs(val project: Project) {
    val root: File
        get() = project.buildDir.resolve("cocoapods")

    val framework: File
        get() = root.resolve("framework")

    val defs: File
        get() = root.resolve("defs")

    val buildSettings: File
        get() = root.resolve("buildSettings")

    val synthetic: File
        get() = root.resolve("synthetic")

    fun synthetic(family: Family) = synthetic.resolve(family.name)

    val externalSources: File
        get() = root.resolve("externalSources")

    val publish: File = root.resolve("publish")

    fun externalSources(fileName: String) = externalSources.resolve(fileName)

    fun fatFramework(buildType: NativeBuildType) =
        root.resolve("fat-frameworks/${buildType.getName()}")
}


/**
 * The task generates a synthetic project with all cocoapods dependencies
 */
open class PatchedPodGenTask : PodGenTask() {
    private val PODFILE_SUFFIX = """
        post_install do |installer|
         installer.pods_project.targets.each do |target|
          target.build_configurations.each do |config|
           config.build_settings['IPHONEOS_DEPLOYMENT_TARGET'] = '11.0'
          end
         end
         
         installer.pods_project.targets.each do |target|
             target.build_configurations.each do |config|
                 config.build_settings['CODE_SIGN_IDENTITY'] = ''
             end
         end
        end
    """.trimIndent()

    @TaskAction
    fun patchedGenerate() {
        val syntheticDir = project.cocoapodsBuildDirs.synthetic(family).apply { mkdirs() }
        val specRepos: Collection<String> = specReposAccessor.get().getAllAccessor()

        val projResource = "/cocoapods/project.pbxproj"
        val projDestination = syntheticDir.resolve("synthetic.xcodeproj").resolve("project.pbxproj")

        projDestination.parentFile.mkdirs()
        projDestination.outputStream().use { file ->
            javaClass.getResourceAsStream(projResource).use { resource ->
                resource.copyTo(file)
            }
        }

        val podfile = syntheticDir.resolve("Podfile")
        podfile.createNewFile()

        val podfileContent = getPodfileContent(specRepos, family.platformLiteral) + PODFILE_SUFFIX
        podfile.writeText(podfileContent)
        val podInstallCommand = listOf("pod", "install")

        runCommand(
            podInstallCommand,
            project.logger,
            exceptionHandler = { e: IOException ->
                CocoapodsErrorHandlingUtil.handle(e, podInstallCommand)
            },
            errorHandler = { retCode, output, _ ->
                CocoapodsErrorHandlingUtil.handlePodInstallSyntheticError(
                    podInstallCommand.joinToString(" "),
                    retCode,
                    output,
                    family,
                    podNameAccessor.get()
                )
            },
            processConfiguration = {
                directory(syntheticDir)
            })

        val podsXcprojFile = podsXcodeProjDirAccessor.get()
        check(podsXcprojFile.exists() && podsXcprojFile.isDirectory) {
            "Synthetic project '${podsXcprojFile.path}' was not created."
        }
    }

    private fun getPodfileContent(specRepos: Collection<String>, xcodeTarget: String) =
        buildString {

            specRepos.forEach {
                appendLine("source '$it'")
            }

            appendLine("target '$xcodeTarget' do")
            //if (useLibraries.get().not()) {
            appendLine("\tuse_frameworks!")
            //}
            pods.get().mapNotNull {
                buildString {
                    append("pod '${it.name}'")

                    val pathType = when (it.source) {
                        is Path -> "path"
                        is Url -> "path"
                        is Git -> "git"
                        else -> null
                    }

                    val path = it.source?.getLocalPathAccessor(project, it.name)

                    if (path != null && pathType != null) {
                        append(", :$pathType => '$path'")
                    }

                }
            }.forEach { appendLine("\t$it") }
            appendLine("end\n")
        }
}

fun <T : Any, R> KClass<T>.invokeDynamic(methodStart: String, instance: Any?, vararg params: Any?): R {
    val method = this.java.methods.firstOrNull { it.name.startsWith(methodStart) } ?: error("Can't find accessor for $methodStart")
    return method.invoke(instance, *params) as R
}


val PodGenTask.podsXcodeProjDirAccessor: Provider<File> get() = this::class.invokeDynamic("getPodsXcodeProjDir", this)
val PodGenTask.specReposAccessor: Provider<SpecRepos> get() = this::class.invokeDynamic("getSpecRepos", this)
val PodGenTask.podNameAccessor: Provider<String> get() = this::class.invokeDynamic("getPodName", this)
fun SpecRepos.getAllAccessor(): Collection<String> = this::class.invokeDynamic("getAll", this)
fun PodLocation.getLocalPathAccessor(project: Project, podName: String): String? = this::class.invokeDynamic("getLocalPath", this, project, podName)

private fun runCommand(
    command: List<String>,
    logger: Logger,
    errorHandler: ((retCode: Int, output: String, process: Process) -> String?)? = null,
    exceptionHandler: ((ex: IOException) -> Unit)? = null,
    processConfiguration: ProcessBuilder.() -> Unit = { }
): String {
    var process: Process? = null
    try {
        process = ProcessBuilder(command)
            .apply {
                this.processConfiguration()
            }.start()
    } catch (e: IOException) {
        if (exceptionHandler != null) exceptionHandler(e) else throw e
    }

    if (process == null) {
        throw IllegalStateException("Failed to run command ${command.joinToString(" ")}")
    }

    var inputText = ""
    var errorText = ""

    val inputThread = thread {
        inputText = process.inputStream.use {
            it.reader().readText()
        }
    }

    val errorThread = thread {
        errorText = process.errorStream.use {
            it.reader().readText()
        }
    }

    inputThread.join()
    errorThread.join()

    val retCode = process.waitFor()
    logger.info(
        """
            |Information about "${command.joinToString(" ")}" call:
            |
            |${inputText}
        """.trimMargin()
    )

    check(retCode == 0) {
        errorHandler?.invoke(retCode, inputText.ifBlank { errorText }, process)
            ?: """
                |Executing of '${command.joinToString(" ")}' failed with code $retCode and message: 
                |
                |$inputText
                |
                |$errorText
                |
                """.trimMargin()
    }

    return inputText
}

private object CocoapodsErrorHandlingUtil {
    fun handle(e: IOException, command: List<String>) {
        if (e.message?.contains("No such file or directory") == true) {
            val message = """ 
               |'${command.take(2).joinToString(" ")}' command failed with an exception:
               | ${e.message}
               |        
               |        Full command: ${command.joinToString(" ")}
               |        
               |        Possible reason: CocoaPods is not installed
               |        Please check that CocoaPods v1.10 or above is installed.
               |        
               |        To check CocoaPods version type 'pod --version' in the terminal
               |        
               |        To install CocoaPods execute 'sudo gem install cocoapods'
               |
            """.trimMargin()
            throw IllegalStateException(message)
        } else {
            throw e
        }
    }

    fun handlePodInstallSyntheticError(command: String, retCode: Int, error: String, family: Family, podName: String): String? {
        var message = """
            |'pod install' command on the synthetic project failed with return code: $retCode
            |
            |        Full command: $command
            |
            |        Error: ${error.lines().filter { it.contains("[!]") }.joinToString("\n")}
            |       
        """.trimMargin()

        if (
            error.contains("deployment target") ||
            error.contains("no platform was specified") ||
            error.contains(Regex("The platform of the target .+ is not compatible with `$podName"))
        ) {
            message += """
                |
                |        Possible reason: ${family.name.toLowerCase()} deployment target is not configured
                |        Configure deployment_target for ALL targets as follows:
                |        cocoapods {
                |           ...
                |           ${family.name.toLowerCase()}.deploymentTarget = "..."
                |           ...
                |        }
                |       
            """.trimMargin()
            return message
        } else if (
            error.contains("Unable to add a source with url") ||
            error.contains("Couldn't determine repo name for URL") ||
            error.contains("Unable to find a specification")
        ) {
            message += """
                |
                |        Possible reason: spec repos are not configured correctly.
                |        Ensure that spec repos are correctly configured for all private pod dependencies:
                |        cocoapods {
                |           specRepos {
                |               url("<private spec repo url>")
                |           }
                |        }
                |       
            """.trimMargin()
            return message
        }
        return null
    }

}