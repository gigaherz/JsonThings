package dev.gigaherz.jsonthings.things.scripting.client;

import static dev.gigaherz.jsonthings.things.scripting.McFunctionScript.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import com.google.gson.JsonObject;

import dev.gigaherz.jsonthings.things.events.FlexEventContext;
import dev.gigaherz.jsonthings.things.events.FlexEventType;
import dev.gigaherz.jsonthings.util.KeyNotFoundException;
import net.minecraft.resources.ResourceLocation;

import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.api.distmarker.Dist;

@OnlyIn(Dist.CLIENT)
public interface IClientLogic {
    public default Object getResult(FlexEventType<?> event, FlexEventContext context)
    {
        return getDefaultByEventType(event, context);
    }
    public static Map<ResourceLocation,Function<JsonObject,IClientLogic>> CLIENT_LOGICS = new HashMap<>();
    public static IClientLogic DEFAULT = new IClientLogic() { };
    public static void registerClientLogic(ResourceLocation rl, Function<JsonObject,IClientLogic> func)
    {
        CLIENT_LOGICS.put(rl, func);
    }
    public static void setup()
    {
        registerClientLogic(ResourceLocation.fromNamespaceAndPath("jsonthings","basic_compare"), BasicCompareLogic::new);
        registerClientLogic(ResourceLocation.fromNamespaceAndPath("jsonthings","value"), ForceValue::new);
    }
    public static IClientLogic getClientLogic(ResourceLocation rl, JsonObject data)
    {

        if(CLIENT_LOGICS.containsKey(rl))
        return CLIENT_LOGICS.get(rl).apply(data);
        throw new KeyNotFoundException("Client logic with id " + rl + " not found.");
    }
    public static IClientLogic getClientLogic(String str, JsonObject data)
    {
        return getClientLogic(ResourceLocation.parse(str),data);
    }
}
