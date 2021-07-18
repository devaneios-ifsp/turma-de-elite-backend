package com.devaneios.turmadeelite.security.verifiers;

import javax.naming.AuthenticationException;

public interface ValidityVerifier {
    public void verify() throws AuthenticationException;
}
