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

package one.oktw.galaxy.command.commands

import com.mojang.brigadier.CommandDispatcher
import net.minecraft.entity.EntityType
import net.minecraft.entity.SpawnReason
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import one.oktw.galaxy.command.Command
import one.oktw.galaxy.mixin.interfaces.BeeBalloon

class Test : Command {
    override fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(
            CommandManager.literal("test").executes { context -> execute(context.source) }
        )
    }

    private fun execute(source: ServerCommandSource): Int {
        val player = source.player ?: return com.mojang.brigadier.Command.SINGLE_SUCCESS

        val bee = EntityType.BEE.create(player.serverWorld, null, null, null, player.blockPos.add(0.0, 3.0, 0.0), SpawnReason.COMMAND, false, false)
        (bee as BeeBalloon).toBalloon()
        bee.attachLeash(player, true)
        player.world.spawnEntity(bee)

        return com.mojang.brigadier.Command.SINGLE_SUCCESS
    }
}
