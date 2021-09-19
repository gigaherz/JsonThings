# Json Things

A mod all about json files.

## What?

Json Things adds a new kind of pack format: Thing Packs.

This is similar to resource packs and datapacks, but it lets you define things that are only able to be defined during
game load. Because of that, _Thing Packs_ cannot be added per save, or reloaded ingame.

To help have a consistent _Thing Pack_ experience, the thing packs also double down as both resource packs and datapacks.
Any pack in the `thingpacks` folder that has been enabled in the config will be included in the list of resourcepacks and datapacks.

Yes, this means Json Things also behaves as a global datapack system.

## How do I install a thingpack?

Easy! Just drop it in the `thingpacks` directory of your installation, or drop it in the Json Things config screen!

## How do I make a thingpack?

Thingpacks contain a `things` directory, similar to `assets` and `data`, which contains definitionsfor things.

See the [Introduction](./documentation/Introduction.md) section of the documentation for more details.
