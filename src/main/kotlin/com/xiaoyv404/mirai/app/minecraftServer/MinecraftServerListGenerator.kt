package com.xiaoyv404.mirai.app.minecraftServer

import com.xiaoyv404.mirai.*
import com.xiaoyv404.mirai.model.mincraftServer.*
import java.awt.*
import java.awt.image.*
import java.io.*
import java.util.*
import javax.imageio.*

class MinecraftServerListGenerator() {
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

    fun drawList(list: List<MinecraftServer>, low: List<Long>, average: List<Long>): ByteArrayInputStream {
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
            drawOne(
                g2d,
                10 + k * 150,
                v.name.uppercase(Locale.getDefault()),
                "%03d".format(v.playerNum),
                "%03d".format(v.playerMaxNum),
                v.status,
                low,
                average
            )
        }
        g2d.dispose()

        val os = ByteArrayOutputStream()
        ImageIO.write(img, "png", os)
        return ByteArrayInputStream(os.toByteArray())
    }

    private fun drawOne(
        g2d: Graphics2D,
        roundY: Int,
        name: String,
        playerNum: String,
        playerMaxNum: String,
        status: Int,
        low: List<Long>,
        average: List<Long>
    ) {
        g2d.color = if (status == 1)
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

        drawBar(g2d, roundY, status, name, average, low)
    }

    private fun drawBar(
        g2d: Graphics2D,
        roundY: Int,
        status: Int,
        name: String,
        average: List<Long>,
        low: List<Long>
    ) {
        if (name == "MCG" && status == 1) {
            for (i in 0..11) {
                g2d.color = setColorByTPS(average[i])
                drawBarPart(g2d, roundY, i)
                g2d.color = setColorByTPS(low[1])
                drawBarLowTpsPart(g2d, roundY, i)
            }
            return
        }
        g2d.color = if (status == 1)
            red
        else
            green

        for (i in 0..11)
            drawBarPart(g2d, roundY, i)
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
}