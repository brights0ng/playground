package net.brights0ng.playground.definitions;

import java.util.HashSet;
import java.util.Set;

public class Role {
    private String roleName;
    private Set<Perm> perms;

    public Role(String roleName) {
        this.roleName = roleName;
        this.perms = new HashSet<>();
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Set<Perm> getPerms() {
        return perms;
    }

    public void addPerm(Perm perm) {
        this.perms.add(perm);
    }

    public void addPermSet(Set<Perm> perms) {
        this.perms.addAll(perms);
    }

    public void removePerm(Perm perm) {
        this.perms.remove(perm);
    }

    public boolean hasPerm(Perm perm) {
        return this.perms.contains(perm);
    }



}
