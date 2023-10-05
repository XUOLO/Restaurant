package owlvernyte.springfood.controller.Admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ContacController {

    @GetMapping("/admin/listContact")
    public String listContact(){

        return "Admin/list_contact";
    }
}
