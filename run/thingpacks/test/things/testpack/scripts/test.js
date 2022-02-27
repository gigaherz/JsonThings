var MobEffectInstance = Java.type("net.minecraft.world.effect.MobEffectInstance");
var MobEffects = Java.type("net.minecraft.world.effect.MobEffects");

function apply(eventName, args)
{
    Log.info("Test");

    let player = args.user;
    if (player != null)
    {
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200))
    }

    return FlexEventResult.pass(args.stack);
}