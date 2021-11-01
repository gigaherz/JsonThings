package dev.gigaherz.jsonthings.util;

import dev.gigaherz.jsonthings.JsonThings;
import net.minecraft.resources.ResourcePackType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Mod.EventBusSubscriber(modid = JsonThings.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CustomPackType
{
    private static final Method M_CREATE = ObfuscationReflectionHelper.findMethod(ResourcePackType.class, "create", String.class, String.class);

    public static final ResourcePackType THINGS;

    static
    {
        try
        {
            THINGS = (ResourcePackType) M_CREATE.invoke(null, "JSONTHINGS_THINGS", "things");
        }
        catch (IllegalAccessException | InvocationTargetException e)
        {
            throw new RuntimeException("Error calling private method", e);
        }
    }

    @SubscribeEvent
    public static void init(FMLConstructModEvent event)
    {
        /* do nothing, we just need this to be classloaded */
    }
}
