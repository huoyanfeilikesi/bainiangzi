import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;
import java.io.File;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;

public final class XXTEA {

    private static final int DELTA = 0x9E3779B9;

    private static int MX(int sum, int y, int z, int p, int e, int[] k) {
        return (z >>> 5 ^ y << 2) + (y >>> 3 ^ z << 4) ^ (sum ^ y) + (k[p & 3 ^ e] ^ z);
    }

    private XXTEA() {}

    public static final byte[] encrypt(byte[] data, byte[] key) {
        if (data.length == 0) {
            return data;
        }
        return toByteArray(
                encrypt(toIntArray(data, true), toIntArray(fixKey(key), false)), false);
    }
    public static final byte[] encrypt(String data, byte[] key) {
        try {
            return encrypt(data.getBytes("UTF-8"), key);
        }
        catch (UnsupportedEncodingException e) {
            return null;
        }
    }
    public static final byte[] encrypt(byte[] data, String key) {
        try {
            return encrypt(data, key.getBytes("UTF-8"));
        }
        catch (UnsupportedEncodingException e) {
            return null;
        }
    }
    public static final byte[] encrypt(String data, String key) {
        try {
            return encrypt(data.getBytes("UTF-8"), key.getBytes("UTF-8"));
        }
        catch (UnsupportedEncodingException e) {
            return null;
        }
    }
    public static final String encryptToBase64String(byte[] data, byte[] key) {
        byte[] bytes = encrypt(data, key);
        if (bytes == null) return null;
        return Base64.encode(bytes);
    }
    public static final String encryptToBase64String(String data, byte[] key) {
        byte[] bytes = encrypt(data, key);
        if (bytes == null) return null;
        return Base64.encode(bytes);
    }
    public static final String encryptToBase64String(byte[] data, String key) {
        byte[] bytes = encrypt(data, key);
        if (bytes == null) return null;
        return Base64.encode(bytes);
    }
    public static final String encryptToBase64String(String data, String key) {
        byte[] bytes = encrypt(data, key);
        if (bytes == null) return null;
        return Base64.encode(bytes);
    }
    public static final byte[] decrypt(byte[] data, byte[] key) {
        if (data.length == 0) {
            return data;
        }
        return toByteArray(
                decrypt(toIntArray(data, false), toIntArray(fixKey(key), false)), true);
    }
    public static final byte[] decrypt(byte[] data, String key) {
        try {
            return decrypt(data, key.getBytes("UTF-8"));
        }
        catch (UnsupportedEncodingException e) {
            return null;
        }
    }
    public static final byte[] decryptBase64String(String data, byte[] key) {
        return decrypt(Base64.decode(data), key);
    }
    public static final byte[] decryptBase64String(String data, String key) {
        return decrypt(Base64.decode(data), key);
    }
    public static final String decryptToString(byte[] data, byte[] key) {
        try {
            byte[] bytes = decrypt(data, key);
            if (bytes == null) return null;
            return new String(bytes, "UTF-8");
        }
        catch (UnsupportedEncodingException ex) {
            return null;
        }
    }
    public static final String decryptToString(byte[] data, String key) {
        try {
            byte[] bytes = decrypt(data, key);
            if (bytes == null) return null;
            return new String(bytes, "UTF-8");
        }
        catch (UnsupportedEncodingException ex) {
            return null;
        }
    }
    public static final String decryptBase64StringToString(String data, byte[] key) {
        try {
            byte[] bytes = decrypt(Base64.decode(data), key);
            if (bytes == null) return null;
            return new String(bytes, "UTF-8");
        }
        catch (UnsupportedEncodingException ex) {
            return null;
        }
    }
    public static final String decryptBase64StringToString(String data, String key) {
        try {
            byte[] bytes = decrypt(Base64.decode(data), key);
            if (bytes == null) return null;
            return new String(bytes, "UTF-8");
        }
        catch (UnsupportedEncodingException ex) {
            return null;
        }
    }

    def static int[] encrypt(int[] v, int[] k) {
        int n = v.length - 1;

        if (n < 1) {
            return v;
        }
        int p, q = 6 + 52 / (n + 1);
        int z = v[n], y, sum = 0, e;

        while (q-- > 0) {
            sum = sum + DELTA;
            e = sum >>> 2 & 3;
            for (p = 0; p < n; p++) {
                y = v[p + 1];
                z = v[p] += MX(sum, y, z, p, e, k);
            }
            y = v[0];
            z = v[n] += MX(sum, y, z, p, e, k);
        }
        return v;
    }

