package com.example.betterleavestop.mixin;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Options;
import net.minecraft.server.packs.repository.PackRepository;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Mixin(Options.class)
public abstract class OptionsMixin {

    // Change this if your pack file/folder is named differently (without ".zip")
    private static final String PACK_NAME = "Better-Leaves-9.5";

    @Shadow
    public List<String> resourcePacks;

    // Last entry in the list = top of the selected packs in the UI
    @Inject(method = "loadSelectedResourcePacks", at = @At("HEAD"))
    private void betterleavesTop$moveToTop(PackRepository repository, CallbackInfo ci) {
        String id = betterleavesTop$findPackId();
        if (id == null) {
            return; // pack not found in resourcepacks folder
        }

        List<String> list = new ArrayList<>(this.resourcePacks);
        List<String> cleaned = new ArrayList<>();
        for (String entry : list) {
            if (!entry.equals(id) && !entry.equals(PACK_NAME) && !entry.equals(PACK_NAME + ".zip")) {
                cleaned.add(entry);
            }
        }
        cleaned.add(id);
        this.resourcePacks = cleaned;
    }

    private static String betterleavesTop$findPackId() {
        Path dir = FabricLoader.getInstance().getGameDir().resolve("resourcepacks");
        try (Stream<Path> files = Files.list(dir)) {
            for (Path p : (Iterable<Path>) files::iterator) {
                String name = p.getFileName().toString();
                if (name.equals(PACK_NAME) || name.equals(PACK_NAME + ".zip")) {
                    return "file/" + name;
                }
            }
        } catch (IOException ignored) {
        }
        return null;
    }
}
