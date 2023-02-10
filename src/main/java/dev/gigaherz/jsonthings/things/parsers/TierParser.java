package dev.gigaherz.jsonthings.things.parsers;

import com.google.gson.JsonObject;
import dev.gigaherz.jsonthings.things.builders.BaseBuilder;
import dev.gigaherz.jsonthings.things.builders.TierBuilder;
import dev.gigaherz.jsonthings.util.Utils;
import dev.gigaherz.jsonthings.util.parse.JParse;
import dev.gigaherz.jsonthings.util.parse.value.Any;
import dev.gigaherz.jsonthings.util.parse.value.ArrayValue;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.TierSortingRegistry;
import net.minecraftforge.common.util.Lazy;
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class TierParser extends ThingParser<TierBuilder>
{
    public TierParser()
    {
        super(GSON, "item_tier");
    }

    @Override
    protected void finishLoadingInternal()
    {
        processAndConsumeErrors(getThingType(), getBuilders(), thing -> TierSortingRegistry.registerTier(thing.get(), thing.getRegistryName(), thing.getSortAfter(), thing.getSortBefore()), BaseBuilder::getRegistryName);
    }

    @Override
    public TierBuilder processThing(ResourceLocation key, JsonObject data, Consumer<TierBuilder> builderModification)
    {
        final TierBuilder builder = TierBuilder.begin(this, key);

        JParse.begin(data)
                .key("uses", val -> val.intValue().min(1).handle(builder::setUses))
                .key("speed", val -> val.floatValue().min(1).handle(builder::setSpeed))
                .key("attack_damage_bonus", val -> val.floatValue().min(1).handle(builder::setAttackDamageBonus))
                .key("enchantment_value", val -> val.intValue().min(1).handle(builder::setEnchantmentValue))
                .key("tag", val -> val.string().map(Utils::blockTag).handle(builder::setTag))
                .key("repair_ingredient", val -> val.map(TierParser::parseMiniIngredient).handle(builder::setRepairIngredient))
                .key("sort_after", val -> val.array().mapWhole(TierParser::parseDependencyList).handle(builder::setAfterDependencies))
                .key("sort_before", val -> val.array().mapWhole(TierParser::parseDependencyList).handle(builder::setBeforeDependencies));

        builderModification.accept(builder);

        return builder;
    }

    public static Supplier<Ingredient> parseMiniIngredient(Any any)
    {
        final MutableObject<Supplier<Ingredient>> out = new MutableObject<>();

        any.obj()
                .noKey("type", () -> new ThingParseException("Custom ingredients not supported yet. Please use an 'item' or 'tag' ingredient."))
                .mutex(List.of("item", "tag"), () -> new ThingParseException("Cannot have both 'tag' and 'item' in the ingredient at the same time."))
                .ifKey("tag", val -> val.string().map(Utils::itemTag).handle(tag -> out.setValue(Lazy.of(() -> Ingredient.of(tag)))))
                .ifKey("tag", val -> val.string().map(ResourceLocation::new).handle(item -> out.setValue(Lazy.of(() -> Ingredient.of(Utils.getItemOrCrash(item))))));

        return out.getValue();
    }

    private static List<Object> parseDependencyList(ArrayValue array)
    {
        return array.flatMap(entries -> entries.map(e -> (Object) e.string().getAsString()).toList());
    }
}
