package com.xiaoyv404.mirai.tool

import java.util.regex.Pattern

object CommandSplit {
    private val argsSplitPattern = Pattern.compile("([^\"]\\S*|\".+?(?<!\\\\)\")\\s*")
    fun splitWhit404(msg: String): ArrayList<String>? {
        val matcher = argsSplitPattern.matcher(msg.trim())
        val argsList: ArrayList<String> = ArrayList()
        while (matcher.find()) {
            var s = matcher.group(1)
            if (s.startsWith("\"") && s.endsWith("\"") && s.length > 1) {
                s = s.substring(1, s.length - 1)
            }
            s = s.replace("\\\"", "\"")
            argsList.add(s)
        }

        if (argsList.isEmpty())
            return null

        if (argsList.size > 1 && argsList[0] == "404") {
            argsList.removeAt(0)
            argsList[0] = "-${argsList[0]}"
        }
        return argsList
    }
}