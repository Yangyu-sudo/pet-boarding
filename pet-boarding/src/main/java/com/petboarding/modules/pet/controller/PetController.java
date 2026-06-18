package com.petboarding.modules.pet.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.petboarding.common.result.Result;
import com.petboarding.modules.pet.entity.Pet;
import com.petboarding.modules.pet.service.PetService;
import com.petboarding.modules.system.entity.SysUser;
import com.petboarding.modules.system.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pets")
@Tag(name = "宠物管理", description = "宠物CRUD")
public class PetController {

    @Autowired
    private PetService petService;

    @Autowired
    private SysUserService sysUserService;

    @GetMapping
    @Operation(summary = "宠物列表（员工看全部，顾客看自己的）")
    public Result<Page<Pet>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Long ownerId) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        SysUser currentUser = sysUserService.getByUsername(auth.getName());

        LambdaQueryWrapper<Pet> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(name)) {
            wrapper.like(Pet::getName, name);
        }
        if (StringUtils.hasText(type)) {
            wrapper.eq(Pet::getType, type);
        }
        if (ownerId != null) {
            wrapper.eq(Pet::getOwnerId, ownerId);
        }
        // 顾客只能看自己的宠物
        if ("CUSTOMER".equals(currentUser.getRole())) {
            wrapper.eq(Pet::getOwnerId, currentUser.getId());
        }
        wrapper.orderByDesc(Pet::getCreateTime);

        return Result.ok(petService.page(new Page<>(page, size), wrapper));
    }

    @GetMapping("/{id}")
    @Operation(summary = "宠物详情")
    public Result<Pet> getById(@PathVariable Long id) {
        return Result.ok(petService.getById(id));
    }

    @GetMapping("/owner/{ownerId}")
    @Operation(summary = "获取某主人的所有宠物")
    public Result<List<Pet>> getByOwner(@PathVariable Long ownerId) {
        List<Pet> pets = petService.list(
                new LambdaQueryWrapper<Pet>().eq(Pet::getOwnerId, ownerId)
        );
        return Result.ok(pets);
    }

    @PostMapping
    @Operation(summary = "添加宠物")
    public Result<?> add(@RequestBody Pet pet) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        SysUser currentUser = sysUserService.getByUsername(auth.getName());

        // 顾客只能给自己的添加宠物
        if ("CUSTOMER".equals(currentUser.getRole())) {
            pet.setOwnerId(currentUser.getId());
        }

        petService.save(pet);
        return Result.ok("添加成功");
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新宠物信息")
    public Result<?> update(@PathVariable Long id, @RequestBody Pet pet) {
        pet.setId(id);
        pet.setOwnerId(null); // 不允许修改归属
        petService.updateById(pet);
        return Result.ok("更新成功");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除宠物")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public Result<?> delete(@PathVariable Long id) {
        petService.removeById(id);
        return Result.ok("删除成功");
    }
}
