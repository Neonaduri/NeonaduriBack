//package com.sparta.neonaduriback;
//
//import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
//import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
//import org.junit.jupiter.api.Test;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//public class JasyptTest {
//
//    @Test
//    public void stringEncryptor(){
//        String text="https://6ded0363763d4c32865e549caa25940d@o1253535.ingest.sentry.io/6420572";
//
//        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
//        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
//        System.out.println(System.getenv("JASYPT_PASSWORD"));
//        config.setPassword(System.getenv("JASYPT_PASSWORD"));
////        config.setPassword("");
//        config.setPoolSize("1");
//        config.setAlgorithm("PBEWithMD5AndDES");
//        config.setStringOutputType("base64");
//        config.setKeyObtentionIterations("1000");
//        config.setProviderName("SunJCE");
//        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
//        config.setIvGeneratorClassName("org.jasypt.iv.NoIvGenerator");
//        //config를 바탕으로 암호화 진행
//        encryptor.setConfig(config);
//
//        String encryptText=encryptor.encrypt(text);
//        System.out.println(encryptText);
//        String decryptText= encryptor.decrypt(encryptText);
//        System.out.println(decryptText);
//        assertThat(text).isEqualTo(decryptText);
//    }
//
//}
