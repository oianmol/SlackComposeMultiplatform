package dev.baseio.security;

import java.security.spec.MGF1ParameterSpec;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource.PSpecified;

/**
 * Contains the constants and enums used by RSA-ECDSA encryption/decryption.
 */
public final class RsaEcdsaConstants {

  static final OAEPParameterSpec OAEP_PARAMETER_SPEC =
      new OAEPParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA1, PSpecified.DEFAULT);
  static final int SIGNATURE_LENGTH_BYTES_LENGTH = 4;

  /**
   * Encapsulates the ciphertext padding modes supported by RSA-ECDSA encryption/decryption.
   */
  public enum Padding {
    OAEP("OAEPPadding"),
    PKCS1("PKCS1Padding");

    private static final String PREFIX = "RSA/ECB/";

    private final String padding;

    Padding(String val) {
      padding = val;
    }

    /**
     * Returns the current padding enum's transformation string that should be used when calling
     * {@code javax.crypto.Cipher.getInstance}.
     *
     * @return the transformation string.
     */
    public String getTransformation() {
      return PREFIX + padding;
    }
  }
}
