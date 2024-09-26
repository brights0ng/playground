package net.brights0ng.playground.registries;

import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Objects;

public class ImperiumRegistry {

    public static void registerImperium(){
        ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register((world, entity, killedEntity) -> {
            if (entity instanceof ServerPlayerEntity killer && killedEntity instanceof ServerPlayerEntity victim){
                String string = "You have been slain by ";
                if (killer.getUuid().equals(victim.getUuid())){
                    victim.sendMessage(Text.literal("You have killed yourself...{insert.revenge_of_the_sith_gif}"), false);
                } else {
                    Objects.requireNonNull(StateRegistry.getStateByPlayerUUID(victim.getUuid())).imperium.addImperium(-10);
                    victim.sendMessage(Text.literal(string + Objects.requireNonNull(victim.getDisplayName()).toString() + ". You have lost 10 Imperium"), false);

                    Objects.requireNonNull(StateRegistry.getStateByPlayerUUID(victim.getUuid())).imperium.addImperium(10);
                    killer.sendMessage(Objects.requireNonNull(victim.getDisplayName()).copy().append(" has been killed. You have gained 10 Imperium."), false);
                }
            }
        });
    }

}
