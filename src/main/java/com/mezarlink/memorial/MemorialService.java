package com.mezarlink.memorial;

import com.mezarlink.auth.User;
import com.mezarlink.memorial.dto.MemorialRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class MemorialService {

    private final MemorialRepository memorialRepository;

    public MemorialService(MemorialRepository memorialRepository) {
        this.memorialRepository = memorialRepository;
    }

    public Memorial create(MemorialRequest request, User owner) {
        Memorial memorial = new Memorial();
        memorial.setOwner(owner);
        memorial.setFullName(request.fullName());
        memorial.setBirthDate(request.birthDate());
        memorial.setDeathDate(request.deathDate());
        memorial.setBiography(request.biography());
        memorial.setSlug(generateUniqueSlug(request.fullName()));

        return memorialRepository.save(memorial);
    }

    public Memorial getBySlug(String slug) {
        return memorialRepository.findBySlug(slug)
                .orElseThrow(() -> new MemorialNotFoundException(slug));
    }

    public List<Memorial> getAllByOwner(User owner) {
        return memorialRepository.findByOwner(owner);
    }

    public Memorial update(UUID id, MemorialRequest request, User requester) {
        Memorial memorial = getByIdOrThrow(id);
        assertOwnership(memorial, requester);

        memorial.setFullName(request.fullName());
        memorial.setBirthDate(request.birthDate());
        memorial.setDeathDate(request.deathDate());
        memorial.setBiography(request.biography());

        return memorialRepository.save(memorial);
    }

    public void delete(UUID id, User requester) {
        Memorial memorial = getByIdOrThrow(id);
        assertOwnership(memorial, requester);

        memorialRepository.delete(memorial);
    }

    public Memorial getByIdOrThrow(UUID id) {
        return memorialRepository.findById(id)
                .orElseThrow(() -> new MemorialNotFoundException(id.toString()));
    }

    // Yetki kontrolunun tek yeri burasi. Ileride "memorial editorleri" eklenirse
    // (ailenin birden fazla uyesi duzenleyebilsin diye) sadece bu metot degisir,
    // controller'lara dokunmaya gerek kalmaz.
    public void assertOwnership(Memorial memorial, User requester) {
        if (!memorial.getOwner().getId().equals(requester.getId())) {
            throw new AccessDeniedException("Bu ani sayfasini duzenleme yetkiniz yok");
        }
    }

    private String generateUniqueSlug(String fullName) {
        String base = slugify(fullName);
        String candidate = base;
        int attempts = 0;

        while (memorialRepository.existsBySlug(candidate)) {
            attempts++;
            candidate = base + "-" + UUID.randomUUID().toString().substring(0, 4);

            if (attempts > 10) {
                throw new IllegalStateException("Benzersiz adres uretilemedi, lutfen tekrar deneyin");
            }
        }

        return candidate;
    }

    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]+");

    private String slugify(String input) {
        String noWhitespace = WHITESPACE.matcher(input).replaceAll("-");
        String normalized = Normalizer.normalize(noWhitespace, Normalizer.Form.NFD);
        String slug = NONLATIN.matcher(normalized).replaceAll("");
        return slug.toLowerCase().replaceAll("-+", "-");
    }
}
