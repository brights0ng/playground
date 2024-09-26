package net.brights0ng.playground.definitions;

import java.util.EnumSet;

public class DefaultRoles {
    public static Role createRoleOwner() {
        Role role = new Role("Owner");
        role.addPermSet(EnumSet.allOf(Perm.class));
        return role;
    }

    public static Role createRoleAdmin() {
        Role role = new Role("Admin");
        role.addPermSet(EnumSet.allOf(Perm.class));
        return role;
    }

    public static Role createRoleCitizen() {
        Role role= new Role("Citizen");
        role.addPermSet(EnumSet.allOf(Perm.class));
        return role;
    }

    public static Role createRoleAlly() {
        Role role = new Role("Ally");
        role.addPermSet(EnumSet.of(Perm.USE_REDSTONE,Perm.USE_BLOCK,Perm.PLACE_BLOCK,Perm.BREAK_BLOCK,Perm.USE_ENTITIES));
        return role;
    }

    public static Role createRoleGuest() {
        Role role = new Role("Guest");
        role.addPermSet(EnumSet.of(Perm.USE_REDSTONE,Perm.USE_ENTITIES));
        return role;
    }

}
