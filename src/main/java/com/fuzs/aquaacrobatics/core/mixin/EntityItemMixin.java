package com.fuzs.aquaacrobatics.core.mixin;

import com.fuzs.aquaacrobatics.config.ConfigHandler;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Allows items to float like post-1.13.
 */
@Mixin(EntityItem.class)
public abstract class EntityItemMixin extends Entity {
    public EntityItemMixin(World p_i1582_1_) {
        super(p_i1582_1_);
    }

    private void applyFloatMotion() {
        if (this.motionY < (double)0.06F) {
            this.motionY += (double)5.0E-4F;
        }
        this.motionX *= 0.99F;
        this.motionZ *= 0.99F;
    }
    
    @Redirect(method = "onUpdate", at = @At(value="INVOKE", target = "Lnet/minecraft/entity/item/EntityItem;hasNoGravity()Z", ordinal = 0), expect = 1, require = 0)
    private boolean applyFloatMotionIfInWater(EntityItem entityItem) {
        double eyePosition = this.posY + (double)this.getEyeHeight();
        BlockPos eyeBlockPos = new BlockPos(this.posX, eyePosition, this.posZ);
        IBlockState state = this.world.getBlockState(eyeBlockPos);
        if(state.getMaterial() == Material.WATER && state.getBlock() instanceof BlockLiquid) {
            float thresholdHeight = eyeBlockPos.getY() + BlockLiquid.getBlockLiquidHeight(state, this.world, eyeBlockPos) + (1f/9f);
            if(eyePosition < thresholdHeight) {
                applyFloatMotion();
                return true;
            }
        }
        return entityItem.hasNoGravity();
    }
}
