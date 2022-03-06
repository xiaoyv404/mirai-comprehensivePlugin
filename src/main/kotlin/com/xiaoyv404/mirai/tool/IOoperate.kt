package com.xiaoyv404.mirai.tool

import java.io.*


object FileUtils {
    fun saveFileFromStream(src: InputStream, dst: File) {
        var len: Int
        val buf = ByteArray(32768)
        val outputStream = FileOutputStream(dst, false)
        while (src.read(buf).also { len = it } != -1) {
            outputStream.write(buf, 0, len)
        }
        outputStream.flush()
        outputStream.close()
    }

    fun copyFile(src: File, dst: File) {
        val `in`: InputStream = FileInputStream(src)
        saveFileFromStream(`in`, dst)
        `in`.close()
    }

    fun saveFileFromString(sc: String, dst: File) {
        try {
            val bw = BufferedWriter(FileWriter(dst, true))

            bw.write(sc)
            bw.close()
        } catch (e: IOException) {
            dst.createNewFile()
            val bw = BufferedWriter(FileWriter(dst, true))

            bw.write(sc)
            bw.close()
        }
    }
}