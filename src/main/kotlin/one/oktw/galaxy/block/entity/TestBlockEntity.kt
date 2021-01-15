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

package one.oktw.galaxy.block.entity

import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventories
import net.minecraft.inventory.SidedInventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.screen.ScreenHandler
import net.minecraft.text.LiteralText
import net.minecraft.text.Text
import net.minecraft.util.Tickable
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.Direction
import one.oktw.galaxy.gui.NewGUI


class TestBlockEntity : BlockEntity(BlockEntityTypes.Test), Tickable, SidedInventory, NamedScreenHandlerFactory {
    private val inventory = DefaultedList.ofSize(1, ItemStack.EMPTY)

    override fun tick() {
//        logger.info("TICK!!!")
    }

    override fun fromTag(state: BlockState, tag: CompoundTag) {
        super.fromTag(state, tag)
        Inventories.fromTag(tag, inventory)
    }

    override fun toTag(tag: CompoundTag): CompoundTag {
        super.toTag(tag)
        Inventories.toTag(tag, inventory)
        return tag
    }

    override fun clear() = inventory.clear()

    override fun size() = inventory.size

    override fun isEmpty() = inventory.all(ItemStack::isEmpty)

    override fun getStack(slot: Int) = inventory.get(slot)

    override fun removeStack(slot: Int, amount: Int): ItemStack = Inventories.splitStack(this.inventory, slot, amount)

    override fun removeStack(slot: Int): ItemStack = Inventories.removeStack(this.inventory, slot)

    override fun setStack(slot: Int, stack: ItemStack) {
        this.inventory[slot] = stack
        if (!stack.isEmpty && stack.count > this.maxCountPerStack) {
            stack.count = this.maxCountPerStack
        }
    }

    override fun canPlayerUse(player: PlayerEntity): Boolean {
        return if (world!!.getBlockEntity(pos) !== this) {
            false
        } else {
            player.squaredDistanceTo(pos.x.toDouble() + 0.5, pos.y.toDouble() + 0.5, pos.z.toDouble() + 0.5) <= 64.0
        }
    }

    override fun getAvailableSlots(side: Direction) = intArrayOf(0)

    override fun canInsert(slot: Int, stack: ItemStack, dir: Direction?): Boolean {
        return true
    }

    override fun canExtract(slot: Int, stack: ItemStack, dir: Direction): Boolean {
        return true
    }

    override fun createMenu(syncId: Int, inv: PlayerInventory, player: PlayerEntity): ScreenHandler {
        return NewGUI(syncId, this, inv)
    }

    override fun getDisplayName(): Text {
        return LiteralText.EMPTY
    }
}
