package dev.gigaherz.jsonthings.things.builders;

import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class FoodBuilder implements Supplier<FoodProperties>
{
    private FoodProperties builtFood = null;

    private final ResourceLocation registryName;
    private final List<Pair<MobEffectInstanceBuilder, Float>> effects = new ArrayList<>();
    private int nutrition;
    private float saturation;
    private boolean isMeat;
    private boolean alwaysEat;
    private boolean fast;


    private FoodBuilder(ResourceLocation registryName)
    {
        this.registryName = registryName;
    }

    public static FoodBuilder begin(ResourceLocation registryName)
    {
        return new FoodBuilder(registryName);
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

    private FoodProperties build()
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
        return builtFood = foodBuilder.build();
    }

    public FoodProperties get()
    {
        if (builtFood == null)
            return build();
        return builtFood;
    }

    public ResourceLocation getRegistryName()
    {
        return registryName;
    }
}
