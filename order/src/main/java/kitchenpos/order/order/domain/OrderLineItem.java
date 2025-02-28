package kitchenpos.order.order.domain;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.springframework.util.Assert;

@Entity
public class OrderLineItem {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long seq;

	@ManyToOne
	@JoinColumn(name = "order_id", foreignKey = @ForeignKey(name = "fk_order_line_item_to_order"))
	private Order order;

	@Embedded
	private OrderLineItemMenu menu;


	@Embedded
	private Quantity quantity;

	protected OrderLineItem() {
	}

	private OrderLineItem(OrderLineItemMenu menu, Quantity quantity) {
		Assert.notNull(menu, "메뉴는 필수입니다.");
		Assert.notNull(quantity, "수량은 필수입니다.");
		this.menu = menu;
		this.quantity = quantity;
	}

	public static OrderLineItem of(OrderLineItemMenu menu, Quantity quantity) {
		return new OrderLineItem(menu, quantity);
	}

	public static OrderLineItem of(OrderLineItemMenu menu, long quantity) {
		return new OrderLineItem(menu, Quantity.from(quantity));
	}

	public Long seq() {
		return seq;
	}

	public Long orderId() {
		return order.getId();
	}

	public OrderLineItemMenu menu() {
		return menu;
	}

	public Quantity quantity() {
		return quantity;
	}

	public void updateOrder(Order order) {
		this.order = order;
	}
}
