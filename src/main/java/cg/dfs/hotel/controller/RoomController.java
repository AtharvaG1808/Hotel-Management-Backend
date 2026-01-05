package cg.dfs.hotel.controller;

import cg.dfs.hotel.dto.RoomCreateUpdateRequest;
import cg.dfs.hotel.dto.RoomDTO;
import cg.dfs.hotel.dto.RoomInventoryUpdateRequest;
import cg.dfs.hotel.service.RoomService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;
    public RoomController(RoomService roomService) { this.roomService = roomService; }

    @PreAuthorize("@roomSecurity.canManageHotel(#request.hotelId)")
    @PostMapping
    public ResponseEntity<RoomDTO> create(@Valid @RequestBody RoomCreateUpdateRequest request) {
        return ResponseEntity.ok(roomService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(roomService.getById(id));
    }

    @GetMapping
    public ResponseEntity<Page<RoomDTO>> list(
            @RequestParam(name="hotelId", required=false) Long hotelId,
            @RequestParam(name="type", required=false) String type,
            Pageable pageable) {
        return ResponseEntity.ok(roomService.list(hotelId, type, pageable));
    }

    @PreAuthorize("@roomSecurity.canManageHotel(#request.hotelId)")
    @PutMapping("/{id}")
    public ResponseEntity<RoomDTO> update(@PathVariable("id") Long id,
                                          @Valid @RequestBody RoomCreateUpdateRequest request) {
        return ResponseEntity.ok(roomService.update(id, request));
    }

    @PreAuthorize("@roomSecurity.canManageRoom(#id)")
    @PatchMapping("/{id}/inventory")
    public ResponseEntity<RoomDTO> updateInventory(@PathVariable("id") Long id,
                                                   @Valid @RequestBody RoomInventoryUpdateRequest request) {
        return ResponseEntity.ok(roomService.updateInventory(id, request.getInventory()));
    }

    @PreAuthorize("@roomSecurity.canManageRoom(#id)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        roomService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

