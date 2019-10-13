/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2019
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

package one.oktw.galaxy.gui

import net.minecraft.container.ContainerType
import net.minecraft.container.GenericContainer
import net.minecraft.container.SlotActionType
import net.minecraft.container.SlotActionType.*
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.BasicInventory
import net.minecraft.item.ItemStack

class TestContainer(playerInventory: PlayerInventory, syncId: Int) :
    GenericContainer(ContainerType.GENERIC_9X6, syncId, playerInventory, BasicInventory(9 * 6), 6) {
    private val guiItemCount = 6 * 9
    private val playerInventoryRange = guiItemCount..guiItemCount + 3 * 9
    private val playerHotBarRange = playerInventoryRange.last..playerInventoryRange.last + 9
    override fun onSlotClick(slot: Int, button: Int, action: SlotActionType, player: PlayerEntity): ItemStack? {
        println("slot: $slot, button: $button, action: $action")

        if (slot < 54 && slot != -999) return null

        return when (action) {
            PICKUP, SWAP, CLONE, THROW, QUICK_CRAFT -> super.onSlotClick(slot, button, action, player)
            QUICK_MOVE -> {
                if (slot in 0 until guiItemCount) return null

                var itemStack = ItemStack.EMPTY
                val inventorySlot = slotList[slot]

                if (inventorySlot != null && inventorySlot.hasStack()) {
                    val slotItemStack = inventorySlot.stack
                    itemStack = slotItemStack.copy()

                    if (slot in playerInventoryRange) {
                        if (!insertItem(slotItemStack, playerHotBarRange.first, playerHotBarRange.last, false)) return ItemStack.EMPTY
                    } else if (slot in playerHotBarRange) {
                        if (!insertItem(slotItemStack, playerInventoryRange.first, playerInventoryRange.last, false)) return ItemStack.EMPTY
                    }

                    // clean up empty slot
                    if (slotItemStack.isEmpty) {
                        inventorySlot.stack = ItemStack.EMPTY
                    } else {
                        inventorySlot.markDirty()
                    }
                }

                return itemStack
            }
            PICKUP_ALL -> { // Rewrite PICKUP_ALL only take from player inventory
                if (slot < 0) return null

                val cursorItemStack = player.inventory.cursorStack
                val clickSlot = slotList[slot]
                if (!cursorItemStack.isEmpty && (!clickSlot.hasStack() || !clickSlot.canTakeItems(player))) {
                    val step = if (button == 0) 1 else -1

                    for (tryTime in 0..1) {
                        var index = guiItemCount
                        while (index >= guiItemCount && index < slotList.size && cursorItemStack.count < cursorItemStack.maxCount) {
                            val scanSlot = slotList[index]
                            if (scanSlot.hasStack()
                                && canInsertItemIntoSlot(scanSlot, cursorItemStack, true)
                                && scanSlot.canTakeItems(player)
                                && canInsertIntoSlot(cursorItemStack, scanSlot)
                            ) {
                                val selectItemStack = scanSlot.stack
                                if (tryTime != 0 || selectItemStack.count != selectItemStack.maxCount) {
                                    val takeCount = (cursorItemStack.maxCount - cursorItemStack.count).coerceAtMost(selectItemStack.count)
                                    val selectItemStack2 = scanSlot.takeStack(takeCount)
                                    cursorItemStack.increment(takeCount)
                                    if (selectItemStack2.isEmpty) {
                                        scanSlot.stack = ItemStack.EMPTY
                                    }

                                    scanSlot.onTakeItem(player, selectItemStack2)
                                }
                            }
                            index += step
                        }
                    }
                }

                this.sendContentUpdates()

                cursorItemStack
            }
        }
    }
}
