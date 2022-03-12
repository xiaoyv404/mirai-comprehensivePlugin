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
     * �ṩ������
     *
     * @return ������
     */
    fun getCommands(): Array<String>

    /**
     * ������� (�첽)
     *
     * @param args �����в���
     * @param msg ���յ�ԭ��Ϣ
     * @return �Ƿ���е��üƴ�
     */
    @Throws(Exception::class)
    suspend fun executeRsh(args: Array<String>, msg: MessageEvent): Boolean
}