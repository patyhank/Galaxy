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
import net.minecraft.container.GenericContainer
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.LiteralText
import one.oktw.galaxy.gui.inventory.ReadOnlyInventory

class TestGUI : GUI(LiteralText("Test")) {
    private val inventory = ReadOnlyInventory(9 * 6)

    init {
        inventory
    }

    override fun createMenu(syncId: Int, playerInventory: PlayerInventory, player: PlayerEntity): Container {
        return GenericContainer.createGeneric9x6(syncId, playerInventory, inventory)
    }
}
