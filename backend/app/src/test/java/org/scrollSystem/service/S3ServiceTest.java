//package org.scrollSystem.service;
//
//
//import com.amazonaws.services.s3.AmazonS3;
//import com.amazonaws.services.s3.model.PutObjectRequest;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.mock.web.MockMultipartFile;
//import org.springframework.test.context.TestPropertySource;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.io.IOException;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.doNothing;
//import static org.mockito.Mockito.when;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//@TestPropertySource("/test.properties")
//@Transactional
//public class S3ServiceTest
//{
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Mock
//    private AmazonS3 amazonS3;
//
//    @MockBean
//    private S3Service s3Service;
//
//    private final String bucketName = "test-bucket";
//    private String baseUrl = "baseUrl";
//
//    @BeforeEach
//    void initdata()
//    {
//        amazonS3 = Mockito.mock(AmazonS3.class);
//        doNothing().when(amazonS3).put
//        doNothing().when(amazonS3).deleteObject(anyString(), anyLong());
//
//    }
//
//    @Test
//    void testUploadFile() throws IOException {
//
//        MockMultipartFile mockFile = new MockMultipartFile(
//                "file", "test.txt", "text/plain", "some content".getBytes()
//        );
//
//        when(amazonS3.getUrl(bucketName, "test.txt"))
//                .thenReturn(new java.net.URL("https://s3.amazon.com/test-bucket/test.txt"));
//
//        String fileUrl = s3Service.uploadFile(mockFile);
//
//
//    }
//
//}
