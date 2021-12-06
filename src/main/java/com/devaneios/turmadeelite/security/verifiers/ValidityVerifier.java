package com.devaneios.turmadeelite.security.verifiers;

import javax.naming.AuthenticationException;

public interface ValidityVerifier {
    void verify() throws AuthenticationException;
}
