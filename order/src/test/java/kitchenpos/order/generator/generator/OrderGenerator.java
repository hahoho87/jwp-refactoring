package kitchenpos.order.generator.generator;

import static kitchenpos.order.generator.generator.OrderLineItemGenerator.*;
import static org.mockito.BDDMockito.*;

import java.util.Collections;

import kitchenpos.order.order.domain.Order;
import kitchenpos.order.order.domain.OrderLineItems;
import kitchenpos.order.order.domain.OrderStatus;

public class OrderGenerator {
	private OrderGenerator() {
	}

	public static Order 조리중_주문() {
		Order order = spy(Order.of(1L, OrderLineItems.from(Collections.singletonList(주문_품목()))));
		given(order.getId()).willReturn(1L);
		return order;
	}

	public static Order 계산_완료_주문() {
		Order order = 조리중_주문();
		order.updateStatus(OrderStatus.COMPLETION);
		return order;
	}
}
