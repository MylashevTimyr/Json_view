package org.example.jsonview.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.jsonview.model.Order;
import org.example.jsonview.repository.OrderRepository;
import org.example.jsonview.service.OrderService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Override
    public Order save(Order order) {
        return orderRepository.save(order);
    }
}
