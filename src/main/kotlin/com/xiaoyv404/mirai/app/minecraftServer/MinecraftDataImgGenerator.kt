package com.xiaoyv404.mirai.app.minecraftServer

import com.xiaoyv404.mirai.*
import com.xiaoyv404.mirai.entity.mincraftServer.*
import java.awt.*
import java.awt.image.*
import java.io.*
import java.util.*
import javax.imageio.*
import kotlin.math.*

/**
 * 用于生成Info图片
 * @author xiaoyv_404
 * @create 2022/4/8
 *
 */
class MinecraftDataImgGenerator {
    companion object {
        fun getImg(playerList: List<Player>, playerNum: String, ip: String, port: String): ByteArrayInputStream {
            val font = Font.createFont(
                Font.TRUETYPE_FONT,
                PluginMain.resolveDataFile("resources/Minecraft/LXGWWenKai-Light.ttf")
            ).deriveFont(Font.BOLD, 18f)

            val fontMono = Font.createFont(
                Font.TRUETYPE_FONT,
                PluginMain.resolveDataFile("resources/Minecraft/LXGWWenKaiMono-Light.ttf")
            ).deriveFont(Font.PLAIN, 16f)

            val imgWidth = 350
            val imgHeight = 400

            val img = BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_RGB)

            val g2d = img.createGraphics()

            g2d.font = font

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

            val xMin = 10
            val xMax = 340

            val xSize: Double = (xMax - xMin) / 2.0

            //绘制背景
            g2d.drawImage(
                ImageIO.read(PluginMain.resolveDataFile("resources/Minecraft/background.png")),
                0, 0, 350, 400, null
            )

            //绘制状态
            g2d.drawImage(
                ImageIO.read(PluginMain.resolveDataFile("resources/Minecraft/Running.png")),
                (xMin + xSize + 50).roundToInt(),
                20, 100, 39,
                null
            )

            //绘制logo
            g2d.drawImage(
                ImageIO.read(PluginMain.resolveDataFile("resources/Minecraft/logo.png")),
                (xMin + 20),
                15, 50, 50,
                null
            )

            val xPeople = xMin + 40
            val yData = 120
            val yDataSize = 40

            //绘制基本信息
            g2d.drawString(playerNum, (xPeople + xSize).roundToInt(), yData)
            g2d.drawString(ip, xSize.roundToInt(), yData + yDataSize)
            g2d.drawString(port, (xPeople + xSize).roundToInt(), yData + yDataSize * 2)

            g2d.paint = Color.decode("#CCCCCC")

            g2d.drawString("人数", xPeople, yData)
            g2d.drawString("IP", xPeople, yData + yDataSize)
            g2d.drawString("Port", xPeople, yData + yDataSize * 2)

            //绘制虚线
            g2d.paint = Color.decode("#999999")
            val st: Stroke = g2d.stroke
            val bs: Stroke
            bs = BasicStroke(
                0.5F, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_BEVEL, 0F, floatArrayOf(16f, 4f), 0F
            )
            g2d.stroke = bs
            g2d.drawLine(10, yData + yDataSize * 2 + 20, 340, yData + yDataSize * 2 + 20)
            g2d.stroke = st


            //绘制玩家列表
            g2d.font = fontMono
            g2d.paint = Color.decode("#FFFFFF")
            var i = 3
            val fm = g2d.getFontMetrics(fontMono)
            playerList.forEachIndexed { index, player ->
                if (index >= 8)
                    return@forEachIndexed

                val name = player.name
                val xText = (165 - fm.stringWidth(name)) / 2
                val yText = yData + yDataSize * i + 10
                if (index % 2 == 0) {
                    g2d.drawString(name, xText, yText)
                } else {
                    g2d.drawString(name, xText + 165, yText)
                    i++
                }
            }

            g2d.dispose()

            val os = ByteArrayOutputStream()
            ImageIO.write(img, "JPEG", os)
            return ByteArrayInputStream(os.toByteArray())


        }
    }

    fun drawList(list: List<MinecraftServer>, low: List<Long>, average: List<Long>): ByteArrayInputStream {
        val imgWidth = 800
        val imgHeight = 150 * list.size + 10
        val img = BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_RGB)

        val font = Font.createFont(
            Font.TRUETYPE_FONT,
            PluginMain.resolveDataFile("resources/Minecraft/Minecraft AE.ttf")
        ).deriveFont(Font.PLAIN, 55f)
        val font6 = Font.createFont(
            Font.TRUETYPE_FONT,
            PluginMain.resolveDataFile("resources/Minecraft/Minecraft AE.ttf"),
        ).deriveFont(Font.PLAIN, 22f)

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
                font,
                font6,
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
        font: Font,
        font6: Font,
        low: List<Long>,
        average: List<Long>
    ) {
        val red = Color.decode("#FF3D38")
        val green = Color.decode("#76FFA1")
        val roundX = 10

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

        if (name == "MCG")
            for (i in 0..11) {
                println(average[i])
                println(low[i])
                println(setColorByTPS(1))
                g2d.color = setColorByTPS(average[i])
                g2d.fillPolygon(
                    intArrayOf(
                        (roundX + 365 + 6 + 28 * i),
                        (roundX + 365 + 6 + 16 + 28 * i),
                        (roundX + 365 + 16 + 35 + 28 * i),
                        (roundX + 365 + 35 + 28 * i)
                    ),
                    intArrayOf(roundY + 72 + 56 - 10, roundY + 72 + 56 - 10, roundY + 72, roundY + 72),
                    4
                )
                g2d.color = setColorByTPS(1)
                g2d.fillPolygon(
                    intArrayOf(
                        (roundX + 365 + 28 * i),
                        (roundX + 365 + 16 + 28 * i),
                        (roundX + 365 + 6 + 28 * i),
                        (roundX + 365 + 6 + 16 + 28 * i),
                    ),
                    intArrayOf(roundY + 72 + 56, roundY + 72 + 56, roundY + 72 + 56 - 10, roundY + 72 + 56 - 10),
                    4
                )
            }
        else {
            g2d.color = Color.decode("#76FFA1")

            for (i in 0..11)
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
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun setColorByTPS(tps: Long): Color? {
        return when (tps) {
            in 17..100 -> Color.decode("#76FFA1")
            in 10.rangeUntil(17) -> Color.decode("#FF9C38")
            else -> Color.decode("#FF3D38")
        }
    }
}
