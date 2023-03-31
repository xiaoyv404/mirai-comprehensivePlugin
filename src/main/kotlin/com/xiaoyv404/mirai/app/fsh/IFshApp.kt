package com.xiaoyv404.mirai.app.fsh

import com.xiaoyv404.mirai.core.*
import net.mamoe.mirai.event.events.*
import org.apache.commons.cli.*
import java.io.*
import java.nio.charset.*

interface IFshApp {
    companion object {
        private val parser: CommandLineParser = DefaultParser()
        fun cmdLine(options: Options, args: Array<String>): CommandLine =
            parser.parse(options, args.takeLast(args.size - 1).toTypedArray())
    }

    /**
     * 提供命令名
     *
     * @return 命令名
     */
    fun getCommands(): Array<String>

    /**
     * 命令处理方法 (异步)
     *
     * @param args 命令行参数
     * @param msg 接收的原消息
     * @return 是否进行调用计次
     */
    @Throws(Exception::class)
    suspend fun executeRsh(args: Array<String>, msg: MessageEvent): Boolean

    /**
     * 取命令的说明
     *
     * @param cmd 命令
     * @return 命令说明
     */
    fun help(cmd: String, width: Int = 60): String {
        val formatter = HelpFormatter()
        val options = getOptions(cmd)
        val out = ByteArrayOutputStream()
        val pw = PrintWriter(out)
        formatter.printHelp(
            pw,
            width,
            getHelpCmdLineSyntax(cmd),
            getHelpHeader(),
            options,
            1,
            3,
            getHelpFooter(),
            false
        )
        pw.flush()
        return out.toString()
            .replace(Regex("^usage"), "调用命令")
            .replace(Regex("\n+$"), "")
    }

    fun getOptions(cmd: String): Options = Options().apply {
        addOption("h", "help", false, "查看使用说明")
    }

    fun getHelpCmdLineSyntax(cmd: String): String = cmd

    fun getHelpHeader(): String {
        this as NfApp
        return "所属应用: [${this.getAppName()}@${this.getVersion()}]: ${this.getAppDescription()}\n" +
            "使用限制: 每${this.getLimitExpiresTime()}秒限制使用${this.getLimitCount()}次\n" +
            "\n"
    }

    fun getHelpFooter(): String? = null
}