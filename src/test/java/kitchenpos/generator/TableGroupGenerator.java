package kitchenpos.generator;

import java.time.LocalDateTime;
import java.util.List;

import kitchenpos.order.domain.OrderTable;
import kitchenpos.order.domain.TableGroup;

public class TableGroupGenerator {

	private TableGroupGenerator() {
	}

	public static TableGroup 단체_지정(List<OrderTable> orderTables) {
		return new TableGroup(1L, LocalDateTime.now(), orderTables);
	}
}
