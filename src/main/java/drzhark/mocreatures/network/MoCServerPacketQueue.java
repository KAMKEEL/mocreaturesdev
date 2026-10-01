package drzhark.mocreatures.network;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import drzhark.mocreatures.utils.MoCLog;

public final class MoCServerPacketQueue {

    private static final Queue<Runnable> TASKS = new ConcurrentLinkedQueue<Runnable>();

    public MoCServerPacketQueue() {}

    public static void enqueue(Runnable task) {
        TASKS.add(task);
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.START) {
            return;
        }

        Runnable task;
        while ((task = TASKS.poll()) != null) {
            try {
                task.run();
            } catch (RuntimeException exception) {
                MoCLog.logger.error("Error while handling a server packet", exception);
            }
        }
    }
}
