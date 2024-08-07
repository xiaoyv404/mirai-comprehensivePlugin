package com.xiaoyv404.mirai.app.minecraftServer

import com.google.gson.Gson
import com.xiaoyv404.mirai.PluginConfig
import com.xiaoyv404.mirai.PluginMain
import com.xiaoyv404.mirai.model.mincraftServer.MinecraftServer
import com.xiaoyv404.mirai.model.mincraftServer.MinecraftServerStatus
import com.xiaoyv404.mirai.tool.ClientUtils
import java.awt.Color
import java.awt.Font
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.*
import javax.imageio.ImageIO

class MinecraftServerListGenerator {
    private val roundX = 10
    private val red = Color.decode("#FF3D38")
    private val green = Color.decode("#76FFA1")
    private val yellow = Color.decode("#FF9C38")

    private val font = Font.createFont(
        Font.TRUETYPE_FONT,
        PluginMain.resolveDataFile("resources/Minecraft/Minecraft AE.ttf")
    ).deriveFont(Font.PLAIN, 55f)
    private val font6 = Font.createFont(
        Font.TRUETYPE_FONT,
        PluginMain.resolveDataFile("resources/Minecraft/Minecraft AE.ttf"),
    ).deriveFont(Font.PLAIN, 22f)

    suspend fun drawList(list: List<MinecraftServer>): ByteArrayInputStream {
        val imgWidth = 800
        val imgHeight = 150 * list.size + 10
        val img = BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_RGB)

        val g2d = img.createGraphics()

        //消除画图锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        //消除文字锯齿
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)

        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
        g2d.setRenderingHint(
            RenderingHints.KEY_INTERPOLATION,
            RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR
        )
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY)
        g2d.setRenderingHint(
            RenderingHints.KEY_ALPHA_INTERPOLATION,
            RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY
        )
        g2d.paint = Color.WHITE//设置画笔颜色
        g2d.fillRect(0, 0, imgWidth, imgHeight)//填充区域，将区域背景设置为白色

        list.forEachIndexed { k, v ->
            val name = v.name.uppercase(Locale.getDefault())
            val status = v.status
            val roundY = 10 + k * 150
            drawInfo(g2d, status, roundY, name, "%03d".format(v.playerNum), "%03d".format(v.playerMaxNum))
            drawBar(g2d, roundY, status, name)
        }
        g2d.dispose()

        val os = ByteArrayOutputStream()
        ImageIO.write(img, "png", os)
        return ByteArrayInputStream(os.toByteArray())
    }

    private fun drawInfo(
        g2d: Graphics2D,
        status: MinecraftServerStatus,
        roundY: Int,
        name: String,
        playerNum: String,
        playerMaxNum: String
    ) {
        g2d.color = if (status == MinecraftServerStatus.Online)
            green
        else
            red

        g2d.fillOval(roundX, roundY, 130, 130)
        g2d.color = green

        g2d.font = font
        g2d.color = Color.BLACK
        g2d.drawString(name, roundX + 140, roundY + 55 + 5)
        g2d.drawString(playerNum, roundX + 140, roundY + 55 + 55 + 12)
        g2d.font = font6
        g2d.drawString("/$playerMaxNum", roundX + 140 + 130, roundY + 55 + 55 + 12)

        g2d.drawImage(
            ImageIO.read(PluginMain.resolveDataFile("resources/Minecraft/logo.png")),
            (roundX + 365),
            roundY + 3, 53, 53,
            null
        )

    }

    private suspend fun drawBar(
        g2d: Graphics2D,
        roundY: Int,
        status: MinecraftServerStatus,
        name: String,
    ) {
        if (name == "MCG" && status == MinecraftServerStatus.Online) {
            getMCGTps()
            for (i in 0..11) {
                (average.getOrNull(i) ?: 0).let {
                    g2d.color = setColorByTPS(it)
                }
                drawBarPart(g2d, roundY, i)
                (low.getOrNull(i) ?: 0).let {
                    g2d.color = setColorByTPS(it)
                }
                drawBarLowTpsPart(g2d, roundY, i)
            }
        }

        g2d.color = if (status == MinecraftServerStatus.Online)
            green
        else
            red

        for (i in 0..11)
            drawBarPart(g2d, roundY, i)
        return
    }

    private fun drawBarPart(g2d: Graphics2D, roundY: Int, i: Int) {
        g2d.fillPolygon(
            intArrayOf(
                (roundX + 365 + 28 * i),
                (roundX + 365 + 16 + 28 * i),
                (roundX + 365 + 16 + 35 + 28 * i),
                (roundX + 365 + 35 + 28 * i)
            ),
            intArrayOf(roundY + 72 + 56, roundY + 72 + 56, roundY + 72, roundY + 72),
            4
        )
    }

    private fun drawBarLowTpsPart(g2d: Graphics2D, roundY: Int, i: Int) {
        g2d.fillPolygon(
            intArrayOf(
                (roundX + 365 + 16 + 28 * i),
                (roundX + 365 + 28 * i),
                (roundX + 365 + 6 + 28 * i),
                (roundX + 365 + 6 + 16 + 28 * i),
            ),
            intArrayOf(roundY + 72 + 56, roundY + 72 + 56, roundY + 72 + 56 - 10, roundY + 72 + 56 - 10),
            4
        )
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun setColorByTPS(tps: Long): Color? {
        return when (tps) {
            in 17..100 -> green
            in 10.rangeUntil(17) -> yellow
            else -> red
        }
    }

    private val low = mutableListOf<Long>()
    private val average = mutableListOf<Long>()

    private suspend fun getMCGTps(){
        val tps = try {
            Gson().fromJson(
                ClientUtils.get<String>(
                    "${PluginConfig.etc.planApiUrl}/v1/graph?server=Minecraft幻想乡&type=performance"
                ), Performance::class.java
            ).tps.takeLast(720)
        } catch (e: Exception) {
            null
        }

        var lowi: Long = 20
        var averagei: Long = 0
        var k = 1
        tps?.forEach {
            if (k == 60) {
                low.add(lowi)
                average.add(averagei / 60)
                lowi = 20
                averagei = 0
                k = 0
            }
            if (lowi > it[1])
                lowi = it[1].toLong()
            k++
            averagei += it[1].toLong()
        }
    }
}