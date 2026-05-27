# CTPP KNOWLEDGE BASE

## OVERVIEW
CTPP (`CT++`) is the Create/GregTech compatibility module. It defines kinetic/electric machines, Create fan catalyst recipes, custom recipe builders, generated data, and GTCEu addon registration under mod id `ctpp`.

## WHERE TO LOOK
- Mod entry: `src/main/java/com/mo_guang/ctpp/CTPP.java`. Forge mod initialization.
- GT addon: `src/main/java/com/mo_guang/ctpp/CTPPGTAddon.java`. GTCEu integration.
- Registrate: `src/main/java/com/mo_guang/ctpp/CTPPRegistrate.java`, `CTPPRegistration.java`. Core registration helpers.
- API: `src/main/java/com/mo_guang/ctpp/api/`. Recipe capabilities, multiblock builder, predicates, parallel logic.
- Recipes/datagen: `src/main/java/com/mo_guang/ctpp/common/data/recipe/`. Recipe builders/providers.
- Models: `src/main/java/com/mo_guang/ctpp/common/data/model/`. Machine model generation.
- Generated resources: `src/generated/resources/data/ctpp/recipes/`. Fan catalyst and machine recipe output.
- Static resources: `src/main/resources/assets/ctpp/`. Hand-authored models/assets.

## REGISTRATION ENTRYPOINTS
- Registrate/root: `CTPPRegistration.java`, `CTPPRegistrate.java`; mod/addon entrypoints are `CTPP.java` and `CTPPGTAddon.java`.
- Items/blocks/entities: `registry/CTPPItems.java`, `registry/CTPPBlocks.java`, `CTPPEntityTypes.java`.
- Machines/multiblocks: `registry/CTPPMachines.java`, `registry/CTPPMultiblockMachines.java`, builder support in `api/CTPPMultiblockBuilder.java`.
- Materials: `registry/CTPPMaterials.java`, `registry/GTMaterialAddon.java`.
- Recipe types/modifiers/capabilities/conditions: `registry/CTPPRecipeTypes.java`, `CTPPRecipeModifiers.java`, `api/CTPPRecipeCapabilities.java`, `api/CTPPRecipeConditions.java`.
- Recipe generation: `CTPPGTAddon.addRecipes()` calls `common/data/recipe/CTPPRecipes.java`; specific Create/kinetic recipes live under `common/data/recipe/` and `common/data/recipe/builder/`.
- Datagen/fan processing: `data/CTPPDatagen.java`, `common/data/recipe/fan_processing/`.

## CONVENTIONS
- Namespace is `com.mo_guang.ctpp`; class prefixes use `CTPP`.
- `src/generated/resources` contains many Create/Forge/Minecraft tag outputs from datagen.
- Static machine part models also exist under `src/main/resources`; check path before regenerating or editing.

## COMMANDS
```bash
./gradlew :modules:CTPP:build
./gradlew :modules:CTPP:runData
./gradlew :modules:CTPP:spotlessCheck
```

## ANTI-PATTERNS
- Do not treat all recipe JSON as equivalent: fan catalyst/generated outputs and static assets live in different source roots.
- Do not change kinetic/electric machine tiers without checking both registry code and generated models/recipes.
