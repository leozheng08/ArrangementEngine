package cn.tongdun.kunpeng.api.engine.util;

import cn.tongdun.kunpeng.share.utils.TraceUtils;
import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4SafeDecompressor;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xerial.snappy.Snappy;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @Author: liuq
 * @Date: 2020/2/18 4:23 PM
 */
public class CompressUtil {

    private static final Logger logger                = LoggerFactory.getLogger(CompressUtil.class);

    private static final int    MAX_DECOMPRESS_LENGTH = 1024 * 1024 * 10;

    private static String LOG_FUNC = "解压缩工具类";

    public static final String ENCODE_UTF_8 = "UTF-8";

    public static void main(String[] args) throws IOException {
        String str = "{\"accountLogin\":\"zhangsan\",\"create\":1399789008328,\"deviceId\":\"\",\"eventId\":\"login\",\"ipAddress\":\"101.69.252.146\",\"location\":\"杭州市\",\"payAmount\":0,\"smartId\":\"d5e14bb594fd45c7d8608c18e4e4d2c7\",\"status\":\"COMPLETED\"};{\"accountLogin\":\"zhangsan\",\"create\":1399789219292,\"deviceId\":\"\",\"eventId\":\"login\",\"ipAddress\":\"101.69.252.146\",\"location\":\"杭州市\",\"payAmount\":0,\"smartId\":\"d5e14bb594fd45c7d8608c18e4e4d2c7\",\"status\":\"COMPLETED\"};{\"accountLogin\":\"zhangsan\",\"create\":1399789393589,\"deviceId\":\"\",\"eventId\":\"login\",\"ipAddress\":\"101.69.252.146\",\"location\":\"杭州市\",\"payAmount\":0,\"smartId\":\"d5e14bb594fd45c7d8608c18e4e4d2c7\",\"status\":\"COMPLETED\"};{\"accountLogin\":\"zhangsan\",\"create\":1399791467023,\"deviceId\":\"\",\"eventId\":\"login\",\"ipAddress\":\"101.69.252.146\",\"location\":\"杭州市\",\"payAmount\":0,\"smartId\":\"d5e14bb594fd45c7d8608c18e4e4d2c7\",\"status\":\"COMPLETED\"};{\"accountLogin\":\"zhangsan\",\"create\":1399791495807,\"deviceId\":\"\",\"eventId\":\"login\",\"ipAddress\":\"101.69.252.142\",\"location\":\"杭州市\",\"payAmount\":0,\"smartId\":\"d5e14bb594fd45c7d8608c18e4e4d2c7\",\"status\":\"COMPLETED\"};{\"accountLogin\":\"zhangsan\",\"create\":1399794465521,\"deviceId\":\"\",\"eventId\":\"login\",\"ipAddress\":\"101.69.252.142\",\"location\":\"杭州市\",\"payAmount\":0,\"smartId\":\"d5e14bb594fd45c7d8608c18e4e4d2c7\",\"status\":\"COMPLETED\"};{\"accountLogin\":\"zhangsan\",\"create\":1399794498409,\"deviceId\":\"\",\"eventId\":\"login\",\"ipAddress\":\"101.69.252.142\",\"location\":\"杭州市\",\"payAmount\":0,\"smartId\":\"d5e14bb594fd45c7d8608c18e4e4d2c7\",\"status\":\"COMPLETED\"};{\"accountLogin\":\"zhangsan\",\"create\":1399794499600,\"deviceId\":\"\",\"eventId\":\"login\",\"ipAddress\":\"101.69.252.142\",\"location\":\"杭州市\",\"payAmount\":0,\"smartId\":\"d5e14bb594fd45c7d8608c18e4e4d2c7\",\"status\":\"COMPLETED\"};{\"accountLogin\":\"zhangsan\",\"create\":1399794499955,\"deviceId\":\"\",\"eventId\":\"login\",\"ipAddress\":\"101.69.252.142\",\"location\":\"杭州市\",\"payAmount\":0,\"smartId\":\"d5e14bb594fd45c7d8608c18e4e4d2c7\",\"status\":\"COMPLETED\"};{\"accountLogin\":\"zhangsan\",\"create\":1399794500385,\"deviceId\":\"\",\"eventId\":\"login\",\"ipAddress\":\"101.69.252.142\",\"location\":\"杭州市\",\"payAmount\":0,\"smartId\":\"d5e14bb594fd45c7d8608c18e4e4d2c7\",\"status\":\"COMPLETED\"};{\"accountLogin\":\"zhangsan\",\"create\":1399794500490,\"deviceId\":\"\",\"eventId\":\"login\",\"ipAddress\":\"101.69.252.142\",\"location\":\"杭州市\",\"payAmount\":0,\"smartId\":\"d5e14bb594fd45c7d8608c18e4e4d2c7\",\"status\":\"COMPLETED\"};{\"accountLogin\":\"zhangsan\",\"create\":1399794501580,\"deviceId\":\"\",\"eventId\":\"login\",\"ipAddress\":\"101.69.252.142\",\"location\":\"杭州市\",\"payAmount\":0,\"smartId\":\"d5e14bb594fd45c7d8608c18e4e4d2c7\",\"status\":\"COMPLETED\"};{\"accountLogin\":\"zhangsan\",\"create\":1399794501702,\"deviceId\":\"\",\"eventId\":\"login\",\"ipAddress\":\"101.69.252.142\",\"location\":\"杭州市\",\"payAmount\":0,\"smartId\":\"d5e14bb594fd45c7d8608c18e4e4d2c7\",\"status\":\"COMPLETED\"};{\"accountLogin\":\"zhangsan\",\"create\":1399794502454,\"deviceId\":\"\",\"eventId\":\"login\",\"ipAddress\":\"101.69.252.142\",\"location\":\"杭州市\",\"payAmount\":0,\"smartId\":\"d5e14bb594fd45c7d8608c18e4e4d2c7\",\"status\":\"COMPLETED\"};{\"accountLogin\":\"zhangsan\",\"create\":1399794502546,\"deviceId\":\"\",\"eventId\":\"login\",\"ipAddress\":\"101.69.252.142\",\"location\":\"杭州市\",\"payAmount\":0,\"smartId\":\"d5e14bb594fd45c7d8608c18e4e4d2c7\",\"status\":\"COMPLETED\"};{\"accountLogin\":\"zhangsan\",\"create\":1399794502858,\"deviceId\":\"\",\"eventId\":\"login\",\"ipAddress\":\"101.69.252.142\",\"location\":\"杭州市\",\"payAmount\":0,\"smartId\":\"d5e14bb594fd45c7d8608c18e4e4d2c7\",\"status\":\"COMPLETED\"};{\"accountLogin\":\"zhangsan\",\"create\":1399794503300,\"deviceId\":\"\",\"eventId\":\"login\",\"ipAddress\":\"101.69.252.142\",\"location\":\"杭州市\",\"payAmount\":0,\"smartId\":\"d5e14bb594fd45c7d8608c18e4e4d2c7\",\"status\":\"COMPLETED\"};{\"accountLogin\":\"zhangsan\",\"create\":1399794891872,\"deviceId\":\"\",\"eventId\":\"login\",\"ipAddress\":\"101.69.252.142\",\"location\":\"杭州市\",\"payAmount\":0,\"smartId\":\"d5e14bb594fd45c7d8608c18e4e4d2c7\",\"status\":\"COMPLETED\"};{\"accountLogin\":\"zhangsan\",\"create\":1399794945496,\"deviceId\":\"\",\"eventId\":\"login\",\"ipAddress\":\"101.69.252.142\",\"location\":\"杭州市\",\"payAmount\":0,\"smartId\":\"d5e14bb594fd45c7d8608c18e4e4d2c7\",\"status\":\"ONHOLD\"};{\"accountLogin\":\"zhangsan\",\"create\":1399794977108,\"deviceId\":\"\",\"eventId\":\"login\",\"ipAddress\":\"101.69.252.142\",\"location\":\"杭州市\",\"payAmount\":0,\"smartId\":\"d5e14bb594fd45c7d8608c18e4e4d2c7\",\"status\":\"ONHOLD\"};{\"accountLogin\":\"zhangsan\",\"create\":1399794993718,\"deviceId\":\"\",\"eventId\":\"login\",\"ipAddress\":\"101.69.252.142\",\"location\":\"杭州市\",\"payAmount\":0,\"smartId\":\"d5e14bb594fd45c7d8608c18e4e4d2c7\",\"status\":\"ONHOLD\"};{\"accountLogin\":\"zhangsan\",\"create\":1399795068256,\"deviceId\":\"\",\"eventId\":\"login\",\"ipAddress\":\"101.69.252.142\",\"location\":\"杭州市\",\"payAmount\":0,\"smartId\":\"d5e14bb594fd45c7d8608c18e4e4d2c7\",\"status\":\"ONHOLD\"};";
        long start = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            String zipstr = gzip(str);
            ungzip(zipstr);
        }
        long end = System.currentTimeMillis();
        System.out.println("gzip time:" + (end - start) + ",avg:" + (end - start) / 10000.0);
    }

    /**
     * 对原始字符串进行压缩并转成base64编码
     *
     * @param originString
     * @return
     * @throws UnsupportedEncodingException
     * @throws IOException
     */
    public static String gzip(String originString) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(out);
        gzip.write(originString.getBytes("utf-8"));
        gzip.close();

        byte[] zipbytes = out.toByteArray();
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(zipbytes);
    }

    public static String listGzip(ArrayList<Map> items) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(out);
        gzip.write(SerializationUtils.serialize(items));
        gzip.close();

        byte[] zipbytes = out.toByteArray();
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(zipbytes);
    }

    /**
     * 对base64进行解码并用gzip进行解压缩
     *
     * @param base64String
     * @return
     * @throws IOException
     */
    public static String ungzip(String base64String) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BASE64Decoder decoder = new BASE64Decoder();
        byte[] decodedBytes = decoder.decodeBuffer(base64String);
        ByteArrayInputStream in = new ByteArrayInputStream(decodedBytes);
        GZIPInputStream gunzip = new GZIPInputStream(in);

        int n;
        byte[] buffer = new byte[1024];
        while ((n = gunzip.read(buffer)) >= 0) {
            out.write(buffer, 0, n);
        }
        return out.toString("utf-8");
    }

    /**
     * 对base64进行解码并用gzip进行解压缩
     *
     * @param base64String
     * @return
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    public static ArrayList<Map> ungzipList(String base64String) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BASE64Decoder decoder = new BASE64Decoder();
        byte[] decodedBytes = decoder.decodeBuffer(base64String);
        ByteArrayInputStream in = new ByteArrayInputStream(decodedBytes);
        GZIPInputStream gunzip = new GZIPInputStream(in);

        int n;
        byte[] buffer = new byte[1024];
        while ((n = gunzip.read(buffer)) >= 0) {
            out.write(buffer, 0, n);
        }
        return (ArrayList<Map>) SerializationUtils.deserialize(out.toByteArray());
    }

    public static String lz4(String input) {
        StringBuilder result = new StringBuilder();

        // 调用lz4进行压缩
        byte[] lz4Bytes;
        try {
            // 压缩格式标记
            result.append("LZ4:");

            // 压缩前长度
            byte[] inputBytes = input.getBytes(Charset.defaultCharset().name());
            int inputLength = inputBytes.length;
            result.append(Integer.toString(inputLength));
            result.append(":");
            LZ4Factory factory = LZ4Factory.fastestInstance();
            LZ4Compressor compressor = factory.fastCompressor();
            int maxCompressedLength = compressor.maxCompressedLength(inputLength);
            byte[] compressed = new byte[maxCompressedLength];
            int compressedLength = compressor.compress(inputBytes, 0, inputLength, compressed, 0, maxCompressedLength);
            lz4Bytes = new byte[compressedLength];
            System.arraycopy(compressed, 0, lz4Bytes, 0, compressedLength);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        // 将压缩后的内容用base64编码
        String lz4Base64 = base64(lz4Bytes);
        result.append(lz4Base64);

        // 最终格式为：LZ4:<length>:<base64>
        return result.toString();
    }

    public static String unlz4(String input) {
        // 解析解压之后的长度
        int markEnd = input.indexOf(':');
        if (markEnd < 0) {
            logger.error(TraceUtils.getFormatTrace()+"CompressUtil Failed to decompress lz4: not found prefix mark");
            return null;
        }
        int lengthEnd = input.indexOf(':', markEnd + 1);
        if (lengthEnd < 0) {
            logger.error(TraceUtils.getFormatTrace()+"CompressUtil Failed to decompress lz4: not found decompress length");
            return null;
        }
        String lengthString = input.substring(markEnd + 1, lengthEnd);
        int length = Integer.parseInt(lengthString);

        // 安全检查，防止内存爆掉
        if (length > MAX_DECOMPRESS_LENGTH) {
            logger.error(TraceUtils.getFormatTrace()+"CompressUtil Failed to decompress lz4: decompress length is too large: " + length);
            return null;
        }

        // 先用base64解压
        byte[] inputBytes = unbase64(input.substring(lengthEnd + 1));

        // 调用lz4解压
        try {
            LZ4Factory factory = LZ4Factory.fastestInstance();
            LZ4SafeDecompressor decompressor = factory.safeDecompressor();
            byte[] buffer = new byte[length];
            int compressedLength2 = decompressor.decompress(inputBytes, 0, inputBytes.length, buffer, 0, buffer.length);
            return new String(buffer, 0, compressedLength2, Charset.defaultCharset().name());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private static String base64(byte[] bytes) {
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(bytes);
    }

    private static byte[] unbase64(String base64) {
        try {
            BASE64Decoder decoder = new BASE64Decoder();
            return decoder.decodeBuffer(base64);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static byte[] binaryLz4(String input) {
        // 将字符进行编码
        byte[] inputBytes = utf8Encode(input);
        int inputLength = inputBytes.length;

        // 保存原始数据大小
        byte[] sizeBytes = intToByteArray(inputBytes.length);

        // 使用lz4压缩
        try {
            LZ4Factory factory = LZ4Factory.fastestInstance();
            LZ4Compressor compressor = factory.fastCompressor();
            int maxCompressedLength = compressor.maxCompressedLength(inputLength);
            byte[] compressed = new byte[maxCompressedLength];
            int compressedLength = compressor.compress(inputBytes, 0, inputLength, compressed, 0, maxCompressedLength);

            // 封装结果，前4个字节储存原始数据的大小
            byte[] result = new byte[compressedLength + sizeBytes.length];
            System.arraycopy(sizeBytes, 0, result, 0, sizeBytes.length);
            System.arraycopy(compressed, 0, result, 4, compressedLength);
            return result;
        } catch (Exception ex) {
            throw new RuntimeException("Failed to compress lz4", ex);
        }
    }

    private static byte[] utf8Encode(String input) {
        try {
            return input.getBytes("utf8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private static String utf8Decode(byte[] input) {
        try {
            return new String(input, "utf8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] intToByteArray(int value) {
        ByteBuffer b = ByteBuffer.allocate(4);
        b.putInt(value);
        return b.array();
    }

    public static int byteArrayToInt(byte[] bytes) {
        ByteBuffer b = ByteBuffer.wrap(bytes);
        return b.getInt();
    }

    public static String binaryUnlz4(byte[] data) {
        // 读取原始数据的大小
        if (data == null) {
            throw new RuntimeException("Failed to decompress for null");
        }
        if (data.length < 4) {
            throw new RuntimeException("Failed to get original size, data is too small: " + data.length);
        }
        int length = byteArrayToInt(data);

        // 安全检查，防止内存爆掉
        if (length > MAX_DECOMPRESS_LENGTH) {
            throw new RuntimeException("Failed to decompress lz4: decompress length is too large: " + length);
        }

        // 调用lz4解压
        try {
            LZ4Factory factory = LZ4Factory.fastestInstance();
            LZ4SafeDecompressor decompressor = factory.safeDecompressor();
            byte[] buffer = new byte[length];
            int compressedLength2 = decompressor.decompress(data, 4, data.length - 4, buffer, 0, buffer.length);
            return new String(buffer, 0, compressedLength2, Charset.defaultCharset().name());
        } catch (Exception ex) {
            throw new RuntimeException("Failed to lz4 decompress", ex);
        }
    }

    public static byte[] safeBinaryLz4(String input) {
        try {
            return binaryLz4(input);
        } catch (Exception ex) {
            logger.error(TraceUtils.getFormatTrace()+"CompressUtil Failed to compress lz4", ex);
            return null;
        }
    }

    public static String safeBinaryUnlz4(byte[] input) {
        try {
            return binaryUnlz4(input);
        } catch (Exception ex) {
            logger.error(TraceUtils.getFormatTrace()+"CompressUtil Failed to decomress lz4", ex);
            return null;
        }
    }

    public static byte[] compressBySnappy(String str){
        try {
            if(StringUtils.isNotBlank(str)){
                return Snappy.compress(str.getBytes(ENCODE_UTF_8));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static String uncompressBySnappy(byte[] bytes){
        try {
            if(bytes != null && bytes.length > 0){
                return new String(Snappy.uncompress(bytes),ENCODE_UTF_8);
            }
        } catch (Exception e) {
            logger.error(TraceUtils.getFormatTrace()+"snappy uncompress to string error.", e);
        }
        return null;
    }
}
