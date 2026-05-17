package com.Util;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.Date;

@Component  // ← 必须加，让Spring管理这个类
public class UploadUtil {

    public static final String DOMIN = "https://robert-plant.oss-cn-shenzhen.aliyuncs.com//";
    private static final String ENDPOINT = "https://oss-cn-shenzhen.aliyuncs.com";
    private static final String BUCKET_NAME = "robert-plant";
    private static final long EXPIRE_TIME = 3600 * 1000;

    private static String ACCESS_KEY_ID;
    private static String ACCESS_KEY_SECRET;

    // ← 用setter方法注入static字段
    @Value("${aliyun.accessKeyId}")
    public void setAccessKeyId(String accessKeyId) {
        ACCESS_KEY_ID = accessKeyId;
        System.out.println("注入accessKeyId=" + accessKeyId); // 加这行
    }

    @Value("${aliyun.accessKeySecret}")
    public void setAccessKeySecret(String accessKeySecret) {
        ACCESS_KEY_SECRET = accessKeySecret;
        System.out.println("注入accessKeyId=" + accessKeySecret); // 加这行
    }

        // ===== 无参方法，返回带签名 URL =====
        public static String getSignedUrl(String ossUrl) {
            if (ossUrl == null || ossUrl.isEmpty()) {
                return null;
            }

            // 去掉域名前缀，得到 OSS Object Name
            String objectName = ossUrl.replace("https://robert-plant.oss-cn-shenzhen.aliyuncs.com//", "");

            OSS ossClient = new OSSClientBuilder().build(ENDPOINT, ACCESS_KEY_ID, ACCESS_KEY_SECRET);
            try {
                Date expiration = new Date(System.currentTimeMillis() + EXPIRE_TIME);
                URL signedUrl = ossClient.generatePresignedUrl(BUCKET_NAME, objectName, expiration);
                return signedUrl.toString();
            } finally {
                ossClient.shutdown();
            }
        }
    public static String uploadFile(MultipartFile file, String name) throws IOException {
        String originalFileName = file.getOriginalFilename();
        String suffix = "";
        int lastDotIndex = originalFileName.lastIndexOf(".");
        if (lastDotIndex > 0) {
            suffix = originalFileName.substring(lastDotIndex);
        }
        String finalFileName = name + suffix;
        String Endpoint = "https://oss-cn-shenzhen.aliyuncs.com";

        OSS ossClient = new OSSClientBuilder().build(Endpoint, ACCESS_KEY_ID, ACCESS_KEY_SECRET);
        ossClient.putObject("robert-plant", finalFileName, file.getInputStream());
        return DOMIN + finalFileName;
    }

}
