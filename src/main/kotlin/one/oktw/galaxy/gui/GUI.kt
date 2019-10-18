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

import net.minecraft.container.Container
import net.minecraft.container.ContainerType
import net.minecraft.container.ContainerType.*
import net.minecraft.container.NameableContainerProvider
import net.minecraft.container.SlotActionType
import net.minecraft.container.SlotActionType.*
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.BasicInventory
import net.minecraft.item.ItemStack
import net.minecraft.text.LiteralText
import net.minecraft.text.Text

class GUI private constructor(private val type: ContainerType<out Container>, size: Int, private val title: Text) : NameableContainerProvider {
    companion object {
        private val genericContainerType = listOf(GENERIC_9X1, GENERIC_9X2, GENERIC_9X3, GENERIC_9X4, GENERIC_9X5, GENERIC_9X6)
    }

    private val inventory = BasicInventory(size)
    private val functionBindings = HashMap<Int, (GUI, ItemStack) -> Any>()
    private val rangeFunctionBindings = HashMap<Pair<IntRange, IntRange>, (GUI, ItemStack) -> Any>()
    private var allowSlotIndex = intArrayOf()

    override fun getDisplayName() = title

    override fun createMenu(syncId: Int, playerInventory: PlayerInventory, player: PlayerEntity): Container {
        return GenericContainer(syncId, playerInventory)
    }

    fun addBinding(index: Int, function: (GUI, ItemStack) -> Any) {
        if (index !in 0 until inventory.invSize) throw IndexOutOfBoundsException("Binding index out of inventory range")

        functionBindings[index] = function
    }

    fun addBinding(x: Int, y: Int, function: (GUI, ItemStack) -> Any) {
        if (!checkRange(x, y)) throw IndexOutOfBoundsException("Binding index out of inventory range")

        functionBindings[x * y] = function
    }

    //
    // Useless?
    //
//    fun addBinding(indexRange: IntRange, function: (GUI, ItemStack) -> Any) {
//        if (indexRange.first < 0 || indexRange.last >= inventory.invSize) throw IndexOutOfBoundsException("Binding index out of inventory range")
//
//        rangeFunctionBindings[indexRange] = function
//    }

    fun addBinding(xRange: IntRange, yRange: IntRange, function: (GUI, ItemStack) -> Any) {
        if (!checkRange(xRange.first, yRange.first) || !checkRange(
                xRange.last,
                yRange.last
            )
        ) throw IndexOutOfBoundsException("Binding index out of inventory range")

        rangeFunctionBindings[Pair(xRange, yRange)] = function
    }

    private fun checkRange(x: Int, y: Int): Boolean {
        return when (type) {
            in genericContainerType -> x in 0..9 && y in 0..6
            GENERIC_3X3 -> x in 0..3 && y in 0..3
            HOPPER -> x in 0..5 && y in 0..1
            else -> x * y in 0 until inventory.invSize
        }
    }

    open class Builder(private val type: ContainerType<out Container>) {
        private var size: Int = 0
        private var title: Text = LiteralText("")

        init {
            when (type) {
                GENERIC_9X1 -> size = 9
                GENERIC_9X2 -> size = 9 * 2
                GENERIC_9X3 -> size = 9 * 3
                GENERIC_9X4 -> size = 9 * 4
                GENERIC_9X5 -> size = 9 * 5
                GENERIC_9X6 -> size = 9 * 6
                GENERIC_3X3 -> size = 9
                HOPPER -> size = 5
            }
        }

        fun setTitle(title: Text) {
            this.title = title
        }

        fun build(): GUI {
            return GUI(type, size, title)
        }
    }

    private inner class GenericContainer(syncId: Int, playerInventory: PlayerInventory) :
        net.minecraft.container.GenericContainer(type, syncId, playerInventory, inventory, 6) {
        private val playerInventoryRange = inventory.invSize until inventory.invSize + 3 * 9
        private val playerHotBarRange = playerInventoryRange.last + 1..playerInventoryRange.last + 1 + 9

        override fun onSlotClick(slot: Int, button: Int, action: SlotActionType, player: PlayerEntity): ItemStack? {
            if (slot < inventory.invSize && slot != -999 && slot !in allowSlotIndex) {
                if (action == QUICK_CRAFT) endQuickCraft()
                return null
            }

            return when (action) {
                PICKUP, SWAP, CLONE, THROW, QUICK_CRAFT -> super.onSlotClick(slot, button, action, player)
                QUICK_MOVE -> {
                    if (slot in 0 until inventory.invSize && slot !in allowSlotIndex) return null

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
                            var index = inventory.invSize
                            while (index >= inventory.invSize && index < slotList.size && cursorItemStack.count < cursorItemStack.maxCount) {
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

    private inner class Generic3x3Container(syncId: Int, playerInventory: PlayerInventory) :
        net.minecraft.container.Generic3x3Container(syncId, playerInventory, inventory) {
        // TODO
    }

    private inner class HopperContainer(syncId: Int, playerInventory: PlayerInventory) :
        net.minecraft.container.HopperContainer(syncId, playerInventory, inventory) {
        // TODO
    }
}
