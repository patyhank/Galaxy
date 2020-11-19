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

package one.oktw.galaxy.mixin.tweak;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.world.World;
import one.oktw.galaxy.entity.ai.BalloonGoal;
import one.oktw.galaxy.mixin.accessor.GoalSelectorAccessor;
import one.oktw.galaxy.mixin.interfaces.BeeBalloon;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BeeEntity.class)
public abstract class BeeToBalloon extends AnimalEntity implements BeeBalloon {
    protected BeeToBalloon(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void toBalloon() {
        var goals = ((GoalSelectorAccessor) goalSelector).getGoals();
        var targetGoals = ((GoalSelectorAccessor) targetSelector).getGoals();
        goals.stream().filter(PrioritizedGoal::isRunning).forEach(PrioritizedGoal::stop);
        goals.clear();
        targetGoals.stream().filter(PrioritizedGoal::isRunning).forEach(PrioritizedGoal::stop);
        targetGoals.clear();
        goalSelector.add(0, new BalloonGoal(this));
    }
}
