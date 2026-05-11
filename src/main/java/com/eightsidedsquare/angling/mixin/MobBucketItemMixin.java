package com.eightsidedsquare.angling.mixin;

import com.eightsidedsquare.angling.common.entity.util.CrabVariant;
import com.eightsidedsquare.angling.common.entity.util.SeaSlugColor;
import com.eightsidedsquare.angling.common.entity.util.SeaSlugPattern;
import com.eightsidedsquare.angling.common.entity.util.SunfishVariant;
import com.eightsidedsquare.angling.core.AnglingEntities;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MobBucketItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

@Mixin(MobBucketItem.class)
public abstract class MobBucketItemMixin {

    @Shadow @Final
    private EntityType<?> type;

    @Inject(method = "appendHoverText", at = @At("HEAD"))
    public void appendHoverText(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag context, CallbackInfo ci) {
        CompoundTag nbt = stack.getTag();
        ChatFormatting[] formatting = new ChatFormatting[]{ChatFormatting.ITALIC, ChatFormatting.GRAY};
        if(nbt != null) {
            if (type.equals(AnglingEntities.SUNFISH)) {
                if (nbt.contains("BucketVariantTag", Tag.TAG_STRING)) {
                    SunfishVariant variant = SunfishVariant.fromId(nbt.getString("BucketVariantTag"));
                    tooltip.add(Component.translatable(variant.getTranslationKey()).withStyle(formatting));
                }
            }else if(type.equals(AnglingEntities.SEA_SLUG)) {
                if (nbt.contains("Pattern", Tag.TAG_STRING)) {
                    MutableComponent text = Component.translatable(SeaSlugColor.fromId(nbt.getString("BaseColor")).getTranslationKey());
                    if (!SeaSlugPattern.fromId(nbt.getString("Pattern")).equals(SeaSlugPattern.NONE)) {
                        tooltip.add(Component.translatable(SeaSlugPattern.fromId(nbt.getString("Pattern")).getTranslationKey()).withStyle(formatting));
                        if (!nbt.getString("BaseColor").equals(nbt.getString("PatternColor")))
                            text.append(", ").append(Component.translatable(SeaSlugColor.fromId(nbt.getString("PatternColor")).getTranslationKey()));
                    }
                    tooltip.add(text.withStyle(formatting));
                    if (nbt.getBoolean("Bioluminescent"))
                        tooltip.add(Component.translatable("item.angling.sea_slug_bucket.bioluminescent").withStyle(formatting));
                }
            }else if(type.equals(AnglingEntities.CRAB)) {
                if(nbt.contains("Variant", Tag.TAG_STRING)) {
                    CrabVariant variant = CrabVariant.fromId(nbt.getString("Variant"));
                    tooltip.add(Component.translatable(variant.getTranslationKey()).withStyle(formatting));
                }
            }
        }
    }
}
