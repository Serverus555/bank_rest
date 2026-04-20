package com.example.bankcards.controller.admin;

import com.example.bankcards.util.controller.CommonErrorResponses;
import com.example.bankcards.dto.mappers.AdminDtoMappers;
import com.example.bankcards.dto.admin.in.CreateUserRequest;
import com.example.bankcards.dto.admin.out.AdminUserView;
import com.example.bankcards.dto.admin.in.EditUserRequest;
import com.example.bankcards.entity.User;
import com.example.bankcards.util.security.PreAuthorizeRole;
import com.example.bankcards.service.admin.AdminUserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/user")
@RequiredArgsConstructor
@PreAuthorizeRole("ADMIN")
@Validated
@CommonErrorResponses
public class AdminUserController {

    private final AdminUserService adminUserService;
    private final AdminDtoMappers adminDtoMappers;

    @PostMapping
    public AdminUserView create(@RequestBody @Valid CreateUserRequest createUserRequest) {
        User user = adminUserService.create(createUserRequest);
        return adminDtoMappers.adminUserView(user);
    }

    @GetMapping
    public PagedModel<AdminUserView> getAll(@ParameterObject @PageableDefault Pageable pageable) {
        return new PagedModel<>(adminUserService.getAll(pageable).map(adminDtoMappers::adminUserView));
    }

    @PutMapping
    public AdminUserView edit(@RequestBody @Valid EditUserRequest editUserRequest) {
        User user = adminUserService.edit(editUserRequest);
        return adminDtoMappers.adminUserView(user);
    }

    @DeleteMapping
    public void delete(@NotNull Long id) {
        adminUserService.delete(id);
    }
}
