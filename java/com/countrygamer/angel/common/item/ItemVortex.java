package com.countrygamer.angel.common.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

import com.countrygamer.angel.common.WeepingAngels;
import com.countrygamer.core.Base.common.item.ItemBase;
import com.countrygamer.countrygamercore.lib.CoreUtil;

public class ItemVortex extends ItemBase {

	public static String modeTag = "MODE";

	public ItemVortex(String modid, String name) {
		super(modid, name);
		this.setMaxStackSize(1);
	}

	public ItemStack onItemRightClick(ItemStack itemStack, World world,
			EntityPlayer player) {

		// prevents null pointers if player scrolls too fast
		if (itemStack.getItem() == this) {

			NBTTagCompound tagCom = itemStack.hasTagCompound() ? itemStack.getTagCompound() : new NBTTagCompound();
			boolean mode = itemStack.hasTagCompound() ? tagCom.getBoolean(ItemVortex.modeTag) : true;
			if (player.isSneaking()) {
				boolean newMode = !mode;

				tagCom.setBoolean(ItemVortex.modeTag, newMode);
				itemStack.setTagCompound(tagCom);

				String message = "The Vortex Manipulator is now in ";
				if (!world.isRemote)
					if (newMode) {
						player.addChatComponentMessage(new ChatComponentText(
								message + "RANDOM mode"));
					} else {
						player.addChatComponentMessage(new ChatComponentText(
								message + "CONTROLLED mode"));
					}
			} else {
				// TODO teleportation types
				if (mode) { // Random
					CoreUtil.teleportPlayer(player, 10, 100, player.posX,
							player.posZ, true, true);
				} else { // Controlled
					// Open GUI
					player.openGui(WeepingAngels.instance, 0, world,
							(int) player.posX, (int) player.posY,
							(int) player.posZ);
				}
			}

		}
		return itemStack;
	}

}