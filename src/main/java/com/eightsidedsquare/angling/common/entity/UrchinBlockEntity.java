package com.eightsidedsquare.angling.common.entity;

import com.eightsidedsquare.angling.core.AnglingEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.InstancedAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;


public class UrchinBlockEntity extends BlockEntity implements GeoBlockEntity {
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.urchin.idle");

    private ItemStack hat = ItemStack.EMPTY;
    AnimatableInstanceCache factory = new InstancedAnimatableInstanceCache(this);

    public UrchinBlockEntity(BlockPos pos, BlockState state) {
        super(AnglingEntities.URCHIN, pos, state);
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        nbt.put("Hat", (hat != null ? hat : ItemStack.EMPTY).save(new CompoundTag()));
    }

    @Override
    public void load(CompoundTag nbt) {
        hat = nbt.contains("Hat", Tag.TAG_COMPOUND) ? ItemStack.of(nbt.getCompound("Hat")) : ItemStack.EMPTY;
    }

    public ItemStack getHat() {
        return hat;
    }

    public void setHat(ItemStack hat) {
        this.hat = hat;
    }

    public void update() {
        setChanged();
        if(level != null)
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar registrar) {
        registrar.add(new AnimationController<>(this, "controller", 2, this::controller));
    }

    private PlayState controller(AnimationState<UrchinBlockEntity> event) {
        event.getController().setAnimation(IDLE);
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }
}
