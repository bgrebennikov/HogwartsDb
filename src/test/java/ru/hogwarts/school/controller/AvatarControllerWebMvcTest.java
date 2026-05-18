package ru.hogwarts.school.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.hogwarts.school.model.school.Avatar;
import ru.hogwarts.school.model.school.Student;
import ru.hogwarts.school.service.AvatarService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AvatarController.class)
class AvatarControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AvatarService avatarService;

    @Test
    void getPage_shouldReturnCollectionOfAvatars() throws Exception {
        Avatar avatar = new Avatar();
        avatar.setId(1L);
        List<Avatar> avatars = Collections.singletonList(avatar);

        when(avatarService.findAll(1, 10)).thenReturn(avatars);

        mockMvc.perform(get("/avatar")
                        .param("page", String.valueOf(1))
                        .param("size", String.valueOf(10)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));


        verify(avatarService, times(1)).findAll(1, 10);

    }

    @Test
    void getPage_shouldThrownExceptionWhenParametersIsInvalid() throws Exception {
        mockMvc.perform(get("/avatar")
                        .param("page", String.valueOf(0))
                        .param("size", String.valueOf(10)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value(notNullValue()));

        verifyNoInteractions(avatarService);
    }


    @Test
    void upload_shouldReturnAvatarId() throws Exception {
        Long studentId = 1L;
        Long expectedAvatarId = 1L;
        MockMultipartFile mockFile = new MockMultipartFile(
                "file", "avatar.jpg", MediaType.IMAGE_JPEG_VALUE, "image-content".getBytes()
        );

        when(avatarService.uploadAvatar(eq(studentId), any())).thenReturn(expectedAvatarId);

        mockMvc.perform(multipart("/avatar/{id}", studentId)
                        .file(mockFile))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedAvatarId.toString()));

        verify(avatarService, times(1)).uploadAvatar(eq(studentId), any());
    }

    @Test
    void getAvatarFromDb_shouldReturnBytesAndHeaders() throws Exception {
        Long avatarId = 1L;
        byte[] testData = "image-bytes".getBytes();

        Avatar mockAvatar = new Avatar();
        mockAvatar.setMediaType(MediaType.IMAGE_JPEG_VALUE);
        mockAvatar.setData(testData);

        when(avatarService.findAvatar(avatarId)).thenReturn(mockAvatar);

        mockMvc.perform(
                get("/avatar/{id}/from-db", avatarId))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.IMAGE_JPEG_VALUE))
                .andExpect(header().string("Content-Length", String.valueOf(testData.length)))
                .andExpect(content().bytes(testData));

        verify(avatarService, times(1)).findAvatar(avatarId);

    }

    @Test
    void getAvatarFromFile_shouldReturnFileContentWhenExists() throws Exception {
        Long avatarId = 1L;

        Path tempFile = Files.createTempFile("avatar_test", ".jpg");
        Files.writeString(tempFile, "image-content");

        Avatar mockAvatar = new Avatar();
        mockAvatar.setMediaType(MediaType.IMAGE_JPEG_VALUE);
        mockAvatar.setFilePath(tempFile.toAbsolutePath().toString());
        mockAvatar.setFileSize(Files.size(tempFile));

        when(avatarService.findAvatar(avatarId)).thenReturn(mockAvatar);

        try {
            mockMvc.perform(
                    get("/avatar/{id}/from-file", avatarId)
            )
                    .andExpect(status().isOk())
                    .andExpect(header().string("Content-Type", MediaType.IMAGE_JPEG_VALUE))
                    .andExpect(header().longValue("Content-Length", Files.size(tempFile)))
                    .andExpect(content().string("image-content"));
        } finally {
            Files.delete(tempFile);
        }

        verify(avatarService, times(1)).findAvatar(avatarId);

    }

    @Test
    void getAvatarFromFile_ShouldReturn404WhenFileDoesNotExist() throws Exception {
        Long avatarId = 1L;
        Avatar mockAvatar = new Avatar();
        mockAvatar.setMediaType(MediaType.IMAGE_JPEG_VALUE);
        mockAvatar.setFilePath("/unknown-dir/unknown-file.jpg");

        when(avatarService.findAvatar(avatarId)).thenReturn(mockAvatar);

        mockMvc.perform(get("/avatar/{id}/from-file", avatarId))
                .andExpect(status().isNotFound());

        verify(avatarService, times(1)).findAvatar(avatarId);
    }

    @Test
    void deleteAvatar_shouldReturnStatusOk() throws Exception {
        Long avatarId = 1L;
        doNothing().when(avatarService).deleteAvatar(avatarId);
        mockMvc.perform(delete("/avatar/{id}", avatarId))
                .andExpect(status().isOk());

        verify(avatarService, times(1)).deleteAvatar(avatarId);
    }

    @Test
    void getPage_ShouldNotCauseInfiniteRecursion() throws Exception {
        Avatar mockAvatar = new Avatar();
        mockAvatar.setId(1L);
        mockAvatar.setMediaType(MediaType.IMAGE_JPEG_VALUE);

        Student mockStudent = new Student();
        mockStudent.setId(100L);
        mockStudent.setName("Harry");

        mockAvatar.setStudent(mockStudent);
        mockStudent.setAvatar(mockAvatar);

        List<Avatar> avatars = Collections.singletonList(mockAvatar);

        when(avatarService.findAll(1, 10)).thenReturn(avatars);

        mockMvc.perform(get("/avatar")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].student.id").value(100L))
                .andExpect(jsonPath("$[0].student.avatar").doesNotExist());

        verify(avatarService, times(1)).findAll(1, 10);
    }

}