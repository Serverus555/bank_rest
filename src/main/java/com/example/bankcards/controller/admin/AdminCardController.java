package com.example.bankcards.controller.admin;

import com.example.bankcards.util.controller.CommonErrorResponses;
import com.example.bankcards.dto.admin.out.AdminCardView;
import com.example.bankcards.dto.mappers.AdminDtoMappers;
import com.example.bankcards.dto.admin.in.CreateCardRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.util.security.PreAuthorizeRole;
import com.example.bankcards.service.admin.AdminCardService;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/card")
@PreAuthorizeRole("ADMIN")
@Validated
@CommonErrorResponses
public class AdminCardController {

    private final AdminCardService adminCardService;
    private final AdminDtoMappers adminDtoMappers;

    @PostMapping
    public AdminCardView create(@RequestBody @Valid CreateCardRequest createCardRequest) {
        Card card = adminCardService.create(createCardRequest);
        return adminDtoMappers.adminCardView(card);
    }

    @GetMapping
    public PagedModel<AdminCardView> getAll(@ParameterObject @PageableDefault Pageable pageable) {
        return new PagedModel<>(adminCardService.getAll(pageable).map(adminDtoMappers::adminCardView));
    }

    @PostMapping("/activate")
    public void activate(@RequestParam @NotNull Long id) {
        adminCardService.activate(id);
    }

    @DeleteMapping
    public void delete(@RequestParam @NotNull Long id) {
        adminCardService.delete(id);
    }
}
