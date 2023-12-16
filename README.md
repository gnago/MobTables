# MobTables



## Configuration

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

### Example

spawning.yml
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
