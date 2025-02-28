package kitchenpos.table.table.ui;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import kitchenpos.table.table.appliation.TableService;
import kitchenpos.table.table.ui.request.NumberOfGuestsRequest;
import kitchenpos.table.table.ui.request.OrderTableRequest;
import kitchenpos.table.table.ui.request.TableStatusRequest;
import kitchenpos.table.table.ui.response.OrderTableResponse;

@RestController
public class TableRestController {
	private final TableService tableService;

	public TableRestController(final TableService tableService) {
		this.tableService = tableService;
	}

	@PostMapping("/api/tables")
	public ResponseEntity<OrderTableResponse> create(@RequestBody final OrderTableRequest request) {
		final OrderTableResponse created = tableService.create(request);
		final URI uri = URI.create("/api/tables/" + created.getId());
		return ResponseEntity.created(uri).body(created);
	}

	@GetMapping("/api/tables")
	public ResponseEntity<List<OrderTableResponse>> list() {
		return ResponseEntity.ok()
			.body(tableService.list());
	}

	@PutMapping("/api/tables/{orderTableId}/empty")
	public ResponseEntity<OrderTableResponse> changeEmpty(
		@PathVariable final Long orderTableId,
		@RequestBody final TableStatusRequest request
	) {
		return ResponseEntity.ok()
			.body(tableService.changeEmpty(orderTableId, request));
	}

	@PutMapping("/api/tables/{orderTableId}/number-of-guests")
	public ResponseEntity<OrderTableResponse> changeNumberOfGuests(
		@PathVariable final Long orderTableId,
		@RequestBody final NumberOfGuestsRequest request
	) {
		return ResponseEntity.ok()
			.body(tableService.changeNumberOfGuests(orderTableId, request));
	}

	@GetMapping("/api/tables/{orderTableId}")
	public ResponseEntity<OrderTableResponse> getTable(@PathVariable final Long orderTableId) {
		return ResponseEntity.ok()
			.body(tableService.getTable(orderTableId));
	}

	@PostMapping("/{orderTableId}/order")
	public ResponseEntity<Void> changeOrdered(@PathVariable long orderTableId) {
		tableService.changeOrdered(orderTableId);
		return ResponseEntity.noContent().build();
	}
}
