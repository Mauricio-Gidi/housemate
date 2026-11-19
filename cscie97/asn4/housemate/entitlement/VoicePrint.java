package cscie97.asn4.housemate.entitlement;

import java.util.Objects;

/** Instructor-specified simulated voiceprint. */
public final class VoicePrint {
    private final String guid;
    private final String signature;

    public VoicePrint(String guid, String signature) {
        this.guid = Objects.requireNonNull(guid, "guid");
        this.signature = Objects.requireNonNull(signature, "signature");
    }

    public String getGuid() {
        return guid;
    }

    public String getSignature() {
        return signature;
    }
}