package dev.gigaherz.jsonthings.things.scripting.rhino.dsl;

import com.google.common.collect.ImmutableMap;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.NativeJavaMap;
import dev.latvian.mods.rhino.NativeJavaObject;
import dev.latvian.mods.rhino.Scriptable;
import net.minecraft.nbt.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class NbtDSL
{
    public static void use(Context cx, Scriptable scope)
    {
        if (scope.has(".use_nbt", scope))
            return;

        scope.put("NBT", scope, new NativeJavaMap(cx.sharedContextData, scope, new Object(), ImmutableMap.<String, Object>builder()
                .put("of", new LambdaBaseFunction(NbtDSL::wrap))
                .put("boolean", new LambdaBaseFunction(NbtDSL::makeBoolTag))
                .put("byte", new LambdaBaseFunction(NbtDSL::makeByteTag))
                .put("short", new LambdaBaseFunction(NbtDSL::makeShortTag))
                .put("int", new LambdaBaseFunction(NbtDSL::makeIntTag))
                .put("long", new LambdaBaseFunction(NbtDSL::makeLongTag))
                .put("float", new LambdaBaseFunction(NbtDSL::makeFloatTag))
                .put("double", new LambdaBaseFunction(NbtDSL::makeDoubleTag))
                .put("string", new LambdaBaseFunction(NbtDSL::makeStringTag))
                .put("list", new LambdaBaseFunction(NbtDSL::makeListTag))
                .put("compound", new LambdaBaseFunction(NbtDSL::makeCompoundTag))
                //.put("byteArray", new LambdaBaseFunction(NbtDSL::makeByteArrayTag))
                //.put("intArray", new LambdaBaseFunction(NbtDSL::makeIntArrayTag))
                //.put("longArray", new LambdaBaseFunction(NbtDSL::makeLongArrayTag))
                .build()
        ));

        scope.put(".use_nbt", scope, true);
    }

    private static Object wrap(Context cx, Scriptable scope, Scriptable thisObj, Object[] args)
    {
        return DSLHelpers.wrap(scope, wrapInternal(args[0]), TagProxy.class);
    }

    private static TagProxy<?> wrapInternal(Object arg0)
    {
        if (arg0 instanceof NativeJavaObject obj) arg0 = obj.unwrap();

        if (arg0 instanceof TagProxy<?> t) return t;

        if (arg0 instanceof ByteTag b) return new ByteTagProxy(b.getAsByte());
        if (arg0 instanceof ShortTag s) return new ShortTagProxy(s.getAsShort());
        if (arg0 instanceof IntTag i) return new IntTagProxy(i.getAsInt());
        if (arg0 instanceof LongTag l) return new LongTagProxy(l.getAsLong());
        if (arg0 instanceof FloatTag f) return new FloatTagProxy(f.getAsFloat());
        if (arg0 instanceof DoubleTag d) return new DoubleTagProxy(d.getAsDouble());
        if (arg0 instanceof StringTag s) return new StringTagProxy(s.getAsString());
        if (arg0 instanceof ListTag l) return new ListTagProxy(l);
        if (arg0 instanceof CompoundTag c) return new CompoundTagProxy(c);

        if (arg0 instanceof Boolean b) return new BooleanTagProxy(b);
        if (arg0 instanceof Byte b) return new ByteTagProxy(b);
        if (arg0 instanceof Short s) return new ShortTagProxy(s);
        if (arg0 instanceof Integer i) return new IntTagProxy(i);
        if (arg0 instanceof Long l) return new LongTagProxy(l);
        if (arg0 instanceof Float f) return new FloatTagProxy(f);
        if (arg0 instanceof Double d) return new DoubleTagProxy(d);
        if (arg0 instanceof String s) return new StringTagProxy(s);
        if (arg0 instanceof List l) return new ListTagProxy(makeListTagInternal(l));
        if (arg0 instanceof Map m) return new CompoundTagProxy(makeCompoundTagInternal(m));

        throw new RuntimeException("Cannot convert " + arg0.getClass() + " to a Tag");
    }

    public static Tag wrapVanillaInternal(Object arg0)
    {
        if (arg0 instanceof NativeJavaObject obj) arg0 = obj.unwrap();

        if (arg0 instanceof TagProxy<?> t) return t.getTag();

        if (arg0 instanceof ByteTag b) return b;
        if (arg0 instanceof ShortTag s) return s;
        if (arg0 instanceof IntTag i) return i;
        if (arg0 instanceof LongTag l) return l;
        if (arg0 instanceof FloatTag f) return f;
        if (arg0 instanceof DoubleTag d) return d;
        if (arg0 instanceof StringTag s) return s;
        if (arg0 instanceof ListTag l) return l;
        if (arg0 instanceof CompoundTag c) return c;

        if (arg0 instanceof Boolean b) return ByteTag.valueOf(b);
        if (arg0 instanceof Byte b) return ByteTag.valueOf(b);
        if (arg0 instanceof Short s) return ShortTag.valueOf(s);
        if (arg0 instanceof Integer i) return IntTag.valueOf(i);
        if (arg0 instanceof Long l) return LongTag.valueOf(l);
        if (arg0 instanceof Float f) return FloatTag.valueOf(f);
        if (arg0 instanceof Double d) return DoubleTag.valueOf(d);
        if (arg0 instanceof String s) return StringTag.valueOf(s);
        if (arg0 instanceof List l) return makeListTagInternal(l);
        if (arg0 instanceof Map m) return makeCompoundTagInternal(m);

        throw new RuntimeException("Cannot convert " + arg0.getClass() + " to a Tag");
    }

    private static Object makeBoolTag(Context cx, Scriptable scope, Scriptable thisObj, Object[] args)
    {
        var arg0 = args[0];
        var value = (arg0 instanceof ByteTag b) ? b.getAsByte() != 0 : (boolean) arg0;
        return DSLHelpers.wrap(scope, new BooleanTagProxy(value), TagProxy.class);
    }

    private static Object makeByteTag(Context cx, Scriptable scope, Scriptable thisObj, Object[] args)
    {
        var arg0 = args[0];
        var value = (arg0 instanceof ByteTag b) ? b.getAsByte() : (byte) arg0;
        return DSLHelpers.wrap(scope, new ByteTagProxy(value), TagProxy.class);
    }

    private static Object makeShortTag(Context cx, Scriptable scope, Scriptable thisObj, Object[] args)
    {
        var arg0 = args[0];
        var value = (arg0 instanceof ShortTag b) ? b.getAsShort() : (short) arg0;
        return DSLHelpers.wrap(scope, new ShortTagProxy(value), TagProxy.class);
    }

    private static Object makeIntTag(Context cx, Scriptable scope, Scriptable thisObj, Object[] args)
    {
        var arg0 = args[0];
        var value = (arg0 instanceof IntTag b) ? b.getAsInt() : (int) arg0;
        return DSLHelpers.wrap(scope, new IntTagProxy(value), TagProxy.class);
    }

    private static Object makeLongTag(Context cx, Scriptable scope, Scriptable thisObj, Object[] args)
    {
        var arg0 = args[0];
        var value = (arg0 instanceof LongTag b) ? b.getAsLong() : (long) arg0;
        return DSLHelpers.wrap(scope, new LongTagProxy(value), TagProxy.class);
    }

    private static Object makeFloatTag(Context cx, Scriptable scope, Scriptable thisObj, Object[] args)
    {
        var arg0 = args[0];
        var value = (arg0 instanceof FloatTag b) ? b.getAsFloat() : (float) arg0;
        return DSLHelpers.wrap(scope, new FloatTagProxy(value), TagProxy.class);
    }

    private static Object makeDoubleTag(Context cx, Scriptable scope, Scriptable thisObj, Object[] args)
    {
        var arg0 = args[0];
        var value = (arg0 instanceof DoubleTag b) ? b.getAsDouble() : (double) arg0;
        return DSLHelpers.wrap(scope, new DoubleTagProxy(value), TagProxy.class);
    }

    private static Object makeStringTag(Context cx, Scriptable scope, Scriptable thisObj, Object[] args)
    {
        var arg0 = args[0];
        var value = (arg0 instanceof StringTag b) ? b.getAsString() : (String) arg0;
        return DSLHelpers.wrap(scope, new StringTagProxy(value), TagProxy.class);
    }

    private static Object makeListTag(Context cx, Scriptable scope, Scriptable thisObj, Object[] args)
    {
        ListTag list;

        var arg0 = args[0];
        if (args.length == 1 && arg0 instanceof ListTag tag)
        {
            list = tag;
        }
        else if (args.length == 1 && arg0 instanceof List<?> arr)
        {
            list = makeListTagInternal(arr);
        }
        else
        {
            list = makeListTagInternal(Arrays.asList(args));
        }

        return DSLHelpers.wrap(scope, new ListTagProxy(list), TagProxy.class);
    }

    private static ListTag makeListTagInternal(List<?> args)
    {
        var list = new ListTag();
        addAllElements(list, args);
        return list;
    }

    private static void addAllElements(ListTag list, List<?> args)
    {
        Class<?> tagType = null;

        for (var arg : args)
        {
            var wrapped = wrapVanillaInternal(arg);
            if (tagType == null)
            {
                tagType = wrapped.getClass();
            }
            else if (tagType != wrapped.getClass())
            {
                throw new IllegalStateException("List tags must have homogenous data type. Tried to add " + wrapped.getClass() + " but the list already contains " + tagType);
            }
            list.add(wrapped);
        }
    }

    private static void addAllElements(CompoundTag map, Map<?, ?> values)
    {
        for (var arg : values.entrySet())
        {
            var key = (String) arg.getKey();
            var value = arg.getValue();
            var wrapped = wrapVanillaInternal(value);
            map.put(key, wrapped);
        }
    }

    private static Object makeCompoundTag(Context cx, Scriptable scope, Scriptable thisObj, Object[] args)
    {
        CompoundTag compound;

        var arg0 = args[0];
        if (args.length == 1 && arg0 instanceof CompoundTag tag)
        {
            compound = tag;
        }
        else if (args.length == 1 && arg0 instanceof Map arr)
        {
            compound = makeCompoundTagInternal(arr);
        }
        else
        {
            compound = new CompoundTag();
            if ((args.length % 2) != 0)
                throw new IllegalStateException("compound constructor must either have one compound param, or a sequence of key-value pairs");
            for (int i = 0; i < args.length; i += 2)
            {
                compound.put(
                        (String) args[i],
                        wrapVanillaInternal(args[i + 1])
                );
            }
        }

        return DSLHelpers.wrap(scope, new CompoundTagProxy(compound), TagProxy.class);
    }

    private static CompoundTag makeCompoundTagInternal(Map<?, ?> map)
    {
        var compound = new CompoundTag();
        addAllElements(compound, map);
        return compound;
    }

    public static class BooleanTagProxy extends PrimitiveTagProxy<Boolean, ByteTag>
    {
        public BooleanTagProxy(boolean value)
        {
            super(value);
        }

        @Override
        public ByteTag getTag()
        {
            return ByteTag.valueOf(value);
        }
    }

    public static class ByteTagProxy extends PrimitiveTagProxy<Byte, ByteTag>
    {
        public ByteTagProxy(byte value)
        {
            super(value);
        }

        @Override
        public ByteTag getTag()
        {
            return ByteTag.valueOf(value);
        }
    }

    public static class ShortTagProxy extends PrimitiveTagProxy<Short, ShortTag>
    {
        public ShortTagProxy(short value)
        {
            super(value);
        }

        @Override
        public ShortTag getTag()
        {
            return ShortTag.valueOf(value);
        }
    }

    public static class IntTagProxy extends PrimitiveTagProxy<Integer, IntTag>
    {
        public IntTagProxy(int value)
        {
            super(value);
        }

        @Override
        public IntTag getTag()
        {
            return IntTag.valueOf(value);
        }
    }

    public static class LongTagProxy extends PrimitiveTagProxy<Long, LongTag>
    {
        public LongTagProxy(long value)
        {
            super(value);
        }

        @Override
        public LongTag getTag()
        {
            return LongTag.valueOf(value);
        }
    }

    public static class FloatTagProxy extends PrimitiveTagProxy<Float, FloatTag>
    {
        public FloatTagProxy(float value)
        {
            super(value);
        }

        @Override
        public FloatTag getTag()
        {
            return FloatTag.valueOf(value);
        }
    }

    public static class DoubleTagProxy extends PrimitiveTagProxy<Double, DoubleTag>
    {
        public DoubleTagProxy(double value)
        {
            super(value);
        }

        @Override
        public DoubleTag getTag()
        {
            return DoubleTag.valueOf(value);
        }
    }

    public static class StringTagProxy extends PrimitiveTagProxy<String, StringTag>
    {
        public StringTagProxy(String value)
        {
            super(value);
        }

        @Override
        public StringTag getTag()
        {
            return StringTag.valueOf(value);
        }
    }

    private abstract static class PrimitiveTagProxy<V, T extends Tag> implements TagProxy<T>
    {
        public final V value;

        protected PrimitiveTagProxy(V value)
        {
            this.value = value;
        }
    }

    // TODO: make implement List
    public static class ListTagProxy implements TagProxy<ListTag>
    {
        private final ListTag list;

        public ListTagProxy(ListTag value)
        {
            list = value;
        }

        public Object get(int index)
        {
            return wrapInternal(list.get(index));
        }

        public void add(Tag tag)
        {
            list.add(tag);
        }

        public void add(TagProxy<?> tag)
        {
            list.add(tag.getTag());
        }

        public void remove(int index)
        {
            list.remove(index);
        }

        @Override
        public ListTag getTag()
        {
            return list;
        }
    }

    // TODO: make implement Map
    public static class CompoundTagProxy implements TagProxy<CompoundTag>
    {
        private final CompoundTag compound;

        public CompoundTagProxy(CompoundTag value)
        {
            compound = value;
        }

        public Object get(String key)
        {
            return wrapInternal(compound.get(key));
        }

        public void put(String key, Tag tag)
        {
            compound.put(key, tag);
        }

        public void put(String key, TagProxy<?> tag)
        {
            compound.put(key, tag.getTag());
        }

        public void remove(String key)
        {
            compound.remove(key);
        }

        @Override
        public CompoundTag getTag()
        {
            return compound;
        }
    }

    public interface TagProxy<T extends Tag>
    {
        T getTag();
    }
}
