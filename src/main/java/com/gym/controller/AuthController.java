package com.gym.controller;

import com.gym.model.User;
import com.gym.repository.UserRepository;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository users;

    public AuthController(UserRepository u) {
        this.users = u;
    }

    // ============ 1. REGISTER OWNER - OWNER WA KWANZA TU ============
    @PostMapping("/register-owner")
    public Map<String, Object> registerOwner(@RequestBody Map<String, Object> b) {
        try {
            String name = getStr(b, "name");
            String username = getStr(b, "username");
            String phone = getStr(b, "phone");
            String password = getStr(b, "password");

            if (name.isEmpty() || username.isEmpty() || password.isEmpty()) {
                return Map.of("success", false, "message", "Jaza Jina, Username na Password!");
            }
            if (password.length() < 4) {
                return Map.of("success", false, "message", "Password iwe angalau herufi 4");
            }

            // Owner tayari yupo?
            if (users.countByRole("OWNER") > 0) {
                return Map.of("success", false, "message", "Owner tayari yupo. Ingia kwa Login.");
            }

            // Username imetumika?
            if (users.findByUsername(username).isPresent()) {
                return Map.of("success", false, "message", "Username '" + username + "' tayari inatumika. Chagua nyingine.");
            }
            if (!phone.isEmpty() && users.findByPhone(phone).isPresent()) {
                return Map.of("success", false, "message", "Simu '" + phone + "' tayari inatumika.");
            }

            User u = new User();
            u.setName(name);
            u.setUsername(username);
            u.setPhone(phone);
            u.setPassword(password);
            u.setRole("OWNER");
            u.setStatus("active");
            u.setSubscriptionStatus("active");
            u.setGymGroup("All Gyms");
            u.setMonthlyFee(0.0);
            u.setSubscriptionExpiresAt(LocalDateTime.now().plusYears(10));
            u.setCreatedAt(LocalDateTime.now());
            users.save(u);

            return Map.of(
                "success", true,
                "message", "Owner amesajiliwa!",
                "id", u.getId(),
                "name", u.getName(),
                "username", u.getUsername(),
                "phone", u.getPhone() == null ? "" : u.getPhone(),
                "role", "OWNER",
                "status", "active"
            );
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("success", false, "message", "Error: " + e.getMessage());
        }
    }

    // ============ 2. REGISTER ADMIN - OWNER ANAMSAJILI ADMIN - HAPA NDIO KOSA LILIKUWA ============
    @PostMapping("/register-admin")
    public Map<String, Object> registerAdmin(@RequestBody Map<String, Object> b) {
        try {
            // 1. ANGALIA OWNER
            if (b.get("ownerId") == null || b.get("ownerId").toString().isEmpty()) {
                return Map.of("success", false, "message", "ownerId haipo. Ingia tena kama Owner.");
            }
            Long ownerId;
            try {
                ownerId = Long.parseLong(b.get("ownerId").toString());
            } catch (Exception e) {
                return Map.of("success", false, "message", "ownerId sio sahihi. Ingia tena.");
            }

            var ownerOpt = users.findById(ownerId);
            if (ownerOpt.isEmpty()) {
                return Map.of("success", false, "message", "Owner hajapatikana. Ingia tena kama Owner.");
            }
            var owner = ownerOpt.get();
            if (!"OWNER".equals(owner.getRole())) {
                return Map.of("success", false, "message", "Ni Owner tu anayeweza kusajili Admin. Wewe ni " + owner.getRole());
            }

            // 2. ANGALIA FOMU
            String name = getStr(b, "name");
            String username = getStr(b, "username");
            String phone = getStr(b, "phone");
            String password = getStr(b, "password");
            String gymGroup = getStr(b, "gymGroup");
            if (gymGroup.isEmpty()) gymGroup = "Gym Stone Town - Jenga Mwili";

            if (name.isEmpty() || username.isEmpty() || password.isEmpty() || phone.isEmpty()) {
                return Map.of("success", false, "message", "Jaza Jina, Username, Simu na Password zote!");
            }
            if (password.length() < 4) {
                return Map.of("success", false, "message", "Password iwe angalau herufi 4");
            }
            if (phone.length() < 10) {
                return Map.of("success", false, "message", "Simu sio sahihi. Andika 077... au 088...");
            }

            // 3. ANGALIA KAMA USERNAME / SIMU ZIMETUMIKA - HII NDIO SABABU KUBWA YA KUSHINDIKANA
            if (users.findByUsername(username).isPresent()) {
                return Map.of("success", false, "message", "Username '" + username + "' tayari inatumika. Jaribu '" + username + "1' au '" + username + "_01'");
            }
            if (users.findByPhone(phone).isPresent()) {
                return Map.of("success", false, "message", "Simu '" + phone + "' tayari inatumika na mtu mwingine. Tumia simu nyingine.");
            }

            // 4. ADA
            Double monthlyFee = 50000.0;
            try {
                if (b.get("monthlyFee") != null && !b.get("monthlyFee").toString().isEmpty()) {
                    monthlyFee = Double.parseDouble(b.get("monthlyFee").toString());
                }
            } catch (Exception e) {
                monthlyFee = 50000.0;
            }

            // 5. SAJILI ADMIN
            User a = new User();
            a.setName(name);
            a.setUsername(username);
            a.setPhone(phone);
            a.setPassword(password);
            a.setRole("ADMIN");
            a.setStatus("active");
            a.setSubscriptionStatus("active");
            a.setGymGroup(gymGroup);
            a.setMonthlyFee(monthlyFee);
            a.setCreatedBy(ownerId);
            a.setSubscriptionExpiresAt(LocalDateTime.now().plusDays(30));
            a.setCreatedAt(LocalDateTime.now());
            users.save(a);

            return Map.of(
                "success", true,
                "message", "Admin " + name + " amesajiliwa!",
                "id", a.getId(),
                "name", a.getName(),
                "username", a.getUsername(),
                "phone", a.getPhone(),
                "role", "ADMIN",
                "status", "active",
                "gymGroup", a.getGymGroup(),
                "monthlyFee", a.getMonthlyFee()
            );

        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("success", false, "message", "Error kwenye server: " + e.getMessage());
        }
    }

    // ============ 3. REGISTER MEMBER - ADMIN ANAMSAJILI MEMBER ============
    @PostMapping("/register-member")
    public Map<String, Object> registerMember(@RequestBody Map<String, Object> b) {
        try {
            if (b.get("adminId") == null) {
                return Map.of("success", false, "message", "adminId haipo. Ingia tena kama Admin.");
            }
            Long adminId = Long.parseLong(b.get("adminId").toString());
            var adminOpt = users.findById(adminId);
            if (adminOpt.isEmpty()) return Map.of("success", false, "message", "Admin hajapatikana.");
            var admin = adminOpt.get();
            if (!"ADMIN".equals(admin.getRole())) return Map.of("success", false, "message", "Ni Admin tu anayeweza kusajili Member.");
            if ("locked".equals(admin.getStatus())) return Map.of("success", false, "message", "Admin umejifunga. Lipa ada kwa Owner kwanza.");

            String name = getStr(b, "name");
            String username = getStr(b, "username");
            String phone = getStr(b, "phone");
            String password = getStr(b, "password");
            String gymGroup = getStr(b, "gymGroup");
            if (gymGroup.isEmpty()) gymGroup = admin.getGymGroup();
            String plan = getStr(b, "plan");
            if (plan.isEmpty()) plan = "MWEZI";

            if (name.isEmpty() || username.isEmpty() || password.isEmpty() || phone.isEmpty()) {
                return Map.of("success", false, "message", "Jaza Jina, Username, Simu na Password zote!");
            }
            if (password.length() < 4) return Map.of("success", false, "message", "Password iwe angalau herufi 4");
            if (users.findByUsername(username).isPresent()) {
                return Map.of("success", false, "message", "Username '" + username + "' tayari inatumika.");
            }
            if (users.findByPhone(phone).isPresent()) {
                return Map.of("success", false, "message", "Simu '" + phone + "' tayari inatumika.");
            }

            User m = new User();
            m.setName(name);
            m.setUsername(username);
            m.setPhone(phone);
            m.setPassword(password);
            m.setRole("MEMBER");
            m.setStatus("pending");
            m.setSubscriptionStatus("pending");
            m.setGymGroup(gymGroup);
            m.setPlan(plan);
            m.setCreatedBy(adminId);
            m.setCreatedAt(LocalDateTime.now());
            users.save(m);

            return Map.of("success", true, "message", "Member amesajiliwa! Subiri Admin amthibitishe.", "id", m.getId(), "name", m.getName(), "username", m.getUsername(), "role", "MEMBER");
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("success", false, "message", "Error: " + e.getMessage());
        }
    }

    // ============ 4. LOGIN ============
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, Object> b) {
        try {
            String username = getStr(b, "username");
            String password = getStr(b, "password");
            if (username.isEmpty() || password.isEmpty()) {
                return Map.of("success", false, "message", "Jaza Username na Password");
            }
            var userOpt = users.findByUsername(username);
            if (userOpt.isEmpty()) {
                return Map.of("success", false, "message", "Username haijapatikana. Hakikisha umeandika sahihi.");
            }
            var u = userOpt.get();
            if (!password.equals(u.getPassword())) {
                return Map.of("success", false, "message", "Password sio sahihi.");
            }
            if ("locked".equals(u.getStatus()) && "ADMIN".equals(u.getRole())) {
                return Map.of("success", false, "message", "Admin umejifunga. Lipa ada kwa Owner kwanza ili ufunguliwe.");
            }
            return Map.of(
                "success", true,
                "message", "Karibu " + u.getName(),
                "id", u.getId(),
                "name", u.getName(),
                "username", u.getUsername(),
                "phone", u.getPhone() == null ? "" : u.getPhone(),
                "role", u.getRole(),
                "status", u.getStatus() == null ? "active" : u.getStatus(),
                "gymGroup", u.getGymGroup() == null ? "" : u.getGymGroup(),
                "monthlyFee", u.getMonthlyFee() == null ? 0 : u.getMonthlyFee(),
                "createdBy", u.getCreatedBy() == null ? 0 : u.getCreatedBy()
            );
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("success", false, "message", "Error: " + e.getMessage());
        }
    }

    // ============ HELPER ============
    private String getStr(Map<String, Object> b, String key) {
        Object v = b.get(key);
        if (v == null) return "";
        return v.toString().trim();
    }
}
