import("net.minecraft.world.effect.MobEffectInstance");
import("net.minecraft.world.effect.MobEffects");
import("net.minecraft.world.item.ItemStack");
import("net.minecraft.world.item.Items");

function apply(eventName, args)
{
    Log.info("Test");

    let player = args.user;
    if (player != null)
    {
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200))
    }

    return FlexEventResult.success(new ItemStack(Items.STICK));
}