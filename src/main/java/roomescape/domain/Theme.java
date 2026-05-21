package roomescape.domain;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Theme {
    private final Long id;
    private final String name;
    private final String thumbnailUrl;
    private final String description;
    private final Long storeId;

    public static Theme create(long id, String name, String thumbnailUrl, String description, long storeId) {
        return new Theme(id, name, thumbnailUrl, description, storeId);
    }

    public long id() {
        return id;
    }

    public String name() {
        return name;
    }

    public String thumbnailUrl() {
        return thumbnailUrl;
    }

    public String description() {
        return description;
    }

    public Long storeId() {
        return storeId;
    }
}