    def static int[] decrypt(int[] v, int[] k) {
        int n = v.length - 1;

        if (n < 1) {
            return v;
        }
        int p, q = 6 + 52 / (n + 1);
        int z, y = v[0], sum = q * DELTA, e;

        while (sum != 0) {
            e = sum >>> 2 & 3;
            for (p = n; p > 0; p--) {
                z = v[p - 1];
                y = v[p] -= MX(sum, y, z, p, e, k);
            }
            z = v[n];
            y = v[0] -= MX(sum, y, z, p, e, k);
            sum = sum - DELTA;
        }
        return v;
    }

    private static byte[] fixKey(byte[] key) {
        if (key.length == 16) return key;
        byte[] fixedkey = new byte[16];
        if (key.length < 16) {
            System.arraycopy(key, 0, fixedkey, 0, key.length);
        }
        else {
            System.arraycopy(key, 0, fixedkey, 0, 16);
        }
        return fixedkey;
    }

    private static int[] toIntArray(byte[] data, boolean includeLength) {
        int n = (((data.length & 3) == 0)
                ? (data.length >>> 2)
                : ((data.length >>> 2) + 1));
        int[] result;

        if (includeLength) {
            result = new int[n + 1];
            result[n] = data.length;
        }
        else {
            result = new int[n];
        }
        n = data.length;
        for (int i = 0; i < n; ++i) {
            result[i >>> 2] |= (0x000000ff & data[i]) << ((i & 3) << 3);
        }
        return result;
    }

    private static byte[] toByteArray(int[] data, boolean includeLength) {
        int n = data.length << 2;

        if (includeLength) {
            int m = data[data.length - 1];
            n -= 4;
            if ((m < n - 3) || (m > n)) {
                return null;
            }
            n = m;
        }
        byte[] result = new byte[n];

        for (int i = 0; i < n; ++i) {
            result[i] = (byte) (data[i >>> 2] >>> ((i & 3) << 3));
        }
        return result;
    }
}

public class DLLXXTea
{
  public static final String key = "QHE8BxTiPWzMr8Je";
  public static final byte[] CILU_DLL_MAGIC_TAG = "CLDLL".getBytes();
  
  public static void ciluEncryptFile(File orig, File out)
    throws Exception
  {
    int tagLength = CILU_DLL_MAGIC_TAG.length;
    byte[] bytes = Files.readAllBytes(orig.toPath());
    byte[] magicTag = Arrays.copyOf(bytes, tagLength);
    if (Arrays.equals(magicTag, CILU_DLL_MAGIC_TAG))
    {
      System.err.println("orig file is encrypt file: " + orig);
      Files.copy(orig.toPath(), out.toPath(), [StandardCopyOption.REPLACE_EXISTING]);
      System.err.println("copy orig file to out file!");
      return;
    }
    byte[] encryptBytes = XXTEA.encrypt(bytes, "QHE8BxTiPWzMr8Je");
    ByteBuffer buf = ByteBuffer.allocate(encryptBytes.length + tagLength);
    buf.put(CILU_DLL_MAGIC_TAG);
    buf.put(encryptBytes);
    Files.write(out.toPath(), buf.array(), new OpenOption[0]);
    System.out.println("encrypt output file :" + out);
  }
  
  public static void ciluDecryptFile(File orig, File out)
    throws Exception
  {
    int tagLength = CILU_DLL_MAGIC_TAG.length;
    byte[] bytes = Files.readAllBytes(orig.toPath());
    byte[] magicTag = Arrays.copyOf(bytes, tagLength);
    if (Arrays.equals(magicTag, CILU_DLL_MAGIC_TAG))
    {
      byte[] encryptBytes = new byte[bytes.length - tagLength];
      System.arraycopy(bytes, tagLength, encryptBytes, 0, encryptBytes.length);
      byte[] decryptBytes = XXTEA.decrypt(encryptBytes, "QHE8BxTiPWzMr8Je");
      Files.write(out.toPath(), decryptBytes, new OpenOption[0]);
      System.out.println("decrypt output file :" + out);
    }
    else
    {
      System.err.println("orig is not an encrypt file :" + orig);
      Files.copy(orig.toPath(), out.toPath(), [ StandardCopyOption.REPLACE_EXISTING ]);
      System.err.println("copy orig file to out file!");
    }
  }
}

if(args.length < 2) {
	println("args not match: <e|d> dll_file [output_file]")
	return
}
String opt = args[0]
def dllFile = new File(args[1])

if(!dllFile.exists()) {
	println("dll_file: " + dllFile + " not exists!")
}

if("e".equals(opt)) {
	def outputFile = args.length>2 ? new File(args[2]) : new File(args[1]+"_encrypt")
	DLLXXTea.ciluEncryptFile(dllFile, outputFile);
} else if("d".equals(opt)) {
	def outputFile = args.length>2 ? new File(args[2]) : new File(args[1]+"_decrypt")
	DLLXXTea.ciluDecryptFile(dllFile, outputFile);
} else {
	println("option can't understand: " + opt)
}
