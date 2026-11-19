// TokenLifecycleTest.java

package tests;

import cscie97.asn4.housemate.entitlement.EntitlementServiceApi;
import cscie97.asn4.housemate.entitlement.EntitlementServiceImp;
import cscie97.asn4.housemate.entitlement.InvalidAccessTokenException;

/**
 * Tests the lifecycle of access tokens.
 *
 * @author Mauricio Gidi
 * @version November 17, 2026
 */
public final class TokenLifecycleTest {

    public static void main(String[] args) throws InterruptedException {
        long timeoutSeconds = Long.getLong("housemate.tokenTimeoutSeconds", 0L);

        if (timeoutSeconds < 2) {
            throw new IllegalArgumentException(
                    "Set housemate.tokenTimeoutSeconds to at least 2");
        }

        System.out.println("Token timeout: " + timeoutSeconds + " seconds");

        EntitlementServiceApi service = EntitlementServiceImp.getInstance();
        service.bootstrapAdministrator("admin", "Administrator", "admin", "secret");

        long pause = timeoutSeconds * 600;

        String token = service.loginWithUsername("admin", "secret");
        check(service.checkAccess(token, "user_admin", "housemate"),
                "Initial access");

        Thread.sleep(pause);
        check(service.checkAccess(token, "user_admin", "housemate"),
                "Access refreshes timeout");

        Thread.sleep(pause);
        check(service.checkAccess(token, "user_admin", "housemate"),
                "Still active past original expiry");

        Thread.sleep(timeoutSeconds * 1000 + 250);
        expectInvalid(service, token, "Expired token rejected");

        String revoked = service.loginWithUsername("admin", "secret");
        check(service.checkAccess(revoked, "user_admin", "housemate"),
                "New session works");

        service.logout(revoked);
        expectInvalid(service, revoked, "Revoked token rejected directly");

        System.out.println("ALL TOKEN TESTS PASSED");
    }

    private static void check(boolean result, String label) {
        if (!result) {
            throw new AssertionError(label);
        }
        System.out.println("PASS: " + label);
    }

    private static void expectInvalid(
            EntitlementServiceApi service, String token, String label) {

        try {
            service.checkAccess(token, "user_admin", "housemate");
        } catch (InvalidAccessTokenException expected) {
            System.out.println("PASS: " + label);
            return;
        }

        throw new AssertionError(label);
    }
}
