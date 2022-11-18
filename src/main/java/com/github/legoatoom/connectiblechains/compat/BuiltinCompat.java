package com.github.legoatoom.connectiblechains.compat;

import com.github.legoatoom.connectiblechains.chain.ChainTypesRegistry;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class BuiltinCompat {

    /**
     * A list of item ids that this mod provides basic support for by default
     */
    public static final Set<Identifier> BUILTIN_TYPES = new HashSet<>() {{
        for (String s : Arrays.asList("adamantite", "aquarium", "banglum", "bronze", "carmot", "celestium", "durasteel", "hallowed", "kyber", "manganese", "metallurgium", "midas_gold", "mythril", "orichalcum", "osmium", "palladium", "platinum", "prometheum", "quadrillum", "runite", "silver", "star_platinum", "steel", "stormyx")) {
            add(new Identifier("mythicmetals_decorations:%s_chain".formatted(s)));
        }
        for (String s : Arrays.asList("golden", "netherite", "copper", "exposed_copper", "weathered_copper", "oxidized_copper", "waxed_copper", "waxed_exposed_copper", "waxed_weathered_copper", "waxed_oxidized_copper")) {
            add(new Identifier("valley:%s_chain".formatted(s)));
        }
        add(new Identifier("betterend:thallasium_chain"));
        add(new Identifier("betterend:terminite_chain"));
        add(new Identifier("betternether:cincinnasite_chain"));
        add(new Identifier("charm:gold_chain"));
        add(new Identifier("dustrial_decor:gold_chain"));
        add(new Identifier("blockus:golden_chain"));
        add(new Identifier("blockus:golden_chain"));
        add(new Identifier("architects_palette:nether_brass_chain"));
    }};


    private static final Set<Identifier> REGISTERED_BUILTIN_TYPES = new HashSet<>();

    /**
     * Registers all builtin types for currently registered items and
     * sets up a listener for furure item registrations.
     */
    public static void init() {
        RegistryEntryAddedCallback.event(Registry.ITEM).register((rawId, id, object) -> registerTypeForBuiltin(id));

        for (Identifier itemId : BUILTIN_TYPES) {
            registerTypeForBuiltin(itemId);
        }
    }

    /**
     * Checks if a builtin type exists for {@code itemId} and then registers a type for it once.
     *
     * @param itemId The id of an item
     */
    public static void registerTypeForBuiltin(Identifier itemId) {
        if (!BUILTIN_TYPES.contains(itemId)) return;
        if (REGISTERED_BUILTIN_TYPES.contains(itemId)) return;
        if (!Registry.ITEM.containsId(itemId)) return;
        ChainTypesRegistry.register(Registry.ITEM.get(itemId));
        REGISTERED_BUILTIN_TYPES.add(itemId);
    }
}
