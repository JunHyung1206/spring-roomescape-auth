package roomescape.domain;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Store {
    private final Long id;
    private final String name;
    private final Long managerId;

    public static Store create(long id, String name, long managerId) {
        return new Store(id, name, managerId);
    }

    public Long id() {
        return id;
    }

    public String name() {
        return name;
    }

    public Long managerId() {
        return managerId;
    }

    public boolean isManagedBy(long userId) {
        return managerId != null && managerId == userId;
    }
}
