package org.example.jsonview.controller;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.RequiredArgsConstructor;
import org.example.jsonview.model.Order;
import org.example.jsonview.model.User;
import org.example.jsonview.service.OrderService;
import org.example.jsonview.service.UserService;
import org.example.jsonview.view.UserDetails;
import org.example.jsonview.view.UserSummary;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final OrderService orderService;

    @GetMapping
    @JsonView(UserSummary.class)
    public List<User> getAllUsers() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    @JsonView(UserDetails.class)
    public User getUserById(@PathVariable Long id) {
        return userService.findById(id);
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return new ResponseEntity<>(userService.save(user), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        User existingUser = userService.findById(id);
        existingUser.setName(user.getName());
        existingUser.setEmail(user.getEmail());
        return ResponseEntity.ok(userService.save(existingUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/orders")
    public ResponseEntity<Order> createOrderForUser(@PathVariable Long id, @RequestBody Order order) {
        User user = userService.findById(id);
        order.setUser(user);
        Order savedOrder = orderService.save(order);
        return new ResponseEntity<>(savedOrder, HttpStatus.CREATED);
    }

    @DeleteMapping("/{userId}/orders/{orderId}")
    public ResponseEntity<Void> deleteOrderFromUser(@PathVariable Long userId, @PathVariable Long orderId) {
        User user = userService.findById(userId);
        user.getOrders().removeIf(order -> order.getId().equals(orderId));
        userService.save(user);
        return ResponseEntity.noContent().build();
    }
}