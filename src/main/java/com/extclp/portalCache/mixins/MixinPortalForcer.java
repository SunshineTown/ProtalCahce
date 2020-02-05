package com.extclp.portalCache.mixins;

import com.extclp.portalCache.Constant;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.*;
import net.minecraft.village.PointOfInterestStorage;
import net.minecraft.world.PortalForcer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.lang.reflect.Constructor;
import java.util.Comparator;
import java.util.Map;

@Mixin(PortalForcer.class)
public class MixinPortalForcer {

    @Shadow
    @Final
    private ServerWorld world;

    @Redirect(method = "getPortal", at =@At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"))
    public Object getPortal(Map<Object, Object> map, Object key, BlockPos pos, Vec3d vec3d, Direction direction, double x, double y, boolean canActivate){
        java.lang.Object o = map.get(key);
        if(o != null){
            return o;
        }
        ChunkPos.stream(new ChunkPos(pos), 4).forEach(chunkPos -> {
            world.getChunk(chunkPos.x, chunkPos.z);
        });
        return world.getPointOfInterestStorage()
                .get(pointOfInterestType -> pointOfInterestType == Constant.nether_portal,
                        pos, 128, PointOfInterestStorage.OccupationStatus.ANY)
                .filter(pointOfInterest -> pointOfInterest.getPos().getY() < world.getEffectiveHeight())
                .min(Comparator.comparingDouble(p -> p.getPos().getSquaredDistance(pos)))
                .map(pointOfInterest -> {
                    BlockPos blockPos = pointOfInterest.getPos();
                    ColumnPos columnPos = new ColumnPos(blockPos);
                    this.world.method_14178().addTicket(ChunkTicketType.PORTAL, new ChunkPos(blockPos), 3, columnPos);
                    Constructor<?> constructor = PortalForcer.class.getDeclaredClasses()[0].getConstructors()[0];
                    constructor.setAccessible(true);
                    java.lang.Object ticketInfo = null;
                    try {
                        ticketInfo = constructor.newInstance(blockPos, world.getTime());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    map.put(columnPos, ticketInfo);
                    return ticketInfo;
                }).orElse(null);
    }
}