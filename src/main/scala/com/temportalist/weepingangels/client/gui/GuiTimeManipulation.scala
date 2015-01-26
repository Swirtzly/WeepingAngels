package com.temportalist.weepingangels.client.gui

import com.temportalist.origin.library.common.nethandler.PacketHandler
import com.temportalist.origin.wrapper.client.gui.GuiScreenWrapper
import com.temportalist.weepingangels.common.WeepingAngels
import com.temportalist.weepingangels.common.network.PacketSetTime
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiButton
import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

/**
 *
 *
 * @author TheTemportalist
 */
@SideOnly(Side.CLIENT)
class GuiTimeManipulation(val player: EntityPlayer) extends GuiScreenWrapper() {

	var dawn, noon, sunset, midnight: GuiButton = null
	var p1hr, p2hr: GuiButton = null

	override def initGui(): Unit = {
		super.initGui()

		val guiL: Int = this.getX()
		val guiT: Int = this.getY()

		this.dawn = this.makeButton(guiL + 10, guiT + 10, 40, 20, "Dawn")
		this.noon = this.makeButton(guiL + 60, guiT + 10, 40, 20, "Noon")
		this.sunset = this.makeButton(guiL + 100, guiT + 10, 40, 20, "Sunset")
		this.midnight = this.makeButton(guiL + 140, guiT + 10, 40, 20, "Midnight")

	}

	def makeButton(x: Int, y: Int, w: Int, h: Int, text: String): GuiButton = {
		val button: GuiButton = new GuiButton(this.buttonList.size(), x, y, w, h, text)
		this.addButton(button)
		button
	}

	override def actionPerformed(button: GuiButton): Unit = {
		var setTime: Int = -1
		if (button.id == this.dawn.id) {
			setTime = 0 // day = 1000
		}
		else if (button.id == this.noon.id) {
			setTime = 6000
		}
		else if (button.id == this.sunset.id) {
			setTime = 12500 // night = 13000
		}
		else if (button.id == this.midnight.id) {
			setTime = 17500
		}
		if (setTime >= 0) {
			PacketHandler.sendToServer(WeepingAngels.MODID,
				new PacketSetTime(setTime)
			)
		}
		Minecraft.getMinecraft.displayGuiScreen(null)
	}

}
