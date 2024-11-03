package dev.gigaherz.jsonthings.things.parsers;

import com.google.gson.JsonObject;
import dev.gigaherz.jsonthings.things.ThingRegistries;
import dev.gigaherz.jsonthings.things.builders.BaseBuilder;
import dev.gigaherz.jsonthings.things.builders.ToolMaterialBuilder;
import dev.gigaherz.jsonthings.util.Utils;
import dev.gigaherz.jsonthings.util.parse.JParse;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ToolMaterial;

import java.util.function.Consumer;

public class ToolMaterialParser extends ThingParser<ToolMaterial, ToolMaterialBuilder>
{
    public ToolMaterialParser()
    {
        super(GSON, "item_tier");
    }

    @Override
    protected void finishLoadingInternal()
    {
        processAndConsumeErrors(getThingType(), getBuilders(), thing -> Registry.register(ThingRegistries.TOOL_MATERIAL, thing.getRegistryName(), thing.get()), BaseBuilder::getRegistryName);
    }

    @Override
    public ToolMaterialBuilder processThing(ResourceLocation key, JsonObject data, Consumer<ToolMaterialBuilder> builderModification)
    {
        final ToolMaterialBuilder builder = ToolMaterialBuilder.begin(this, key);

        JParse.begin(data)
                .key("uses", val -> val.intValue().min(1).handle(builder::setUses))
                .key("speed", val -> val.floatValue().min(1).handle(builder::setSpeed))
                .key("attack_damage_bonus", val -> val.floatValue().min(1).handle(builder::setAttackDamageBonus))
                .key("enchantment_value", val -> val.intValue().min(1).handle(builder::setEnchantmentValue))
                .key("tag", val -> val.string().map(Utils::blockTag).handle(builder::setTag))
                .key("repair_ingredient", val -> val.string().map(Utils::itemTag).handle(builder::setRepairIngredient));

        builderModification.accept(builder);

        return builder;
    }
}
