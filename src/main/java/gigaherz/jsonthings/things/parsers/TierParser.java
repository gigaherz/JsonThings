package gigaherz.jsonthings.things.parsers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import gigaherz.jsonthings.things.builders.TierBuilder;
import gigaherz.jsonthings.util.Utils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.TierSortingRegistry;
import net.minecraftforge.common.util.Lazy;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class TierParser extends ThingParser<TierBuilder>
{
    public TierParser()
    {
        super(GSON, "tier");
    }

    @Override
    public void finishLoading()
    {
        getBuilders().forEach(thing -> TierSortingRegistry.registerTier(thing.build(), thing.getRegistryName(), thing.getSortAfter(), thing.getSortBefore()));
    }

    @Override
    public TierBuilder processThing(ResourceLocation key, JsonObject data)
    {
        TierBuilder builder = TierBuilder.begin(key);

        int uses = GsonHelper.getAsInt(data, "uses");
        if (uses > 0)
        {
            builder = builder.withUses(uses);
        }
        else
        {
            throw new RuntimeException("'uses' must be positive and not zero.");
        }

        float speed = GsonHelper.getAsFloat(data, "speed");
        if (speed > 0)
        {
            builder = builder.withSpeed(speed);
        }
        else
        {
            throw new RuntimeException("'speed' must be positive and not zero.");
        }

        float attachDamageBonus = GsonHelper.getAsFloat(data, "attack_damage_bonus");
        if (attachDamageBonus > 0)
        {
            builder = builder.withAttackDamageBonus(attachDamageBonus);
        }
        else
        {
            throw new RuntimeException("'attack_damage_bonus' must be positive and not zero.");
        }

        int enchantmentValue = GsonHelper.getAsInt(data, "enchantment_value");
        if (enchantmentValue > 0)
        {
            builder = builder.withEnchantmentValue(enchantmentValue);
        }
        else
        {
            throw new RuntimeException("'enchantment_value' must be positive and not zero.");
        }

        builder = builder.withTag(BlockTags.bind(GsonHelper.getAsString(data, "tag")));

        if (data.has("repair_ingredient"))
        {
            builder = builder.withRepairIngredient(parseMiniIngredient(data.get("repair_ingredient")));
        }
        else
        {
            throw new RuntimeException("Missing required 'repair_ingredient'.");
        }

        if (data.has("sort_after"))
        {
            builder = builder.withAfterDependencies(parseStringList(GsonHelper.getAsJsonArray(data, "sort_after")));
        }

        if (data.has("sort_before"))
        {
            builder = builder.withBeforeDependencies(parseStringList(GsonHelper.getAsJsonArray(data, "sort_before")));
        }

        return builder;
    }

    private List<Object> parseStringList(JsonArray array)
    {
        List<Object> l = new ArrayList<>();
        for(JsonElement e : array)
        {
            l.add(e.getAsString());
        }
        return l;
    }

    public static Supplier<Ingredient> parseMiniIngredient(JsonElement tag)
    {
        if (tag.isJsonObject())
        {
            var obj = tag.getAsJsonObject();
            if (obj.has("type"))
            {
                throw new RuntimeException("Custom ingredients not supported yet. Please use an 'item' or 'tag' ingredient.");
            }
            else if(obj.has("tag"))
            {
                if (obj.has("item"))
                    throw new IllegalStateException("Cannot have both 'tag' and 'item' in the ingredient at the same time.");

                var tagName = obj.get("tag").getAsString();
                var tagWrapper = ItemTags.bind(tagName);

                return Lazy.of(() -> Ingredient.of(tagWrapper));
            }
            else if(obj.has("item"))
            {
                var loc = new ResourceLocation(obj.get("item").getAsString());
                return Lazy.of(() -> Ingredient.of(Utils.getItemOrCrash(loc)));
            }
            else
            {
                throw new RuntimeException("'repair_ingredient' must have a 'tag' or 'item' key.");
            }
        }

        throw new RuntimeException("'repair_ingredient' must be a json object.");
    }
}
