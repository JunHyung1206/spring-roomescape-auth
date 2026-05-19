package roomescape.domain;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class User {
    private final Long id;
    private final String name;
    private final String loginId;
    private final String password;
    private final Role role;

    public static User create(long id, String name, String loginId, String password, String role) {
        return new User(id, name, loginId, password, Role.valueOf(role));
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

    public Role role() {
        return role;
    }

    public boolean isAdmin() {
        return role == Role.ADMIN;
    }
}
