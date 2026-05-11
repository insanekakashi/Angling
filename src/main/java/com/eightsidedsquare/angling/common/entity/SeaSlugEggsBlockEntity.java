package com.eightsidedsquare.angling.common.entity;

import com.eightsidedsquare.angling.common.entity.util.SeaSlugColor;
import com.eightsidedsquare.angling.common.entity.util.SeaSlugPattern;
import com.eightsidedsquare.angling.core.AnglingEntities;
import com.eightsidedsquare.angling.core.AnglingSounds;
import com.eightsidedsquare.angling.core.AnglingUtil;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachedBlockView;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachmentBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class SeaSlugEggsBlockEntity extends BlockEntity implements RenderAttachmentBlockEntity {

    int color;
    CompoundTag parentData;
    CompoundTag mateData;

    public SeaSlugEggsBlockEntity(BlockPos pos, BlockState state) {
        super(AnglingEntities.SEA_SLUG_EGGS, pos, state);
        setColor(SeaSlugColor.IVORY);
        setParentsData(new CompoundTag(), new CompoundTag());
    }

    public void setParentsData(CompoundTag parentData, CompoundTag mateData) {
        this.parentData = parentData;
        this.mateData = mateData;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setColor(SeaSlugColor color) {
        this.color = color.color();
    }

    public static int getColor(BlockState state, @Nullable BlockAndTintGetter world, BlockPos pos, int tintIndex) {
        if(world != null && ((RenderAttachedBlockView) world).getBlockEntityRenderAttachment(pos) instanceof SeaSlugEggsBlockEntity entity) {
            return entity.color;
        }
        return 0xffffff;
    }

    public void readFrom(ItemStack stack) {
        CompoundTag nbt = BlockItem.getBlockEntityData(stack);
        if(nbt != null) {
            setColor(nbt.getInt("Color"));
            if(nbt.contains("ParentData", Tag.TAG_COMPOUND) && nbt.contains("MateData", Tag.TAG_COMPOUND))
                setParentsData(nbt.getCompound("ParentData"), nbt.getCompound("MateData"));
        }
    }

    public static int getItemColor(ItemStack stack, int tintIndex) {
        CompoundTag nbt = BlockItem.getBlockEntityData(stack);
        if(nbt != null) {
            return nbt.getInt("Color");
        }
        return 0xffffff;
    }

    private SeaSlugColor pickColor(ServerLevel world, RandomSource random, boolean base) {
        String key = base ? "BaseColor" : "PatternColor";
        if(random.nextInt(16) == 0) {
            TagKey<SeaSlugColor> tag = base ? SeaSlugColor.Tag.BASE_COLORS : SeaSlugColor.Tag.PATTERN_COLORS;
            return AnglingUtil.getRandomTagValue(world, tag, random);
        }
        return SeaSlugColor.fromId((random.nextBoolean() ? parentData : mateData).getString(key));
    }

    private SeaSlugPattern pickPattern(ServerLevel world, RandomSource random) {
        if(random.nextInt(16) == 0) {
            return AnglingUtil.getRandomTagValue(world, SeaSlugPattern.Tag.NATURAL_PATTERNS, random);
        }
        return SeaSlugPattern.fromId((random.nextBoolean() ? parentData : mateData).getString("Pattern"));
    }

    public void hatch(ServerLevel world) {
        RandomSource random = world.getRandom();
        int count = random.nextIntBetweenInclusive(1, 3);
        if(mateData != null && parentData != null) {
            for (int i = 0; i < count; i++) {
                SeaSlugEntity entity = AnglingEntities.SEA_SLUG.create(world);
                if (entity != null) {
                    entity.setBaseColor(pickColor(world, random, true));
                    entity.setPatternColor(pickColor(world, random, false));
                    entity.setPattern(pickPattern(world, random));
                    entity.setBioluminescent((random.nextBoolean() ? parentData : mateData).getBoolean("Bioluminescent"));
                    entity.setLoveCooldown(3000);
                    entity.setYBodyRot(random.nextIntBetweenInclusive(-180, 180));
                    entity.setYHeadRot(entity.yBodyRot);
                    entity.setPosRaw(worldPosition.getX() + 0.5d, worldPosition.getY() + 0.1d, worldPosition.getZ() + 0.5d);
                    entity.setFromBucket(true);
                    world.addFreshEntity(entity);
                }
            }
        }
        world.setBlockAndUpdate(worldPosition, Blocks.WATER.defaultBlockState());
        world.playSound(null, worldPosition, AnglingSounds.BLOCK_SEA_SLUG_EGGS_HATCH, SoundSource.BLOCKS, 1, 1);
        world.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, getBlockState()), worldPosition.getX() + 0.5d, worldPosition.getY(), worldPosition.getZ() + 0.5d, 20, 0.25d, 0.05d, 0.25d, 0);
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
        nbt.putInt("Color", color);
        nbt.put("ParentData", parentData);
        nbt.put("MateData", mateData);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);

        color = nbt.getInt("Color");
        if(nbt.contains("ParentData", Tag.TAG_COMPOUND) && nbt.contains("MateData", Tag.TAG_COMPOUND))
            setParentsData(nbt.getCompound("ParentData"), nbt.getCompound("MateData"));
    }

    @Override
    public @Nullable Object getRenderAttachmentData() {
        return this;
    }
}
