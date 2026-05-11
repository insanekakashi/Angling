package com.eightsidedsquare.angling.core;

import static com.eightsidedsquare.angling.core.AnglingMod.MOD_ID;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.resources.ResourceLocation;

public class AnglingCriteria {

    public static final PlayerTrigger TRADED_WITH_PELICAN = CriteriaTriggers.register(new PlayerTrigger(new ResourceLocation(MOD_ID, "traded_with_pelican")));

    public static void init() {}

}
