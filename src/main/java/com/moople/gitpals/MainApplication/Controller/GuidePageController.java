package com.moople.gitpals.MainApplication.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GuidePageController
{
    @GetMapping("/guide/how-to-create-a-good-description-for-my-project")
    public String goodDescriptionGuidwe()
    {
        return "guide/goodProjDescription";
    }
}
