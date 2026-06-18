package com.petboarding.modules.dashboard.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.petboarding.common.result.Result;
import com.petboarding.modules.cage.entity.Cage;
import com.petboarding.modules.cage.service.CageService;
import com.petboarding.modules.finance.entity.Billing;
import com.petboarding.modules.finance.service.BillingService;
import com.petboarding.modules.order.entity.BoardingOrder;
import com.petboarding.modules.order.service.BoardingOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/dashboard")
@Tag(name = "仪表盘", description = "数据统计和可视化")
@PreAuthorize("hasAnyRole('ADMIN','STAFF')")
public class DashboardController {

    @Autowired
    private BoardingOrderService orderService;

    @Autowired
    private CageService cageService;

    @Autowired
    private BillingService billingService;

    @GetMapping("/stats")
    @Operation(summary = "仪表盘核心统计")
    public Result<Map<String, Object>> stats() {
        Map<String, Object> data = new HashMap<>();

        // 今日寄养数量
        long todayBoarding = orderService.count(new LambdaQueryWrapper<BoardingOrder>()
                .eq(BoardingOrder::getStatus, "CHECKED_IN"));
        data.put("todayBoarding", todayBoarding);

        // 笼舍使用率
        long totalCages = cageService.count();
        long occupiedCages = cageService.count(new LambdaQueryWrapper<Cage>()
                .eq(Cage::getStatus, "OCCUPIED"));
        data.put("totalCages", totalCages);
        data.put("occupiedCages", occupiedCages);
        data.put("cageUsageRate", totalCages > 0
                ? BigDecimal.valueOf(occupiedCages).divide(BigDecimal.valueOf(totalCages), 2, BigDecimal.ROUND_HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                : 0);

        // 各状态订单数量
        long pendingOrders = orderService.count(new LambdaQueryWrapper<BoardingOrder>().eq(BoardingOrder::getStatus, "PENDING"));
        long confirmedOrders = orderService.count(new LambdaQueryWrapper<BoardingOrder>().eq(BoardingOrder::getStatus, "CONFIRMED"));
        long completedOrders = orderService.count(new LambdaQueryWrapper<BoardingOrder>().eq(BoardingOrder::getStatus, "COMPLETED"));

        data.put("pendingOrders", pendingOrders);
        data.put("confirmedOrders", confirmedOrders);
        data.put("completedOrders", completedOrders);

        // 今日收入（简化：统计今日支付成功的结算单）
        List<Billing> billings = billingService.list(new LambdaQueryWrapper<Billing>()
                .eq(Billing::getPaymentStatus, "FULLY_PAID"));
        BigDecimal todayRevenue = billings.stream()
                .map(b -> b.getFinalAmount() != null ? b.getFinalAmount() : b.getTotalAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        data.put("todayRevenue", todayRevenue);

        return Result.ok(data);
    }

    @GetMapping("/revenue")
    @Operation(summary = "收入趋势（近7天）")
    public Result<Map<String, Object>> revenue() {
        // 近7天订单统计
        List<Map<String, Object>> dailyData = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            Map<String, Object> dayData = new HashMap<>();
            dayData.put("date", date.toString());

            long count = orderService.count(new LambdaQueryWrapper<BoardingOrder>()
                    .ge(BoardingOrder::getCreateTime, date.atStartOfDay())
                    .lt(BoardingOrder::getCreateTime, date.plusDays(1).atStartOfDay()));
            dayData.put("orderCount", count);
            dailyData.add(dayData);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("dailyData", dailyData);

        // 笼舍类型分布
        List<Cage> cages = cageService.list();
        Map<String, Long> typeDistribution = new HashMap<>();
        for (Cage cage : cages) {
            typeDistribution.merge(cage.getType(), 1L, Long::sum);
        }
        result.put("cageTypeDistribution", typeDistribution);

        return Result.ok(result);
    }
}
