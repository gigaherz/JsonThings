package gigaherz.jsonthings.things.parsers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import gigaherz.jsonthings.things.builders.FoodBuilder;
import gigaherz.jsonthings.util.Utils;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class FoodParser extends ThingParser<FoodBuilder>
{
    public FoodParser()
    {
        super(GSON, "food");
    }

    @Override
    public FoodBuilder processThing(ResourceLocation key, JsonObject data)
    {
        FoodBuilder builder = FoodBuilder.begin(key);

        int heal_amount = JSONUtils.getAsInt(data, "heal_amount");
        if (heal_amount > 0)
        {
            builder=builder.withHealAmount(heal_amount);
        }
        else
        {
            throw new RuntimeException("'heal_amount' must be positive and not zero.");
        }

        float saturation = JSONUtils.getAsFloat(data, "saturation");
        if (saturation >= 0)
        {
            builder=builder.withSaturation(saturation);
        }
        else
        {
            throw new RuntimeException("'saturation' not be negative.");
        }

        if (JSONUtils.getAsBoolean(data, "meat", false))
        {
            builder=builder.makeMeat();
        }

        if (JSONUtils.getAsBoolean(data, "fast", false))
        {
            builder=builder.fast();
        }

        if (JSONUtils.getAsBoolean(data, "always_eat", false))
        {
            builder=builder.alwaysEat();
        }

        if (data.has("effects"))
        {
            JsonArray effects = data.get("effects").getAsJsonArray();
            for(JsonElement element : effects)
            {
                JsonObject effect = element.getAsJsonObject();
                EffectInstance ei = parseEffectInstance(effect);
                float probability = JSONUtils.getAsFloat(effect, "probability", 1.0f);
                builder=builder.effect(ei, probability);
            }
        }

        return builder;
    }

    private EffectInstance parseEffectInstance(JsonObject data)
    {
        Effect ef = Utils.getOrCrash(ForgeRegistries.POTIONS, new ResourceLocation(JSONUtils.getAsString(data, "effect")));
        int duration = JSONUtils.getAsInt(data, "duration", 0);
        int amplifier = JSONUtils.getAsInt(data, "amplifier", 0);
        boolean isAmbient = JSONUtils.getAsBoolean(data, "ambient", false);
        boolean visible = JSONUtils.getAsBoolean(data, "visible", true);
        boolean showParticles = JSONUtils.getAsBoolean(data, "show_particles", visible);
        boolean showIcon = JSONUtils.getAsBoolean(data, "show_icon", visible);
        return new EffectInstance(ef, duration, amplifier, isAmbient, showParticles, showIcon);
    }
}
