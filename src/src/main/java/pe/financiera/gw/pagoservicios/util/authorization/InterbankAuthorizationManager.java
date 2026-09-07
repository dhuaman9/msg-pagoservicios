package pe.financiera.gw.pagoservicios.util.authorization;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pe.financiera.gw.pagoservicios.interbank.business.domain.OpenBankingAccess;
import pe.financiera.gw.pagoservicios.interbank.services.proxy.security.SecurityRestClientAdapter;

import java.time.Instant;
import java.util.Optional;

@Slf4j
@Component
public class InterbankAuthorizationManager {
    private OpenBankingAccess openBankingAccess = null;

    @Autowired
    private JWTParser jwtParser;

    @Autowired
    private SecurityRestClientAdapter securityRestClientAdapter;

    public Optional<OpenBankingAccess> getOpenBankingAccess() {
        processAccess();
        return Optional.ofNullable(openBankingAccess);
    }

    public synchronized boolean processAccess() {
        Optional<OpenBankingAccess> accessOpt = Optional.ofNullable(openBankingAccess);
        boolean hasAccess = false;
        if (accessOpt.isPresent()) {
            String jwt = accessOpt.get().getAccessToken();
            Optional<JWTParser.Access> opt = jwtParser.parse(jwt);
            if (opt.isPresent()) {
                JWTParser.Access access = opt.get();
                Long now = Instant.now().getEpochSecond();
                if (access.getExp() <= now) {
                    // expired token
                    hasAccess = renewCredentials();
                } else {
                    log.info("Token already valid");
                    hasAccess = true;
                }
            } else {
                hasAccess = renewCredentials();
            }
        } else {
            hasAccess = renewCredentials();
        }
        return hasAccess;
    }

    private synchronized void updateAccess(OpenBankingAccess openBankingAccess) {
        this.openBankingAccess = openBankingAccess;
    }


    private boolean renewCredentials() {
        boolean credentialsRenewed = false;
        try {
            OpenBankingAccess newCredentials = securityRestClientAdapter.getAccess();
            updateAccess(newCredentials);
            credentialsRenewed = true;
        } catch (Exception exception) {
            log.error("Exception occurred: ", exception);
        }
        return credentialsRenewed;
    }
}
