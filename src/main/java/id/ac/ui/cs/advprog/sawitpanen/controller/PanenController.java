package id.ac.ui.cs.advprog.sawitpanen.controller;

import id.ac.ui.cs.advprog.sawitpanen.model.Panen;
import id.ac.ui.cs.advprog.sawitpanen.service.PanenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/panen")
public class PanenController {
    @Autowired
    private PanenService panenService;

    @GetMapping("/lapor")
    public String laporPanenPage(Model model) {
        Panen panen = new Panen();
        model.addAttribute("panen", panen);
        return "laporPanenSawit";
    }

    @PostMapping("/lapor")
    public String laporPanenPost(@ModelAttribute Panen panen, Model model) {
        panenService.create(panen);
        return "redirect:list";
    }

    @GetMapping("/list")
    public String daftarPanenPage(Model model) {
        List<Panen> daftarPanen = panenService.findAll();
        model.addAttribute("daftarPanen", daftarPanen);
        return "daftarPanenSawit";
    }
}
