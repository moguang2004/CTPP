# CTPP KNOWLEDGE BASE

## OVERVIEW
CTPP (`CT++`) is the Create/GregTech compatibility module. It defines kinetic/electric machines, Create fan catalyst recipes, custom recipe builders, generated data, and GTCEu addon registration under mod id `ctpp`.

## WHERE TO LOOK
- Mod entry: `src/main/java/com/mo_guang/ctpp/CTPP.java`. Forge mod initialization.
- GT addon: `src/main/java/com/mo_guang/ctpp/CTPPGTAddon.java`. GTCEu integration.
- Registrate: `src/main/java/com/mo_guang/ctpp/CTPPRegistrate.java`, `CTPPRegistration.java`. Core registration helpers.
- API: `src/main/java/com/mo_guang/ctpp/api/`. Recipe capabilities, multiblock builder, predicates, parallel logic.
- Dynamic contraptions: `src/main/java/com/mo_guang/ctpp/dynamicPart/`. Rotation wand, moving/rotating contraption entities, renderers, and rotation state helpers.
- KubeJS integration: `src/main/java/com/mo_guang/ctpp/integration/kjs/`. Kinetic machine builder and stress recipe components.
- Recipes/datagen: `src/main/java/com/mo_guang/ctpp/common/data/recipe/`. Recipe builders/providers.
- Models: `src/main/java/com/mo_guang/ctpp/common/data/model/`. Machine model generation.
- Ponder/client: `src/main/java/com/mo_guang/ctpp/client/ponder/`. CTPP Ponder plugin/scenes/tags, including Carbon Brushes.
- Mixins: `src/main/java/com/mo_guang/ctpp/mixin/`, `src/main/resources/ctpp.mixins.json`. Create rotation/kinetic fixes, deployer/sequenced assembly fixes, GT bucket and multiblock-state hooks.
- Generated resources: `src/generated/resources/data/ctpp/recipes/`. Fan catalyst and machine recipe output.
- Static resources: `src/main/resources/assets/ctpp/`. Hand-authored models/assets.

## REGISTRATION ENTRYPOINTS
- Registrate/root: `CTPPRegistration.java`, `CTPPRegistrate.java`; mod/addon entrypoints are `CTPP.java` and `CTPPGTAddon.java`.
- Mod entry hook: `CTPP.java` initializes client/common proxy through `DistExecutor` and calls `CTPPEntityTypes.init()`.
- Common proxy: `common/CommonProxy.java` initializes config, creative tabs, registrate, datagen, fan-processing deferred registers, machine/recipe listeners, Create arm interaction point type, materials, and client Ponder lang extraction.
- GT addon hooks: `CTPPGTAddon.initializeAddon()` initializes `CTPPBlocks` and `CTPPBlockMaps`; `registerRecipeCapabilities()` initializes stress capabilities; `registerRecipeKeys()` exposes KubeJS `SU_IN` / `SU_OUT`; `registerMultiblockPreviewHighlighters()` adds kinetic/upgrade ability colors.
- Items/blocks/entities: `registry/CTPPItems.java`, `registry/CTPPBlocks.java`, `CTPPEntityTypes.java`.
- Machines/multiblocks: `registry/CTPPMachines.java`, `registry/CTPPMultiblockMachines.java`, builder support in `api/CTPPMultiblockBuilder.java`.
- Materials: `registry/CTPPMaterials.java`, `registry/GTMaterialAddon.java`.
- Recipe types/modifiers/capabilities/conditions: `registry/CTPPRecipeTypes.java`, `CTPPRecipeModifiers.java`, `api/CTPPRecipeCapabilities.java`, `api/CTPPRecipeConditions.java`.
- Recipe generation: `CTPPGTAddon.addRecipes()` calls `common/data/recipe/CTPPRecipes.java`; specific Create/kinetic recipes live under `common/data/recipe/` and `common/data/recipe/builder/`.
- Datagen/fan processing: `data/CTPPDatagen.java`, `common/data/recipe/fan_processing/`.
- Recipe removals: `CTPPGTAddon.removeRecipes()` currently removes `create_new_age:shaped/carbon_brushes`.

