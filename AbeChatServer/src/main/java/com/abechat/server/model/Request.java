package com.abechat.server.model;


import org.springframework.security.crypto.password.PasswordEncoder;


public class Request {
    // eventually want to be able to clear out these passwords in memory once they are used
    // Run into argon2 matching issues if I use char[] - I suspect some sort of encoding issue
    // byte[] is not serializable as JSON from what I can tell.

    public record Login(String username, String password) {
        @Override
        public String toString() {
            return "LoginRequest{" +
                    "username = " + username + ", " +
                    "password = [REDACTED]}";
        }
    }
    public record NewUser(String username, String password) {

        /**
         * Takes {@code this} NewUser, hashes the password, clears {@code this.password} so the password from the request
         * is no longer in memory (TBD), and returns a {@link NewHashedUser} indicating this is a new User that has a hashed password.
         * @param passwordEncoder the password encoder used to has this users password
         * @return a user with a hashed password (no more plain text)
         */
        public NewHashedUser intoHashedUser(PasswordEncoder passwordEncoder) {
            var hashedPassword = passwordEncoder.encode(password);
            return new NewHashedUser(username, hashedPassword);
        }

    }
}
