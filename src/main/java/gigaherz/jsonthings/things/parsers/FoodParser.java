package gigaherz.jsonthings.things.parsers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import gigaherz.jsonthings.things.builders.FoodBuilder;
import gigaherz.jsonthings.util.Utils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
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

        int heal_amount = GsonHelper.getAsInt(data, "heal_amount");
        if (heal_amount > 0)
        {
            builder = builder.withHealAmount(heal_amount);
        }
        else
        {
            throw new RuntimeException("'heal_amount' must be positive and not zero.");
        }

        float saturation = GsonHelper.getAsFloat(data, "saturation");
        if (saturation >= 0)
        {
            builder = builder.withSaturation(saturation);
        }
        else
        {
            throw new RuntimeException("'saturation' not be negative.");
        }

        if (GsonHelper.getAsBoolean(data, "meat", false))
        {
            builder = builder.makeMeat();
        }

        if (GsonHelper.getAsBoolean(data, "fast", false))
        {
            builder = builder.fast();
        }

        if (GsonHelper.getAsBoolean(data, "always_eat", false))
        {
            builder = builder.alwaysEat();
        }

        if (data.has("effects"))
        {
            JsonArray effects = data.get("effects").getAsJsonArray();
            for (JsonElement element : effects)
            {
                JsonObject effect = element.getAsJsonObject();
                MobEffectInstance ei = parseEffectInstance(effect);
                float probability = GsonHelper.getAsFloat(effect, "probability", 1.0f);
                builder = builder.effect(ei, probability);
            }
        }

        return builder;
    }

    private MobEffectInstance parseEffectInstance(JsonObject data)
    {
        MobEffect ef = Utils.getOrCrash(ForgeRegistries.MOB_EFFECTS, new ResourceLocation(GsonHelper.getAsString(data, "effect")));
        int duration = GsonHelper.getAsInt(data, "duration", 0);
        int amplifier = GsonHelper.getAsInt(data, "amplifier", 0);
        boolean isAmbient = GsonHelper.getAsBoolean(data, "ambient", false);
        boolean visible = GsonHelper.getAsBoolean(data, "visible", true);
        boolean showParticles = GsonHelper.getAsBoolean(data, "show_particles", visible);
        boolean showIcon = GsonHelper.getAsBoolean(data, "show_icon", visible);
        return new MobEffectInstance(ef, duration, amplifier, isAmbient, showParticles, showIcon);
    }
}
