package com.petboarding.modules.order.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.petboarding.common.result.Result;
import com.petboarding.modules.order.entity.BoardingOrder;
import com.petboarding.modules.order.entity.OrderServiceItem;
import com.petboarding.modules.order.service.BoardingOrderService;
import com.petboarding.modules.system.entity.SysUser;
import com.petboarding.modules.system.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "订单管理", description = "寄养订单全流程管理")
public class BoardingOrderController {

    @Autowired
    private BoardingOrderService orderService;

    @Autowired
    private SysUserService sysUserService;

    @GetMapping
    @Operation(summary = "订单列表")
    public Result<Page<BoardingOrder>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long ownerId,
            @RequestParam(required = false) Long petId) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        SysUser currentUser = sysUserService.getByUsername(auth.getName());

        LambdaQueryWrapper<BoardingOrder> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(orderNo)) {
            wrapper.like(BoardingOrder::getOrderNo, orderNo);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(BoardingOrder::getStatus, status);
        }
        if (ownerId != null) {
            wrapper.eq(BoardingOrder::getOwnerId, ownerId);
        }
        if (petId != null) {
            wrapper.eq(BoardingOrder::getPetId, petId);
        }
        // 顾客只看自己的订单
        if ("CUSTOMER".equals(currentUser.getRole())) {
            wrapper.eq(BoardingOrder::getOwnerId, currentUser.getId());
        }
        wrapper.orderByDesc(BoardingOrder::getCreateTime);

        Page<BoardingOrder> result = orderService.page(new Page<>(page, size), wrapper);
        // 填充关联信息
        for (BoardingOrder order : result.getRecords()) {
            BoardingOrder detail = orderService.getOrderDetail(order.getId());
            order.setPetName(detail.getPetName());
            order.setOwnerName(detail.getOwnerName());
            order.setCageNo(detail.getCageNo());
        }

        return Result.ok(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "订单详情")
    public Result<BoardingOrder> getById(@PathVariable Long id) {
        return Result.ok(orderService.getOrderDetail(id));
    }

    @PostMapping
    @Operation(summary = "创建订单")
    public Result<BoardingOrder> create(@RequestBody CreateOrderRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        SysUser currentUser = sysUserService.getByUsername(auth.getName());

        BoardingOrder order = new BoardingOrder();
        order.setPetId(request.getPetId());
        order.setCageId(request.getCageId());
        order.setCheckInDate(request.getCheckInDate());
        order.setCheckOutDate(request.getCheckOutDate());
        order.setSpecialRequirements(request.getSpecialRequirements());
        // 顾客自己的订单
        if ("CUSTOMER".equals(currentUser.getRole())) {
            order.setOwnerId(currentUser.getId());
        } else {
            order.setOwnerId(request.getOwnerId());
        }

        BoardingOrder created = orderService.createOrder(order, request.getServices());
        return Result.ok("下单成功", created);
    }

    @PutMapping("/{id}/confirm")
    @Operation(summary = "确认订单")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public Result<?> confirm(@PathVariable Long id) {
        orderService.confirmOrder(id);
        return Result.ok("订单已确认");
    }

    @PutMapping("/{id}/checkin")
    @Operation(summary = "入住登记")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public Result<?> checkIn(@PathVariable Long id, @RequestParam(required = false) Long cageId) {
        orderService.checkIn(id, cageId);
        return Result.ok("入住登记成功");
    }

    @PutMapping("/{id}/complete")
    @Operation(summary = "完成订单")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public Result<?> complete(@PathVariable Long id) {
        orderService.completeOrder(id);
        return Result.ok("订单已完成");
    }

    @PutMapping("/{id}/cancel")
    @Operation(summary = "取消订单")
    public Result<?> cancel(@PathVariable Long id) {
        orderService.cancelOrder(id);
        return Result.ok("订单已取消");
    }

    @lombok.Data
    public static class CreateOrderRequest {
        private Long petId;
        private Long ownerId;
        private Long cageId;
        private java.time.LocalDate checkInDate;
        private java.time.LocalDate checkOutDate;
        private String specialRequirements;
        private List<OrderServiceItem> services;
    }
}
