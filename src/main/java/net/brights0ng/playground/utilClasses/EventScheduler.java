package net.brights0ng.playground.utilClasses;

import net.brights0ng.playground.main.State;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class EventScheduler {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public void borderTask(State state, ServerPlayerEntity player) {
        // Schedule the borderTask to run every 2 seconds
        scheduler.scheduleAtFixedRate(() -> {
            state.createBorder(player); // Your method to create the border
        }, 0, 2, TimeUnit.SECONDS);

        // Stop the scheduler after 30 seconds
        scheduler.schedule(scheduler::shutdown, 30, TimeUnit.SECONDS);
    }

}
