package biz.pdxtech.daap.ledger;

/**
 * Created by bochenlong on 16-10-18.
 */
public class HexUtil {
    private static final char[] HEX_DIGITS = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C',
            'D', 'E', 'F',
    };

    /**
     * byte[] -> hexString
     *
     * @param input
     * @return
     */
    public static String encode(byte[] input) {
        if (input == null)
            throw new IllegalArgumentException("input should not be null");

        StringBuilder builder = new StringBuilder(input.length * 2);
        for (int i = 0; i < input.length; i++) {
            builder.append(HEX_DIGITS[input[i] >>> 4 & 0xf]);
            builder.append(HEX_DIGITS[input[i] & 0xf]);
        }
        return builder.toString();
    }

    /**
     * hexString -> byte[]
     *
     * @param input
     * @return
     */
    public static byte[] decodeToByteArray(String input) {
        if (input == null)
            throw new IllegalArgumentException("input should not be null");

        if (input.length() % 2 != 0)
            throw new IllegalArgumentException("input should be divisible by four");

        String alphabet = new String(HEX_DIGITS);
        String temp = input.toUpperCase();

        byte[] output = new byte[temp.length() / 2];
        for (int i = 0; i < output.length; i++) {

            int highByte = alphabet.indexOf(temp.charAt(i * 2));
            int lowByte = alphabet.indexOf(temp.charAt(i * 2 + 1));
            if (highByte == -1 || lowByte == -1)
                throw new IllegalArgumentException("input contiain illegal character");

            output[i] = (byte) (highByte << 4 | lowByte);
        }
        return output;
    }

}
