package com.cadastro.pix.exceptions.controller;

import com.cadastro.pix.service.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


//@RunWith(SpringRunner.class)
//@WebMvcTest
public class GlobalExceptionHandlerTest {

    @Mock
    private MockMvc mockMvc;

    @MockBean
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


//    @Test
//    public void testHandleEntityNotFound() throws Exception {
//        when(userService.createUser(any(User.class))).thenThrow(new EntityNotFoundException("Entity not found"));
//
//        User user = new User();
//
//        mockMvc.perform(post("/users")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(asJsonString(user)))
//                .andExpect(status().isNotFound())
//                .andExpect(jsonPath("$.message").value("Entity not found"));
//
//        verify(userService, times(1)).createUser(any(User.class));
//    }
//
//    @Test
//    public void testHandleIllegalArgument() throws Exception {
//        when(userService.createUser(any(User.class))).thenThrow(new IllegalArgumentException("Invalid argument"));
//
//        User user = new User();
//
//        mockMvc.perform(post("/users")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(asJsonString(user)))
//                .andExpect(status().isUnprocessableEntity())
//                .andExpect(jsonPath("$.message").value("Invalid argument"));
//
//        verify(userService, times(1)).createUser(any(User.class));
//    }
//
//    @Test
//    public void testHandleValidationExceptions() throws Exception {
//        User user = new User(); // Dados inválidos
//
//        mockMvc.perform(post("/users")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(asJsonString(user)))
//                .andExpect(status().isUnprocessableEntity())
//                .andExpect(jsonPath("$.message").exists());
//
//        verify(userService, times(0)).createUser(any(User.class));
//    }
//
//    @Test
//    public void testHandleNoHandlerFoundException() throws Exception {
//        mockMvc.perform(get("/invalid-endpoint")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isNotFound())
//                .andExpect(jsonPath("$.message").exists());
//    }
//
//    @Test
//    public void testHandleHttpMessageNotReadableException() throws Exception {
//        String invalidJson = "invalid json";
//
//        mockMvc.perform(post("/users")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(invalidJson))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.message").exists());
//    }
//
//    // Helper method to convert object to JSON string
//    private static String asJsonString(final Object obj) {
//        try {
//            return new ObjectMapper().writeValueAsString(obj);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
}

