package gigaherz.jsonthings.util;

import gigaherz.jsonthings.JsonThings;
import net.minecraft.server.packs.PackType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Mod.EventBusSubscriber(modid = JsonThings.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CustomPackType
{
    private static final Method M_CREATE = ObfuscationReflectionHelper.findMethod(PackType.class, "create", String.class, String.class, com.mojang.bridge.game.PackType.class);

    public static final PackType THINGS;

    static
    {
        try
        {
            THINGS = (PackType) M_CREATE.invoke(null, "JSONTHINGS_THINGS", "things", com.mojang.bridge.game.PackType.DATA);
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
