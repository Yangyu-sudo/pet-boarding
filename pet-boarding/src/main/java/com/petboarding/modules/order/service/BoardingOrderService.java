package com.petboarding.modules.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.petboarding.modules.order.entity.BoardingOrder;
import com.petboarding.modules.order.entity.OrderServiceItem;

import java.util.List;

public interface BoardingOrderService extends IService<BoardingOrder> {

    /**
     * 创建订单
     */
    BoardingOrder createOrder(BoardingOrder order, List<OrderServiceItem> services);

    /**
     * 确认订单
     */
    void confirmOrder(Long orderId);

    /**
     * 入住
     */
    void checkIn(Long orderId, Long cageId);

    /**
     * 完成订单
     */
    void completeOrder(Long orderId);

    /**
     * 取消订单
     */
    void cancelOrder(Long orderId);

    /**
     * 获取订单详情(含关联信息)
     */
    BoardingOrder getOrderDetail(Long orderId);
}
