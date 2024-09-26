package net.brights0ng.playground.main;

public class Imperium {
    private int imperiumQuantity;

    public Imperium() {
        this.imperiumQuantity = 100;
    }

    public void addImperium(int quantity) {
        this.imperiumQuantity += quantity;
    }

    public int getImperium(){
        return this.imperiumQuantity;
    }

}
