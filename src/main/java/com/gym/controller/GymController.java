
package com.gym.controller;
import com.gym.model.GymGroup;
import com.gym.repository.GymGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api")
public class GymController {

    @Autowired GymGroupRepository gymGroupRepo;

    // Hii ndiyo health pekee inayobaki - usiweke nyingine popote
    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> res = new HashMap<>();
        res.put("status", "UP");
        res.put("message", "Gym Zanzibar Backend is Live");
        return res;
    }

    @GetMapping("/groups")
    public List<GymGroup> getGroups() {
        return gymGroupRepo.findAll();
    }

    @PostMapping("/groups")
    public Map<String, Object> createGroup(@RequestBody GymGroup g) {
        Map<String, Object> res = new HashMap<>();
        try {
            GymGroup saved = gymGroupRepo.save(g);
            res.put("success", true);
            res.put("message", "Group imetengenezwa");
            res.put("data", saved);
        } catch (Exception e) {
            res.put("success", false);
            res.put("message", "Imeshindwa: " + e.getMessage());
        }
        return res;
    }

    @GetMapping("/groups/{id}")
    public Map<String, Object> getGroup(@PathVariable Long id) {
        Map<String, Object> res = new HashMap<>();
        Optional<GymGroup> g = gymGroupRepo.findById(id);
        if (g.isEmpty()) {
            res.put("success", false);
            res.put("message", "Group haikupatikana");
            return res;
        }
        res.put("success", true);
        res.put("data", g.get());
        return res;
    }

    @DeleteMapping("/groups/{id}")
    public Map<String, Object> deleteGroup(@PathVariable Long id) {
        Map<String, Object> res = new HashMap<>();
        if (!gymGroupRepo.existsById(id)) {
            res.put("success", false);
            res.put("message", "Group haikupatikana");
            return res;
        }
        gymGroupRepo.deleteById(id);
        res.put("success", true);
        res.put("message", "Group imefutwa");
        return res;
    }
}
