package com.extclp.portalCache.mixins;

import com.extclp.portalCache.Constant;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.sound.SoundEvent;
import net.minecraft.village.PointOfInterestType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Set;

@Mixin(PointOfInterestType.class)
public abstract class MixinPointOfInterestType {

    @Shadow
    private static PointOfInterestType register(String id, Set<BlockState> set, int i, SoundEvent soundEvent, int j) {
        return null;
    }

    @Shadow
    private static Set<BlockState> getAllStatesOf(Block block) {
        return null;
    }

    static {
        Constant.nether_portal = register("nether_portal", getAllStatesOf(Blocks.NETHER_PORTAL),1, null, 1);
    }
}

