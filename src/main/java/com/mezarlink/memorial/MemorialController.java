package com.mezarlink.memorial;

import com.mezarlink.auth.User;
import com.mezarlink.auth.UserRepository;
import com.mezarlink.memorial.dto.MemorialRequest;
import com.mezarlink.memorial.dto.MemorialResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/memorials")
public class MemorialController {

    private final MemorialService memorialService;
    private final UserRepository userRepository;

    public MemorialController(MemorialService memorialService, UserRepository userRepository) {
        this.memorialService = memorialService;
        this.userRepository = userRepository;
    }

    // PUBLIC: QR kod okutan herkes buraya erisebilir, giris gerekmez.
    // SecurityConfig'de bu path'in herkese acik oldugunu unutma.
    @GetMapping("/{slug}")
    public ResponseEntity<MemorialResponse> getBySlug(@PathVariable String slug) {
        Memorial memorial = memorialService.getBySlug(slug);
        return ResponseEntity.ok(MemorialResponse.from(memorial));
    }

    // PRIVATE: sadece giris yapmis kullanicinin kendi sayfalarini listeler.
    @GetMapping("/mine")
    public ResponseEntity<List<MemorialResponse>> getMine(Authentication authentication) {
        User currentUser = currentUser(authentication);

        List<MemorialResponse> memorials = memorialService.getAllByOwner(currentUser)
                .stream()
                .map(MemorialResponse::from)
                .toList();

        return ResponseEntity.ok(memorials);
    }

    // PRIVATE: yeni ani sayfasi olusturur, slug otomatik uretilir.
    @PostMapping
    public ResponseEntity<MemorialResponse> create(
            @Valid @RequestBody MemorialRequest request,
            Authentication authentication
    ) {
        User currentUser = currentUser(authentication);
        Memorial created = memorialService.create(request, currentUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(MemorialResponse.from(created));
    }

    // PRIVATE: sadece sayfanin sahibi guncelleyebilir (MemorialService.assertOwnership).
    @PutMapping("/{id}")
    public ResponseEntity<MemorialResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody MemorialRequest request,
            Authentication authentication
    ) {
        User currentUser = currentUser(authentication);
        Memorial updated = memorialService.update(id, request, currentUser);

        return ResponseEntity.ok(MemorialResponse.from(updated));
    }

    // PRIVATE: sadece sayfanin sahibi silebilir.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id, Authentication authentication) {
        User currentUser = currentUser(authentication);
        memorialService.delete(id, currentUser);

        return ResponseEntity.noContent().build();
    }

    private User currentUser(Authentication authentication) {
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Kullanici bulunamadi"));
    }
}
