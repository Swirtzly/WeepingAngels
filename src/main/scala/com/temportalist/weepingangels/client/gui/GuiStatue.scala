package com.temportalist.weepingangels.client.gui

import com.temportalist.origin.library.common.nethandler.PacketHandler
import com.temportalist.origin.wrapper.client.gui.GuiScreenWrapper
import com.temportalist.weepingangels.common.WeepingAngels
import com.temportalist.weepingangels.common.entity.EntityAngel
import com.temportalist.weepingangels.common.network.PacketModifyStatue
import com.temportalist.weepingangels.common.tile.TEStatue
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.{GuiButton, GuiTextField}
import net.minecraft.client.renderer.entity.RenderManager
import net.minecraft.client.renderer.{GlStateManager, OpenGlHelper, RenderHelper}
import net.minecraft.entity.EntityLivingBase
import net.minecraftforge.fml.relauncher.{Side, SideOnly}
import org.lwjgl.opengl.GL11

/**
 *
 *
 * @author TheTemportalist
 */
@SideOnly(Side.CLIENT)
class GuiStatue(val tileEntity: TEStatue) extends GuiScreenWrapper() {

	var facial: GuiButtonIterator = null
	var arms: GuiButtonIterator = null
	var rotationField: GuiTextField = null
	var corruptionField: GuiTextField = null
	// Variable used for rendering
	var angelEntity: EntityAngel = null

	val coords = Map(
		"face" -> Array[Int](30, 40),
		"arms" -> Array[Int](30, 80),
		"rot" -> Array[Int](40, 120, 100, 20),
		"corr" -> Array[Int](40, 160, 100, 20),
		"bkgd" -> Array[Int](200, 30, 150, 170),
		"angel" -> Array[Int](275, 190, 75)
	)

	this.setupGui("Edit Statue", null)

	override def initGui(): Unit = {
		super.initGui()

		var bID: Int = 0
		var coord: Array[Int] = null

		coord = this.coords.get("face").get
		this.facial = new GuiButtonIterator(bID, coord(0), coord(1), Array(
			"Calm", "Angry"
		))
		bID += 1
		this.buttonList.asInstanceOf[java.util.List[GuiButton]].add(this.facial)

		coord = this.coords.get("arms").get
		this.arms = new GuiButtonIterator(bID, coord(0), coord(1), Array(
			"Hiding", "Peaking", "Confident"
		))
		bID += 1
		this.buttonList.asInstanceOf[java.util.List[GuiButton]].add(this.arms)

		coord = this.coords.get("rot").get
		this.rotationField = new GuiTextField(
			0, this.fontRendererObj, coord(0), coord(1), coord(2), coord(3)
		)
		this.setupTextField(this.rotationField, 100)

		coord = this.coords.get("corr").get
		this.corruptionField = new GuiTextField(
			1, this.fontRendererObj, coord(0), coord(1), coord(2), coord(3)
		)
		this.setupTextField(this.corruptionField, 100)

		this.updateComponents()

	}

	def syncStatue(id: Int, value: Float): Unit = {
		PacketHandler.sync(WeepingAngels.MODID, new PacketModifyStatue(
			this.tileEntity.getPos, id, value
		))
	}

