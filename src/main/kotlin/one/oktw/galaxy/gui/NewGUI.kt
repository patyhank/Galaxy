/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2021
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

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.screen.slot.Slot
import net.minecraft.screen.slot.SlotActionType

class NewGUI(syncId: Int, private val inventory: Inventory, private val playerInventory: PlayerInventory) :
    ScreenHandler(ScreenHandlerType.GENERIC_9X1, syncId) {

    init {
        checkSize(inventory, 1)
        inventory.onOpen(playerInventory.player)

        // Block inventory
        for (i in 0..8) addSlot(inventory, 0)

        //Player inventory
        for (i in 9..35) addSlot(playerInventory, i)
        // Player HotBar
        for (i in 0..8) addSlot(playerInventory, i)
    }

    override fun canUse(player: PlayerEntity) = inventory.canPlayerUse(player)

    override fun onSlotClick(index: Int, button: Int, actionType: SlotActionType, playerEntity: PlayerEntity): ItemStack {

        return super.onSlotClick(index, button, actionType, playerEntity)
    }

    override fun close(player: PlayerEntity?) {
        super.close(player)
        inventory.onClose(player)
    }

    private fun addSlot(inventory: Inventory, index: Int) {
        addSlot(Slot(inventory, index, 0, 0))
    }
}
