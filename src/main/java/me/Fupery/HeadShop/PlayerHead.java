package me.Fupery.HeadShop;

public class PlayerHead {

    private String owner;
    private String displayName;
    private Integer cost;

    public PlayerHead(String owner, String name, Integer cost) {
        this.owner = owner;
        this.displayName = name;
        this.cost = cost;
    }

    public String getOwner() {
        return owner;
    }

    public String getName() {
        return displayName;
    }

    public boolean hasCost() {
        return cost != null;
    }

    public Integer getCost() {
        return (cost != null) ? cost : HeadShop.getDefaultCost();
    }
}
