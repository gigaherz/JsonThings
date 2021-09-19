# Effect Instance Definitions

This format applies to effect instances as defined in other json files. There is no names definitions for effect instances
at this point.

See [Food](./Food.md) for an exmaple of where this is used.

## Basic structure of the JSON file

```json
{
  "effect": "minecraft:poison",
  "duration": 5,
  "amplifier": 0,
  "ambient": false,
  "visible": true,
  "show_particles": true,
  "show_icon": true
}
```

## "effect"

Defines which potion effect to apply.

Required.

Must be a resource location string like "poison", or "minecraft:blindness". Like on model jsons and other vanilla files,
if the namespace (the part before the colon) is missing "minecraft" is implied.

## "duration"

Defines how long the effect lasts, in ingame ticks (20ths of a second).

Optional. Default: 0. 

The default will disappear immediately so a non-zero value is advised.

Must be a positive integer.

## "amplifier"

Defines how strong the effect is. Higher numbers increase the potency of the effect further.

Optional. Default: 0. 

The default means no amplification, and will do the standard effect.

Must be a positive integer or zero.

## "ambient"

Defines if the effect should be considered an ambient effect such as coming from a beacon. Ambient effects appear
in a different color and without a countdown. It is advised not to set the "ambient" property to true for foods and other
contextual effects.

Optional. Default: false.

Must be a boolean (`false` or `true`).

    private MobEffectInstance parseEffectInstance(JsonObject data)
    {
        MobEffect ef = Utils.getOrCrash(ForgeRegistries.MOB_EFFECTS, new ResourceLocation(GsonHelper.getAsString(data, "effect")));
        int duration = GsonHelper.getAsInt(data, "duration", 0);
        int amplifier = GsonHelper.getAsInt(data, "amplifier", 0);
        boolean isAmbient = GsonHelper.getAsBoolean(data, "ambient", false);
        boolean visible = GsonHelper.getAsBoolean(data, "visible", true);
        boolean showParticles = GsonHelper.getAsBoolean(data, "show_particles", visible);
        boolean showIcon = GsonHelper.getAsBoolean(data, "show_icon", visible);
        return new MobEffectInstance(ef, duration, amplifier, isAmbient, showParticles, showIcon);
    }