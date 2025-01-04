package org.sxeix.generator;

import org.sxeix.exception.ParameterValidationException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * UUID generation utility class
 */
public class UUIDGen {

    private static final String SHA_1 = "SHA-1";

    /**
     * Create UUIDv5
     *
     * @param namespace a UUID defining the namespace
     * @param name      the value
     * @return a UUID that matches the version 5 specification
     * @throws ParameterValidationException Invalid parameters were provided
     */
    public static UUID makeUUIDv5(final UUID namespace, final byte[] name) throws ParameterValidationException {

        if (namespace == null || name == null) {
            throw new ParameterValidationException("Invalid parameters");
        }

        var messageDigest = getMessageDigest(SHA_1);
        messageDigest.update(toBytes(namespace));
        messageDigest.update(name);
        var sha1Bytes = messageDigest.digest();
        setMetadata(sha1Bytes);
        return fromBytes(sha1Bytes);
    }

    /**
     * Sets various metadata on the sha1 byte array
     * <p>
     * 1. clears the current version
     * 2. sets the version to UUID v5
     * 3. clear the variant
     * 4. set the variant to IETF specification -> <a href="https://datatracker.ietf.org/doc/html/rfc3174">...</a>
     *
     * @param sha1Bytes sha1 byte array
     */
    private static void setMetadata(final byte[] sha1Bytes) {

        // see UUID.randomUUID();
        sha1Bytes[6] &= 0x0f;
        sha1Bytes[6] |= 0x50;
        sha1Bytes[8] &= 0x3f;
        sha1Bytes[8] |= (byte) 0x80;
    }

    /**
     * Gets a new message digest instance for a given algorithm
     *
     * @param algorithm the algorithm
     * @return a new MessageDigest instance
     * @throws ParameterValidationException if the algorithm is incorrect
     */
    private static MessageDigest getMessageDigest(final String algorithm) throws ParameterValidationException {

        try {
            return MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            // change logging
            e.printStackTrace();
            throw new ParameterValidationException("Invalid parameters");
        }
    }

    /**
     * Deconstructs the UUID by doing the inverse of the byte array constructor
     * <p>
     * see private UUID(byte[] data)
     *
     * @param uuid the UUID
     * @return byte array of the UUID
     */
    private static byte[] toBytes(final UUID uuid) {

        // reverse of fromBytes
        var resultBytes = new byte[16];
        var msb = uuid.getMostSignificantBits();
        var lsb = uuid.getLeastSignificantBits();
        for (int i = 0; i < 8; i++)
            resultBytes[i] = (byte) ((msb >> ((7 - i) * 8)) & 0xff);
        for (int i = 8; i < 16; i++)
            resultBytes[i] = (byte) ((lsb >> ((15 - i) * 8)) & 0xff);
        return resultBytes;
    }

    /**
     * Convert a byte array into a UUID by following what the private constructor does for a byte array
     * <p>
     * see private UUID(byte[] data)
     *
     * @param uuidByteArray the byte array
     * @return a UUID from the provided byte array
     */
    private static UUID fromBytes(final byte[] uuidByteArray) {

        // see private UUID(byte[] data)
        var msb = 0L;
        var lsb = 0L;
        assert uuidByteArray.length >= 16;
        for (int i = 0; i < 8; i++)
            msb = (msb << 8) | (uuidByteArray[i] & 0xff);
        for (int i = 8; i < 16; i++)
            lsb = (lsb << 8) | (uuidByteArray[i] & 0xff);
        return new UUID(msb, lsb);
    }

}
