package in.coempt.controller;

import in.coempt.entity.Center;
import in.coempt.service.CenterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/centers")
public class CenterController {
    @Autowired
    private CenterService centerService;

    @GetMapping
    public List<Center> getAllCenters() {
        return centerService.getAllCenters();
    }

    @GetMapping("/{id}")
    public Center getCenterById(@PathVariable Integer id) {
        return centerService.getCenterById(id);
    }

    @PostMapping
    public Center createCenter(@RequestBody Center center) {
        return centerService.saveCenter(center);
    }

    @DeleteMapping("/{id}")
    public void deleteCenter(@PathVariable Integer id) {
        centerService.deleteCenter(id);
    }
}


// Repeat similar structure for all other entities

