package dev.gigaherz.jsonthings.things.parsers;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;

@FunctionalInterface
public interface ThingCondition
{
    boolean test(String thingType, ResourceLocation thingId, JsonObject data);
}
