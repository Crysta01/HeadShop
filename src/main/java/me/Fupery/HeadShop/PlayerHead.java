package me.Fupery.HeadShop;

public class PlayerHead {

    private String owner;
    private String displayName;
    private Integer cost;

    public PlayerHead(String owner, String name, Integer cost) {
        this.owner = owner;
        this.displayName = name;
        this.cost = (cost == null) ? HeadShop.getDefaultCost() : cost;
    }

    public String getOwner() {
        return owner;
    }

    public String getName() {
        return displayName;
    }

    public Integer getCost() {
        return cost;
    }
}
