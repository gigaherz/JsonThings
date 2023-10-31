package dev.gigaherz.jsonthings.things.scripting.rhino.dsl;

import com.mojang.logging.LogUtils;
import dev.latvian.mods.rhino.*;
import net.minecraft.core.Registry;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.IForgeRegistry;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.lang.reflect.Modifier;

public class DSLHelpers
{
    private static Logger LOGGER = LogUtils.getLogger();

    public static <T> T find(IForgeRegistry<T> reg, String n)
    {
        var rl = new ResourceLocation(n);

        if (!reg.containsKey(rl))
            throw new RuntimeException("Cannot find effect with name " + rl);

        //noinspection ConstantConditions
        return reg.getValue(rl);
    }

    public static <T> T find(Registry<T> reg, String n)
    {
        var rl = new ResourceLocation(n);

        if (!reg.containsKey(rl))
            throw new RuntimeException("Cannot find effect with name " + rl);

        //noinspection ConstantConditions
        return reg.get(rl);
    }

    public static <T> T getRegistryEntry(Object arg, IForgeRegistry<T> reg)
    {
        return arg instanceof String str
                ? DSLHelpers.find(reg, str)
                : DSLHelpers.get(arg);
    }

    public static <T> T getRegistryEntry(Object arg, Registry<T> reg)
    {
        return arg instanceof String str
                ? DSLHelpers.find(reg, str)
                : DSLHelpers.get(arg);
    }

    @SuppressWarnings("unchecked")
    public static <T> T get(Object arg)
    {
        return (T) ((NativeJavaObject) arg).unwrap();
    }

    public static <T> T get(Object arg, Class<T> target)
    {
        return target.cast(((NativeJavaObject) arg).unwrap());
    }

    public static byte getByte(Object arg)
    {
        return ((Number) arg).byteValue();
    }

    public static short getShort(Object arg)
    {
        return ((Number) arg).shortValue();
    }

    public static int getInt(Object arg)
    {
        return ((Number) arg).intValue();
    }

    public static long getLong(Object arg)
    {
        return ((Number) arg).longValue();
    }

    public static float getFloat(Object arg)
    {
        return ((Number) arg).floatValue();
    }

    public static double getDouble(Object arg)
    {
        return ((Number) arg).doubleValue();
    }

    public static String getString(Object arg)
    {
        return (String) arg;
    }

    public static Component getComponent(Context cx, Object arg)
    {
        if (arg instanceof NativeJavaObject o)
            arg = o.unwrap();

        if (arg instanceof String s)
            return Component.literal(s);

        if (arg instanceof ConsString cs)
            return Component.literal(cs.toString());

        if (arg instanceof NativeObject obj)
            return Component.Serializer.fromJson(NativeJSON.stringify(obj, null, 0, cx));

        return Component.literal("unknown");
    }

    public static Object wrap(Context cx, Scriptable scope, Object arg)
    {
        return wrap(cx, scope, arg, null);
    }

    public static <T> Object wrap(Context cx, Scriptable scope, @Nullable T value, @Nullable Class<? super T> fieldType)
    {
        scope = ScriptableObject.getTopLevelScope(scope);
        return cx.getWrapFactory().wrap(cx, scope, value, fieldType);
    }


    public static void debugDumpBindings()
    {
        // Items
        debugDumpBindings(Item.class);
        debugDumpBindings(Items.class);
        debugDumpBindings(ItemStack.class);

        // Blocks
        debugDumpBindings(Block.class);
        debugDumpBindings(Blocks.class);
        debugDumpBindings(BlockState.class);

        // Enchantments
        debugDumpBindings(Enchantment.class);
        debugDumpBindings(Enchantments.class);
        debugDumpBindings(EnchantmentHelper.class);

        // MobEffects
        debugDumpBindings(MobEffect.class);
        debugDumpBindings(MobEffects.class);
        debugDumpBindings(MobEffectInstance.class);

        // Misc
        debugDumpBindings(Level.class);

        // NBT
        debugDumpBindings(Tag.class);
        debugDumpBindings(ByteTag.class);
        debugDumpBindings(ShortTag.class);
        debugDumpBindings(IntTag.class);
        debugDumpBindings(LongTag.class);
        debugDumpBindings(FloatTag.class);
        debugDumpBindings(DoubleTag.class);
        debugDumpBindings(StringTag.class);
        debugDumpBindings(ListTag.class);
        debugDumpBindings(CompoundTag.class);
        debugDumpBindings(ByteArrayTag.class);
        debugDumpBindings(IntArrayTag.class);
        debugDumpBindings(LongArrayTag.class);
    }

    //private static Field f_Signature = ObfuscationReflectionHelper.findField(Method.class, "signature");
    public static void debugDumpBindings(Class<?> cls)
    {
        //try
        {
            LOGGER.info("Dumping class bindings for " + cls.getCanonicalName());
            LOGGER.info("Static wrapper: ");
            for (var m : cls.getDeclaredMethods())
            {
                var mod = m.getModifiers();
                var isStatic = Modifier.isStatic(mod);
                if (Modifier.isPublic(mod) && isStatic)
                {
                    LOGGER.info("m {}", m.getName());//, f_Signature.get(m));
                }
            }
            for (var f : cls.getDeclaredFields())
            {
                var mod = f.getModifiers();
                var isStatic = Modifier.isStatic(mod);
                if (Modifier.isPublic(mod) && isStatic)
                {
                    LOGGER.info("f {}", f.getName());
                }
            }

            LOGGER.info("Instance wrapper: ");
            for (var m : cls.getDeclaredMethods())
            {
                var mod = m.getModifiers();
                var isStatic = Modifier.isStatic(mod);
                if (Modifier.isPublic(mod) && !isStatic)
                {
                    LOGGER.info("m {}", m.getName());//, f_Signature.get(m));
                }
            }
            for (var f : cls.getDeclaredFields())
            {
                var mod = f.getModifiers();
                var isStatic = Modifier.isStatic(mod);
                if (Modifier.isPublic(mod) && !isStatic)
                {
                    LOGGER.info("f {}", f.getName());
                }
            }
        }
        /*catch (IllegalAccessException e)
        {
            LOGGER.debug("Opps?", e);
        }*/
    }
}
