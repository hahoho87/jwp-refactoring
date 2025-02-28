package ktichenpos.menu.acceptance.utils;

import static ktichenpos.menu.acceptance.utils.RestAssuredUtils.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpStatus;

import io.restassured.mapper.TypeRef;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import ktichenpos.menu.menu.ui.request.MenuProductRequest;
import ktichenpos.menu.menu.ui.request.MenuRequest;
import ktichenpos.menu.menu.ui.response.MenuGroupResponse;
import ktichenpos.menu.menu.ui.response.MenuResponse;
import ktichenpos.menu.menu.ui.response.ProductResponse;

public class MenuAcceptanceUtils {

	private MenuAcceptanceUtils() {
	}

	public static final String MENU_API_URL = "/api/menus";

	public static MenuResponse 메뉴_등록_되어_있음(String name, BigDecimal price, Long groupMenuId, Long productId,
		int quantity) {
		return 메뉴_등록_요청(name, price, groupMenuId, productId, quantity).as(MenuResponse.class);
	}

	public static ExtractableResponse<Response> 메뉴_등록_요청(
		String name,
		BigDecimal price,
		Long menuGroupId,
		Long productId,
		int quantity
	) {
		return post(MENU_API_URL, createRequest(name, price, menuGroupId, productId, quantity)).extract();
	}

	public static ExtractableResponse<Response> 메뉴_목록_조회_요청() {
		return get(MENU_API_URL).extract();
	}

	public static void 메뉴_등록_됨(ExtractableResponse<Response> response, String expectedName,
		BigDecimal expectedPrice, int expectedQuantity, MenuGroupResponse menuGroup, ProductResponse product) {
		MenuResponse menu = response.as(MenuResponse.class);
		assertAll(
			() -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
			() -> assertThat(menu.getName()).isEqualTo(expectedName),
			() -> assertThat(menu.getPrice().intValue()).isEqualTo(expectedPrice.intValue()),
			() -> assertThat(menu.getMenuGroupId()).isEqualTo(menuGroup.getId()),
			() -> assertThat(menu.getMenuProducts())
				.first()
				.satisfies(menuProduct -> {
					assertThat(menuProduct.getQuantity()).isEqualTo(expectedQuantity);
					assertThat(menuProduct.getProductId()).isEqualTo(product.getId());
				})
		);
	}

	public static void 메뉴_목록_조회_됨(ExtractableResponse<Response> response, MenuResponse expectedMenu,
		MenuGroupResponse menuGroup) {
		List<MenuResponse> menus = response.as(new TypeRef<List<MenuResponse>>() {
		});
		System.out.println("menus = " + menus);
		assertAll(
			() -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
			() -> assertThat(response.jsonPath().getList(".", MenuResponse.class))
				.first()
				.satisfies(menu -> {
					assertThat(menu.getName()).isEqualTo(expectedMenu.getName());
					assertThat(menu.getPrice().intValue()).isEqualTo(expectedMenu.getPrice().intValue());
					assertThat(menu.getMenuGroupId()).isEqualTo(menuGroup.getId());
				})
		);
	}

	private static MenuRequest createRequest(String name, BigDecimal price, Long menuGroupId, Long productId, int quantity) {
		return new MenuRequest(name, price, menuGroupId,
			Collections.singletonList(new MenuProductRequest(productId, quantity)));
	}
}