## RECIPE TYPES
CTPP defines two recipe-type families: GT-style `GTRecipeType` for kinetic/electric machines, and Create-style `ProcessingRecipe` for fan catalyst processing.

### GT Recipe Types (`registry/CTPPRecipeTypes.java`)
Registered via `CTPPRegistration.REGISTRATE.recipeType(...)`. Kinetic types use `StressRecipeCapability` (key `"su"`, Float) instead of EU, with `RPMCondition` + `MechanicalTierCondition`.

| Constant | Registry ID | 中文 | Group | I/O items | I/O fluids | Notes |
|---|---|---|---|---|---|---|
| `KINETIC_MIXER_RECIPES` | `kinetic_mixer` | 应力搅拌 | KINETIC | 6/1 | 2/1 | Commented out |
| `SMASHING_FACTORY_RECIPES` | `smashing_factory_recipes` | 粉碎工厂 | KINETIC | 1/4 | 0/0 | Auto-gen from `MACERATOR_RECIPES`; strips chanced outputs; reads tier/voltage limits from config |
| `KINETIC_GENERATOR_RECIPES` | `kinetic_generator` | 应力发电 | KINETIC | 0/0 | 1/0 | Stress → EU |
| `KINETIC_STEAM_TURBINE_RECIPES` | `kinetic_steam_turbine` | 蒸汽动力 | KINETIC | 0/0 | 1/1 | Steam → EU |
| `SEAWEED_FARM` | `seaweed_farm` | 海草养殖 | ELECTRIC | 2/4 | 0/1 | Multiblock |
| `WINDMILL_CONTROL` | `windmill_control_center` | 风车控制中心 | ELECTRIC | 0/0 | 1/0 | Multiblock |
| `BOOM_OF_CREATE` | `boom_of_create` | 聚爆应力 | KINETIC | 1/0 | 1/0 | EU IN, explosive catalyst → stress |
| `BIG_DAM` | `big_dam` (GTCEu namespace) | 三峡大坝 | ELECTRIC | 0/0 | 1/0 | Registered under `GTCEu.id(...)` |

### Create Fan Processing Recipes (`common/data/recipe/fan_processing/CTPPRecipeTypeInfo.java`)
Uses `IRecipeTypeInfo` / `ProcessingRecipe` from Create. Registered as `DeferredRegister` entries under `ctpp` namespace.

| Enum | ID | Max In/Out | Purpose |
|---|---|---|---|
| `BREATHING` | `ctpp:breathing` | 1/12 | Fan blowing catalyst |
| `ACIDWASHING` | `ctpp:acidwashing` | 4/12 | Fan washing - acid catalyst |
| `OILING` | `ctpp:oiling` | 1/12 | Fan processing - oil catalyst |

### Wrapped Create / Addon Recipe Builders (`common/data/recipe/builder/create/`)
CTPP wraps Create and addon recipe types with datagen-friendly builders. These are NOT new recipe types; they produce standard Create/addon recipe JSON.

**Create vanilla (9 builders)** — direct JSON builders with `"type": "create:<name>"`:

| Builder | Recipe type | Notes |
|---|---|---|
| `CompactingRecipeBuilder` | `create:compacting` | item/fluid I/O, heated/superheated |
| `CrushingRecipeBuilder` | `create:crushing` | item I/O with chanced outputs per entry |
| `CuttingRecipeBuilder` | `create:cutting` | item I/O |
| `ItemApplicationRecipeBuilder` | `create:item_application` | item I/O (deployer-style) |
| `MechanicalCraftingRecipeBuilder` | `create:mechanical_crafting` | shaped pattern with key |
| `MillingRecipeBuilder` | `create:milling` | item I/O with chanced outputs per entry |
| `MixingRecipeBuilder` | `create:mixing` | item/fluid I/O, heated/superheated |
| `SequencedAssemblyRecipeBuilder` | `create:sequenced_assembly` | multi-step (filling, pressing, deploying, cutting, curving) |
| `SplashingRecipeBuilder` | `create:splashing` | item I/O |

**Create Diesel Generators (2 builders)** — use `ProcessingRecipeBuilder<>`:

