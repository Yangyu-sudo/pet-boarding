package com.petboarding.modules.finance.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.petboarding.common.result.Result;
import com.petboarding.modules.finance.entity.Billing;
import com.petboarding.modules.finance.service.BillingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/billings")
@Tag(name = "财务管理", description = "费用结算管理")
public class BillingController {

    @Autowired
    private BillingService billingService;

    @GetMapping("/order/{orderId}")
    @Operation(summary = "获取订单结算信息")
    public Result<Billing> getByOrderId(@PathVariable Long orderId) {
        Billing billing = billingService.getOne(
                new LambdaQueryWrapper<Billing>().eq(Billing::getOrderId, orderId)
        );
        return Result.ok(billing);
    }

    @PostMapping
    @Operation(summary = "创建结算单")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public Result<?> create(@RequestBody Billing billing) {
        billingService.save(billing);
        return Result.ok("结算单创建成功");
    }

    @PutMapping("/{id}/pay")
    @Operation(summary = "确认收款")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public Result<?> pay(@PathVariable Long id, @RequestParam String paymentMethod) {
        Billing billing = billingService.getById(id);
        if (billing != null) {
            billing.setPaymentStatus("FULLY_PAID");
            billing.setPaymentMethod(paymentMethod);
            billing.setPaymentTime(LocalDateTime.now());
            billingService.updateById(billing);
        }
        return Result.ok("收款确认成功");
    }
}
