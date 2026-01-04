package dev.gigaherz.jsonthings.things.parsers;

import com.google.gson.JsonObject;
import net.minecraft.resources.Identifier;

@FunctionalInterface
public interface ThingCondition
{
    boolean test(String thingType, Identifier thingId, JsonObject data);
}
