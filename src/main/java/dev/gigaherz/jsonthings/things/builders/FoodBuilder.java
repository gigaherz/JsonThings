package dev.gigaherz.jsonthings.things.builders;

import com.mojang.datafixers.util.Pair;
import dev.gigaherz.jsonthings.things.parsers.ThingParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodProperties;

import java.util.ArrayList;
import java.util.List;

public class FoodBuilder extends BaseBuilder<FoodProperties, FoodBuilder>
{
    public static FoodBuilder begin(ThingParser<FoodBuilder> ownerParser, ResourceLocation registryName)
    {
        return new FoodBuilder(ownerParser, registryName);
    }

    private final List<Pair<MobEffectInstanceBuilder, Float>> effects = new ArrayList<>();
    private int nutrition;
    private float saturation;
    private boolean isMeat;
    private boolean alwaysEat;
    private boolean fast;

    private FoodBuilder(ThingParser<FoodBuilder> ownerParser, ResourceLocation registryName)
    {
        super(ownerParser, registryName);
    }

    @Override
    protected String getThingTypeDisplayName()
    {
        return "Food Definition";
    }

    public void setNutrition(int num)
    {
        this.nutrition = num;
    }

    public void setSaturation(float num)
    {
        this.saturation = num;
    }

    public void setIsMeat(boolean isMeat)
    {
        this.isMeat = isMeat;
    }

    public void setAlwaysEat(boolean alwaysEat)
    {
        this.alwaysEat = alwaysEat;
    }

    public void setFast(boolean fast)
    {
        this.fast = fast;
    }

    public void effect(MobEffectInstanceBuilder effect, float probability)
    {
        effects.add(Pair.of(effect, probability));
    }

    @Override
    protected FoodProperties buildInternal()
    {
        var foodBuilder = new FoodProperties.Builder();
        foodBuilder.nutrition(nutrition);
        foodBuilder.saturationMod(saturation);
        if (isMeat) foodBuilder.meat();
        if (fast) foodBuilder.fast();
        if (alwaysEat) foodBuilder.alwaysEat();
        effects.forEach(pair -> {
            foodBuilder.effect(pair.getFirst()::get, pair.getSecond());
        });
        return foodBuilder.build();
    }
}
