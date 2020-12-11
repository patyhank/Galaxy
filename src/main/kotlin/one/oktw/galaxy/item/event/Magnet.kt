/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2020
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package one.oktw.galaxy.item.event

import net.minecraft.entity.EntityType
import net.minecraft.item.ItemStack
import net.minecraft.predicate.entity.EntityPredicates
import net.minecraft.server.network.ServerPlayerEntity
import one.oktw.galaxy.event.annotation.EventListener
import one.oktw.galaxy.event.type.PlayerInteractItemEvent
import one.oktw.galaxy.item.Tool
import one.oktw.galaxy.item.type.ToolType

class Magnet {
    private val magnetItemStack = Tool(ToolType.MAGNET).createItemStack()
    private val activate = 1010201
    private val inactivate = 1010200

    @EventListener(sync = true)
    fun onInteract(event: PlayerInteractItemEvent) {
        val player = event.player
        //first interact with main hand magnet
        if (isMagnet(player.mainHandStack)) {
            when (player.isSneaking) {
                true -> magnetSwitch(player.mainHandStack)
                false -> doAttract(player)
            }
        } else if (isMagnet(player.offHandStack)) {
            when (player.isSneaking) {
                true -> magnetSwitch(player.offHandStack)
                false -> doAttract(player)
            }
        }
    }

    private fun isMagnet(itemStack: ItemStack): Boolean {
        val activatedMagnet = Tool(ToolType.MAGNET).createItemStack().apply {
            this.tag!!.remove("CustomModelData")
            this.tag!!.putInt("CustomModelData", activate)
        }

        return (ItemStack.areItemsEqual(magnetItemStack, itemStack) && ItemStack.areTagsEqual(magnetItemStack, itemStack)) ||
            (ItemStack.areItemsEqual(activatedMagnet, itemStack) && ItemStack.areTagsEqual(activatedMagnet, itemStack))
    }

    private fun magnetSwitch(itemStack: ItemStack) {
        val itemTag = itemStack.tag ?: return
        when (itemTag.getInt("CustomModelData")) {
            activate -> {
                itemStack.tag!!.remove("CustomModelData")
                itemStack.tag!!.putInt("CustomModelData", inactivate)
            }
            inactivate -> {
                itemStack.tag!!.remove("CustomModelData")
                itemStack.tag!!.putInt("CustomModelData", activate)
            }
        }
    }

    private fun doAttract(player: ServerPlayerEntity) {
        val world = player.serverWorld
        val itemList = world.getEntitiesByType(EntityType.ITEM, player.boundingBox.expand(8.0)!!, EntityPredicates.VALID_ENTITY)

        for (item in itemList) {
            item.teleport(player.pos.x, player.pos.y, player.pos.z)
            item.setPickupDelay(0)
        }
    }
}
