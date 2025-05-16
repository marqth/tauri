package fr.eseo.tauri.controller;

import fr.eseo.tauri.model.Victim;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vulnerable")
public class VulnerableController {

    @Autowired
    private JdbcTemplate jdbcTemplate;


    @GetMapping("/victim")
    public List<Victim> getVictimById(@RequestParam("id") String id) {
        String query = "SELECT * FROM victims WHERE id = " + id;
        System.out.println(query);
        return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(Victim.class));
    }
}
