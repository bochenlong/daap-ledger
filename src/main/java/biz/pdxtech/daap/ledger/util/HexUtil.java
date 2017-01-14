package biz.pdxtech.daap.ledger.util;

import org.apache.commons.lang3.StringUtils;
import org.spongycastle.util.encoders.Hex;

/**
 * Created by bochenlong on 16-10-18.
 */
public class HexUtil {
    public static String toHex(byte[] data) {
        if (data == null || data.length == 0) {
            return null;
        }
        return Hex.toHexString(data);
    }
    
    public static byte[] hexToBytes(String data) {
        if (StringUtils.isBlank(data) || data.length() % 2 != 0) {
            return null;
        }
        return Hex.decode(data);
    }
}
