package roomescape.domain;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class User {
    private final Long id;
    private final String name;
    private final String loginId;
    private final String password;

    public static User create(long id, String name, String userId, String password) {
        return new User(id, name, userId, password);
    }

    public Long id() {
        return id;
    }

    public String name() {
        return name;
    }

    public String loginId() {
        return loginId;
    }

    public String password() {
        return password;
    }
}
