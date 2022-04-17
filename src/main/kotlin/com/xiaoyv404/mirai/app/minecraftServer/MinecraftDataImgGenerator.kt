package com.xiaoyv404.mirai.app.minecraftServer

import com.xiaoyv404.mirai.PluginMain
import java.awt.*
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO
import kotlin.math.roundToInt

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
}
