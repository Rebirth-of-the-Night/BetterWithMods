package betterwithmods.client;
/* Resource Proxy borrowed from Quark*/

import betterwithmods.BWMod;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import net.minecraft.client.resources.AbstractResourcePack;
import net.minecraftforge.fml.common.Loader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;

public class ResourceProxy extends AbstractResourcePack {

    private static final String MINECRAFT = "minecraft";
    private static final Set<String> RESOURCE_DOMAINS = ImmutableSet.of(MINECRAFT);

    private static final String BARE_FORMAT = "assets/%s/%s/%s/%s.%s";
    private static final String OVERRIDE_FORMAT = "/assets/%s/%s/%s/overrides/%s.%s";

    private static final Map<String, String> overrides = Maps.newHashMap();

    public ResourceProxy() {
        super(Loader.instance().activeModContainer().getSource());
        overrides.put("pack.mcmeta", "/proxypack.mcmeta");
    }

    public void addResource(String space, String domain, String dir, String file, String ext) {
        String bare = String.format(BARE_FORMAT, domain, space, dir, file, ext);
        String override = String.format(OVERRIDE_FORMAT, domain, space, dir, file, ext);
        overrides.put(bare, override);
        BWMod.logger.info("Override texture: {} to {}", bare, override);
    }

    public void addResource(String space, String dir, String file, String ext) {
        String bare = String.format(BARE_FORMAT, MINECRAFT, space, dir, file, ext);
        String override = String.format(OVERRIDE_FORMAT, BWMod.MODID, space, dir, file, ext);
        overrides.put(bare, override);
        BWMod.logger.info("Override texture: {} to {}", bare, override);
    }


    public ResourceProxy(File resourcePackFileIn) {
        super(resourcePackFileIn);
    }

    @Override
    protected InputStream getInputStreamByName(String name) throws IOException {
        if (name == null)
            return null;
        return BWMod.class.getResourceAsStream(overrides.get(name));
    }

    @Override
    protected boolean hasResourceName(String name) {
        return overrides.containsKey(name);
    }


    @Override
    public Set<String> getResourceDomains() {
        return RESOURCE_DOMAINS;
    }

    @Override
    public String getPackName() {
        return "bwm-texture-proxy";
    }

}