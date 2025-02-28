package kitchenpos.table.acceptance;

import static kitchenpos.table.acceptance.utils.TableAcceptanceUtils.*;
import static kitchenpos.table.acceptance.utils.TableGroupAcceptanceUtils.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.table.table.ui.response.OrderTableResponse;
import kitchenpos.table.table.ui.response.TableGroupResponse;

@DisplayName("단체 지정 관련 기능")
class TableGroupAcceptanceTest extends AcceptanceTest {

	@DisplayName("단체 지정을 할 수 있다.")
	@Test
	void createTableGroupTest() {
		// given
		OrderTableResponse firstOrderTable = 주문_테이블_등록_되어_있음(2, true);
		OrderTableResponse secondOrderTable = 주문_테이블_등록_되어_있음(5, true);
		List<Long> orderTableIds = Arrays.asList(firstOrderTable.getId(), secondOrderTable.getId());

		// when
		ExtractableResponse<Response> 단체_지정_요청 = 단체_지정_요청(orderTableIds);

		// then
		단체_지정_됨(단체_지정_요청, orderTableIds);
	}

	@DisplayName("단체 지정을 해제할 수 있다.")
	@Test
	void ungroupTest() {
		// given
		OrderTableResponse firstOrderTable = 주문_테이블_등록_되어_있음(2, true);
		OrderTableResponse secondOrderTable = 주문_테이블_등록_되어_있음(5, true);
		TableGroupResponse tableGroup = 단체_지정_되어잇음(Arrays.asList(firstOrderTable.getId(), secondOrderTable.getId()));

		// when
		ExtractableResponse<Response> 단체_지정_해제_요청 = 단체_지정_해제_요청(tableGroup.getId());

		// then
		단체_지정_해제_됨(단체_지정_해제_요청);
	}
}
