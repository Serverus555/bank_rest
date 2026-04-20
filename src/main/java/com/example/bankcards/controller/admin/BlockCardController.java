package com.example.bankcards.controller.admin;

import com.example.bankcards.util.controller.CommonErrorResponses;
import com.example.bankcards.dto.mappers.AdminDtoMappers;
import com.example.bankcards.dto.admin.out.PendingBlockCardView;
import com.example.bankcards.util.security.PreAuthorizeRole;
import com.example.bankcards.service.admin.AdminBlockCardService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/card/block")
@PreAuthorizeRole("ADMIN")
@Validated
@CommonErrorResponses
public class BlockCardController {

    private final AdminBlockCardService adminBlockCardService;
    private final AdminDtoMappers adminDtoMappers;

    @GetMapping("/pending")
    public PagedModel<PendingBlockCardView> getPendingBlockRequests(@ParameterObject @PageableDefault Pageable pageable) {
        return new PagedModel<>(adminBlockCardService.getPendingBlockRequests(pageable).map(adminDtoMappers::pendingBlockCardView));
    }

    @PostMapping("/block")
    public void block(@RequestParam @NotNull Long cardId) {
        adminBlockCardService.block(cardId);
    }
}
