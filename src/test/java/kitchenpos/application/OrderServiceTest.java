package kitchenpos.application;

import static kitchenpos.generator.MenuGenerator.*;
import static kitchenpos.generator.OrderGenerator.*;
import static kitchenpos.generator.OrderTableGenerator.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import kitchenpos.common.exception.NotFoundException;
import kitchenpos.menu.domain.Menu;
import kitchenpos.menu.domain.MenuRepository;
import kitchenpos.order.application.OrderService;
import kitchenpos.order.domain.Order;
import kitchenpos.order.domain.OrderRepository;
import kitchenpos.order.domain.OrderStatus;
import kitchenpos.order.domain.OrderTable;
import kitchenpos.order.domain.OrderTableRepository;
import kitchenpos.order.ui.request.OrderLineItemRequest;
import kitchenpos.order.ui.request.OrderRequest;
import kitchenpos.order.ui.request.OrderStatusRequest;

@DisplayName("주문 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

	@Mock
	private MenuRepository menuRepository;

	@Mock
	private OrderRepository orderRepository;

	@Mock
	private OrderTableRepository orderTableRepository;

	@InjectMocks
	private OrderService orderService;

	@DisplayName("주문을 등록할 수 있다.")
	@Test
	void createOrderTest() {
		// given
		long orderTableId = 1L;
		long menuId = 1L;
		long quantity = 1L;
		OrderRequest orderRequest = new OrderRequest(orderTableId,
			Collections.singletonList(new OrderLineItemRequest(menuId, quantity)));

		OrderTable table = 비어있지_않은_5명_테이블();
		given(orderTableRepository.orderTable(orderTableId)).willReturn(table);

		Menu menu = 후라이드_세트();
		given(menuRepository.menu(menuId)).willReturn(menu);

		Order order = 조리중_주문();
		given(orderRepository.save(any())).willReturn(order);

		// when
		orderService.create(orderRequest);

		// then
		주문_등록_됨(orderTableId);
	}

	@DisplayName("주문 항목이 없으면 주문을 등록할 수 없다.")
	@Test
	void createOrderWithoutOrderLineItemsTest() {
		// given
		long orderTableId = 1L;
		OrderRequest orderRequest = new OrderRequest(orderTableId, Collections.emptyList());

		// when
		Throwable throwable = catchThrowable(() -> orderService.create(orderRequest));

		// then
		assertThat(throwable).isInstanceOf(IllegalArgumentException.class);
	}

	@DisplayName("메뉴가 등록되어 있지 않으면 주문을 등록할 수 없다.")
	@Test
	void createOrderWithoutMenuTest() {
		// given
		long orderTableId = 1L;
		long menuId = 1L;
		long quantity = 1L;
		OrderRequest orderRequest = new OrderRequest(orderTableId,
			Collections.singletonList(new OrderLineItemRequest(menuId, quantity)));
		OrderTable table = 비어있지_않은_5명_테이블();
		// given(orderTableRepository.findById(orderTableId)).willReturn(Optional.of(table));
		given(menuRepository.menu(anyLong())).willThrow(new NotFoundException("no menu"));

		// when
		Throwable throwable = catchThrowable(() -> orderService.create(orderRequest));

		// then
		assertThat(throwable).isInstanceOf(NotFoundException.class);
	}

	@DisplayName("주문 테이블이 등록되어 있지 않으면 주문을 등록할 수 없다.")
	@Test
	void createOrderWithNotSavedTableTest() {
		// given
		long orderTableId = 1L;
		long menuId = 1L;
		long quantity = 1L;
		OrderRequest orderRequest = new OrderRequest(orderTableId,
			Collections.singletonList(new OrderLineItemRequest(menuId, quantity)));
		given(orderTableRepository.orderTable(orderTableId)).willThrow(NotFoundException.class);

		// when
		Throwable throwable = catchThrowable(() -> orderService.create(orderRequest));

		// then
		assertThat(throwable).isInstanceOf(NotFoundException.class);
	}

	@DisplayName("주문 테이블이 비어있으면 주문을 등록할 수 없다.")
	@Test
	void createOrderWithEmptyTableTest() {
		// given
		long orderTableId = 1L;
		long menuId = 1L;
		long quantity = 1L;
		OrderRequest orderRequest = new OrderRequest(orderTableId,
			Collections.singletonList(new OrderLineItemRequest(menuId, quantity)));
		OrderTable 비어있는_테이블 = 비어있는_다섯명_테이블();
		given(orderTableRepository.orderTable(orderTableId)).willReturn(비어있는_테이블);

		// when
		Throwable throwable = catchThrowable(() -> orderService.create(orderRequest));

		// then
		assertThat(throwable).isInstanceOf(IllegalArgumentException.class);
	}

	@DisplayName("주문을 조회할 수 있다.")
	@Test
	void listOrderTest() {
		// when
		orderService.list();

		// then
		verify(orderRepository, only()).findAll();
	}

	@DisplayName("주문 상태를 변경할 수 있다.")
	@Test
	void changeOrderStatusTest() {
		// given
		OrderStatus orderStatus = OrderStatus.MEAL;
		OrderStatusRequest orderStatusRequest = new OrderStatusRequest(orderStatus.name());
		Order order = 조리중_주문();
		given(orderRepository.findById(anyLong())).willReturn(Optional.of(order));

		// when
		orderService.changeOrderStatus(order.id(), orderStatusRequest);

		// then
		verify(order, times(1)).updateStatus(orderStatus);
	}

	@DisplayName("이미 등록된 주문이 아니면 주문 상태를 변경할 수 없다.")
	@Test
	void changeOrderStatusWithNotSavedOrderTest() {
		// given
		OrderStatus orderStatus = OrderStatus.MEAL;
		OrderStatusRequest orderStatusRequest = new OrderStatusRequest(orderStatus.name());
		given(orderRepository.findById(anyLong())).willReturn(Optional.empty());

		// when
		Throwable throwable = catchThrowable(() -> orderService.changeOrderStatus(1L, orderStatusRequest));

		// then
		assertThat(throwable).isInstanceOf(IllegalArgumentException.class);
	}

	@DisplayName("주문 상태가 계산 완료이면 주문 상태를 변경할 수 없다.")
	@Test
	void changeOrderStatusWithCompletedOrderTest() {
		// given
		OrderStatus orderStatus = OrderStatus.MEAL;
		OrderStatusRequest orderStatusRequest = new OrderStatusRequest(orderStatus.name());
		Order order = 계산_완료_주문();
		given(orderRepository.findById(anyLong())).willReturn(Optional.of(order));

		// when
		Throwable throwable = catchThrowable(() -> orderService.changeOrderStatus(order.id(), orderStatusRequest));

		// then
		assertThat(throwable).isInstanceOf(IllegalArgumentException.class);
	}

	private void 주문_등록_됨(long orderTableId) {
		ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
		verify(orderRepository, only()).save(captor.capture());
		Order savedOrder = captor.getValue();
		assertAll(
			() -> assertThat(savedOrder.table().id()).isEqualTo(orderTableId),
			() -> assertThat(savedOrder.orderStatus().name()).isEqualTo(OrderStatus.COOKING.name()),
			() -> assertThat(savedOrder.orderLineItems()).hasSize(1)
		);
	}

}
