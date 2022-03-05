useClass("net.minecraft.world.effect.MobEffectInstance",
       "net.minecraft.world.effect.MobEffects");
use("nbt", "items", "effects");

function apply(eventName, args)
{
    Log.info("Test");

    let player = args.user;
    if (player != null)
    {
        player.addEffect(effectInstance(effect("regeneration"), 200))
    }

    let tag = NBT.compound({
        Enchantments:[
            {id:"minecraft:sharpness",lvl:5}
        ]
    });

    return success(stack(item("stick"),1,tag));
}