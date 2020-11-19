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
package one.oktw.galaxy.entity.ai

import net.minecraft.entity.ai.goal.Goal
import net.minecraft.entity.mob.MobEntity
import java.util.*

class BalloonGoal(private val entity: MobEntity) : Goal() {
    init {
        controls = EnumSet.of(Control.MOVE)
    }

    override fun canStart(): Boolean {
        return entity.holdingEntity != null
    }

    override fun tick() {
        val target = entity.holdingEntity
        if (target != null) {
            val pos = target.pos.add(0.0, 3.0, 0.0)
            if (!entity.navigation.isFollowingPath) {
                entity.navigation.startMovingTo(pos.x, pos.y, pos.z, 3.0)
            }
        }
    }
}
