package com.altuncode.myshop.services;

import com.altuncode.myshop.model.PersonPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SessionService {

    private final SessionRegistry sessionRegistry;

    @Autowired
    public SessionService(SessionRegistry sessionRegistry) {
        this.sessionRegistry = sessionRegistry;
    }

    public void setUserSessions(String sessionId, Object principal ) {
        sessionRegistry.registerNewSession(sessionId,principal);
    }

    public void expireUserSessions(String email) {
        List<Object> allPrincipals = sessionRegistry.getAllPrincipals();
        for (Object principal : allPrincipals) {
            if (principal instanceof PersonPrincipal) {
                PersonPrincipal user = (PersonPrincipal) principal;
                if (user.getUsername().equals(email)) {
                    List<SessionInformation> sessions = sessionRegistry.getAllSessions(user, false);
                    for (SessionInformation sessionInfo : sessions) {
                        sessionInfo.expireNow(); // Mark session as expired
                    }
                }
            }
        }
    }
}
