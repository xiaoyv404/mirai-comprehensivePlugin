package com.xiaoyv404.mirai.app.fsh

import net.mamoe.mirai.event.events.MessageEvent
import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.CommandLineParser
import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.Options

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
}