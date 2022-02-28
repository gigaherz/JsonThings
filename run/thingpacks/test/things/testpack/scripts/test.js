useClass("net.minecraft.world.effect.MobEffectInstance",
       "net.minecraft.world.effect.MobEffects");
use("nbt", "items");

function apply(eventName, args)
{
    Log.info("Test");

    let player = args.user;
    if (player != null)
    {
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200))
    }

    let tag = NBT.compound({
        "Enchantments":[
            {"id":"minecraft:sharpness","lvl":5}
        ]
    });

    return FlexEventResult.success(stack(item("stick"),1,tag));
}