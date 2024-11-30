package org.example.jsonview.controller;

import org.example.jsonview.model.Order;
import org.example.jsonview.model.User;
import org.example.jsonview.service.OrderService;
import org.example.jsonview.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private OrderService orderService;

    private User user;
    private List<Order> orders;

    @BeforeEach
    void setUp() {
        orders = new ArrayList<>(List.of(
                new Order(1L, "Laptop", 1200.0, "Pending", null),
                new Order(2L, "Phone", 800.0, "Shipped", null)
        ));
        user = new User(1L, "John Doe", "john.doe@example.com", orders);
    }

    @Test
    public void getAllUsers_ShouldReturnUserSummaryView() throws Exception {
        when(userService.findAll()).thenReturn(List.of(user));

        mockMvc.perform(get("/api/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("John Doe"))
                .andExpect(jsonPath("$[0].email").value("john.doe@example.com"))
                .andExpect(jsonPath("$[0].orders").doesNotExist()) // Orders should not be present
                .andDo(print());
    }

    @Test
    public void getUserById_ShouldReturnUserDetailsView() throws Exception {
        when(userService.findById(1L)).thenReturn(user);

        mockMvc.perform(get("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.orders[0].id").value(1))
                .andExpect(jsonPath("$.orders[0].product").value("Laptop"))
                .andExpect(jsonPath("$.orders[0].amount").value(1200.0))
                .andExpect(jsonPath("$.orders[0].status").value("Pending"))
                .andDo(print());
    }

    @Test
    public void createUser() throws Exception {
        String userPayload = """
                {
                    "name": "John Doe",
                    "email": "john.doe@example.com"
                }""";

        when(userService.save(Mockito.any(User.class))).thenReturn(user);

        mockMvc.perform(post("/api/users")
                        .content(userPayload)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @Test
    public void updateUser() throws Exception {
        User updatedUser = new User(1L, "John Updated", "john.updated@example.com", null);
        when(userService.findById(1L)).thenReturn(user);
        when(userService.save(Mockito.any(User.class))).thenReturn(updatedUser);

        String userUpdatePayload = """
                {
                    "name": "John Updated",
                    "email": "john.updated@example.com"
                }""";

        mockMvc.perform(put("/api/users/1")
                        .content(userUpdatePayload)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void deleteUser() throws Exception {
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    public void createOrderForUser() throws Exception {
        String orderPayload = """
                {
                    "product": "Laptop",
                    "amount": 1200.00,
                    "status": "Pending"
                }""";

        Order savedOrder = new Order(1L, "Laptop", 1200.0, "Pending", user);
        when(userService.findById(1L)).thenReturn(user);
        when(orderService.save(Mockito.any(Order.class))).thenReturn(savedOrder);

        mockMvc.perform(post("/api/users/1/orders")
                        .content(orderPayload)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.product").value("Laptop"))
                .andExpect(jsonPath("$.amount").value(1200.00))
                .andExpect(jsonPath("$.status").value("Pending"))
                .andDo(print());
    }

    @Test
    public void deleteOrderFromUser() throws Exception {
        when(userService.findById(1L)).thenReturn(user);

        mockMvc.perform(delete("/api/users/1/orders/1"))
                .andExpect(status().isNoContent())
                .andDo(print());
    }
}