	override def mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int): Unit = {
		super.mouseClicked(mouseX, mouseY, mouseButton)
		if (this.facial.mousePressed(this.mc, mouseX, mouseY)) {
			this.facial.onPressed(mouseButton)
			this.syncStatue(1, this.facial.getIndex.asInstanceOf[Float])
		}
		if (this.arms.mousePressed(this.mc, mouseX, mouseY)) {
			this.arms.onPressed(mouseButton)
			this.syncStatue(2, this.arms.getIndex.asInstanceOf[Float])
		}
	}

	override def canKeyType(textField: GuiTextField, letter: Char, key: Int): Boolean = {
		if (key == 14) return true
		val id: Int = textField.getId
		if (id == this.rotationField.getId)
			Character.isDigit(letter) || letter == '.' || letter == '-'
		else if (id == this.corruptionField.getId)
			Character.isDigit(letter) || letter == '-'
		else true
	}

	override def onKeyTyped(textField: GuiTextField): Unit = {
		if (textField.getId == this.rotationField.getId) {
			this.syncStatue(3, this.parseFromField(this.rotationField.getText))
		}
		else if (textField.getId == this.corruptionField.getId) {
			this.syncStatue(4, this.parseFromField(this.corruptionField.getText).toInt)
		}
	}

	def parseFromField(text: String): Float = {
		var value: Float = 0.0F
		try {
			value = text.toFloat
		}
		catch {
			case e: NumberFormatException =>
		}
		value
	}

	def updateComponents(): Unit = {
		this.facial.updateIndexAndText(this.tileEntity.getFacialState)
		this.arms.updateIndexAndText(this.tileEntity.getArmState)
		this.rotationField.setText(this.tileEntity.getRotation + "")
		this.corruptionField.setText(this.tileEntity.getCorruption() + "")
	}

	override def doesGuiPauseGame(): Boolean = {
		false
	}

	override protected def drawGuiBackground(): Unit = {
		GL11.glPushMatrix()
		// todo move this to a common helper class
		HUDOverlay.renderBlackoutWithAlpha(0.7F, this.width, this.height)
		// Draw statue background
		val coord: Array[Int] = this.coords.get("bkgd").get
		HUDOverlay.renderBlackoutWithAlpha(1.0F, coord(0), coord(1), coord(2), coord(3))
		GL11.glPopMatrix()
	}

	override protected def drawGuiBackgroundLayer(mouseX: Int, mouseY: Int,
			renderPartialTicks: Float): Unit = {
		super.drawGuiBackgroundLayer(mouseX, mouseY, renderPartialTicks)

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F)
		val x: Int = 140 + 125
		val y: Int = 30 + 150

		if (this.angelEntity == null)
			this.angelEntity = new EntityAngel(this.mc.theWorld) {
				override def getCorruption(): Int = tileEntity.getCorruption()
			}
		this.angelEntity.setAngryState(this.tileEntity.getFacialState.asInstanceOf[Byte])
		this.angelEntity.setArmState(this.tileEntity.getArmState.asInstanceOf[Byte])
		// todo corruption, similar to testatuerenderer
		this.angelEntity.setYoungestAdult()

		val coord: Array[Int] = this.coords.get("angel").get
		this.drawStatue(
			coord(0), coord(1), coord(2),
			-this.tileEntity.getRotation, this.angelEntity
		)

	}

	override def drawTitle(x: Int, y: Int): Unit = {
		this.drawString(this.title,
			this.getX() + (this.getWidth() / 2) - (this.getStringWidth(this.title) / 2), 10, -1
		)
	}

	def drawStatue(x: Int, y: Int, scale: Int, rotation: Float, entity: EntityLivingBase): Unit = {

		GlStateManager.enableColorMaterial()

		GlStateManager.pushMatrix()
		// Move to position on screen
		GlStateManager.translate(x.asInstanceOf[Float], y.asInstanceOf[Float], 50.0F)
		// Scale
		GlStateManager.scale(
			(-scale).asInstanceOf[Float],
			scale.asInstanceOf[Float],
			scale.asInstanceOf[Float]
		)
		// Turn right side up
		GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F)
		// todo apply and revert? what effect does this have?
		GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F)
		RenderHelper.enableStandardItemLighting
		GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F)
		GlStateManager.rotate(10.0F, 1.0F, 0.0F, 0.0F)
		GlStateManager.rotate(rotation, 0.0F, 1.0F, 0.0F)
		GlStateManager.translate(0.0F, entity.getYOffset.asInstanceOf[Float], 0.0F)

		this.getRM.playerViewY = 180.0F
		this.getRM.renderEntityWithPosYaw(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F)
		GlStateManager.popMatrix()

		RenderHelper.disableStandardItemLighting

		GlStateManager.disableRescaleNormal()

		OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit)
		GlStateManager.disableTexture2D()
		OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit)

	}

	// todo move this to a generic access helper
	def getRM: RenderManager = Minecraft.getMinecraft.getRenderManager

}
