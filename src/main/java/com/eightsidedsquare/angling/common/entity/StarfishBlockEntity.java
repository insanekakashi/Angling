package com.eightsidedsquare.angling.common.entity;

import com.eightsidedsquare.angling.common.block.StarfishBlock;
import com.eightsidedsquare.angling.core.AnglingEntities;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.InstancedAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import java.awt.*;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class StarfishBlockEntity extends BlockEntity implements GeoBlockEntity {
    private static final RawAnimation DEAD = RawAnimation.begin().thenLoop("animation.starfish.dead");
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.starfish.idle");

    AnimatableInstanceCache factory = new InstancedAnimatableInstanceCache(this);
    private double randomRotation;
    private int color;
    private boolean rainbow;

    public StarfishBlockEntity(BlockPos pos, BlockState state) {
        super(AnglingEntities.STARFISH, pos, state);
        if(level != null) {
            randomRotation = level.random.nextDouble() * 360 - 180;
        }else {
            randomRotation = 0;
        }
        rainbow = false;
        setColor(0xffffff);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar registrar) {
        registrar.add(new AnimationController<>(this, "controller", 2, this::controller));
    }

    private PlayState controller(AnimationState<StarfishBlockEntity> event) {
        if(((StarfishBlock) getBlockState().getBlock()).isDead()) {
            event.getController().setAnimation(DEAD);
        } else {
            event.getController().setAnimation(IDLE);
        }
        return PlayState.CONTINUE;
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putDouble("RandomRotation", randomRotation);
        nbt.putInt("Color", color);
        nbt.putBoolean("Rainbow", rainbow);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        randomRotation = nbt.getDouble("RandomRotation");
        color = nbt.getInt("Color");
        rainbow = nbt.getBoolean("Rainbow");
    }

    public double getRandomRotation() {
        return randomRotation;
    }

    public void setRandomRotation(double randomRotation) {
        this.randomRotation = randomRotation;
    }

    public void setRainbow(boolean rainbow) {
        this.rainbow = rainbow;
    }

    public boolean isRainbow() {
        return this.rainbow;
    }

    public int getColor() {
        return color;
    }

    @SuppressWarnings("unused")
    public static int getColor(BlockState state, BlockAndTintGetter world, BlockPos pos, int i) {
        if(world != null && world.getBlockEntity(pos) instanceof StarfishBlockEntity entity) {
            return entity.isRainbow() ? getRainbowColor() : entity.getColor();
        }
        return 0xffffff;
    }

    public static int getRainbowColor() {
        if(Minecraft.getInstance().player != null) {
            long time = Minecraft.getInstance().player.tickCount;
            return Color.HSBtoRGB((time / 256f), 0.75f, 1f);
        }
        return 0xffffff;
    }

    @SuppressWarnings("unused")
    public static int getItemColor(ItemStack stack, int i) {
        CompoundTag nbt = BlockItem.getBlockEntityData(stack);
        if(nbt != null) {
            return nbt.getBoolean("Rainbow") ? getRainbowColor() : nbt.getInt("Color");
        }
        return 0xffffff;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public Vec3i getRotation() {
        BlockState state = getBlockState();
        return switch (state.getValue(BlockStateProperties.FACING)) {
            case NORTH -> new Vec3i(-90, 0, 0);
            case EAST -> new Vec3i(0, -90, -90);
            case SOUTH -> new Vec3i(90, 0, 180);
            case WEST -> new Vec3i(0, 90, 90);
            case UP -> Vec3i.ZERO;
            case DOWN -> new Vec3i(180, 180, 0);
        };
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }
}
