package ktichenpos.menu.menu.application;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ktichenpos.menu.menu.domain.MenuGroup;
import ktichenpos.menu.menu.domain.MenuGroupRepository;
import ktichenpos.menu.menu.ui.request.MenuGroupRequest;
import ktichenpos.menu.menu.ui.response.MenuGroupResponse;

@Service
public class MenuGroupService {
    private final MenuGroupRepository menuGroupRepository;

    public MenuGroupService(final MenuGroupRepository menuGroupRepository) {
        this.menuGroupRepository = menuGroupRepository;
    }

    @Transactional
    public MenuGroupResponse create(final MenuGroupRequest menuGroup) {
        MenuGroup entity = menuGroup.toEntity();
        return MenuGroupResponse.from(menuGroupRepository.save(entity));
    }

    public List<MenuGroupResponse> list() {
        return MenuGroupResponse.listFrom(menuGroupRepository.findAll());
    }
}
