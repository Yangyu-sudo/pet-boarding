package com.petboarding.modules.order.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.petboarding.common.exception.BusinessException;
import com.petboarding.common.result.ResultCode;
import com.petboarding.modules.cage.entity.Cage;
import com.petboarding.modules.cage.service.CageService;
import com.petboarding.modules.order.entity.BoardingOrder;
import com.petboarding.modules.order.entity.OrderServiceItem;
import com.petboarding.modules.order.mapper.BoardingOrderMapper;
import com.petboarding.modules.order.mapper.OrderServiceItemMapper;
import com.petboarding.modules.order.service.BoardingOrderService;
import com.petboarding.modules.pet.entity.Pet;
import com.petboarding.modules.pet.service.PetService;
import com.petboarding.modules.service.entity.ServiceItem;
import com.petboarding.modules.service.service.ServiceItemService;
import com.petboarding.modules.system.entity.SysUser;
import com.petboarding.modules.system.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class BoardingOrderServiceImpl extends ServiceImpl<BoardingOrderMapper, BoardingOrder>
        implements BoardingOrderService {

    @Autowired
    private OrderServiceItemMapper orderServiceItemMapper;

    @Autowired
    private CageService cageService;

    @Autowired
    private PetService petService;

    @Autowired
    private ServiceItemService serviceItemService;

    @Autowired
    private SysUserService sysUserService;

    @Override
    @Transactional
    public BoardingOrder createOrder(BoardingOrder order, List<OrderServiceItem> services) {
        // 参数校验
        if (order.getCheckInDate().isAfter(order.getCheckOutDate())) {
            throw new BusinessException(ResultCode.DATE_RANGE_ERROR);
        }

        // 生成订单号
        String orderNo = "BO" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + String.format("%04d", (int)(Math.random() * 10000));
        order.setOrderNo(orderNo);
        order.setStatus("PENDING");

        // 计算天数
        long days = ChronoUnit.DAYS.between(order.getCheckInDate(), order.getCheckOutDate());
        if (days == 0) days = 1;

        // 计算总价
        BigDecimal totalPrice = BigDecimal.ZERO;

        // 笼舍费用
        if (order.getCageId() != null) {
            Cage cage = cageService.getById(order.getCageId());
            if (cage != null) {
                totalPrice = totalPrice.add(cage.getDailyPrice().multiply(BigDecimal.valueOf(days)));
            }
        }

        // 服务费用
        BigDecimal serviceTotal = BigDecimal.ZERO;
        if (services != null) {
            for (OrderServiceItem os : services) {
                ServiceItem si = serviceItemService.getById(os.getServiceId());
                if (si != null && si.getStatus() == 1) {
                    os.setPrice(si.getPrice());
                    os.setQuantity(os.getQuantity() != null ? os.getQuantity() : 1);
                    serviceTotal = serviceTotal.add(si.getPrice().multiply(BigDecimal.valueOf(os.getQuantity())));
                }
            }
        }

        totalPrice = totalPrice.add(serviceTotal);
        order.setTotalPrice(totalPrice);

        // 定金为总价的30%
        BigDecimal deposit = totalPrice.multiply(BigDecimal.valueOf(0.3)).setScale(2, BigDecimal.ROUND_HALF_UP);
        order.setDeposit(deposit);

        // 保存订单
        save(order);

        // 保存订单服务关联
        if (services != null && !services.isEmpty()) {
            for (OrderServiceItem os : services) {
                os.setOrderId(order.getId());
                orderServiceItemMapper.insert(os);
            }
        }

        return order;
    }

    @Override
    @Transactional
    public void confirmOrder(Long orderId) {
        BoardingOrder order = getById(orderId);
        if (order == null) throw new BusinessException(ResultCode.ORDER_NOT_FOUND);
        if (!"PENDING".equals(order.getStatus())) {
            throw new BusinessException(ResultCode.ORDER_STATUS_ERROR, "只能确认待审核的订单");
        }
        order.setStatus("CONFIRMED");
        updateById(order);
    }

    @Override
    @Transactional
    public void checkIn(Long orderId, Long cageId) {
        BoardingOrder order = getById(orderId);
        if (order == null) throw new BusinessException(ResultCode.ORDER_NOT_FOUND);
        if (!"CONFIRMED".equals(order.getStatus())) {
            throw new BusinessException(ResultCode.ORDER_STATUS_ERROR, "只能入住已确认的订单");
        }

        // 更新笼舍状态
        if (cageId != null) {
            Cage cage = new Cage();
            cage.setId(cageId);
            cage.setStatus("OCCUPIED");
            cageService.updateById(cage);

            order.setCageId(cageId);
        }

        order.setStatus("CHECKED_IN");
        updateById(order);
    }

    @Override
    @Transactional
    public void completeOrder(Long orderId) {
        BoardingOrder order = getById(orderId);
        if (order == null) throw new BusinessException(ResultCode.ORDER_NOT_FOUND);
        if (!"CHECKED_IN".equals(order.getStatus())) {
            throw new BusinessException(ResultCode.ORDER_STATUS_ERROR, "只能完成已入住的订单");
        }

        // 释放笼舍
        if (order.getCageId() != null) {
            Cage cage = new Cage();
            cage.setId(order.getCageId());
            cage.setStatus("AVAILABLE");
            cageService.updateById(cage);
        }

        order.setStatus("COMPLETED");
        updateById(order);
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId) {
        BoardingOrder order = getById(orderId);
        if (order == null) throw new BusinessException(ResultCode.ORDER_NOT_FOUND);
        if ("CHECKED_IN".equals(order.getStatus()) || "COMPLETED".equals(order.getStatus())) {
            throw new BusinessException(ResultCode.ORDER_STATUS_ERROR, "已入住或已完成的订单不能取消");
        }

        // 如果已分配笼舍，释放
        if (order.getCageId() != null) {
            Cage cage = new Cage();
            cage.setId(order.getCageId());
            cage.setStatus("AVAILABLE");
            cageService.updateById(cage);
        }

        order.setStatus("CANCELLED");
        updateById(order);
    }

    @Override
    public BoardingOrder getOrderDetail(Long orderId) {
        BoardingOrder order = getById(orderId);
        if (order == null) return null;

        // 查询关联信息
        if (order.getPetId() != null) {
            Pet pet = petService.getById(order.getPetId());
            if (pet != null) {
                order.setPetName(pet.getName());
                order.setPetType(pet.getType());
            }
        }

        if (order.getOwnerId() != null) {
            SysUser owner = sysUserService.getById(order.getOwnerId());
            if (owner != null) {
                order.setOwnerName(owner.getRealName());
            }
        }

        if (order.getCageId() != null) {
            Cage cage = cageService.getById(order.getCageId());
            if (cage != null) {
                order.setCageNo(cage.getCageNo());
            }
        }

        // 查询关联服务
        List<OrderServiceItem> services = orderServiceItemMapper.selectList(
                new LambdaQueryWrapper<OrderServiceItem>().eq(OrderServiceItem::getOrderId, orderId)
        );
        for (OrderServiceItem os : services) {
            ServiceItem si = serviceItemService.getById(os.getServiceId());
            if (si != null) {
                os.setServiceName(si.getName());
            }
        }
        order.setServices(services);

        return order;
    }
}
