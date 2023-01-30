package dev.gigaherz.jsonthings.things.scripting.rhino.dsl;

import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Scriptable;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;

public class EntitiesDSL
{
    public static void use(Context cx, Scriptable scope)
    {
        if (scope.has(cx, ".use_entities", scope))
            return;

        scope.put(cx, "entityType", scope, new LambdaBaseFunction(EntitiesDSL::findEntityType));

        scope.put(cx, ".use_entities", scope, true);
    }

    private static Object findEntityType(Context cx, Scriptable scope, Scriptable thisObj, Object[] args)
    {
        var entityType = DSLHelpers.getRegistryEntry(args[0], ForgeRegistries.ENTITY_TYPES);
        return DSLHelpers.wrap(cx, scope, entityType, EntityType.class);
    }
}
