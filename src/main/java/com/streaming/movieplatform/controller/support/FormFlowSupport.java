package com.streaming.movieplatform.controller.support;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.function.Supplier;

public final class FormFlowSupport {

    private FormFlowSupport() {
    }

    public static void addIfAbsent(Model model, String attributeName, Supplier<?> supplier) {
        if (!model.containsAttribute(attributeName)) {
            model.addAttribute(attributeName, supplier.get());
        }
    }

    public static String redirectWithValidationErrors(RedirectAttributes redirectAttributes,
                                                      String redirectPath,
                                                      String attributeName,
                                                      Object form,
                                                      BindingResult bindingResult) {
        redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult." + attributeName, bindingResult);
        redirectAttributes.addFlashAttribute(attributeName, form);
        return "redirect:" + redirectPath;
    }

    public static void flashForm(RedirectAttributes redirectAttributes, String attributeName, Object form) {
        redirectAttributes.addFlashAttribute(attributeName, form);
    }
}
