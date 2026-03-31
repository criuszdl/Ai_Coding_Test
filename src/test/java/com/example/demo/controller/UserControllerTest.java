package com.example.demo.controller;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("UserController 接口测试")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    // -------------------------------------------------------------------------
    // 1. 新增用户 POST /users
    // -------------------------------------------------------------------------
    @Test
    @Order(1)
    @DisplayName("新增用户 - POST /users - 成功返回 201 及用户信息")
    void testSaveUser() throws Exception {
        User user = new User();
        user.setUsername("zhangsan");
        user.setPassword("123456");
        user.setEmail("zhangsan@example.com");

        mockMvc
            .perform(
                post("/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(user))
            )
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.username").value("zhangsan"))
            .andExpect(jsonPath("$.password").value("123456"))
            .andExpect(jsonPath("$.email").value("zhangsan@example.com"))
            .andExpect(jsonPath("$.createTime").isNotEmpty());
    }

    // -------------------------------------------------------------------------
    // 2. 查询全部用户 GET /users
    // -------------------------------------------------------------------------
    @Test
    @Order(2)
    @DisplayName("查询全部用户 - GET /users - 成功返回用户列表")
    void testFindAllUsers() throws Exception {
        userRepository.save(buildUser("lisi", "111111", "lisi@example.com"));
        userRepository.save(
            buildUser("wangwu", "222222", "wangwu@example.com")
        );

        mockMvc
            .perform(get("/users"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(
                jsonPath("$[*].username", containsInAnyOrder("lisi", "wangwu"))
            );
    }

    @Test
    @Order(3)
    @DisplayName("查询全部用户 - GET /users - 数据库为空时返回空列表")
    void testFindAllUsers_Empty() throws Exception {
        mockMvc
            .perform(get("/users"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
    }

    // -------------------------------------------------------------------------
    // 3. 根据 ID 查询用户 GET /users/{id}
    // -------------------------------------------------------------------------
    @Test
    @Order(4)
    @DisplayName("根据ID查询用户 - GET /users/{id} - 用户存在时返回 200")
    void testFindUserById() throws Exception {
        User saved = userRepository.save(
            buildUser("zhaoliu", "333333", "zhaoliu@example.com")
        );

        mockMvc
            .perform(get("/users/" + saved.getId()))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(saved.getId()))
            .andExpect(jsonPath("$.username").value("zhaoliu"))
            .andExpect(jsonPath("$.email").value("zhaoliu@example.com"));
    }

    @Test
    @Order(5)
    @DisplayName("根据ID查询用户 - GET /users/{id} - 用户不存在时返回 500")
    void testFindUserById_NotFound() throws Exception {
        mockMvc
            .perform(get("/users/999999"))
            .andDo(print())
            .andExpect(status().is5xxServerError());
    }

    // -------------------------------------------------------------------------
    // 4. 修改用户 PUT /users/{id}
    // -------------------------------------------------------------------------
    @Test
    @Order(6)
    @DisplayName("修改用户 - PUT /users/{id} - 成功返回更新后的用户信息")
    void testUpdateUser() throws Exception {
        User saved = userRepository.save(
            buildUser("sunqi", "444444", "sunqi@example.com")
        );

        User updateRequest = new User();
        updateRequest.setUsername("sunqi_updated");
        updateRequest.setPassword("999999");
        updateRequest.setEmail("sunqi_new@example.com");

        mockMvc
            .perform(
                put("/users/" + saved.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateRequest))
            )
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(saved.getId()))
            .andExpect(jsonPath("$.username").value("sunqi_updated"))
            .andExpect(jsonPath("$.password").value("999999"))
            .andExpect(jsonPath("$.email").value("sunqi_new@example.com"));
    }

    @Test
    @Order(7)
    @DisplayName("修改用户 - PUT /users/{id} - 用户不存在时返回 500")
    void testUpdateUser_NotFound() throws Exception {
        User updateRequest = new User();
        updateRequest.setUsername("nobody");
        updateRequest.setPassword("000000");
        updateRequest.setEmail("nobody@example.com");

        mockMvc
            .perform(
                put("/users/999999")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateRequest))
            )
            .andDo(print())
            .andExpect(status().is5xxServerError());
    }

    // -------------------------------------------------------------------------
    // 5. 删除用户 DELETE /users/{id}
    // -------------------------------------------------------------------------
    @Test
    @Order(8)
    @DisplayName("删除用户 - DELETE /users/{id} - 成功返回 204")
    void testDeleteUser() throws Exception {
        User saved = userRepository.save(
            buildUser("zhouba", "555555", "zhouba@example.com")
        );

        mockMvc
            .perform(delete("/users/" + saved.getId()))
            .andDo(print())
            .andExpect(status().isNoContent());

        // 验证已被删除，再次查询应返回 500
        mockMvc
            .perform(get("/users/" + saved.getId()))
            .andDo(print())
            .andExpect(status().is5xxServerError());
    }

    // -------------------------------------------------------------------------
    // 工具方法
    // -------------------------------------------------------------------------
    private User buildUser(String username, String password, String email) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setCreateTime(LocalDateTime.now());
        return user;
    }
}
