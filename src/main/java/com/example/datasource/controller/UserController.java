package com.example.datasource.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.datasource.entity.User;
import com.example.datasource.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户 Controller
 * 提供 REST API 接口测试动态数据源切换
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 从 MySQL 获取所有用户
     */
    @GetMapping("/mysql")
    public ResponseEntity<Map<String, Object>> getMysqlUsers() {
        List<User> users = userService.getUsersFromMysql();
        Map<String, Object> result = new HashMap<>();
        result.put("datasource", "MySQL");
        result.put("count", users.size());
        result.put("data", users);
        return ResponseEntity.ok(result);
    }

    /**
     * 从 Oracle 获取所有用户
     */
    @GetMapping("/oracle")
    public ResponseEntity<Map<String, Object>> getOracleUsers() {
        List<User> users = userService.getUsersFromOracle();
        Map<String, Object> result = new HashMap<>();
        result.put("datasource", "Oracle");
        result.put("count", users.size());
        result.put("data", users);
        return ResponseEntity.ok(result);
    }

    /**
     * 从 MySQL 分页获取用户
     */
    @GetMapping("/mysql/page")
    public ResponseEntity<Map<String, Object>> getMysqlUsersByPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<User> userPage = userService.getUsersFromMysqlByPage(page, size);
        Map<String, Object> result = new HashMap<>();
        result.put("datasource", "MySQL");
        result.put("page", page);
        result.put("size", size);
        result.put("total", userPage.getTotal());
        result.put("pages", userPage.getPages());
        result.put("data", userPage.getRecords());
        return ResponseEntity.ok(result);
    }

    /**
     * 从 Oracle 分页获取用户
     */
    @GetMapping("/oracle/page")
    public ResponseEntity<Map<String, Object>> getOracleUsersByPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<User> userPage = userService.getUsersFromOracleByPage(page, size);
        Map<String, Object> result = new HashMap<>();
        result.put("datasource", "Oracle");
        result.put("page", page);
        result.put("size", size);
        result.put("total", userPage.getTotal());
        result.put("pages", userPage.getPages());
        result.put("data", userPage.getRecords());
        return ResponseEntity.ok(result);
    }

    /**
     * 从 MySQL 获取指定 ID 的用户
     */
    @GetMapping("/mysql/{id}")
    public ResponseEntity<Map<String, Object>> getMysqlUserById(@PathVariable Long id) {
        User user = userService.getUserByIdFromMysql(id);
        Map<String, Object> result = new HashMap<>();
        result.put("datasource", "MySQL");
        result.put("data", user);
        return ResponseEntity.ok(result);
    }

    /**
     * 从 Oracle 获取指定 ID 的用户
     */
    @GetMapping("/oracle/{id}")
    public ResponseEntity<Map<String, Object>> getOracleUserById(@PathVariable Long id) {
        User user = userService.getUserByIdFromOracle(id);
        Map<String, Object> result = new HashMap<>();
        result.put("datasource", "Oracle");
        result.put("data", user);
        return ResponseEntity.ok(result);
    }

    /**
     * 保存用户到 MySQL
     */
    @PostMapping("/mysql")
    public ResponseEntity<Map<String, Object>> saveUserToMysql(@RequestBody User user) {
        boolean success = userService.saveUserToMysql(user);
        Map<String, Object> result = new HashMap<>();
        result.put("datasource", "MySQL");
        result.put("success", success);
        result.put("message", success ? "保存成功" : "保存失败");
        result.put("data", user);
        return ResponseEntity.ok(result);
    }

    /**
     * 保存用户到 Oracle
     */
    @PostMapping("/oracle")
    public ResponseEntity<Map<String, Object>> saveUserToOracle(@RequestBody User user) {
        boolean success = userService.saveUserToOracle(user);
        Map<String, Object> result = new HashMap<>();
        result.put("datasource", "Oracle");
        result.put("success", success);
        result.put("message", success ? "保存成功" : "保存失败");
        result.put("data", user);
        return ResponseEntity.ok(result);
    }

    /**
     * 更新 MySQL 中的用户
     */
    @PutMapping("/mysql/{id}")
    public ResponseEntity<Map<String, Object>> updateUserToMysql(
            @PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        boolean success = userService.updateUserToMysql(user);
        Map<String, Object> result = new HashMap<>();
        result.put("datasource", "MySQL");
        result.put("success", success);
        result.put("message", success ? "更新成功" : "更新失败");
        result.put("data", user);
        return ResponseEntity.ok(result);
    }

    /**
     * 更新 Oracle 中的用户
     */
    @PutMapping("/oracle/{id}")
    public ResponseEntity<Map<String, Object>> updateUserToOracle(
            @PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        boolean success = userService.updateUserToOracle(user);
        Map<String, Object> result = new HashMap<>();
        result.put("datasource", "Oracle");
        result.put("success", success);
        result.put("message", success ? "更新成功" : "更新失败");
        result.put("data", user);
        return ResponseEntity.ok(result);
    }

    /**
     * 从 MySQL 删除用户
     */
    @DeleteMapping("/mysql/{id}")
    public ResponseEntity<Map<String, Object>> deleteUserFromMysql(@PathVariable Long id) {
        boolean success = userService.deleteUserFromMysql(id);
        Map<String, Object> result = new HashMap<>();
        result.put("datasource", "MySQL");
        result.put("success", success);
        result.put("message", success ? "删除成功" : "删除失败");
        result.put("id", id);
        return ResponseEntity.ok(result);
    }

    /**
     * 从 Oracle 删除用户
     */
    @DeleteMapping("/oracle/{id}")
    public ResponseEntity<Map<String, Object>> deleteUserFromOracle(@PathVariable Long id) {
        boolean success = userService.deleteUserFromOracle(id);
        Map<String, Object> result = new HashMap<>();
        result.put("datasource", "Oracle");
        result.put("success", success);
        result.put("message", success ? "删除成功" : "删除失败");
        result.put("id", id);
        return ResponseEntity.ok(result);
    }
}