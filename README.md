# MobTables

Ever wanted to change mob spawning easily?
Then this is the right Plugin for you!

## Features
- Create **custom rules** for each mob in a familiar loottable like syntax
- **Disable** the spawning of mobs not defined in the config

## Configuration
Define when, where and how mobs should spawn.

- [Schema](https://github.com/KaninchenSpeed/MobTables#Schema)
  - [Conditions](https://github.com/KaninchenSpeed/MobTables#Conditions)
  - [Attributes](https://github.com/KaninchenSpeed/MobTables#Attributes)
  - [Ranges](https://github.com/KaninchenSpeed/MobTables#Ranges)
- [Examples](https://github.com/KaninchenSpeed/MobTables#Examples)

### Schema
`spawning.yml`
```yaml
keepVanillaSpawning: false | true
pools:
  - rolls: 1 | number(min 1)
    conditions:
      ...conditions...
    entries:
      - type: PIGLIN | mob name in caps, like in spigot/ppaer
        chance: 0.2 | float always with . like 1.0 , from 0.0 to 1.0
        attributes:
          ...attributes...
        conditions:
          ...conditions...
  ...
```

### Conditions
All conditions are combined with a logical `AND` and can be inverted with `invert: true` in their definition.

#### Biome
```yaml
- type: biome
  biome: BASALT_DELTAS | biome name in caps, like in spigot/paper
```

#### Block
**mode: single block**

default offset is `x = 0; y = -1; z = 0`

```yaml
- type: block
  filter:
    - GRASS_BLOCK | block name in caps, like in spigot/paper. Combined with OR
    - STONE
    ...
  offset: | optional
    x: 0
    y: -1
    z: 0
```

**mode: volume**
```yaml
- type: block
  filter:
    - GRASS_BLOCK | block name in caps, like in spigot/paper. Combined with OR
    - STONE
    ...
  offset1: | pos1 like in /fill or worldedit, including this position
    x: -1
    y: -1
    z: -1
  offset2: | pos2 like in /fill or worldedit, including this position
    x: 1
    y: 1
    z: 1
```

#### Entitylimit
> inverting can be used to only spawn when entity is present

```yaml
- type: entity_limit
  range: 100 | block range to count mobs in
  limit: 10 | max amount (non inclusive) of mobs to exist for this to be true
  filter:
    - GHAST | mob name in caps, like in spigot/paper. Combined with OR
    ...
```

### Attributes
Currently supported attributes to spawn mobs with.
Open a [issue](https://github.com/KaninchenSpeed/MobTables/issues) if you want one added.

#### Color
Sets the color of Horses, Sheep, and Shulkers.
For Horses, must be either `WHITE`, `CREAMY`, `CHESTNUT`, `BROWN`, `BLACK`, `GRAY`, or `DARK_BROWN`
For Sheep/Shulkers, must be the name of a dye (`WHITE,ORANGE,MAGENTA,LIGHT_BLUE,YELLOW,LIME,PINK,GRAY,LIGHT_GRAY,CYAN,PURPLE,BLUE,BROWN,GREEN,RED,BLACK`)

```yaml
- values:
    Color: GRAY
  conditions:
    ...conditions...
```

#### Variant
This applies to any mob that has a variant (even if this isn't usually referred to as 'variant' in NBT/code)
Axolotl `LUCY,WILD,GOLD,CYAN,BLUE`
Cat `TABBY,BLACK,RED,SIAMESE,BRITISH_SHORTHAIR,CALICO,PERSIAN,RAGDOLL,WHITE,JELLIE,ALL_BLACK`
Fox `RED,SNOW`
Frog `TEMPERATE,WARM,COLD`
Horse `NONE,WHITE,WHITEFIELD,WHITE_DOTS,BLACK_DOTS`
Llama `CREAMY,WHITE,BROWN,GRAY`
Mooshroom `RED,BROWN`
Parrot `RED,BLUE,GREEN,CYAN,GRAY`
Rabbit `BROWN,WHITE,BLACK,BLACK_AND_WHITE,GOLD,SALT_AND_PEPPER,THE_KILLER_BUNNY`
Wolf `PALE,SPOTTED,SNOWY,BLACK,ASHEN,RUSTY,WOODS,CHESTNUT,STRIPED`

```yaml
- values:
    Variant: RUSTY
  conditions:
    ...conditions...
```

#### Size
Sets the size of slimes and magma cubes.
Starts at 0.

```yaml
- values:
    Size: 3 | int, min 0
  conditions:
    ...conditions...
```

#### IsImmuneToZombification
When set to `true`: Stops Piglins from converting into Zombiefied Piglins.

```yaml
- values:
    IsImmuneToZombification: true | false
  conditions:
    ...conditions...
```

### Ranges
Most integer inputs can be replaced with ranges, where a random value is picked from (all options have the same probability).
This can be done in two ways, both do the same.

**Option 1**
```yaml
...:
  from: 10 | min, inclusive
  to: 20 | max, inclusive
```

**Option 2**
```yaml
...: 10..20 | min..max , both inclusive
```

### Examples
Examples to base your config on

#### Spawn piglins in deserts
`spawning.yml`
```yaml
keepVanillaSpawning: true
pools:
  - rolls: 1
    conditions:
      - type: biome
        biome: DESERT
    entries:
      - type: PIGLIN
        chance: 0.2
```

#### Spawn magmacubes on magma blocks
`spawning.yml`
```yaml
keepVanillaSpawning: true
pools:
  - rolls: 1
    conditions:
      - type: block
        filter:
          - MAGMA_BLOCK
    entries:
      - type: MAGMA_CUBE
        chance: 1.0
```

#### Spawn creamy horses with black dots in badlands
`spawning.yml`
```yaml
keepVanillaSpawning: true
pools:
  - rolls: 1
    conditions:
      - type: biome
        biome: BADLANDS
    entries:
      - type: HORSE
        chance: 0.2
        attributes:
          - values:
              Variant: BLACK_DOTS
              Color: CREAMY
```
