package com.moople.gitpals.MainApplication.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class GuidePageController
{
    @GetMapping("/guide/how-to-create-a-good-description-for-my-project")
    public ModelAndView goodDescriptionGuidwe()
    {
        return new ModelAndView("guide/goodProjDescription");
    }
}