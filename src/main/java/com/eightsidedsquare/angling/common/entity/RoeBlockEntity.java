package com.eightsidedsquare.angling.common.entity;

import com.eightsidedsquare.angling.common.entity.util.FishVariantInheritance;
import com.eightsidedsquare.angling.core.AnglingEntities;
import com.eightsidedsquare.angling.core.AnglingSounds;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachedBlockView;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachmentBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class RoeBlockEntity extends BlockEntity implements RenderAttachmentBlockEntity {

    int primaryColor;
    int secondaryColor;
    CompoundTag parentData;
    CompoundTag mateData;
    @Nullable
    String entityType;

    public RoeBlockEntity(BlockPos pos, BlockState state) {
        super(AnglingEntities.ROE, pos, state);
        setColors(0xffffff, 0xffffff);
        setEntityType(EntityType.COD);
        setParentsData(new CompoundTag(), new CompoundTag());
    }

    public void setParentsData(CompoundTag parentData, CompoundTag mateData) {
        this.parentData = parentData;
        this.mateData = mateData;
    }

    public void setColors(int primaryColor, int secondaryColor) {
        this.primaryColor = primaryColor;
        this.secondaryColor = secondaryColor;
    }

    public void setEntityType(EntityType<?> entityType) {
        this.entityType = BuiltInRegistries.ENTITY_TYPE.getKey(entityType).toString();
    }

    public Optional<EntityType<?>> getEntityType() {
        if(entityType == null)
            return Optional.empty();
        return EntityType.byString(entityType);
    }

    public void readFrom(ItemStack stack) {
        CompoundTag nbt = BlockItem.getBlockEntityData(stack);
        if(nbt != null) {
            setColors(nbt.getInt("PrimaryColor"), nbt.getInt("SecondaryColor"));
            if(nbt.contains("ParentData", Tag.TAG_COMPOUND) && nbt.contains("MateData", Tag.TAG_COMPOUND))
                setParentsData(nbt.getCompound("ParentData"), nbt.getCompound("MateData"));
        }
    }

    public static int getColor(BlockState state, @Nullable BlockAndTintGetter world, BlockPos pos, int tintIndex) {
        if(world != null && ((RenderAttachedBlockView) world).getBlockEntityRenderAttachment(pos) instanceof RoeBlockEntity entity) {
            return tintIndex == 0 ? entity.primaryColor : entity.secondaryColor;
        }
        return 0xffffff;
    }

    public void hatch(ServerLevel world) {
        RandomSource random = world.getRandom();
        int count = random.nextIntBetweenInclusive(2, 5);
        getEntityType().ifPresent(type -> {
            FishVariantInheritance inheritance = FishVariantInheritance.getVariantInheritance(type);
            for(int i = 0; i < count; i++) {
                FryEntity entity = AnglingEntities.FRY.create(world);
                if(entity != null) {
                    entity.setPersistenceRequired();
                    entity.setGrowUpTo(entityType);
                    entity.setColor(random.nextBoolean() ? primaryColor : secondaryColor);
                    entity.setPosRaw(worldPosition.getX() + 0.5d + random.nextGaussian() * 0.1d, worldPosition.getY() + 0.25d, worldPosition.getZ() + 0.5d + random.nextGaussian() * 0.1d);
                    entity.setYRot(random.nextFloat() * 360 - 180);
                    entity.setVariant(inheritance.getChild(parentData, mateData, world));
                    world.addFreshEntity(entity);
                }
                world.setBlockAndUpdate(worldPosition, Blocks.WATER.defaultBlockState());
                world.playSound(null, worldPosition, AnglingSounds.BLOCK_ROE_HATCH, SoundSource.BLOCKS, 1, 1);
                world.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, getBlockState()), worldPosition.getX() + 0.5d, worldPosition.getY(), worldPosition.getZ() + 0.5d, 20, 0.25d, 0.05d, 0.25d, 0);
            }
        });
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putInt("PrimaryColor", primaryColor);
        nbt.putInt("SecondaryColor", secondaryColor);
        nbt.put("ParentData", parentData);
        nbt.put("MateData", mateData);
        if(entityType != null)
            nbt.putString("EntityType", entityType);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);

        primaryColor = nbt.getInt("PrimaryColor");
        secondaryColor = nbt.getInt("SecondaryColor");
        if(nbt.contains("ParentData", Tag.TAG_COMPOUND) && nbt.contains("MateData", Tag.TAG_COMPOUND))
            setParentsData(nbt.getCompound("ParentData"), nbt.getCompound("MateData"));
        if(nbt.contains("EntityType", Tag.TAG_STRING))
            entityType = nbt.getString("EntityType");
    }

    public static int getItemColor(ItemStack stack, int tintIndex) {
        CompoundTag nbt = BlockItem.getBlockEntityData(stack);
        if(nbt != null) {
            int primaryColor = nbt.getInt("PrimaryColor");
            int secondaryColor = nbt.getInt("SecondaryColor");
            return tintIndex == 0 ? primaryColor : secondaryColor;
        }
        return 0xffffff;
    }

    @Override
    public @Nullable Object getRenderAttachmentData() {
        return this;
    }
}
