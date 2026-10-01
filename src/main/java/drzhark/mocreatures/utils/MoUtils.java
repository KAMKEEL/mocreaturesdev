package drzhark.mocreatures.utils;

import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

public class MoUtils {

    /** Gets block light without including sunlight. */
    public static int getBlockLightValue(Chunk chunk, int x, int y, int z) {
        ExtendedBlockStorage storage = chunk.getBlockStorageArray()[y >> 4];
        if (storage == null) {
            return 0;
        }
        return storage.getExtBlocklightValue(x, y & 15, z);
    }
}
