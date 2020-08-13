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

package one.oktw.galaxy.gui

import com.google.common.collect.MapMaker
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.runBlocking
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.network.ServerPlayerEntity
import java.util.concurrent.ConcurrentLinkedDeque

class GUISBackStackManager(private val player: ServerPlayerEntity) {
    private val stack = ConcurrentLinkedDeque<GUI>()

    companion object {
        private val managers = MapMaker().weakKeys().makeMap<ServerPlayerEntity, GUISBackStackManager>()

        fun openGUI(player: ServerPlayerEntity, gui: GUI) {
            managers.getOrPut(player) { GUISBackStackManager(player) }.open(gui)
        }
    }

    fun open(gui: GUI) {
        gui.onClose(this::closeCallback)
        stack.offerLast(gui)
        if (player.server.isOnThread) {
            player.openHandledScreen(gui)
        } else runBlocking(player.server.asCoroutineDispatcher()) {
            player.openHandledScreen(gui)
        }
    }

    private fun closeCallback(player: PlayerEntity) {
        if (player == this.player) {
            stack.pollLast()?.let(player::openHandledScreen)
        }
    }
}