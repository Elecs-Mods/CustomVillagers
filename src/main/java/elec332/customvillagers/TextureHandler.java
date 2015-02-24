package elec332.customvillagers;

import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.util.ResourceLocation;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Elec332 on 12-2-2015.
 */
public class TextureHandler implements IResourcePack {

    String path = "config/Elec's mods/Villagers";

    @Override
    public InputStream getInputStream(ResourceLocation rl) throws IOException {
        return !this.resourceExists(rl) ? null : new FileInputStream(new File(path + "/" + rl.getResourceDomain(), rl.getResourcePath()));
    }

    @Override
    public boolean resourceExists(ResourceLocation rl) {
        File fileRequested = new File(path +  "/" + rl.getResourceDomain(), rl.getResourcePath());
        return fileRequested.exists();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Set getResourceDomains() {
        File folder = new File(path);
        if(!folder.exists()) {
            folder.mkdir();
        }

        String[] content = folder.list();
        HashSet folders = new HashSet();


        for(String s : content) {
            File f = new File(folder, s);
            if(f.exists() && f.isDirectory()) {
                folders.add(f.getName());
            }
        }

        return folders;
    }


    public IMetadataSection getPackMetadata(IMetadataSerializer p_135058_1_, String p_135058_2_) throws IOException {
        return null;
    }

    public BufferedImage getPackImage() throws IOException {
        return null;
    }

    public String getPackName() {
        return "CustomVillagers";
    }
}
