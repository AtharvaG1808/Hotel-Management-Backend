package cg.dfs.hotel.dto;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class RoomInventoryUpdateRequest {

    @NotNull
    @Min(value = 0, message = "inventory cannot be negative")
    private Integer inventory;

    public RoomInventoryUpdateRequest() {}
    public RoomInventoryUpdateRequest(Integer inventory) { this.inventory = inventory; }

    public Integer getInventory() { return inventory; }
    public void setInventory(Integer inventory) { this.inventory = inventory; }
}
