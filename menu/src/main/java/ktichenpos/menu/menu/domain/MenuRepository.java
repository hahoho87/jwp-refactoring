package ktichenpos.menu.menu.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepository extends JpaRepository<Menu, Long> {

	default Menu menu(long id) {
		return findById(id).orElseThrow(
			() -> new IllegalArgumentException(String.format("메뉴 id(%d)를 찾을 수 없습니다.", id)));
	}
}