| Builder | Target recipe class | Mod |
|---|---|---|
| `BasinFermentingRecipeBuilder` | `BasinFermentingRecipe` | createdieselgenerators |
| `DistillationRecipeBuilder` | `DistillationRecipe` | createdieselgenerators |

**Create Metallurgy (5 builders)** — use mod-specific builder APIs:

| Builder | Target recipe class / type | Mod |
|---|---|---|
| `AlloyingRecipeBuilder` | `AlloyingRecipe` (via `ProcessingRecipeBuilder`) | createmetallurgy |
| `BulkMeltingRecipeBuilder` | `BulkMeltingRecipe` (via `FoundryRecipeBuilder`) | createmetallurgy |
| `CastingInBasinRecipeBuilder` | `CMRecipeTypes.CASTING_IN_BASIN` (via `CastingRecipeBuilder`) | createmetallurgy |
| `CastingInTableRecipeBuilder` | `CMRecipeTypes.CASTING_IN_TABLE` (via `CastingRecipeBuilder`) | createmetallurgy |
| `MeltingRecipeBuilder` | `MeltingRecipe` (via `ProcessingRecipeBuilder`) | createmetallurgy |

**Vintage Improvements (7 builders)** — extend `AbstractVintageRecipeBuilder`, use `VintageRecipes` enum:

| Builder | VintageRecipes enum | Notes |
|---|---|---|
| `CentrifugationRecipeBuilder` | `CENTRIFUGATION` | item/fluid I/O, RPM, heat |
| `CoilingRecipeBuilder` | `COILING` | item/fluid I/O, RPM, heat |
| `CurvingRecipeBuilder` | `CURVING` | item/fluid I/O, RPM, heat |
| `HammeringRecipeBuilder` | `HAMMERING` | item/fluid I/O, RPM, heat |
| `PressurizingRecipeBuilder` | `PRESSURIZING` | item/fluid I/O, RPM, heat |
| `TurningRecipeBuilder` | `TURNING` | item/fluid I/O, RPM, heat |
| `VibratingRecipeBuilder` | `VIBRATING` | item/fluid I/O, RPM, heat |

### Custom recipe infrastructure
- **Capability** `StressRecipeCapability` (`"su"` key, Float) — kinetic stress I/O for GT recipes; drives parallel calculation in `KineticWorkableMultiblockMachine` / `KineticOutputMachine`.
- **KubeJS keys** `CTPPGTAddon.SU_IN` / `SU_OUT` — script-facing stress recipe components registered by `registerRecipeKeys()`.
- **Conditions** `RPMCondition` (`"rpm"`) and `MechanicalTierCondition` (`"mechanical_tier"`) — RPM/tier requirements on kinetic recipes.
- **Modifiers** `KINETIC_PARALLEL` (stress-multiplier + accurate parallel) and `KINETIC_PERFECT_PARALLEL` (perfect parallel variant) — both target `KineticWorkableMultiblockMachine`.
- **Recipe builder** `CTPPRecipeBuilder` extends `GTRecipeBuilder` with `.rpm(float)`, `.tier(int)`, `.inputStress(float)`, `.outputStress(float)`, `.noEUt()`.

## CONVENTIONS
- Namespace is `com.mo_guang.ctpp`; class prefixes use `CTPP`.
- `src/generated/resources` contains many Create/Forge/Minecraft tag outputs from datagen.
- Static machine part models also exist under `src/main/resources`; check path before regenerating or editing.
- Create kinetic behavior is patched through mixins and dynamic contraption classes; inspect both when changing rotation or moving-block behavior.

## COMMANDS
```bash
./gradlew :modules:CTPP:build
./gradlew :modules:CTPP:runData
./gradlew :modules:CTPP:spotlessCheck
```

## ANTI-PATTERNS
- Do not treat all recipe JSON as equivalent: fan catalyst/generated outputs and static assets live in different source roots.
- Do not change kinetic/electric machine tiers without checking both registry code and generated models/recipes.
- Do not add stress I/O by raw JSON keys alone; use `StressRecipeCapability`, KubeJS recipe keys, and `CTPPRecipeBuilder` together.
